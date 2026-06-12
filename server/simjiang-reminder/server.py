#!/usr/bin/env python3
import json, sqlite3, time, threading, ssl, smtplib, urllib.parse, urllib.request, secrets, re
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from email.mime.text import MIMEText
from email.header import Header
from email.utils import formataddr
from pathlib import Path
from datetime import date

BASE=Path('/opt/simjiang-reminder')
DB=BASE/'data.db'
OLD_KEY_FILE=BASE/'api_key.txt'
HOST='0.0.0.0'; PORT=8787
KEY_RE=re.compile(r'^[A-Za-z0-9_-]{24,80}$')

def clean_key(k):
    return ''.join(str(k or '').strip().split())

def new_key():
    return secrets.token_urlsafe(24)

def db():
    conn=sqlite3.connect(DB)
    conn.row_factory=sqlite3.Row
    conn.execute("PRAGMA journal_mode=WAL")
    conn.execute("""create table if not exists users(
        api_key text primary key,
        payload text not null,
        updated_at integer not null
    )""")
    conn.execute("""create table if not exists user_meta(
        api_key text primary key,
        nickname text,
        created_at integer not null,
        enabled integer not null default 1
    )""")
    conn.execute("""create table if not exists sent_log(
        api_key text not null,
        record_id text not null,
        channel text not null,
        day text not null,
        primary key(api_key,record_id,channel,day)
    )""")
    if OLD_KEY_FILE.exists():
        old=clean_key(OLD_KEY_FILE.read_text())
        if old and KEY_RE.match(old):
            conn.execute('insert or ignore into user_meta(api_key,nickname,created_at,enabled) values(?,?,?,1)',(old,'default',int(time.time())))
    conn.commit(); return conn

def ensure_user(api_key, nickname=''):
    api_key=clean_key(api_key)
    if not KEY_RE.match(api_key): return False
    conn=db()
    conn.execute('insert or ignore into user_meta(api_key,nickname,created_at,enabled) values(?,?,?,1)',(api_key,nickname or '',int(time.time())))
    conn.commit(); conn.close(); return True

def user_enabled(api_key):
    api_key=clean_key(api_key)
    if not KEY_RE.match(api_key): return False
    conn=db(); row=conn.execute('select enabled from user_meta where api_key=?',(api_key,)).fetchone(); conn.close()
    return bool(row and int(row['enabled'])==1)

def mask_number(n):
    ds=''.join(ch for ch in str(n) if ch.isdigit())
    if len(ds)<=4: return ds or str(n)
    return ds[:3]+'****'+ds[-4:]

def days_left(exp):
    try:
        d=date.fromisoformat(str(exp)[:10])
        return (d-date.today()).days
    except Exception:
        return None

def send_tg(token, chat_id, text):
    if not token or not chat_id: return False, 'Telegram 未配置'
    try:
        url='https://api.telegram.org/bot%s/sendMessage'%token
        data=urllib.parse.urlencode({'chat_id':chat_id,'text':text}).encode()
        with urllib.request.urlopen(url, data=data, timeout=15) as r:
            body=r.read().decode('utf-8','ignore')
        return True, body[:160]
    except urllib.error.HTTPError as e:
        err_body=''; 
        try: err_body=e.read().decode('utf-8','ignore')[:200]
        except: pass
        return False, 'Telegram HTTP %d: %s'%(e.code, err_body or e.reason)
    except Exception as e:
        return False, 'Telegram 发送失败: %s: %s'%(type(e).__name__, str(e))

def send_mail(cfg, subject, body):
    host=cfg.get('smtpHost',''); port=int(cfg.get('smtpPort') or 465)
    user=cfg.get('smtpUser',''); pwd=cfg.get('smtpPass',''); to=cfg.get('smtpTo',''); sender=cfg.get('smtpFrom') or user
    if not (host and user and pwd and to): return False, 'SMTP 未配置完整'
    msg=MIMEText(body,'plain','utf-8')
    msg['Subject']=Header(subject,'utf-8')
    msg['From']=formataddr(('SimJiang', sender))
    msg['To']=to
    try:
        ctx=ssl.create_default_context()
        with smtplib.SMTP_SSL(host, port, context=ctx, timeout=25) as s:
            s.login(user,pwd)
            s.sendmail(sender,[to],msg.as_string())
        return True, 'OK'
    except Exception as e:
        return False, '邮件发送失败: %s: %s'%(type(e).__name__, str(e))

def reminder_text(r, left):
    op=r.get('operator') or r.get('countryName') or 'SIM'
    num=mask_number(r.get('number',''))
    exp=r.get('expireDate','')
    return f"⏰ SimJiang 到期提醒\n{r.get('flag','')} {op} {r.get('countryCode','')} {num}\n到期日期：{exp}\n剩余天数：{left} 天"

