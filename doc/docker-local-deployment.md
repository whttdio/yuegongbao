# 本地 Docker 部署

这套仓库可以直接用 `docker compose` 跑本地环境。当前配置只部署应用相关容器，不再部署 MySQL 和 Redis 镜像。

说明：

- 管理端镜像构建使用的是 `npm install --legacy-peer-deps`，因为当前仓库里的 `yuegongbao-ui/package-lock.json` 和 `package.json` 不完全同步，`npm ci` 会失败。
- MySQL 使用你本地或局域网已有数据库，默认连接 `192.168.1.182:3306/yuegongbao`。
- Redis 使用你本地已有服务，默认连接 `host.docker.internal:6379`。

## 默认启动内容

- `backend`：Spring Boot 后端，宿主机端口固定 `18080`
- `admin-ui`：管理端前端，宿主机端口默认 `8081`

可选：

- `worker-h5`：劳动者 H5 端，宿主机端口默认 `8082`
- `worker-seed`：为劳动者 H5 注入演示数据，执行完成后自动退出

## 数据库要求

Docker 不会创建 MySQL 容器，也不会自动建库。启动前请确认本机或局域网 MySQL 已准备好：

- 地址：`192.168.1.182`
- 端口：`3306`
- 数据库：`yuegongbao`
- 默认账号：`yuegongbao / yuegongbao`

如果你的数据库账号不同，启动前在 PowerShell 设置：

```powershell
$env:YGB_DB_HOST="192.168.1.182"
$env:YGB_DB_PORT="3306"
$env:YGB_DB_NAME="yuegongbao"
$env:YGB_DB_USER="你的账号"
$env:YGB_DB_PASSWORD="你的密码"
docker compose up -d --build
```

如果数据库还没有初始化，需要先手动导入：

1. `sql/yuegongbao_20260417.sql`
2. 全部 `sql/ygb_phase*.sql`
3. 需要演示数据时再导入 `sql/ygb_gd*_seed.sql`

## Redis 要求

Docker 不会创建 Redis 容器。启动前请确认本机 Redis 已准备好：

- 地址：`host.docker.internal`
- 端口：`6379`
- 默认库：`0`
- 默认密码：空

如果你的 Redis 端口、库或密码不同，启动前在 PowerShell 设置：

```powershell
$env:YGB_REDIS_HOST="192.168.1.182"
$env:YGB_REDIS_PORT="6379"
$env:YGB_REDIS_DATABASE="0"
$env:YGB_REDIS_PASSWORD="你的密码"
docker compose up -d --build
```

## 首次启动

在仓库根目录执行：

```powershell
docker compose up -d --build
```

首次启动主要耗时在 Maven 打包和前端镜像构建。MySQL 和 Redis 都不在 Docker 里启动。

## 访问地址

- 管理端：`http://localhost:8081`
- 后端接口：`http://localhost:18080`
- Swagger：`http://localhost:8081/prod-api/swagger-ui/index.html`
- Druid：`http://localhost:8081/prod-api/druid/login.html`

默认账号：

- 管理端：`admin / admin123`
- Druid：`ygbadmin / 123456`

## 启动劳动者 H5

如果还要把 `yuegongbao-worker-uniapp` 的 H5 一起跑起来：

```powershell
docker compose --profile worker up -d --build
```

启动后访问：

- 劳动者 H5：`http://localhost:8082`

默认 demo 账号：

- 劳动者：`13700010001 / admin123`

说明：

- `worker-seed` 会连接同一个外部 MySQL，写入演示数据后退出，这是正常现象。
- H5 端通过 `/prod-api` 反向代理访问同一个后端。

## 停止

```powershell
docker compose down
```

如需删除上传目录数据卷：

```powershell
docker compose down -v
```

注意：`docker compose down -v` 不会删除外部 MySQL 和 Redis 的数据。

## 常用自定义

如果端口冲突，可以在 PowerShell 临时改环境变量后再启动：

```powershell
$env:YGB_ADMIN_PORT="18081"
docker compose up -d --build
```

可用变量：

- `YGB_DB_HOST`
- `YGB_DB_PORT`
- `YGB_DB_NAME`
- `YGB_DB_USER`
- `YGB_DB_PASSWORD`
- `YGB_REDIS_HOST`
- `YGB_REDIS_PORT`
- `YGB_REDIS_DATABASE`
- `YGB_REDIS_PASSWORD`
- `YGB_ADMIN_PORT`
- `YGB_WORKER_H5_PORT`
- `YGB_PORTAL_MODE`

## 说明

- 当前 `admin-ui` 默认构建的是 `ygb` 门户模式。
- 如果要改成 `azb`，启动前设置：

```powershell
$env:YGB_PORTAL_MODE="azb"
docker compose up -d --build
```

- 上传目录挂在 Docker volume `ygb_upload_data`，容器重建不会丢。
