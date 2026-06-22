# simJ

simJ — SIM 卡 & eSIM 全能管家。

一款专为多卡用户打造的号码保号 + eSIM 管理 Android 工具。

## 功能

- 📱 **号码保号管理** — 130+ 国家号码录入，智能到期提醒，批量管理
- 🌐 **eSIM 管理** — 内置 eSIM (OMAPI) + USB 实体卡双通道，扫码/相册/手动下载 Profile
- ☁️ **云端提醒** — Telegram Bot / SMTP 邮件 / 云端 API 同步
- 🛠️ **实用工具** — 刷流量测试、拨号测试、JSON/CSV 导入导出
- 🌙 **深色模式** — 全局适配
- 🌍 **多语言** — 简体中文、繁体中文、English、日本語、阿拉伯语 (RTL)
- 🔒 **本地存储** — 数据不上传，隐私安全
- 🚫 **零广告** — 纯工具，无打扰

## 构建

```bash
gradle assembleDebug --no-daemon --max-workers=1
```

## 下载

[Latest Release](https://github.com/yanglh1/SimJ/releases)

---

## 云端提醒服务 — 自建教程

simJ 支持用户自建云端提醒服务。服务端仅需 Python 3.8+，**零外部依赖**。

### 服务器要求

| 项目 | 要求 |
|------|------|
| 系统 | Linux（Debian/Ubuntu/CentOS 均可） |
| Python | 3.8+ |
| 端口 | 8787（可自定义） |
| 内存 | 128MB+ |

### 1. 创建目录

```bash
mkdir -p /opt/simjiang-reminder
chmod 700 /opt/simjiang-reminder
```

### 2. 部署代码

将仓库中的 `server.py` 和 `backup.sh` 复制到服务器：

```bash
cp server.py backup.sh /opt/simjiang-reminder/
chmod +x /opt/simjiang-reminder/backup.sh
```

### 3. 创建 systemd 服务

```bash
cat > /etc/systemd/system/simjiang-reminder.service << 'EOF'
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
EOF

systemctl daemon-reload
systemctl enable --now simjiang-reminder
```

### 4. 验证

```bash
# 检查状态
systemctl status simjiang-reminder

# 测试接口
curl http://localhost:8787/api/status
# 期望返回: {"ok":true,"service":"simjiang-reminder",...}
```

### 5. App 端配置

在 simJ App 设置 → 云端提醒中：

- **服务地址**：填入你的服务器地址，如 `http://你的IP:8787`
- **API Key**：点击「生成我的 Key」获取

支持 Telegram Bot 和 SMTP 邮件两种提醒方式，按需配置即可。

### API 接口

| 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|
| `/api/status` | GET | 无 | 服务状态 |
| `/api/register` | POST | 无 | 注册用户，返回 API Key |
| `/api/sync` | POST | X-API-Key | 同步号码数据 |
| `/api/test-telegram` | POST | X-API-Key | 测试 Telegram |
| `/api/test-email` | POST | X-API-Key | 测试邮件 |
| `/api/check-now` | POST | X-API-Key | 立即检查到期 |

### 反向代理（可选）

如需 HTTPS，用 Nginx 反代：

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
    }
}
```

### 运维

```bash
# 查看日志
journalctl -u simjiang-reminder -f

# 手动备份
bash /opt/simjiang-reminder/backup.sh

# 重启
systemctl restart simjiang-reminder
```

> 详细文档见 [simjiang-reminder-deploy-guide.md](simjiang-reminder-deploy-guide.md)
