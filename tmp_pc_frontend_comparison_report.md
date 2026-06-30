# 粤工保平台 PC 前端实现与功能文档对比分析

> 分析日期：2026/06/27
> 文档来源：`C:\Users\Admin\Desktop\粤工保\粤工保平台PC端及三小程序端菜单模块功能文档.docx`
> 代码来源：`D:\JAVA\YueGongBao-Vue\YueGongBao-Vue\yuegongbao-ui`

---

## 1. 说明：当前的两个 PC 前端实现

在 `yuegongbao-ui` 工程中存在两套构建模式（`package.json` 中的 `scripts`）：

| 构建模式 | 目录入口 | 定位 |
|----------|----------|------|
| `ygb`（粤工保） | `src/views/ygb/**` | 主监管平台 PC 后台，覆盖省/市/县/企业四级监管 |
| `azb`（安责险） | `src/views/azb/**` | 安责险专项治理子平台，聚焦安全生产责任保险业务 |

因此本文档对比的“两个 PC 前端实现”即：
1. **粤工保 PC 后台（YGB）**
2. **安责险 PC 子平台（AZB）**

---

## 2. 总体覆盖情况

| 维度 | YGB 后台 | AZB 子平台 |
|------|----------|------------|
| 文档 23 个 PC 一级模块 | 22 个可见实现（系统管理在 `views/system`） | 14 个完整实现 + 多个通过 wrapper 复用 YGB |
| 二级/三级功能 | 大部分已落地，部分为部分实现 | 聚焦安责险相关模块，其余缺失或复用 YGB |
| 与文档匹配度 | 高（约 95%+ 模块有对应目录） | 中（核心安责险链路完整，其他业务模块缺失） |

---

## 3. 文档 23 个 PC 模块 vs 实际实现对照

