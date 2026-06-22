# SimJiang 云端提醒服务 — 搭建教程

## 一、概述

simjiang-reminder 是一个纯 Python 实现的轻量级云端提醒服务，配合 simJ（原 SimJiang）App 使用。

**核心功能：**
- 多用户数据同步（每人独立 API Key）
- 号码到期提醒（Telegram / 邮件双通道）
- 自动数据库备份（每 6 小时，保留 30 份）
- 数据库自动恢复（异常丢失时从备份还原）

**技术栈：**
- Python 3.8+（无第三方依赖，纯标准库）
- SQLite（WAL 模式）
- systemd 守护进程

---

## 二、服务器要求

| 项目 | 最低要求 |
|------|----------|
| 系统 | Linux（Debian/Ubuntu/CentOS 均可） |
| Python | 3.8+ |
| 内存 | 128MB+ |
| 磁盘 | 1GB+（含备份） |
| 端口 | 8787（可自定义） |

---

## 三、安装步骤

### 3.1 创建项目目录

```bash
mkdir -p /opt/simjiang-reminder
chmod 700 /opt/simjiang-reminder
cd /opt/simjiang-reminder
```

### 3.2 创建服务端程序

创建 `server.py`：

```python
#!/usr/bin/env python3
import json, sqlite3, time, threading, ssl, smtplib
import urllib.parse, urllib.request, secrets, re
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from email.mime.text import MIMEText
from email.header import Header
from email.utils import formataddr
from pathlib import Path
from datetime import date

BASE = Path('/opt/simjiang-reminder')
DB = BASE / 'data.db'
OLD_KEY_FILE = BASE / 'api_key.txt'
HOST = '0.0.0.0'; PORT = 8787
KEY_RE = re.compile(r'^[A-Za-z0-9_-]{24,80}$')

def clean_key(k):
    return ''.join(str(k or '').strip().split())

def new_key():
    return secrets.token_urlsafe(24)

def db():
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
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
    conn.commit(); return conn

def ensure_user(api_key, nickname=''):
    api_key = clean_key(api_key)
    if not KEY_RE.match(api_key): return False
    conn = db()
    conn.execute('insert or ignore into user_meta(api_key,nickname,created_at,enabled) values(?,?,?,1)',
                 (api_key, nickname or '', int(time.time())))
    conn.commit(); conn.close(); return True

def user_enabled(api_key):
    api_key = clean_key(api_key)
    if not KEY_RE.match(api_key): return False
    conn = db()
    row = conn.execute('select enabled from user_meta where api_key=?', (api_key,)).fetchone()
    conn.close()
    return bool(row and int(row['enabled']) == 1)

def mask_number(n):
    ds = ''.join(ch for ch in str(n) if ch.isdigit())
    if len(ds) <= 4: return ds or str(n)
    return ds[:3] + '****' + ds[-4:]

def days_left(exp):
    try:
        d = date.fromisoformat(str(exp)[:10])
        return (d - date.today()).days
    except Exception:
        return None

def send_tg(token, chat_id, text):
    if not token or not chat_id: return False, 'Telegram 未配置'
    try:
        url = 'https://api.telegram.org/bot%s/sendMessage' % token
        data = urllib.parse.urlencode({'chat_id': chat_id, 'text': text}).encode()
        with urllib.request.urlopen(url, data=data, timeout=15) as r:
            body = r.read().decode('utf-8', 'ignore')
        return True, body[:160]
    except urllib.error.HTTPError as e:
        err_body = ''
        try: err_body = e.read().decode('utf-8', 'ignore')[:200]
        except: pass
        return False, 'Telegram HTTP %d: %s' % (e.code, err_body or e.reason)
    except Exception as e:
        return False, 'Telegram 发送失败: %s: %s' % (type(e).__name__, str(e))

def send_mail(cfg, subject, body):
    host = cfg.get('smtpHost', ''); port = int(cfg.get('smtpPort') or 465)
    user = cfg.get('smtpUser', ''); pwd = cfg.get('smtpPass', '')
    to = cfg.get('smtpTo', ''); sender = cfg.get('smtpFrom') or user
    if not (host and user and pwd and to): return False, 'SMTP 未配置完整'
    msg = MIMEText(body, 'plain', 'utf-8')
    msg['Subject'] = Header(subject, 'utf-8')
    msg['From'] = formataddr(('SimJiang', sender))
    msg['To'] = to
    try:
        ctx = ssl.create_default_context()
        with smtplib.SMTP_SSL(host, port, context=ctx, timeout=25) as s:
            s.login(user, pwd)
            s.sendmail(sender, [to], msg.as_string())
        return True, 'OK'
    except Exception as e:
        return False, '邮件发送失败: %s: %s' % (type(e).__name__, str(e))

def reminder_text(r, left):
    op = r.get('operator') or r.get('countryName') or 'SIM'
    num = mask_number(r.get('number', ''))
    exp = r.get('expireDate', '')
    return f"⏰ SimJiang 到期提醒\n{r.get('flag','')} {op} {r.get('countryCode','')} {num}\n到期日期：{exp}\n剩余天数：{left} 天"

def check_once(only_key=None):
    conn = db(); today = date.today().isoformat()
    if only_key:
        rows = conn.execute('select u.* from users u join user_meta m on u.api_key=m.api_key where u.api_key=? and m.enabled=1',
                            (clean_key(only_key),)).fetchall()
    else:
        rows = conn.execute('select u.* from users u join user_meta m on u.api_key=m.api_key where m.enabled=1').fetchall()
    stats = {'users': len(rows), 'tg': 0, 'mail': 0, 'due': 0}
    for row in rows:
        api = row['api_key']; payload = json.loads(row['payload'])
        settings = payload.get('settings') or {}; records = payload.get('records') or []
        remind = int(settings.get('remindDays') or 7)
        cloud_tg = bool(settings.get('cloudTelegramEnabled', True))
        cloud_mail = bool(settings.get('cloudEmailEnabled', True))
        for r in records:
            rid = str(r.get('id') or r.get('number') or '')
            left = days_left(str(r.get('expireDate', '')))
            if left is None or left < 0 or left > remind: continue
            stats['due'] += 1
            text = reminder_text(r, left)
            subject = 'SimJiang 到期提醒：' + mask_number(r.get('number', ''))
            if cloud_tg and settings.get('tgEnabled') and settings.get('botToken') and settings.get('chatId'):
                if not conn.execute('select 1 from sent_log where api_key=? and record_id=? and channel=? and day=?',
                                    (api, rid, 'tg', today)).fetchone():
                    try:
                        send_tg(settings.get('botToken'), settings.get('chatId'), text)
                        conn.execute('insert or ignore into sent_log values(?,?,?,?)', (api, rid, 'tg', today))
                        conn.commit(); stats['tg'] += 1
                    except Exception as e: print('tg error', api, e, flush=True)
            if cloud_mail and settings.get('smtpEnabled') and settings.get('smtpTo'):
                if not conn.execute('select 1 from sent_log where api_key=? and record_id=? and channel=? and day=?',
                                    (api, rid, 'mail', today)).fetchone():
                    try:
                        send_mail(settings, subject, text)
                        conn.execute('insert or ignore into sent_log values(?,?,?,?)', (api, rid, 'mail', today))
                        conn.commit(); stats['mail'] += 1
                    except Exception as e: print('mail error', api, e, flush=True)
    conn.close(); return stats

def loop():
    while True:
        try: print('check', check_once(), flush=True)
        except Exception as e: print('check error', e, flush=True)
        time.sleep(1800)

class H(BaseHTTPRequestHandler):
    def _json(self, code, obj):
        data = json.dumps(obj, ensure_ascii=False).encode()
        self.send_response(code)
        self.send_header('Content-Type', 'application/json; charset=utf-8')
        self.send_header('Content-Length', str(len(data)))
        self.end_headers(); self.wfile.write(data)
    def _read_json(self):
        n = int(self.headers.get('Content-Length', '0') or 0)
        body = self.rfile.read(n).decode('utf-8', 'ignore')
        try: return json.loads(body or '{}')
        except Exception: return None
    def _auth_key(self):
        k = clean_key(self.headers.get('X-API-Key', ''))
        return k if user_enabled(k) else ''
    def do_GET(self):
        if self.path.startswith('/api/status'):
            conn = db()
            users = conn.execute('select count(*) c from user_meta where enabled=1').fetchone()['c']
            conn.close()
            return self._json(200, {'ok': True, 'service': 'simjiang-reminder',
                                    'version': 'v2-multi-user', 'users': users, 'time': int(time.time())})
        return self._json(404, {'ok': False, 'error': 'not found'})
    def do_POST(self):
        if self.path.startswith('/api/register'):
            payload = self._read_json()
            if payload is None: return self._json(400, {'ok': False, 'error': 'bad json'})
            k = new_key(); nickname = str(payload.get('nickname') or '').strip()[:50]
            ensure_user(k, nickname)
            return self._json(200, {'ok': True, 'apiKey': k, 'message': '已生成独立 API Key'})
        api_key = self._auth_key()
        if not api_key: return self._json(401, {'ok': False, 'error': 'bad api key'})
        payload = self._read_json()
        if payload is None: return self._json(400, {'ok': False, 'error': 'bad json'})
        if self.path.startswith('/api/sync'):
            conn = db()
            conn.execute('insert or replace into users values(?,?,?)',
                         (api_key, json.dumps(payload, ensure_ascii=False), int(time.time())))
            conn.commit(); conn.close()
            return self._json(200, {'ok': True, 'records': len(payload.get('records') or []),
                                    'message': '同步成功', 'apiKeyTail': api_key[-6:]})
        if self.path.startswith('/api/test-telegram'):
            s = payload.get('settings') or payload
            ok, msg = send_tg(s.get('botToken'), s.get('chatId'),
                              '✅ SimJiang 云端 Telegram 测试成功\nKey: ****' + api_key[-6:])
            return self._json(200, {'ok': ok, 'message': msg})
        if self.path.startswith('/api/test-email'):
            s = payload.get('settings') or payload
            ok, msg = send_mail(s, 'SimJiang 云端邮件测试',
                                '✅ SimJiang 云端邮件测试成功。\nKey: ****' + api_key[-6:])
            return self._json(200, {'ok': ok, 'message': msg})
        if self.path.startswith('/api/check-now'):
            stats = check_once(api_key)
            return self._json(200, {'ok': True, 'message': '已触发当前 Key 检查', 'stats': stats})
        return self._json(404, {'ok': False, 'error': 'not found'})
    def log_message(self, fmt, *args):
        print('%s - %s' % (self.address_string(), fmt % args), flush=True)

if __name__ == '__main__':
    BASE.mkdir(parents=True, exist_ok=True); db().close()
    threading.Thread(target=loop, daemon=True).start()
    print(f'SimJiang reminder v2 multi-user listening on {HOST}:{PORT}', flush=True)
    ThreadingHTTPServer((HOST, PORT), H).serve_forever()
```

