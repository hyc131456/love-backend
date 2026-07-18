# Docker Compose 服务器部署

Compose 文件位于后端仓库中，从 GitHub Container Registry（GHCR）拉取已经由 GitHub Actions 构建的前后端镜像。服务器需要安装 Docker Engine、Docker Compose 插件和 Git，并能访问 Docker Hub 与 `ghcr.io`。

## 自动发布流程

- 后端 `master` 分支有新提交时，`.github/workflows/build-and-deploy.yml` 构建后端镜像并推送到 GHCR，然后统一部署。
- 前端 `main` 分支有新提交时，`.github/workflows/build-image.yml` 构建前端镜像并推送到 GHCR，然后通过 `repository_dispatch` 通知后端工作流部署。
- 两个镜像都会推送 `latest` 和当前 Git 提交 SHA 两个标签。

后端仓库需要配置以下 Actions Secrets：

| Secret | 说明 |
| --- | --- |
| `DEPLOY_HOST` | 服务器 IP 或域名 |
| `DEPLOY_PORT` | SSH 端口，可省略，默认 `22` |
| `DEPLOY_USER` | SSH 用户，需有执行 Docker 的权限 |
| `DEPLOY_PATH` | 后端仓库路径，可省略，默认 `/opt/love-app/love-backend` |
| `DEPLOY_SSH_KEY` | GitHub Actions 使用的 SSH 私钥全文 |
| `DEPLOY_KNOWN_HOSTS` | `ssh-keyscan -H -p 22 服务器IP` 的完整输出 |

前端仓库需要配置 `BACKEND_DISPATCH_TOKEN`。它应是只授权 `love-backend` 仓库的 Fine-grained PAT，并具有 `Contents: Read and write` 权限，用于调用 `repository_dispatch`。

建议为 Actions 创建专用 SSH 密钥：

```bash
ssh-keygen -t ed25519 -C github-actions-love-app -f love_app_deploy
ssh-copy-id -i love_app_deploy.pub deploy@服务器IP
```

将 `love_app_deploy` 私钥内容保存为 `DEPLOY_SSH_KEY`，不要提交到仓库。

首次启用时建议按以下顺序操作：

1. 完成下面的服务器初始化并创建 `.env`。
2. 在前端仓库手动运行一次 `Build frontend image`，生成前端镜像。
3. 在后端仓库手动运行一次 `Build backend and deploy`，生成后端镜像并部署。

SSH Secrets 未配置时，后端工作流仍会构建并推送镜像，但会安全跳过部署。`BACKEND_DISPATCH_TOKEN` 未配置时，前端工作流仍会推送镜像，但不会通知后端部署。

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

## 更新部署

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