| 序号 | 文档一级模块 | YGB 实现目录 | YGB 状态 | AZB 实现目录 | AZB 状态 | 说明 |
|------|--------------|--------------|----------|--------------|----------|------|
| 1 | 驾驶舱 | `cockpit`, `cockpitConfig`, `cockpitTrend` | ✅ 完整 | `cockpit`(24KB), `cockpitConfig`(wrapper), `cockpitTrend`(wrapper) | ✅ 完整 | AZB 有独立的安责险驾驶舱 |
| 2 | 预警中心 | `warning`, `warningRule`, `warningStat` | ✅ 完整 | `warning`(39KB), `warningRule`(41KB), `warningStat`(wrapper) | ✅ 完整 | |
| 3 | AI 监测报告 | `aiReport`, `aiReportConfig`, `aiReportSubscription`, `aiReportTask` | ✅ 完整 | `aiReport`(47KB), `aiReportConfig`(40KB), `aiReportSubscription`(wrapper), `aiReportTask`(wrapper) | ✅ 完整 | |
| 4 | 合同备案 | `contract`, `contractTemplate` | ✅ 完整 | 无 | ❌ 未实现 | AZB 无合同备案 |
| 5 | 用工考勤 | `attendanceMonthly`, `attendanceRaw`, `deviceAttendance` | ✅ 完整 | 仅 `deviceAttendance`(wrapper) | ⚠️ 部分 | AZB 无完整考勤模块 |
| 6 | 工资监管 | `salaryBatch`, `salaryDetail`, `salaryArrears` | ✅ 完整 | 无 | ❌ 未实现 | |
| 7 | 社保监管 | `socialPayment`, `socialBaseCompare`, `socialEnrollment`, `socialSupplement` | ✅ 完整 | 无 | ❌ 未实现 | |
| 8 | 工伤监管 | `injuryEvent`, `injuryPersonMonitor`, `injuryRegionMonitor`, `injuryEmployerMonitor`, `injuryAccidentWarning`, `injuryOccHazardMonitor`, `injuryNewformHazardMonitor`, `preventionProject`, `preventionTraining`, `preventionFund`, `preventionPublicity`, `preventionAi` | ⚠️ 部分 | `injuryEvent`(50KB), `preventionProject`(47KB), `preventionFund`(44KB), `preventionTraining`(wrapper), `preventionAi`(wrapper), `preventionPublicity`(wrapper) | ⚠️ 部分 | 认定辅助、独立统计分析视图待完善；AZB 仅有事件与预防项目/资金 |
| 9 | 专项治理 | `employmentRatio`, `threeNaturePost`, `fakeOutsourcing`, `specialRectification` | ✅ 完整 | 无 | ❌ 未实现 | |
| 10 | 税务监管 | `taxCompare`, `taxInvoice`, `taxFundFlow` | ✅ 完整 | 无 | ❌ 未实现 | |
| 11 | 安责险管理 | `aqInsurance`, `aqInsuranceClaim`, `preventionFund` | ✅ 完整 | `aqInsurance`(39KB), `aqInsuranceClaim`(wrapper) | ✅ 完整 | AZB 核心模块 |
| 12 | 设备管理 | `device`, `deviceAi`, `deviceAttendance`, `deviceChip`, `deviceChipInventory`, `deviceGeofence`, `deviceIotCard`, `deviceUninstallAlert` | ✅ 完整 | `device`(54KB) + 多个 device 子模块 wrapper | ✅ 完整 | AZB 主入口完整，子类型复用 YGB |
| 13 | 扩面减损 | `uninsuredList`, `expansionSubsidy`, `preventionTraining` | ⚠️ 部分 | `uninsuredList`(39KB) | ⚠️ 部分 | 催缴跟踪、培训效果评估待完善；AZB 仅漏保清单 |
| 14 | 信用评价 | `creditScore`, `creditRule`, `creditSanction`, `creditRepair` | ✅ 完整 | `creditScore`(40KB) | ✅ 完整 | AZB 仅信用评分，规则/惩戒/修复复用或缺失 |
| 15 | 统计报表 | `statReport` + 多个子报表目录 | ✅ 完整 | `statReport`(38KB) + 10 个子报表 | ✅ 完整 | |
| 16 | 人员管理 | `person`, `personBlacklist`, `personCertificate`, `personTraining`, `personHighRiskPost`, `personRiskPost`, `personExpert` | ✅ 完整 | `person`(51KB) | ✅ 完整 | AZB 仅人员台账，证书/黑名单/岗位库等复用或缺失 |
| 17 | 单位管理 | `enterprise`, `enterpriseRegulator`, `enterpriseDispatch`, `enterpriseEmployer`, `enterpriseHighRisk`, `enterpriseRelation`, `enterpriseUnion`, `enterprisePortal` | ✅ 完整 | `enterprise`(50KB) | ✅ 完整 | AZB 仅企业主数据，其他主体类型复用或缺失 |
| 18 | 系统管理 | `src/views/system/`（config/dept/dict/menu/notice/post/role/user） | ✅ 完整（框架标准实现） | 无独立目录 | ❌ 未实现 | YGB 使用 RuoYi 标准 `views/system`；AZB 未单独实现 |
| 19 | 运营后台 | `operation/*`, `workerJob`, `workerComplaint`, `workerLegalConsult`, `workerFeedback`, `workerUploadRecord`, `workerActivity`, `portalContent`, `platform` | ⚠️ 部分 | `operation/*`(多为 wrapper/占位) | ⚠️ 部分 | 运营数据总览、企业入驻审核、广告轮播、部分设备安装运维待完善 |
| 20 | 企业后台 | `enterprisePortal/overview` | ⚠️ 部分 | 无 | ❌ 未实现 | 仅企业门户入口，企业级人员/设备/工资/作业/保险/培训/招聘/财务/信用视图待完善 |
| 21 | 便民服务 | `citizenService`, `workerJob`, `unionLegalAid` | ⚠️ 部分 | 无 | ❌ 未实现 | 暖新地图、培训课程、法规库、互助圈待完善 |
| 22 | 新业态监管 | `newformWorker`, `newformPlatform`, `newformInjuryMonitor`, `newformTraining` | ✅ 完整 | 无 | ❌ 未实现 | |
| 23 | 职业病监管 | `occupationMonitor`, `occupationPrevention`, `occupationHealthArchive` | ✅ 完整 | 无 | ❌ 未实现 | |

### 状态说明
- ✅ 完整：二级模块与核心功能基本都有对应目录/页面
- ⚠️ 部分：主流程已落地，部分二级模块或三级功能缺失/待完善
- ❌ 未实现：无对应实现或仅有空目录/wrapper

---

## 4. YGB 后台实现亮点与缺口

### 已实现较完整的模块（14 个）
1. 驾驶舱
2. 预警中心
3. AI 监测报告
4. 合同备案
5. 用工考勤
6. 工资监管
7. 社保监管
9. 专项治理
10. 税务监管
11. 安责险管理
12. 设备管理
14. 信用评价
15. 统计报表
16. 人员管理
17. 单位管理
18. 系统管理
22. 新业态监管
23. 职业病监管

