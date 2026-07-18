# Docker Compose 服务器部署

Compose 文件位于后端仓库中，从 GitHub Container Registry（GHCR）拉取已经由 GitHub Actions 构建的前后端镜像。服务器需要安装 Docker Engine、Docker Compose 插件和 Git，并能访问 Docker Hub 与 `ghcr.io`。

## 镜像发布流程

- 后端 `master` 分支有新提交时，`.github/workflows/build-image.yml` 构建后端镜像并推送到 GHCR。
- 前端 `main` 分支有新提交时，`.github/workflows/build-image.yml` 构建前端镜像并推送到 GHCR。
- 两个镜像都会推送 `latest` 和当前 Git 提交 SHA 两个标签。
- Actions 只负责构建镜像，不连接服务器，也不会自动启动或更新服务。

工作流使用 GitHub 自动提供的 `GITHUB_TOKEN` 写入 GHCR，不需要配置 SSH Secrets 或跨仓库 Token。也可以在两个仓库的 Actions 页面通过 `Run workflow` 手动触发镜像构建。

## 首次部署

```bash
sudo mkdir -p /opt/love-app
sudo chown -R "$USER":"$USER" /opt/love-app
cd /opt/love-app

git clone https://github.com/hyc131456/love-backend.git

cd love-backend
cp .env.example .env
nano .env
```

必须替换 `.env` 中的 `MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD` 和 `JWT_SECRET`。使用微信登录时还需设置 `WECHAT_APPID` 与 `WECHAT_SECRET`。正常部署保持两个 `IMAGE_TAG` 为 `latest`；需要回滚时可改为 GitHub Actions 推送的提交 SHA 标签。

如果 GHCR 中的镜像不是公开包，先使用具有 `read:packages` 权限的 GitHub Token 登录：

```bash
echo "$GHCR_TOKEN" | docker login ghcr.io -u hyc131456 --password-stdin
```

```bash
docker compose config
docker compose pull
docker compose up -d --remove-orphans
docker compose ps
curl http://127.0.0.1:8080/api/health
```

默认访问地址为 `http://服务器IP:8080`。确保云平台安全组和服务器防火墙允许 `.env` 中 `APP_PORT` 对应的 TCP 端口。

## 手动更新部署

```bash
cd /opt/love-app/love-backend
git pull --ff-only
docker compose pull
docker compose up -d --remove-orphans
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