### 3.3 创建备份脚本

创建 `backup.sh`：

```bash
#!/bin/bash
# simjiang-reminder 数据库自动备份
BACKUP_DIR=/opt/simjiang-reminder/backups
DB=/opt/simjiang-reminder/data.db
mkdir -p "$BACKUP_DIR"

if [ -f "$DB" ]; then
    cp "$DB" "$BACKUP_DIR/data.db.$(date +%Y%m%d_%H%M%S)"
    # 保留最近 30 份备份
    ls -1t "$BACKUP_DIR"/data.db.* 2>/dev/null | tail -n +31 | xargs rm -f 2>/dev/null
fi

# 数据库丢失时自动从最新备份恢复
if [ ! -f "$DB" ]; then
    LATEST=$(ls -1t "$BACKUP_DIR"/data.db.* 2>/dev/null | head -1)
    if [ -n "$LATEST" ]; then
        cp "$LATEST" "$DB"
        echo "$(date) Restored DB from $LATEST" >> /opt/simjiang-reminder/restore.log
    fi
fi
```

设置权限：

```bash
chmod +x /opt/simjiang-reminder/backup.sh
```

### 3.4 创建 systemd 服务

创建 `/etc/systemd/system/simjiang-reminder.service`：

```ini
[Unit]
Description=SimJiang Cloud Reminder Service
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
WorkingDirectory=/opt/simjiang-reminder
ExecStartPre=/opt/simjiang-reminder/backup.sh
ExecStart=/usr/bin/python3 /opt/simjiang-reminder/server.py
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
```

