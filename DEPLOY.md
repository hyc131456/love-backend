# Docker Compose 服务器部署

Compose 从 GitHub Container Registry（GHCR）拉取已经由 GitHub Actions 构建的前端、后端和数据库镜像。前端默认监听宿主机的 `18080` 端口，既可以通过服务器 IP 直接访问，也可以由现有 OpenResty 反向代理。服务器不需要克隆代码仓库。

## 域名准备

1. 在域名 DNS 控制台添加 `A` 记录：主机记录 `rachel`，记录值为服务器公网 IPv4。
2. 只有服务器正确配置了公网 IPv6 时才添加 `AAAA` 记录；错误的 `AAAA` 会导致部分客户端无法访问。
3. 在云安全组和服务器防火墙中开放 TCP `80`、TCP `443`。
4. 在现有 OpenResty 或服务器管理面板中，为 `rachel.4inlove.top` 创建站点并申请 HTTPS 证书。

OpenResty 的反向代理目标设置为 `http://127.0.0.1:18080`。域名使用标准 HTTPS 端口，访问地址写作 `https://rachel.4inlove.top`，无需显式添加 `:443`。

OpenResty 反向代理的核心配置如下，证书路径由现有面板或证书管理方式提供：

```nginx
location / {
    proxy_pass http://127.0.0.1:18080;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

## 镜像发布流程

- 后端 `master` 分支有新提交时，`.github/workflows/build-image.yml` 构建后端与数据库镜像并推送到 GHCR。
- 前端 `main` 分支有新提交时，`.github/workflows/build-image.yml` 构建前端镜像并推送到 GHCR。
- 三个镜像都会推送 `latest` 和对应的 Git 提交 SHA 标签。
- Actions 只负责构建镜像，不连接服务器，也不会自动启动或更新服务。

工作流使用 GitHub 自动提供的 `GITHUB_TOKEN` 写入 GHCR，不需要配置 SSH Secrets 或跨仓库 Token。也可以在两个仓库的 Actions 页面通过 `Run workflow` 手动触发镜像构建。

## 首次部署

```bash
sudo mkdir -p /opt/love-app
sudo chown -R "$USER":"$USER" /opt/love-app
cd /opt/love-app

curl --fail --location \
  --output docker-compose.yml \
  https://raw.githubusercontent.com/hyc131456/love-backend/master/docker-compose.yml
curl --fail --location \
  --output .env.example \
  https://raw.githubusercontent.com/hyc131456/love-backend/master/.env.example

cp .env.example .env
nano .env
```

确认 `.env` 中 `FRONTEND_PORT=18080`、`DATABASE_PORT=13306`，并替换 `MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD` 和 `JWT_SECRET`。使用微信登录时还需设置 `WECHAT_APPID` 与 `WECHAT_SECRET`。正常部署保持三个 `IMAGE_TAG` 为 `latest`；需要回滚时可改为 GitHub Actions 推送的提交 SHA 标签。

如果 GHCR 中的镜像不是公开包，先使用具有 `read:packages` 权限的 GitHub Token 登录：

```bash
echo "$GHCR_TOKEN" | docker login ghcr.io -u hyc131456 --password-stdin
```

```bash
docker compose config
docker compose pull
docker compose up -d --remove-orphans
docker compose ps
curl http://127.0.0.1:18080/api/health
```

本机健康检查通过后，可以直接访问 `http://服务器IP:18080`；完成 OpenResty 域名和证书配置后，也可以访问 `https://rachel.4inlove.top`。

MySQL 映射到宿主机 `13306` 端口，连接参数为服务器 IP、端口 `13306`、数据库 `love_app`、用户 `love_app`。该端口会监听所有网卡，必须使用云安全组或服务器防火墙限制可信来源 IP，不建议向整个公网开放。

## 手动更新部署

```bash
cd /opt/love-app
docker compose pull backend frontend
docker compose up -d --no-deps backend frontend
docker compose ps
```

日常更新只拉取镜像，不需要 `git pull`。仅当 Compose 结构或环境变量模板本身发生变化时，才需要重新下载对应文件。

## 自动更新脚本

服务器首次下载脚本并赋予执行权限：

```bash
cd /opt/love-app
curl --fail --location \
  --output deploy.sh \
  https://raw.githubusercontent.com/hyc131456/love-backend/master/deploy.sh
chmod +x deploy.sh
./deploy.sh
```

脚本只拉取 `backend` 和 `frontend` 镜像，不会拉取代码仓库、更新数据库镜像或删除数据卷。它会串行更新后端和前端，并等待两个服务健康后才退出。同一时间重复执行时，后一个进程会自动退出。

使用 Cron 每 5 分钟检查一次：

```bash
crontab -l 2>/dev/null | grep -v '/opt/love-app/deploy.sh' > /tmp/love-app-cron || true
echo '*/5 * * * * /opt/love-app/deploy.sh >> /var/log/love-app-deploy.log 2>&1' >> /tmp/love-app-cron
crontab /tmp/love-app-cron
rm -f /tmp/love-app-cron
```

查看自动更新日志：

```bash
tail -f /var/log/love-app-deploy.log
```

## 日常运维

```bash
# 查看状态
docker compose ps

# 查看日志
docker compose logs -f backend frontend database

# 停止服务并保留数据
docker compose down
```

MySQL 数据保存在 `love-app_mysql-data` 卷，上传文件保存在 `love-app_upload-data` 卷。初始化 SQL 仅在 MySQL 数据卷首次创建时运行。`docker compose down -v` 会永久删除数据库和上传文件，不要在正常更新时使用。
