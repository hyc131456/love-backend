# Docker Compose 服务器部署

Compose 文件位于后端仓库中，并从相邻的 `love-frontend` 目录构建 H5 前端。服务器需要安装 Docker Engine、Docker Compose 插件和 Git，并能访问 Docker Hub、Maven Central 与 npm registry。

## 首次部署

```bash
sudo mkdir -p /opt/love-app
sudo chown -R "$USER":"$USER" /opt/love-app
cd /opt/love-app

git clone https://github.com/hyc131456/love-backend.git
git clone https://github.com/hyc131456/love-frontend.git

cd love-backend
cp .env.example .env
nano .env
```

必须替换 `.env` 中的 `MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD` 和 `JWT_SECRET`。使用微信登录时还需设置 `WECHAT_APPID` 与 `WECHAT_SECRET`。

```bash
docker compose config
docker compose up -d --build
docker compose ps
curl http://127.0.0.1:8080/api/health
```

默认访问地址为 `http://服务器IP:8080`。确保云平台安全组和服务器防火墙允许 `.env` 中 `APP_PORT` 对应的 TCP 端口。

## 更新部署

```bash
cd /opt/love-app/love-backend
git pull --ff-only

cd /opt/love-app/love-frontend
git pull --ff-only

cd /opt/love-app/love-backend
docker compose up -d --build
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
