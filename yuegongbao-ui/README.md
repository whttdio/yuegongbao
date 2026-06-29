# 粤工保前端管理端

## 说明

当前目录为 `Vue 3 + Vite + Element Plus` 的 PC 管理端工程，对应 `yuegongbao-ui`。

## 技术栈

- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router 4
- Axios

## 启动命令

```bash
npm.cmd install
npm.cmd run dev:ygb
npm.cmd run dev:azb
```

## 打包命令

```bash
npm.cmd run build:ygb
npm.cmd run build:azb
npm.cmd run preview
```

## 校验脚本

```bash
npm.cmd run verify:role-portal
npm.cmd run verify:dual-portal-delivery
npm.cmd run verify:portal-aggregation-contract
npm.cmd run verify:portal-runtime-behavior
```

## PC 四 Skill 工作流

### 适用范围

当前工作流只适用于 `yuegongbao-ui`，不覆盖 `uniapp` 三端。

### 前置条件

- Windows 下统一使用 `npm.cmd`
- 本地开发入口优先 `npm.cmd run dev:ygb` 或 `npm.cmd run dev:azb`
- 已安装全局 `playwright-skill`
- 已执行 `npm.cmd run skill:pc:playwright:setup`
- 已安装 `kane-cli`
- 首次使用 `kane-cli` 前，先手动执行 `kane-cli login`

### 固定顺序

1. `frontend-design`
2. 页面实现
3. `web-design-guidelines`
4. `playwright-skill`
5. `kane-cli generate / testmd`

### 团队统一入口

```bash
npm.cmd run skill:pc:preflight
npm.cmd run skill:pc:playwright:setup
npm.cmd run skill:pc:playwright:smoke -- --url http://localhost:5173
npm.cmd run skill:pc:kane:whoami
npm.cmd run skill:pc:kane:generate -- "登录、菜单进入、列表筛选、详情打开"
npm.cmd run skill:pc:kane:testmd -- smoke/login_test.md
```

### 推荐验证对象

- 登录
- 门户切换
- 菜单导航
- 列表加载
- 筛选查询
- 表单提交
- 详情查看
- 弹窗交互
- 响应式布局

### `frontend-design` Prompt

用于新页面或重构页面，在写代码前先定视觉和结构。

```text
使用 frontend-design skill，只针对 yuegongbao-ui 的 PC 管理端页面设计。
目标文件范围：yuegongbao-ui/src/views/** 以及相关 components。
请先给出该页面的视觉方向、布局结构、文案语气、交互重点和响应式处理建议，
再给出实现时应保持的设计约束。
页面必须符合监管后台的专业、稳定、可读特征，不要做营销站风格。
```

### `web-design-guidelines` Prompt

用于代码完成后的 UI/UX 审查，不做全量扫描，只审目标文件或变更文件。

```text
使用 web-design-guidelines skill 审查以下 PC 页面代码，
范围仅限 yuegongbao-ui/src/views/** 和相关 components / styles。
请检查可读性、层级、交互一致性、键盘可访问性、状态反馈、响应式和表单体验，
按 file:line 输出问题，不要扫描整个 src。
```

### `playwright-skill` 用法

- 先跑 `npm.cmd run skill:pc:preflight`
- 再跑 `npm.cmd run skill:pc:playwright:smoke`
- 本地有多个 dev server 时，优先通过 `--url` 明确指定

### `kane-cli` 用法

- 先跑 `npm.cmd run skill:pc:kane:whoami`
- 未登录时先执行 `kane-cli login`
- 用 `skill:pc:kane:generate` 生成测试场景
- 用 `skill:pc:kane:testmd` 回放或沉淀 `_test.md` 用例
- 项目级测试目录固定为 `yuegongbao-ui/.testmuai/tests`

## 备注

- 旧版 Vue 2 前端已备份到 `../yuegongbao-ui-vue2-backup`
- 当前默认后端代理地址为 `http://localhost:8080`