### 部分实现的模块（5 个）
| 模块 | 已落地 | 缺失/待完善 |
|------|--------|-------------|
| 工伤监管 | 事件管理、多维监测、预防项目/培训/资金/宣传/AI | 工伤认定辅助独立页、独立统计分析页 |
| 扩面减损 | 漏保清单、参保补贴、预防培训 | 催缴跟踪、培训效果评估 |
| 运营后台 | 岗位/简历/消息/部分设备运维/劳动者服务 | 运营数据总览、企业入驻审核、广告/轮播图、部分设备安装运维 |
| 便民服务 | 入口、岗位、法律援助 | 暖新地图、培训课程、法规库、互助圈 |
| 企业后台 | 企业门户 overview | 企业仪表盘、企业级人员/设备/工资/作业/保险/培训/招聘/财务/信用报告 |

### 未在 `views/ygb` 但已实现的模块
- **系统管理**：实现在 `src/views/system/`（RuoYi 标准目录），包含组织架构、用户、角色、菜单、岗位、字典、参数、通知公告、文档等。从功能视角应视为已完整实现。

---

## 5. AZB 子平台实现亮点与缺口

### AZB 实现模式
AZB 采用“**自有核心 + YGB 复用**”的架构：
- **完全自研**（有独立大文件）：驾驶舱、预警、AI 报告、安责险、信用、设备中台、企业、人员、工伤事件、预防项目/资金、漏保清单、统计报表、高空作业报备等
- **Wrapper 复用 YGB**：大量子模块通过 `import YgbXxx from '../ygb/xxx'` 方式直接渲染，如 `cockpitConfig`, `cockpitTrend`, `aiReportSubscription`, `aiReportTask`, `aqInsuranceClaim`, 各 device 子类型, `preventionPublicity`, `preventionTraining`, `warningStat` 等
- **缺失未做**：合同备案、工资、社保、专项治理、税务、企业后台、便民服务、新业态、职业病等

### AZB 特有功能
1. **角色视图体系**：`useRoleViewMode()` 支持应急监管、企业执行复核、保险机构、银行协同、现场企业等多角色
2. **治理焦点队列**：按风险等级排序的待办队列
3. **高空作业报备** (`heightWorkReport`)：AZB 独有，采用“只报备、不审批”模式
4. **AZB 品牌 UI**：`azb-page`、`azb-summary-grid` 等统一样式类

### AZB 与文档 23 个模块的匹配
| 类别 | 数量 | 模块 |
|------|------|------|
| 完整实现 | 14 | 驾驶舱、预警中心、AI 监测报告、安责险管理、设备管理、信用评价、统计报表、人员管理、单位管理、工伤监管（部分）、扩面减损（部分）、运营后台（部分） |
| Wrapper 复用 | 12 | 多为各模块的配置/统计/子类型页面 |
| 缺失 | 9 | 合同备案、用工考勤、工资监管、社保监管、专项治理、税务监管、企业后台、便民服务、新业态监管、职业病监管 |

> 注：AZB 本质是 YGB 的垂直业务子平台，缺失的模块正是安责险业务关联度较低的工资、社保、税务、合同备案、专项治理等，属于合理的产品边界。

---

## 6. 两个 PC 前端的主要差异

| 对比项 | YGB 后台 | AZB 子平台 |
|--------|----------|------------|
| 业务定位 | 全量监管平台 | 安责险专项治理 |
| 模块数量 | 22/23 有实现 | 14/23 完整实现 |
| 实现方式 | 全自研 | 核心自研 + 大量 wrapper 复用 YGB |
| 角色体系 | 省/市/县/企业四级 | 应急监管/企业/保险机构/银行/现场企业 |
| 特色功能 | 全业务覆盖 | 高空作业报备、治理焦点队列、安责险品牌 |
| 缺失较多领域 | 企业后台、便民服务、运营后台部分 | 工资/社保/税务/合同/专项治理/新业态/职业病 |

---

## 7. 发现的问题与建议

