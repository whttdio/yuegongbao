<template>
  <div class="portal-dashboard">
    <section class="hero-card">
      <div class="hero-card__body">
        <span class="hero-card__eyebrow">{{ workbench.eyebrow }}</span>
        <h1 class="hero-card__title">{{ workbench.title }}</h1>
        <p class="hero-card__desc">{{ workbench.description }}</p>
        <div class="hero-card__meta">
          <span>{{ workbench.versionTag }}</span>
          <span class="hero-card__dot" />
          <span>{{ appTitle }}</span>
          <span class="hero-card__dot" />
          <span>{{ portalSummaryText }}</span>
        </div>
      </div>

      <div class="hero-card__aside">
        <div class="phase-card" v-loading="homeLoading">
          <span class="phase-card__label">{{ workbench.phaseLabel }}</span>
          <strong class="phase-card__value">{{ workbench.phaseValue }}</strong>
          <p class="phase-card__desc">{{ workbench.phaseDescription }}</p>
          <div class="phase-card__tags">
            <el-tag
              v-for="item in currentRoleLabels"
              :key="item"
              effect="plain"
              round
              size="small"
            >
              {{ item }}
            </el-tag>
          </div>
          <div class="phase-card__suggest">
            <span class="phase-card__suggest-label">建议先做</span>
            <p>{{ homeSummaryText || '当前首页聚合未返回摘要字段，请补充门户聚合接口的 homeSummary。' }}</p>
            <span v-if="homeError" class="phase-card__hint">{{ homeError }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="metric-grid" v-loading="homeLoading">
      <article v-for="item in metricCards" :key="item.label" class="metric-card">
        <span class="metric-card__topline" :style="{ color: item.color }">{{ item.topline }}</span>
        <strong class="metric-card__value">{{ item.value }}</strong>
        <span class="metric-card__label">{{ item.label }}</span>
      </article>
      <div v-if="!metricCards.length" class="panel-empty panel-empty--full">
        当前暂无可展示的门户聚合指标。
      </div>
    </section>

    <section class="workspace-grid">
      <el-card class="panel-card panel-card--wide" shadow="never" v-loading="homeLoading">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ workspaceSectionTitle }}</h3>
              <span>{{ workspaceSectionHint }}</span>
            </div>
          </div>
        </template>
        <div v-if="availableQuickSections.length" class="quick-section-list">
          <section v-for="group in availableQuickSections" :key="group.key || group.title" class="quick-section">
            <div class="quick-section__header">
              <div>
                <h4>{{ group.title }}</h4>
                <p>{{ group.desc }}</p>
              </div>
              <span class="quick-section__count">{{ group.actions.length }} 项</span>
            </div>
            <div class="quick-action-grid">
              <button
                v-for="action in group.actions"
                :key="action?.key || action?.path || action?.label"
                type="button"
                class="quick-action"
                @click="action?.path && $emit('open-action', action)"
              >
                <span class="quick-action__tag">{{ action?.badge || '' }}</span>
                <strong class="quick-action__title">{{ action?.label || '-' }}</strong>
                <p class="quick-action__desc">{{ action?.desc || '' }}</p>
                <span class="quick-action__footer">{{ action?.liveText || action?.footer || '' }}</span>
              </button>
            </div>
          </section>
        </div>
        <div v-else class="panel-empty">
          当前暂无可展示的门户快捷入口。
        </div>
      </el-card>

      <el-card class="panel-card" shadow="never" v-loading="homeLoading">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ playbookTitle }}</h3>
              <span>{{ playbookHint }}</span>
            </div>
          </div>
        </template>
        <div class="playbook-list">
          <article v-for="playbook in activePlaybooks" :key="playbook.key || playbook.title" class="playbook-card">
            <div class="playbook-card__top">
              <strong>{{ playbook.title }}</strong>
              <span class="playbook-card__type">{{ playbook.badge }}</span>
            </div>
            <p class="playbook-card__summary">{{ playbook.liveSummary }}</p>
            <div class="playbook-card__actions">
              <el-button
                v-for="action in playbook.actions"
                :key="action?.key || action?.path || action?.label"
                size="small"
                plain
                @click="action?.path && $emit('open-action', action)"
              >
                {{ action?.label || '-' }}
              </el-button>
            </div>
            <div class="playbook-card__steps">
              <div v-for="(step, index) in playbook.steps" :key="step" class="playbook-step">
                <span class="playbook-step__index">{{ index + 1 }}</span>
                <span class="playbook-step__text">{{ step }}</span>
              </div>
            </div>
          </article>
        </div>
      </el-card>
    </section>
    <section class="content-grid">
      <el-card class="panel-card panel-card--wide" shadow="never" v-loading="homeLoading">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ queueSectionTitle }}</h3>
              <span>{{ queueSectionHint }}</span>
            </div>
          </div>
        </template>
        <div v-if="homeQueueSections.length" class="queue-section-list">
          <section v-for="section in homeQueueSections" :key="section.key || section.title" class="queue-section">
            <div class="queue-section__header">
              <div>
                <h4>{{ section.title }}</h4>
                <p>{{ section.desc }}</p>
              </div>
              <span class="queue-section__count">{{ section.items.length }} 项</span>
            </div>
            <div class="queue-list">
              <button
                v-for="item in section.items"
                :key="item?.key || item?.title"
                type="button"
                class="queue-item"
                :disabled="!item?.action?.path"
                @click="item?.action?.path && $emit('open-action', item.action)"
              >
                <div class="queue-item__body">
                  <strong class="queue-item__title">{{ item?.title || '-' }}</strong>
                  <p class="queue-item__desc">{{ item?.desc || '' }}</p>
                </div>
                <div class="queue-item__meta">
                  <span class="queue-item__status">{{ item?.status || '' }}</span>
                  <span class="queue-item__hint">{{ item?.hint || '' }}</span>
                </div>
              </button>
            </div>
          </section>
        </div>
        <div v-else class="queue-empty">
          当前权限下暂无可展示的重点队列，首页仍保留门户级摘要和推荐入口。
        </div>
      </el-card>

      <el-card class="panel-card" shadow="never">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ moduleTitle }}</h3>
              <span>{{ moduleHint }}</span>
            </div>
          </div>
        </template>
        <div class="module-list">
          <div v-for="item in workbench.modules" :key="item.name" class="module-item">
            <div class="module-item__body">
              <strong>{{ item.name }}</strong>
              <p>{{ item.desc }}</p>
            </div>
            <el-tag effect="plain" :type="item.tagType" round size="small">{{ item.phase }}</el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="panel-card" shadow="never">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ taskTitle }}</h3>
              <span>{{ taskHint }}</span>
            </div>
          </div>
        </template>
        <div class="task-list">
          <div v-for="(item, index) in workbench.tasks" :key="item" class="task-item">
            <span class="task-item__index">{{ String(index + 1).padStart(2, '0') }}</span>
            <span class="task-item__text">{{ item }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="panel-card" shadow="never">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ stackTitle }}</h3>
              <span>{{ stackHint }}</span>
            </div>
          </div>
        </template>
        <div class="stack-grid">
          <div v-for="item in workbench.stack" :key="item.title" class="stack-card">
            <span class="stack-card__kicker">{{ item.kicker }}</span>
            <strong class="stack-card__title">{{ item.title }}</strong>
            <div class="stack-card__items">
              <span v-for="tech in item.items" :key="tech" class="stack-card__tag">{{ tech }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="panel-card panel-card--wide" shadow="never">
        <template #header>
          <div class="panel-card__header">
            <div>
              <h3>{{ policyTitle }}</h3>
              <span>{{ policyHint }}</span>
            </div>
          </div>
        </template>
        <div class="policy-list">
          <div v-for="item in workbench.policies" :key="item.title" class="policy-item">
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { homeWorkbenchProps } from './homeWorkbenchProps'

defineOptions({
  name: 'HomeWorkbenchShell'
})

defineEmits(['open-action'])

const props = defineProps({
  ...homeWorkbenchProps,
  portalSummaryText: {
    type: String,
    default: '按门户、角色与真实摘要重排默认工作台'
  },
  playbookTitle: {
    type: String,
    default: '当前角色工作路径'
  },
  playbookHint: {
    type: String,
    default: '按当前账号角色和可见模块自动给出建议顺序'
  },
  focusTitle: {
    type: String,
    default: '当前真实态势'
  },
  focusHint: {
    type: String,
    default: '从共享后台的真实摘要接口直接装配，不再只展示静态说明'
  },
  moduleTitle: {
    type: String,
    default: '当前业务拆分'
  },
  moduleHint: {
    type: String,
    default: '按门户语义和共享后台能力组织模块边界'
  },
  taskTitle: {
    type: String,
    default: '高频使用路径'
  },
  taskHint: {
    type: String,
    default: '围绕企业、工地、财务和监管的实际操作顺序组织'
  },
  stackTitle: {
    type: String,
    default: '前端架构层次'
  },
  stackHint: {
    type: String,
    default: '门户独立演进，共享后台不重复建设'
  },
  policyTitle: {
    type: String,
    default: '平台治理原则'
  },
  policyHint: {
    type: String,
    default: '保证双门户差异明确，但企业、人员、预警和高危作业仍共底座'
  }
})

const workbench = computed(() => props.workbench)
const { setPageGuide } = useWorkbenchAssist()

const guideWorkflow = computed(() => {
  const playbook = Array.isArray(props.activePlaybooks) ? props.activePlaybooks[0] : null
  return (playbook?.steps || []).map(step => ({
    label: step,
    desc: playbook?.liveSummary || playbook?.title || ''
  }))
})

const guideHints = computed(() => {
  const tags = []
  if (props.homeSummaryText) {
    tags.push({ label: props.homeSummaryText, type: 'info' })
  }
  if (props.homeError) {
    tags.push({ label: props.homeError, type: 'warning' })
  }
  return tags
})

watchEffect(() => {
  setPageGuide({
    title: workbench.value.title || props.playbookTitle || '首页工作台指引',
    description: workbench.value.description || props.playbookHint || '',
    portalExplanation: props.portalExplanationItems || [],
    focus: props.focusPanels || [],
    selection: (props.homeQueueSections || []).flatMap(section => (section.items || []).slice(0, 3).map(item => ({
      label: item?.title || section.title || '重点队列',
      value: item?.status || item?.hint || item?.desc || '-'
    }))),
    workflow: guideWorkflow.value,
    hints: guideHints.value
  })
})
</script>

<style lang="scss" scoped src="./homeWorkbench.scss"></style>
