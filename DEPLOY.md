# Docker Compose 服务器部署

Compose 从 GitHub Container Registry（GHCR）拉取已经由 GitHub Actions 构建的前端、后端和数据库镜像，并使用 Caddy 为 `rachel.4inlove.top` 自动申请、续期 HTTPS 证书。服务器只需要 Docker Engine、Docker Compose 插件和用于首次下载部署文件的 `curl`，不需要克隆代码仓库。

## 域名准备

1. 在域名 DNS 控制台添加 `A` 记录：主机记录 `rachel`，记录值为服务器公网 IPv4。
2. 只有服务器正确配置了公网 IPv6 时才添加 `AAAA` 记录；错误的 `AAAA` 会导致部分客户端无法访问。
3. 在云安全组和服务器防火墙中开放 TCP `80`、TCP `443`；UDP `443` 可选，用于 HTTP/3。
4. 确认服务器上没有其他程序占用 `80/443`。

Caddy 需要公网能够访问 `80/443` 才能完成证书签发。域名使用标准 HTTPS 端口，访问地址写作 `https://rachel.4inlove.top`，无需显式添加 `:443`。

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

确认 `.env` 中 `APP_DOMAIN=rachel.4inlove.top`，并替换 `MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD` 和 `JWT_SECRET`。使用微信登录时还需设置 `WECHAT_APPID` 与 `WECHAT_SECRET`。正常部署保持三个 `IMAGE_TAG` 为 `latest`；需要回滚时可改为 GitHub Actions 推送的提交 SHA 标签。

如果 GHCR 中的镜像不是公开包，先使用具有 `read:packages` 权限的 GitHub Token 登录：

```bash
echo "$GHCR_TOKEN" | docker login ghcr.io -u hyc131456 --password-stdin
```

```bash
docker compose config
docker compose pull
docker compose up -d --remove-orphans
docker compose ps
curl https://rachel.4inlove.top/api/health
```

访问地址为 `https://rachel.4inlove.top`。首次签发证书可能需要几十秒，可通过 `docker compose logs -f caddy` 查看进度。

## 手动更新部署

```bash
cd /opt/love-app
docker compose pull backend frontend
docker compose up -d --no-deps backend frontend
docker compose ps
```

日常更新只拉取镜像，不需要 `git pull`。仅当 Compose 结构或环境变量模板本身发生变化时，才需要重新下载对应文件。

## 日常运维

```bash
# 查看状态
docker compose ps

# 查看日志
docker compose logs -f caddy backend frontend database

# 停止服务并保留数据
docker compose down
```

MySQL 数据保存在 `love-app_mysql-data` 卷，上传文件保存在 `love-app_upload-data` 卷，HTTPS 证书保存在 `love-app_caddy-data` 卷。初始化 SQL 仅在 MySQL 数据卷首次创建时运行。`docker compose down -v` 会永久删除数据库、上传文件和本机证书数据，不要在正常更新时使用。
