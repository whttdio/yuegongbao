# Phase 45 数据隔离验收清单

执行顺序：

1. `sql/ygb_gd_test_seed.sql`
2. `sql/ygb_phase45_data_scope.sql`

## 测试账号

| 账号 | 角色 | enterprise_id | 预期区域 |
|------|------|---------------|----------|
| gzentadmin | 企业管理员 | 1001 | 440106 |
| szentadmin | 企业管理员 | 1002 | 440305 |
| 天河监管账号 | 区县监管 | - | 440106 |
| admin | 全省 | - | 440000 |

## 验收场景

| 场景 | 操作 | 期望 |
|------|------|------|
| 区县隔离 | 天河监管 vs 南山监管登录，查人员列表 | 仅本区 `region_code` 前缀数据 |
| 企业隔离 | gzentadmin vs szentadmin 查人员/合同 | 互不可见对方企业数据 |
| 双主体 OR | 企业 1001 作为 dispatch 或 employer 的合同 | 均可见 |
| 详情越权 | 企业 A 直接请求企业 B 的 contractId/personId | 后端返回业务异常 |
| 参数篡改 | 企业用户请求中带 `enterpriseId=1002` | 列表仍仅 1001 数据 |
| 企业下拉 | 企业用户打开信用评分/预警统计 | 企业筛选锁定且仅一项 |
| 全省账号 | admin 登录 | 不受企业/区县限制 |

## 回归项

- 登录后 `getInfo` 返回 `enterpriseId`、`allowedRegionCodes`
- `selectEnterpriseOptions` 按角色收缩
- 月报/预警统计/驾驶舱无重复错误提示
- 侧边栏菜单正常加载