### 7.1 YGB 后台
1. **企业后台（模块 20）**  weakest：目前只有 `enterprisePortal/overview`，文档要求的 10 个二级模块均未独立成页。建议作为下一阶段重点建设。
2. **便民服务（模块 21）** 仅有入口和岗位/法援，缺少暖新地图、培训课程、法规库、互助圈。
3. **工伤监管** 的“认定辅助”和独立“统计分析”未明确对应目录，建议补充或合并到现有页面。
4. **扩面减损** 缺少“催缴跟踪”和“培训效果评估”独立视图。
5. **系统管理** 虽然功能已实现，但位于 `views/system/`，与 `views/ygb/` 分开，建议在项目文档或路由说明中明确归属。

### 7.2 AZB 子平台
1. **缺失模块的产品决策需明确**：工资/社保/税务/合同等模块在 AZB 是“ intentionally 不做”还是“待排期”？建议在 README 或 AZB 产品文档中声明边界。
2. **Wrapper 模式的风险**：大量子模块通过 wrapper 直接渲染 YGB 组件，若 YGB 组件中有 `portal-code` 等条件判断不当，可能出现 AZB 页面展示 YGB 品牌或权限元素的问题。建议统一回归验证。
3. **企业后台/便民服务/新业态/职业病**：若 AZB 需要完整监管视角，建议通过 wrapper 引入 YGB 对应模块，而非完全缺失。

### 7.3 跨平台一致性问题
1. **命名规范不统一**：
   - 新业态使用 `newform` 前缀
   - 安责险使用 `aqInsurance`
   - 职业病使用 `occupation`
   - 其余多为英文直译或拼音缩写
   建议制定统一的目录命名规范。
2. **部分目录为空容器**：`operation`、`platform`、`enterprisePortal` 等仅有子目录而无 `index.vue`，需确认是否作为路由占位或未完成。

---

## 8. 小程序/APP 实现状态（简要）

文档还涉及三个非 PC 端：

| 端口 | 代码目录 | 状态 |
|------|----------|------|
| 从业人员小程序 | `yuegongbao-worker-miniapp` / `yuegongbao-worker-uniapp` | 已存在，pages 结构待进一步核对 |
| 企业端小程序 | `yuegongbao-enterprise-miniapp` | 已存在，pages 结构待进一步核对 |
| 用工服务屏 APP | `yuegongbao-worker-screen-app` | 已存在，pages 结构待进一步核对 |

> 本次分析主要聚焦两个 PC 前端；如需进一步对比小程序/APP，可继续深入各 `pages` 目录。

---

## 9. 结论

1. **YGB 后台**已基本覆盖文档中 23 个 PC 一级模块的绝大多数功能，仅企业后台、便民服务、运营后台部分子模块、工伤/扩面减损部分三级功能需要补齐。
2. **AZB 子平台**作为安责险垂直门户，核心安责险链路（驾驶舱、预警、AI 报告、安责险、工伤事件、预防项目/资金、设备、人员、企业、统计报表）完整，但对非安责险业务模块（工资、社保、税务、合同、专项治理、新业态、职业病、企业后台、便民服务）未实现或仅通过 wrapper 复用。
3. 两套前端共享大量基础组件与页面，AZB 是 YGB 的子集/特化，建议后续明确产品边界、补齐 YGB 的企业后台与便民服务、并规范目录命名与 wrapper 复用机制。

---

## 10. 2026-06-29 增量修正记录

本轮已修正扩面减损下 3 个培训相关二级菜单：

| 二级菜单 | 修正结果 |
|---------|---------|
| 工伤预防培训管理 | 改为接入 `/ygb/injury/prevention/list`、`/summary` 和 `/export`，默认筛选培训类项目 `projectType=2`，展示计划落地、推进中、待验收、低分整改等三级功能。 |
| 培训课程与学时管理 | 从新业态培训接口切换为工伤预防项目接口，按培训项目展示课程周期、预计学时、预算投入、实际投入、验收评价和详情。 |
| 培训效果评估 | 从新业态培训接口切换为工伤预防项目接口，按评价分、项目状态、评价报告承载待评估、已评估、低分整改等三级功能。 |

同步修正 `sql/ygb_phase56_pc_menu_document_final.sql`：

- 以上 3 个二级菜单的列表权限统一为 `ygb:preventionProject:list`。
- 课程学时和效果评估导出权限从 `ygb:newformTraining:export` 改为 `ygb:preventionProject:export`。
- 菜单图标仍保留 `education`、`time`、`chart`，未留空。

验证结果：

- `npm.cmd run verify:pc-menu-health` 通过。
- `npm.cmd run build:ygb` 通过。

