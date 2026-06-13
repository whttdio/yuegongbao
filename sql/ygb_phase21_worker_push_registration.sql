alter table ygb_worker_setting
  add column push_client_id varchar(128) default '' after notify_enabled,
  add column notification_permission varchar(32) default '' after push_client_id,
  add column push_platform varchar(32) default '' after notification_permission;

-- 推送网关参数示例
-- ygb.worker.push.gateway.enabled=true
-- ygb.worker.push.gateway.url=http://your-push-gateway/api/push/send
-- ygb.worker.push.gateway.provider=custom
-- ygb.worker.push.gateway.authToken=

delete from sys_config
where config_key in (
  'ygb.worker.push.gateway.enabled',
  'ygb.worker.push.gateway.url',
  'ygb.worker.push.gateway.provider',
  'ygb.worker.push.gateway.authToken'
);

insert into sys_config (
  config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark
) values
(
  '劳动者端推送网关开关',
  'ygb.worker.push.gateway.enabled',
  'false',
  'Y',
  'admin',
  sysdate(),
  '',
  null,
  'true=启用服务端推送发送链，false=关闭。未启用时 /app/worker/settings/push-test 会直接报未启用。'
),
(
  '劳动者端推送网关地址',
  'ygb.worker.push.gateway.url',
  '',
  'Y',
  'admin',
  sysdate(),
  '',
  null,
  '服务端推送网关 POST 地址，例如 http://push-gateway.local/api/push/send'
),
(
  '劳动者端推送网关提供方',
  'ygb.worker.push.gateway.provider',
  'custom',
  'Y',
  'admin',
  sysdate(),
  '',
  null,
  '用于标记当前网关提供方，默认 custom，可按实际改成 unipush、getui 等'
),
(
  '劳动者端推送网关鉴权令牌',
  'ygb.worker.push.gateway.authToken',
  '',
  'Y',
  'admin',
  sysdate(),
  '',
  null,
  '如网关需要 Bearer Token，则在此配置；为空时后端不会附加 Authorization 头。'
);
