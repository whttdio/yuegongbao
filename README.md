# 粤工保管理平台

## 项目说明

本项目基于 `Spring Boot 3` 单体后台框架改造，当前定位为 **粤工保管理平台初版基座**。

当前技术栈：
- 后端：Java 17、Spring Boot 3、Spring Security、MyBatis
- 前端：Vue 3、Vite、Element Plus
- 架构：模块化单体

## 当前模块

- `yuegongbao-admin`：启动入口
- `yuegongbao-framework`：安全、拦截器、通用框架能力
- `yuegongbao-system`：系统管理模块
- `yuegongbao-business`：粤工保业务模块
- `yuegongbao-quartz`：定时任务模块
- `yuegongbao-generator`：代码生成模块
- `yuegongbao-common`：公共组件模块
- `yuegongbao-ui`：前端管理端

## 本次基座改造内容

- 接入 `yuegongbao-business` 业务模块
- 替换前后端默认品牌为“粤工保”
- 调整系统标题、首页、Swagger、初始化数据
- 统一模块名、包名、脚本名为 `yuegongbao`

## 启动前要求

1. 安装 JDK 17
2. 安装 Maven 3.8+
3. 准备 MySQL 8、Redis
4. 导入 `sql/yuegongbao_20260417.sql`

## 后续建议

1. 补齐企业、项目、班组、工人、合同、考勤、工资等核心业务表设计
2. 基于业务模型继续生成和完善后台 CRUD
3. 补充菜单、权限、首页看板和统计能力
4. 对接 UniApp 端业务接口