## 11. 2026-06-29 运营后台增量修正记录

本轮继续修正“运营后台”下的设备安装运维二级菜单：

| 二级菜单 | 修正结果 |
|---------|---------|
| 设备安装运维 | 保留文档要求的单一二级菜单入口，不新增多余二级菜单；页面内通过 tabs 承载“安装工单、维修工单、巡检计划、运维统计”四类三级功能。 |
| 设备安装运维权限 | 在 `sql/ygb_phase56_pc_menu_document_final.sql` 中把 `deviceRepairOrder`、`deviceInspectPlan`、`operationMaintenanceStats` 的 list/query/add/edit/remove/export 按钮权限挂到“设备安装运维”二级菜单下，避免 tab 内接口 403。 |
| 菜单图标 | 运营后台二级菜单继续保留可视图标，设备安装运维使用 `build`，页面 tab 使用 Element Plus 图标。 |

同步核对运营后台其他二级页：
- 招聘岗位审核、简历管理、数据统计与分析、广告/轮播图管理均有对应后端控制器和权限。
- 企业入驻审核、消息推送管理、职位分类管理走共享业务记录控制器 `/ygb/{module}`，后端枚举已覆盖，菜单 SQL 自动补齐 list/query/add/edit/remove/export 权限。

验证结果：
- `git diff --check -- yuegongbao-ui/src/views/ygb/operation/deviceInstallOrder/index.vue sql/ygb_phase56_pc_menu_document_final.sql` 通过。
- `npm.cmd run verify:pc-menu-health` 通过。
- `npm.cmd run build:ygb` 通过。
- `npm.cmd run build:azb` 通过。

## 12. 2026-06-29 企业后台与便民服务复核记录

本轮继续复核文档模块 20「企业后台」与模块 21「便民服务」：

| 模块 | 复核结果 |
|------|---------|
| 企业后台菜单 | `sql/ygb_phase56_pc_menu_document_final.sql` 仅保留文档要求的 10 个二级菜单：企业仪表盘、企业人员管理、企业设备管理、企业工资管理、企业作业管理、企业保险管理、企业培训管理、企业招聘管理、企业财务管理、企业信用报告；每项均配置可见图标。 |
| 企业后台页面 | 仪表盘使用 `enterprisePortal/dashboard`；其余二级菜单复用 `enterprisePortal/overview`，通过当前路由段切换模块配置，在二级页面内承载人员、设备、工资、作业、保险、培训、招聘、财务、信用等三级功能。 |
| 企业后台接口 | 前端调用的 `/app/enterprise/home/dashboard`、`/workbench/dashboard`、`/people/*`、`/device/*`、`/salary/*`、`/operation-approval/*`、`/insurance/*`、`/training/*`、`/job-publish/*` 均有 `AppEnterpriseController` 对应接口。 |
| 企业后台跳转 | 页面内 18 个三级功能跳转均能在当前 PC 菜单 SQL 中找到对应二级路由，未发现跳转到已清理菜单的 404 入口。 |
| 便民服务菜单 | 仅保留文档要求的 5 个二级菜单：暖新地图、培训课程、法规库、互助圈、招聘用工市场；每项均配置可见图标。 |
| 便民服务接口 | 暖新地图、培训课程、法规库、互助圈使用公开门户 `/open/portal/{portalCode}/home` 与 `/search`；招聘市场使用 `/open/portal/{portalCode}/jobs`，后端 `YgbPortalPublicController` 已覆盖。 |
| 便民服务修正 | 修正招聘市场“岗位详情”按钮：从直接打开后端 JSON 接口 `/open/portal/jobs/{jobId}` 改为跳转前端详情页 `/portal/job/{jobId}`，详情页再调用公开接口取数，避免用户进入接口响应页面。 |

验证结果：
- `git diff --check -- yuegongbao-ui/src/views/ygb/citizenService/recruitMarket/index.vue` 通过。
- `npm.cmd run verify:pc-menu-health` 通过：417 个菜单组件、54 个业务模块。
- `npm.cmd run build:ygb` 通过。

## 13. 2026-06-29 社保业务页语义修正记录

本轮继续复核共享业务记录页承载的 PC 业务模块，发现部分二级菜单虽然前后端接口可通，但页面仍使用“金额、数量、业务内容、处置结果”等泛化字段，业务人员录入参保率、补缴事项时语义不清。