### 3.5 启动服务

```bash
# 重载 systemd
systemctl daemon-reload

# 启动服务
systemctl start simjiang-reminder

# 设置开机自启
systemctl enable simjiang-reminder

# 查看状态
systemctl status simjiang-reminder
```

---

## 四、验证安装

```bash
# 检查端口是否监听
ss -tlnp | grep 8787

# 测试服务状态
curl http://localhost:8787/api/status

# 期望返回：
# {"ok":true,"service":"simjiang-reminder","version":"v2-multi-user","users":0,"time":...}
```

---

## 五、API 接口文档

### 5.1 服务状态（无需认证）

```
GET /api/status
```

### 5.2 注册用户（获取 API Key）

```
POST /api/register
Content-Type: application/json

{"nickname": "我的昵称"}
```

返回：`{"ok":true, "apiKey": "xxx...xxx"}`

每个用户独立 API Key，数据互相隔离。

### 5.3 同步数据

```
POST /api/sync
X-API-Key: <你的API Key>
Content-Type: application/json

{
  "settings": {
    "remindDays": 7,
    "cloudTelegramEnabled": true,
    "tgEnabled": true,
    "botToken": "123456:ABC-xxx",
    "chatId": "123456789",
    "cloudEmailEnabled": false
  },
  "records": [
    {
      "id": "record-uuid",
      "number": "+8613800138000",
      "countryCode": "CN",
      "flag": "🇨🇳",
      "operator": "中国移动",
      "expireDate": "2026-07-01"
    }
  ]
}
```

