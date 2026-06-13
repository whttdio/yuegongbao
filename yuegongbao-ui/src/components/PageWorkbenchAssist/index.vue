<template>
  <div v-if="visible" class="page-workbench-assist">
    <el-button class="page-workbench-assist__trigger" type="primary" circle @click="openGuide">
      <el-icon><Guide /></el-icon>
    </el-button>

    <el-dialog
      :model-value="open"
      class="page-workbench-assist__dialog"
      width="min(960px, calc(100vw - 32px))"
      top="6vh"
      append-to-body
      destroy-on-close
      @close="closeGuide"
      @update:model-value="handleDialogModelValue"
    >
      <template #header>
        <div class="page-workbench-assist__header">
          <div>
            <div class="page-workbench-assist__eyebrow">工作指引</div>
            <h3 class="page-workbench-assist__title">{{ pageGuide.title || '当前页面工作指引' }}</h3>
            <p v-if="pageGuide.description" class="page-workbench-assist__desc">{{ pageGuide.description }}</p>
          </div>
        </div>
      </template>

      <div class="page-workbench-assist__content">
        <section v-if="pageGuide.portalExplanation.length" class="page-workbench-assist__section">
          <div class="page-workbench-assist__section-head">
            <h4>门户解释</h4>
            <span>按当前页面口径解释关键对象</span>
          </div>
          <div class="page-workbench-assist__explanations">
            <article
              v-for="item in pageGuide.portalExplanation"
              :key="item.key || item.dimensionName || item.label"
              class="page-workbench-assist__explanation"
            >
              <div class="page-workbench-assist__explanation-top">
                <strong>{{ item.dimensionName || item.label }}</strong>
                <span class="page-workbench-assist__metric">
                  {{ item.currentValue ?? '--' }}
                  <small>/ {{ item.targetValue ?? '--' }}</small>
                </span>
              </div>
              <p>{{ item.summary || item.sourceDescription || '-' }}</p>
              <div class="page-workbench-assist__meta">
                <span>来源：{{ item.sourceLabel || '-' }}</span>
                <span>证据：{{ item.evidenceModule || '-' }}</span>
                <span>下钻：{{ item.recommendModule || '-' }}</span>
              </div>
              <el-button
                v-if="item.action?.path"
                link
                type="primary"
                @click="handleOpenAction(item.action)"
              >
                {{ item.actionText || `进入${item.moduleLabel || item.recommendModule || '模块'}` }}
              </el-button>
            </article>
          </div>
        </section>

        <section v-if="pageGuide.focus.length" class="page-workbench-assist__section">
          <div class="page-workbench-assist__section-head">
            <h4>焦点对象</h4>
            <span>先看当前页最值得优先处理的对象</span>
          </div>
          <div class="page-workbench-assist__grid">
            <article v-for="item in pageGuide.focus" :key="`${item.label}-${item.value}`" class="page-workbench-assist__card">
              <span class="page-workbench-assist__label">{{ item.label }}</span>
              <strong class="page-workbench-assist__value">{{ item.value }}</strong>
              <p v-if="item.tip" class="page-workbench-assist__hint">{{ item.tip }}</p>
            </article>
          </div>
        </section>

        <section v-if="pageGuide.selection.length" class="page-workbench-assist__section">
          <div class="page-workbench-assist__section-head">
            <h4>当前选中</h4>
            <span>当前对象或当前焦点的摘要信息</span>
          </div>
          <div class="page-workbench-assist__selection">
            <div v-for="item in pageGuide.selection" :key="`${item.label}-${item.value}`" class="page-workbench-assist__row">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
            </div>
          </div>
        </section>

        <section v-if="pageGuide.workflow.length" class="page-workbench-assist__section">
          <div class="page-workbench-assist__section-head">
            <h4>办理路径</h4>
            <span>建议按顺序推进，减少页面来回切换</span>
          </div>
          <div class="page-workbench-assist__steps">
            <article v-for="(item, index) in pageGuide.workflow" :key="`${item.label}-${index}`" class="page-workbench-assist__step">
              <span class="page-workbench-assist__step-index">{{ String(index + 1).padStart(2, '0') }}</span>
              <div>
                <strong>{{ item.label }}</strong>
                <p>{{ item.desc || '-' }}</p>
              </div>
            </article>
          </div>
        </section>

        <section v-if="pageGuide.hints.length" class="page-workbench-assist__section">
          <div class="page-workbench-assist__section-head">
            <h4>办理提示</h4>
            <span>当前页面最需要关注的风险或补录点</span>
          </div>
          <div class="page-workbench-assist__tags">
            <el-tag v-for="item in pageGuide.hints" :key="item.label" :type="item.type || 'info'" effect="plain">
              {{ item.label }}
            </el-tag>
          </div>
        </section>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { Guide } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'

const router = useRouter()
const { visible, open, pageGuide, openGuide, closeGuide } = useWorkbenchAssist()

function handleDialogModelValue(value) {
  if (!value) {
    closeGuide()
  }
}

function handleOpenAction(action) {
  if (!action?.path) {
    return
  }
  closeGuide()
  router.push({
    path: action.path,
    query: action.query
  })
}
</script>
