# 本地真实后端联调清单

## 当前边界

本轮不做 UAT/test/prod 覆盖；在没有真实 UAT 地址和登录凭据前，只做本地后端联调准备与手动验证。

2026-06-10 当前实测结果：

- `npm run verify:acceptance` 通过。
- `npm run verify:local-readiness` 通过，`/captchaImage` 返回 200。
- `npm run verify:local-readiness -- --probe-auth` 未通过，`/app/worker/auth/send-sms-code` 返回业务 `code=500`，提示短信网关未启用。
- `13700010001 / admin123` 已可通过 `/app/worker/auth/login` 完成本地 worker 密码登录。
- `npm run verify:local-smoke` 通过，34/34 个只读接口通过，覆盖资料、首页、工作台、考勤、上传记录、通知、活动、视频、工会等读链路。
- `npm run verify:local-write` 通过，13 项写链路通过，0 失败；`pushGatewayTest` 因后端推送网关未启用标记为 `BLOCKED`。
- `npm run verify:device-capabilities` 通过，静态确认定位权限降级、拍照/上传、离线补传、推送监听和验收导出能力仍在代码中。
- `npm run build:h5:test`、`npm run build:app:uat`、`npm run build:mp-weixin:prod` 均通过；`build:h5:test` 仅输出 Sass legacy JS API deprecation warning。
- 真机权限授权、真实拍照、真实 GPS 坐标、离线补传恢复过程、真实推送送达仍需要真机证据；终端脚本不能宣称这些已经完成。

本地默认接口地址来自 `.env.example`：

```env
VITE_WORKER_API_BASE_URL=http://127.0.0.1:8080
```

真机访问时不能使用 `127.0.0.1`，需要改为电脑局域网 IP，例如 `http://192.168.x.x:8080`。

## 已知本地依赖

- 后端端口：`8080`
- 后端 context path：`/`
- MySQL：`localhost:3306/yuegongbao`
- MySQL 账号：`root/root`
- Redis：`localhost:6379`
- 集成模式：`ygb.integration.mode=stub`
- 候选劳动者：`13700010001 / 赵志成`

## 执行步骤

1. 启动 MySQL 与 Redis。
2. 导入基础 SQL 与 worker 业务 seed，确认 `tmp/sql-runner/seed-worker-data.js` 已执行成功。
3. 启动 Java 后端，确认 `http://127.0.0.1:8080/captchaImage` 返回 200。当前可用启动方式：

```powershell
java -jar ..\yuegongbao-admin\target\yuegongbao-admin.jar
```

4. 在 `yuegongbao-worker-uniapp` 执行：

```powershell
npm run verify:local-readiness
npm run verify:acceptance
npm run verify:local-smoke
npm run verify:local-write
npm run verify:device-capabilities
```

5. 如需检查 worker 账号绑定，执行：

```powershell
npm run verify:local-readiness -- --probe-auth
```

该命令会同时校验 HTTP 状态与 AjaxResult 业务 `code`。如果返回 `code=500` 且提示短信网关未启用，说明账号查询链路已到达短信发送阶段，但短信登录仍不可完成。

6. 本机 H5 可直接运行：

```powershell
npm run dev:h5
```

7. 真机 App 联调前，将接口地址改为电脑局域网 IP 后再构建或运行。

## 登录限制

当前 `WorkerAuthController` 的短信验证码为随机生成并写入 Redis，没有固定验证码策略；自动化脚本不能完成短信登录。

密码登录要求本地库存在 `13700010001` 对应的 `sys_user`，且有已知密码。当前本地实测 `13700010001 / admin123` 可登录；如果其他机器或重置库后不可登录，需要先检查 `sys_user` 和 `ygb_person` 绑定数据。

## 手动验证模块

- 登录：账号密码或短信登录成功；401 后只清理当前本地环境登录态。
- 资料：首页、个人资料、资料导出快照显示 `13700010001 / 赵志成` 相关信息。
- 首页/工作台：聚合数据、入口跳转、空态/异常态不崩溃。
- 考勤：定位授权成功、拒绝降级、打卡接口返回、离线入队与恢复补传。
- 上传：拍照/相册选择、`/common/upload` 成功、上传记录回查、401 上传失效清登录。
- 通知/活动/工会：列表、详情、已读/参与/跳转结构返回符合页面预期。
- 推送：本轮只验证本地接口环境和已有 push 配置，真实推送送达后续专项处理。

## 自动化写链路

2026-06-10 本地实测：`13 passed, 1 blocked, 0 failed`。阻塞项为 `pushGatewayTest`，后端返回“推送网关未启用，请先配置 ygb.worker.push.gateway.enabled=true”，不计为写链路失败。

`npm run verify:local-write` 会使用 `13700010001 / admin123` 登录本地后端，并执行以下写操作：

- `/common/upload` 上传 1x1 PNG 测试图。
- `/app/worker/upload-record/create` 写入上传记录。
- `/app/worker/settings/save` 保存通知设置。
- `/app/worker/settings/push-register` 写入本地 push client 绑定。
- `/app/worker/settings/push-test` 探测推送网关。
- `/app/worker/complaint/create` 创建投诉并带入附件。
- `/app/worker/legal-consult/create` 创建法律咨询并带入附件。
- `/app/worker/activity/join` 创建活动参与记录。
- `/app/worker/video/progress` 保存视频进度。
- `/app/worker/attendance/check-in` 与 `/app/worker/attendance/check-out` 写入考勤。
- `/app/worker/notice/read` 写入通知已读。

如果推送网关未启用，`pushGatewayTest` 会标记为 `BLOCKED`，这代表服务端网关配置阻塞真实送达，不代表前端登记与后端测试入口缺失。

`npm run verify:device-capabilities` 是静态门禁，确认真机权限、拍照/定位/上传、离线补传、推送监听与验收导出能力仍在代码中。它不能替代真机权限弹窗、拍照、定位坐标、断网恢复补传和推送送达截图/录屏证据。

## 留痕

每完成一个模块，在设置页复制：

- 真机联调总览
- 接口逐项联调清单
- 全量联调导出

如果 `verify:local-readiness` 失败，优先按输出处理后端未启动、端口不可达、接口地址错误、MySQL/Redis 未就绪或 worker 登录账号缺失。