### 5.4 测试 Telegram

```
POST /api/test-telegram
X-API-Key: <你的API Key>
Content-Type: application/json

{"botToken": "xxx", "chatId": "xxx"}
```

### 5.5 测试邮件

```
POST /api/test-email
X-API-Key: <你的API Key>
Content-Type: application/json

{
  "smtpHost": "smtp.qq.com",
  "smtpPort": 465,
  "smtpUser": "you@qq.com",
  "smtpPass": "授权码",
  "smtpTo": "target@email.com"
}
```

### 5.6 手动触发检查

```
POST /api/check-now
X-API-Key: <你的API Key>
```

---

## 六、反向代理（可选）

如需通过域名 + HTTPS 访问，可用 Nginx 反代：

```nginx
server {
    listen 443 ssl;
    server_name reminder.yourdomain.com;

    ssl_certificate     /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://127.0.0.1:8787;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

也可以用 Lucky、Caddy 等自动 HTTPS 工具。

---

## 七、目录结构

```
/opt/simjiang-reminder/
├── server.py          # 主程序（唯一代码文件）
├── backup.sh          # 备份脚本
├── data.db            # SQLite 数据库（WAL 模式）
└── backups/           # 备份目录
    ├── data.db.20260622_000001
    ├── data.db.20260622_060001
    └── ...            # 保留最近 30 份
```

---

## 八、运维命令

```bash
# 查看实时日志
journalctl -u simjiang-reminder -f

# 重启服务
systemctl restart simjiang-reminder

# 停止服务
systemctl stop simjiang-reminder

# 手动备份
bash /opt/simjiang-reminder/backup.sh

# 查看数据库用户数
sqlite3 /opt/simjiang-reminder/data.db "SELECT count(*) FROM user_meta WHERE enabled=1"

# 查看所有用户
sqlite3 /opt/simjiang-reminder/data.db "SELECT api_key, nickname, created_at, enabled FROM user_meta"

# 禁用某用户
sqlite3 /opt/simjiang-reminder/data.db "UPDATE user_meta SET enabled=0 WHERE api_key='xxx'"
```

---

## 九、App 端对接

在 simJ App 的「云端提醒」设置页中填写：

- **云端地址**：`http://你的IP:8787` 或 `https://你的域名`
- **API Key**：通过 `/api/register` 获取

App 会自动将号码数据和提醒设置同步到云端，服务端每 30 分钟自动检查一次到期号码并发送提醒。

---

## 十、安全建议

1. **目录权限**设为 700，仅 root 可读写
2. 生产环境建议加 **反向代理 + HTTPS**
3. 如果不需要公网直接访问，防火墙只开放 Nginx 端口，8787 仅监听 127.0.0.1
4. API Key 长度 24-80 位，使用 `secrets.token_urlsafe(24)` 生成，安全性足够
5. 数据库定期备份到异地（可配合 rclone/rsync）

---

## 十一、故障排查

| 问题 | 排查方法 |
|------|----------|
| 服务启动失败 | `journalctl -u simjiang-reminder -n 50` 查看错误 |
| 端口未监听 | `ss -tlnp \| grep 8787` 确认进程 |
| Telegram 发送失败 | 先用 `/api/test-telegram` 测试，检查 Bot Token 和 Chat ID |
| 邮件发送失败 | 先用 `/api/test-email` 测试，检查 SMTP 配置 |
| 数据库损坏 | 停止服务 → 删除 data.db → 重启（自动从备份恢复） |
| 提醒未触发 | 确认号码的 `expireDate` 格式为 `YYYY-MM-DD`，且剩余天数 ≤ `remindDays` |