def check_once(only_key=None):
    conn=db(); today=date.today().isoformat()
    if only_key:
        rows=conn.execute('select u.* from users u join user_meta m on u.api_key=m.api_key where u.api_key=? and m.enabled=1',(clean_key(only_key),)).fetchall()
    else:
        rows=conn.execute('select u.* from users u join user_meta m on u.api_key=m.api_key where m.enabled=1').fetchall()
    stats={'users':len(rows),'tg':0,'mail':0,'due':0}
    for row in rows:
        api=row['api_key']; payload=json.loads(row['payload'])
        settings=payload.get('settings') or {}; records=payload.get('records') or []
        remind=int(settings.get('remindDays') or settings.get('remind天') or 7)
        cloud_tg=bool(settings.get('cloudTelegramEnabled', True))
        cloud_mail=bool(settings.get('cloudEmailEnabled', True))
        for r in records:
            rid=str(r.get('id') or r.get('number') or '')
            left=days_left(str(r.get('expireDate','')))
            if left is None or left<0 or left>remind: continue
            stats['due']+=1
            text=reminder_text(r,left)
            subject='SimJiang 到期提醒：'+mask_number(r.get('number',''))
            if cloud_tg and settings.get('tgEnabled') and settings.get('botToken') and settings.get('chatId'):
                if not conn.execute('select 1 from sent_log where api_key=? and record_id=? and channel=? and day=?',(api,rid,'tg',today)).fetchone():
                    try:
                        send_tg(settings.get('botToken'),settings.get('chatId'),text)
                        conn.execute('insert or ignore into sent_log values(?,?,?,?)',(api,rid,'tg',today)); conn.commit(); stats['tg']+=1
                    except Exception as e: print('tg error',api,e, flush=True)
            if cloud_mail and settings.get('smtpEnabled') and settings.get('smtpTo'):
                if not conn.execute('select 1 from sent_log where api_key=? and record_id=? and channel=? and day=?',(api,rid,'mail',today)).fetchone():
                    try:
                        send_mail(settings,subject,text)
                        conn.execute('insert or ignore into sent_log values(?,?,?,?)',(api,rid,'mail',today)); conn.commit(); stats['mail']+=1
                    except Exception as e: print('mail error',api,e, flush=True)
    conn.close(); return stats

def loop():
    while True:
        try: print('check', check_once(), flush=True)
        except Exception as e: print('check error',e, flush=True)
        time.sleep(1800)

class H(BaseHTTPRequestHandler):
    def _json(self, code, obj):
        data=json.dumps(obj,ensure_ascii=False).encode()
        self.send_response(code); self.send_header('Content-Type','application/json; charset=utf-8'); self.send_header('Content-Length',str(len(data))); self.end_headers(); self.wfile.write(data)
    def _read_json(self):
        n=int(self.headers.get('Content-Length','0') or 0)
        body=self.rfile.read(n).decode('utf-8','ignore')
        try: return json.loads(body or '{}')
        except Exception: return None
    def _auth_key(self):
        k=clean_key(self.headers.get('X-API-Key',''))
        return k if user_enabled(k) else ''
    def do_GET(self):
        if self.path.startswith('/api/status'):
            conn=db(); users=conn.execute('select count(*) c from user_meta where enabled=1').fetchone()['c']; conn.close()
            return self._json(200, {'ok':True,'service':'simjiang-reminder','version':'v2-multi-user','users':users,'time':int(time.time())})
        return self._json(404, {'ok':False,'error':'not found'})
    def do_POST(self):
        if self.path.startswith('/api/register'):
            payload=self._read_json()
            if payload is None: return self._json(400, {'ok':False,'error':'bad json'})
            k=new_key(); nickname=str(payload.get('nickname') or '').strip()[:50]
            ensure_user(k,nickname)
            return self._json(200, {'ok':True,'apiKey':k,'message':'已生成独立 API Key'})
        api_key=self._auth_key()
        if not api_key: return self._json(401, {'ok':False,'error':'bad api key'})
        payload=self._read_json()
        if payload is None: return self._json(400, {'ok':False,'error':'bad json'})
        if self.path.startswith('/api/sync'):
            conn=db(); conn.execute('insert or replace into users values(?,?,?)',(api_key,json.dumps(payload,ensure_ascii=False),int(time.time()))); conn.commit(); conn.close()
            return self._json(200, {'ok':True,'records':len(payload.get('records') or []),'message':'同步成功','apiKeyTail':api_key[-6:]})
        if self.path.startswith('/api/test-telegram'):
            s=payload.get('settings') or payload
            ok,msg=send_tg(s.get('botToken'),s.get('chatId'),'✅ SimJiang 云端 Telegram 测试成功\nKey: ****'+api_key[-6:])
            return self._json(200, {'ok':ok,'message':msg})
        if self.path.startswith('/api/test-email'):
            s=payload.get('settings') or payload
            ok,msg=send_mail(s,'SimJiang 云端邮件测试','✅ SimJiang 云端邮件测试成功。\nKey: ****'+api_key[-6:])
            return self._json(200, {'ok':ok,'message':msg})
        if self.path.startswith('/api/check-now'):
            stats=check_once(api_key); return self._json(200, {'ok':True,'message':'已触发当前 Key 检查','stats':stats})
        return self._json(404, {'ok':False,'error':'not found'})
    def log_message(self, fmt, *args): print('%s - %s'%(self.address_string(), fmt%args), flush=True)

if __name__=='__main__':
    BASE.mkdir(parents=True, exist_ok=True); db().close()
    threading.Thread(target=loop,daemon=True).start()
    print(f'SimJiang reminder v2 multi-user listening on {HOST}:{PORT}', flush=True)
    ThreadingHTTPServer((HOST,PORT),H).serve_forever()