| 模块 | 修正结果 |
|------|---------|
| 共享业务记录页 | `BusinessRecordPage` 新增 `fieldLabels` 与 `fieldPlaceholders` 配置入口，列表、表单、详情均可按业务模块改写金额、数量、内容、结果、责任人、时间、来源等字段语义；未配置模块仍保持原默认标签，兼容现有页面。 |
| 参保率统计 | 将“业务名称”改为“参保统计对象”，将金额/数量改为“参保率(%) / 应参保人数”，内容与结果改为“统计口径 / 整改/督办结果”，用于承载企业参保排名、低参保识别和统计台账。 |
| 社保补缴跟踪 | 将补缴事项的金额/数量改为“应补缴金额 / 补缴人数”，内容与结果改为“断缴/补缴说明 / 补缴进度与结果”，用于承载断缴识别、补缴整改和进度记录。 |

验证结果：
- `rg -n "[ \t]+$"` 检查本轮 4 个修改文件，无尾随空白。
- `npm.cmd run verify:pc-menu-health` 通过：417 个菜单组件、54 个业务模块。
- `npm.cmd run build:ygb` 通过。

## 14. 2026-06-29 共享业务记录页批量语义补齐记录

本轮继续对 PC 端使用 `BusinessRecordPage` 的业务模块做统一检索，确认共有 51 个共享业务记录模块，其中上一轮已对“参保率统计、社保补缴跟踪”做页面级字段覆盖，其余 49 个模块仍主要依赖默认“金额、数量、业务内容、处置结果”等泛化字段。

本轮修正方式：

| 范围 | 修正结果 |
|------|---------|
| 共享配置层 | 在 `businessRecordPageConfig.js` 新增 `moduleFieldLabelMap`，按模块提供默认字段口径；页面自身传入的 `fieldLabels` 仍优先覆盖模块默认值。 |
| 自动占位符 | 新增默认占位符生成逻辑，按业务标签生成“请输入xxx”，减少各页面重复配置。 |
| 覆盖模块 | 为信用修复/规则/惩戒、工伤监测、安责险赔付、扩面补贴、税票/资金流、三性岗位、工会、平台运维、运营后台设备/消息/招聘/芯片等 49 个共享业务模块补齐金额、数量、内容、结果、责任人、时间、来源字段语义。 |
| 兼容性 | 未改变接口地址、权限标识和后端数据结构，仍通过 `/ygb/{module}` 通用业务记录接口联调；仅改前端展示与录入语义。 |

验证结果：
- 覆盖性脚本确认 51 个共享业务模块均能获得字段级业务语义，缺失项为 `none`。
- `rg -n "[ \t]+$" yuegongbao-ui/src/views/ygb/shared/businessRecordPageConfig.js` 无尾随空白。
- `npm.cmd run verify:pc-menu-health` 通过：417 个菜单组件、54 个业务模块。
- `npm.cmd run build:ygb` 通过。

## 15. 2026-06-29 企业后台总览页业务字段修正记录

本轮继续检索非共享业务记录页，发现企业后台总览页 `enterprisePortal/overview` 承载企业人员、设备、工资、作业、保险、培训、招聘、财务、信用 9 个文档二级菜单，其中工资和财务表格仍显示泛化“金额”列。

修正结果：

| 模块 | 修正结果 |
|------|---------|
| 企业工资管理 | 后端 `AppEnterpriseServiceImpl.salaryBatchRow` 中 `amount` 来源为 `YgbSalaryBatch.totalPayableAmount`，前端列名由“金额”改为“应发工资”，并右对齐展示。 |
| 企业财务管理 | 该页复用工资仪表盘接口展示企业工资财务批次，前端列名同步由“金额”改为“应发工资”，避免企业端误解为到账、实发或费用总额。 |
| 表格渲染 | 企业后台总览表格新增列宽、最小宽度、对齐和 `formatter` 支持，后续各企业端二级菜单可按真实业务字段继续细化展示。 |
| 办理反馈 | `runOperation` 增加失败提示，接口异常时企业端会显示“操作失败，请稍后重试”或后端错误信息，避免只结束 loading 但无反馈。 |

验证结果：
- `rg -n "[ \t]+$" yuegongbao-ui/src/views/ygb/enterprisePortal/overview/index.vue` 无尾随空白。
- `npm.cmd run verify:pc-menu-health` 通过：417 个菜单组件、54 个业务模块。
- `npm.cmd run build:ygb` 通过。
