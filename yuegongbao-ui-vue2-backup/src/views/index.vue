<template>
  <div class="app-container home">
    <el-row :gutter="16" class="hero-row">
      <el-col :xs="24" :lg="14">
        <section class="hero-panel">
          <div class="hero-meta">
            项目首页
          </div>
          <h2>
            粤工保管理平台
          </h2>
          <p class="hero-desc">
            面向企业、项目、班组和工人全链路管理的业务基座，当前版本承载组织企业、人员实名、
            合同备案、考勤打卡、工资发放、社保工伤、风险预警和 AI 报告等核心能力。
          </p>
          <div class="hero-tags">
            <el-tag size="mini">模块化单体</el-tag>
            <el-tag size="mini" type="success">Spring Boot 3</el-tag>
            <el-tag size="mini" type="warning">Vue 2</el-tag>
            <el-tag size="mini" type="danger">JDK 17</el-tag>
          </div>
        </section>
      </el-col>
      <el-col :xs="24" :lg="10">
        <section class="summary-panel">
          <div class="summary-item">
            <span class="label">
              当前版本
            </span>
            <span class="value">
              v{{ version }}
            </span>
          </div>
          <div class="summary-item">
            <span class="label">
              部署形态
            </span>
            <span class="value">
              粤工保单体改造基座
            </span>
          </div>
          <div class="summary-item">
            <span class="label">
              接口文档
            </span>
            <span class="value">
              /swagger-ui.html
            </span>
          </div>
          <div class="summary-item">
            <span class="label">
              业务模块
            </span>
            <span class="value">
              14 个初版域模块
            </span>
          </div>
        </section>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="module-row">
      <el-col v-for="group in moduleGroups" :key="group.title" :xs="24" :md="12" :xl="8">
        <section class="module-panel">
          <div class="panel-title">
            {{ group.title }}
          </div>
          <ul class="module-list">
            <li v-for="item in group.items" :key="item.name">
              <div class="module-name">
                {{ item.name }}
              </div>
              <div class="module-desc">
                {{ item.desc }}
              </div>
            </li>
          </ul>
        </section>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <section class="module-panel">
          <div class="panel-title">
            实施建议
          </div>
          <ol class="plan-list">
            <li>先完成企业、项目、班组、工人、合同、考勤、工资六条核心数据链路。</li>
            <li>同步补齐菜单、角色、权限标识，避免后续跨模块返工。</li>
            <li>稳定表结构优先接入代码生成器，提升后台 CRUD 开发效率。</li>
            <li>一期先做后台和管理端，UniApp 端按实名、考勤、工资查询优先落地。</li>
          </ol>
        </section>
      </el-col>
      <el-col :xs="24" :lg="12">
        <section class="module-panel">
          <div class="panel-title">
            交付边界
          </div>
          <ul class="module-list compact">
            <li>
              <div class="module-name">
                当前已完成
              </div>
              <div class="module-desc">
                框架基座、业务模块骨架、统一权限入口、初版模块目录接口。
              </div>
            </li>
            <li>
              <div class="module-name">
                下一阶段
              </div>
              <div class="module-desc">
                数据库建模、菜单初始化、首页看板、核心业务表单与流程。
              </div>
            </li>
            <li>
              <div class="module-name">
                上线前必须补齐
              </div>
              <div class="module-desc">
                真实域名、正式 logo、短信、存储、第三方接口、安全审计和压测。
              </div>
            </li>
          </ul>
        </section>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      version: "3.9.2",
      moduleGroups: [
        {
          title: "一期核心",
          items: [
            { name: "组织企业", desc: "企业、项目、班组、劳务关系管理" },
            { name: "人员档案", desc: "实名、证照、标签、履历与黑名单管理" },
            { name: "合同备案", desc: "劳动合同、用工协议、签署留痕" },
            { name: "考勤打卡", desc: "进退场、班次、工时、异常考勤处理" },
            { name: "工资税务", desc: "工资核算、发放、对账、凭证留存" }
          ]
        },
        {
          title: "保障闭环",
          items: [
            { name: "社保工伤", desc: "参保、缴费、工伤申报与跟踪" },
            { name: "预警中心", desc: "规则触发、消息推送、处置闭环" },
            { name: "AI 报告", desc: "巡检分析、风险摘要、报告归档" },
            { name: "文件中心", desc: "合同、附件、图片、报告统一存储" }
          ]
        },
        {
          title: "扩展模块",
          items: [
            { name: "培训考试", desc: "安全培训、考试、持证合规管理" },
            { name: "投诉工会", desc: "投诉、维权、调解和工会服务" },
            { name: "设备物联", desc: "闸机、摄像头、IoT 数据接入" },
            { name: "招聘服务", desc: "招工、岗位撮合、班组服务" },
            { name: "官网门户", desc: "资讯发布、平台展示、对外宣传" }
          ]
        }
      ]
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  color: #303133;
}

.hero-row,
.module-row {
  margin-bottom: 16px;
}

.hero-panel,
.summary-panel,
.module-panel {
  height: 100%;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
}

.hero-meta,
.panel-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.hero-panel h2 {
  margin: 0 0 12px;
  font-size: 28px;
  font-weight: 600;
  color: #1f2d3d;
}

.hero-desc {
  margin: 0 0 16px;
  line-height: 1.8;
  color: #606266;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.summary-panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.summary-item {
  min-height: 84px;
  border: 1px solid #f0f2f5;
  border-radius: 6px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.summary-item .label {
  font-size: 13px;
  color: #909399;
}

.summary-item .value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.module-list,
.plan-list {
  margin: 0;
  padding: 0;
}

.module-list {
  list-style: none;
}

.module-list li,
.plan-list li {
  padding: 12px 0;
  border-bottom: 1px solid #f2f6fc;
}

.module-list li:last-child,
.plan-list li:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.module-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.module-desc,
.plan-list li {
  line-height: 1.7;
  color: #606266;
}

.plan-list {
  padding-left: 18px;
}

.compact li {
  padding: 10px 0;
}
</style>
