# 粤工保联调工程

## 运行基线

- 后端：`JDK 17+`
- Node：`20.20.2`
- Maven：`3.8+`
- Windows 下请使用 `npm.cmd`，不要直接在 PowerShell 中执行 `npm`

本仓库默认联调脚本优先使用：

1. `YGB_JAVA_HOME`
2. `JAVA_HOME`
3. `D:\JAVA\JDK21\jdk-21`

## 交付模块

- `yuegongbao-admin` / `yuegongbao-business`
- `yuegongbao-ui`：`ygb` / `azb`
- `yuegongbao-worker-miniapp`
- `yuegongbao-enterprise-miniapp`
- `yuegongbao-worker-screen-app`
- `yuegongbao-worker-uniapp`

## 本地联调默认值

- `YGB_DB_URL=jdbc:mysql://127.0.0.1:3306/yuegongbao?...`
- `YGB_DB_USERNAME=root`
- `YGB_DB_PASSWORD=root123456`
- `YGB_REDIS_HOST=127.0.0.1`
- `YGB_REDIS_PORT=6379`
- `YGB_REDIS_DATABASE=0`
- `YGB_UPLOAD_DIR=D:\yuegongbao\uploadPath`
- `YGB_SPRING_PROFILE=druid`
- `YGB_INTEGRATION_MODE=stub`

说明：

- 默认配置以本地联调可启动为目标，均支持通过环境变量覆盖
- `stub` 仍保留给非核心外部依赖模块；核心劳动者、企业端、用工屏链路以真实后端接口联调

## 常用命令

后端：

```bat
scripts\test-backend.cmd
scripts\dev-backend.cmd
```

管理端：

```bat
scripts\dev-admin.cmd ygb
scripts\dev-admin.cmd azb
cd yuegongbao-ui && npm.cmd run build:ygb
cd yuegongbao-ui && npm.cmd run build:azb
```

三端与 UniApp：

```bat
scripts\dev-worker-miniapp.cmd
scripts\dev-enterprise-miniapp.cmd
scripts\dev-worker-screen.cmd
scripts\dev-worker-uniapp.cmd
```

```bat
cd yuegongbao-worker-miniapp && npm.cmd run build:h5
cd yuegongbao-enterprise-miniapp && npm.cmd run build:h5
cd yuegongbao-worker-screen-app && npm.cmd run build:h5
cd yuegongbao-worker-uniapp && npm.cmd run build:h5
cd yuegongbao-worker-uniapp && npm.cmd run build:app
cd yuegongbao-worker-uniapp && npm.cmd run build:mp-weixin
```

## 工程结构

- `yuegongbao-admin`：启动入口
- `yuegongbao-framework`：安全、拦截器、通用框架能力
- `yuegongbao-system`：系统管理模块
- `yuegongbao-business`：粤工保业务模块
- `yuegongbao-quartz`：定时任务模块
- `yuegongbao-generator`：代码生成模块
- `yuegongbao-common`：公共组件模块
- `yuegongbao-ui`：管理端双门户
