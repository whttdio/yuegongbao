<template>
  <div class="app-container page-shell">
    <el-card shadow="never" class="hero-card">
      <div class="hero-card__body">
        <div>
          <div class="hero-card__eyebrow">用工宝 / 劳动者服务</div>
          <div class="hero-card__title">用户反馈记录</div>
          <div class="hero-card__desc">
            集中处理劳动者提交的产品反馈、功能建议和服务意见，形成受理、办结和重新跟进的闭环记录。
          </div>
        </div>
        <div class="hero-card__meta">
          <div class="hero-card__meta-label">当前筛选总数</div>
          <div class="hero-card__meta-value">{{ total }}</div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="panel-card search-card">
      <el-form
        ref="queryRef"
        :model="queryParams"
        :inline="true"
        v-show="showSearch"
        label-width="88px"
      >
        <el-form-item label="反馈标题" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入反馈标题"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="劳动者" prop="personName">
          <el-input
            v-model="queryParams.personName"
            placeholder="请输入劳动者姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="账号手机号" prop="mobile">
          <el-input
            v-model="queryParams.mobile"
            placeholder="请输入账号手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="反馈状态" prop="status">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="overview-grid">
      <div
        v-for="card in overviewCards"
        :key="card.key"
        class="overview-card"
        :class="`overview-card--${card.tone}`"
      >
        <div class="overview-card__label">{{ card.label }}</div>
        <div class="overview-card__value">{{ card.value }}</div>
        <div class="overview-card__hint">{{ card.hint }}</div>
      </div>
    </div>

    <el-card shadow="never" class="panel-card table-card">
      <template #header>
        <div class="table-card__header">
          <div class="table-card__tabs">
            <el-tag
              v-for="filter in statusFilters"
              :key="filter.value"
              class="table-card__tag"
              :effect="queryParams.status === filter.value ? 'dark' : 'plain'"
              :type="queryParams.status === filter.value ? filter.type : 'info'"
              @click="handleStatusFilter(filter.value)"
            >
              {{ filter.label }}
            </el-tag>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="feedbackList"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="反馈ID" prop="feedbackId" width="90" align="center" />
        <el-table-column label="反馈信息" min-width="280">
          <template #default="{ row }">
            <div class="main-cell">
              <div class="main-cell__title">{{ row.title || '-' }}</div>
              <div class="main-cell__sub">
                <span>账号手机号：{{ row.mobile || '-' }}</span>
                <span>联系手机号：{{ row.contactMobile || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="劳动者" width="140">
          <template #default="{ row }">
            <div class="main-cell">
              <div class="main-cell__title">{{ row.personName || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="反馈状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="反馈内容" prop="content" min-width="260" show-overflow-tooltip />
        <el-table-column label="提交时间" prop="createTime" width="168" />
        <el-table-column label="最近更新" prop="updateTime" width="168" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog v-model="detailOpen" title="反馈详情" width="900px" append-to-body>
      <template v-if="detail">
        <div class="detail-banner">
          <div>
            <div class="detail-banner__title">{{ detail.title || '-' }}</div>
            <div class="detail-banner__sub">
              劳动者：{{ detail.personName || '-' }} | 账号手机号：{{ detail.mobile || '-' }}
            </div>
          </div>
          <div class="detail-banner__tags">
            <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </div>
        </div>

        <el-row :gutter="16">
          <el-col :span="16">
            <el-card shadow="never" class="detail-section">
              <template #header>
                <div class="detail-section__title">反馈信息</div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="反馈ID">{{ detail.feedbackId }}</el-descriptions-item>
                <el-descriptions-item label="反馈状态">{{ statusLabel(detail.status) }}</el-descriptions-item>
                <el-descriptions-item label="反馈标题" :span="2">{{ detail.title || '-' }}</el-descriptions-item>
                <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="账号手机号">{{ detail.mobile || '-' }}</el-descriptions-item>
                <el-descriptions-item label="联系手机号">{{ detail.contactMobile || '-' }}</el-descriptions-item>
                <el-descriptions-item label="提交时间">{{ detail.createTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="最近更新">{{ detail.updateTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="反馈内容" :span="2">{{ detail.content || '-' }}</el-descriptions-item>
                <el-descriptions-item label="备注信息" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never" class="detail-section">
              <template #header>
                <div class="detail-section__title">处理跟进</div>
              </template>
              <div class="handle-panel">
                <div class="handle-panel__item">
                  <div class="handle-panel__label">当前状态</div>
                  <div class="handle-panel__value">
                    <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
                  </div>
                </div>
                <div class="handle-panel__item">
                  <div class="handle-panel__label">最近处理记录</div>
                  <div class="handle-panel__value">{{ detail.remark || '暂无处理记录' }}</div>
                </div>
                <el-form label-position="top" class="handle-form">
                  <el-form-item label="处理意见">
                    <el-input
                      v-model="handleForm.replyContent"
                      type="textarea"
                      :rows="5"
                      maxlength="300"
                      show-word-limit
                      placeholder="请输入核实结果、处理方式或重新跟进原因"
                    />
                  </el-form-item>
                </el-form>
                <div class="handle-panel__actions">
                  <el-button
                    type="success"
                    icon="Check"
                    :loading="handleLoading"
                    :disabled="detail.status === '1'"
                    @click="submitFeedbackHandle('1')"
                    v-hasPermi="['ygb:workerMessage:flow']"
                  >
                    标记已处理
                  </el-button>
                  <el-button
                    icon="RefreshLeft"
                    :loading="handleLoading"
                    :disabled="detail.status === '0'"
                    @click="submitFeedbackHandle('0')"
                    v-hasPermi="['ygb:workerMessage:flow']"
                  >
                    重新打开
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbWorkerFeedback">
import { computed } from 'vue'
import { getWorkerFeedback, listWorkerFeedback, updateWorkerFeedbackStatus } from '@/api/ygb/workerMessage'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const feedbackList = ref([])
const detail = ref(null)
const detailOpen = ref(false)
const handleLoading = ref(false)
const handleForm = reactive({
  replyContent: ''
})

const statusOptions = [
  { label: '待处理', value: '0' },
  { label: '已处理', value: '1' }
]

const statusFilters = [
  { label: '全部', value: undefined, type: 'info' },
  { label: '待处理', value: '0', type: 'danger' },
  { label: '已处理', value: '1', type: 'success' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    personName: undefined,
    mobile: undefined,
    status: undefined
  }
})

const { queryParams } = toRefs(data)

const overviewCards = computed(() => {
  const rows = feedbackList.value || []
  const pendingCount = rows.filter(item => item.status === '0').length
  const completedCount = rows.filter(item => item.status === '1').length
  const mobileMatchedCount = rows.filter(item => item.contactMobile || item.mobile).length
  return [
    { key: 'all', label: '筛选结果', value: total.value, hint: '当前条件下的反馈记录', tone: 'blue' },
    { key: 'pending', label: '待处理', value: pendingCount, hint: '仍处于待处理状态的反馈', tone: 'red' },
    { key: 'completed', label: '已处理', value: completedCount, hint: '状态已标记为已处理的反馈', tone: 'green' },
    { key: 'mobile', label: '留有联系方式', value: mobileMatchedCount, hint: '可用于后续联系核实的反馈', tone: 'slate' }
  ]
})

function getList() {
  loading.value = true
  listWorkerFeedback(queryParams.value).then(response => {
    feedbackList.value = (response.rows || []).map(item => ({
      ...item,
      feedbackId: resolveFeedbackId(item)
    }))
    total.value = response.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleStatusFilter(undefined)
}

function handleStatusFilter(status) {
  queryParams.value.status = status
  handleQuery()
}

function resolveFeedbackId(row = {}) {
  return row.feedbackId || row.id
}

function handleDetail(row) {
  const feedbackId = resolveFeedbackId(row)
  if (!feedbackId) {
    proxy.$modal.msgWarning('当前反馈记录缺少有效ID，无法打开详情')
    return
  }
  getWorkerFeedback(feedbackId).then(response => {
    detail.value = response.data
    handleForm.replyContent = ''
    detailOpen.value = true
  })
}

function submitFeedbackHandle(status) {
  if (!detail.value?.feedbackId) {
    proxy.$modal.msgWarning('当前反馈记录缺少有效ID，无法处理')
    return
  }
  const replyContent = (handleForm.replyContent || '').trim()
  if (status === '1' && !replyContent) {
    proxy.$modal.msgWarning('标记已处理前请填写处理意见')
    return
  }
  handleLoading.value = true
  updateWorkerFeedbackStatus(detail.value.feedbackId, {
    status,
    replyContent
  }).then(() => {
    proxy.$modal.msgSuccess(status === '1' ? '反馈已标记为已处理' : '反馈已重新打开')
    return getWorkerFeedback(detail.value.feedbackId)
  }).then(response => {
    detail.value = response.data
    handleForm.replyContent = ''
    getList()
  }).finally(() => {
    handleLoading.value = false
  })
}

function statusLabel(value) {
  return statusOptions.find(item => item.value === value)?.label || value || '-'
}

function statusTagType(value) {
  return value === '1' ? 'success' : 'danger'
}

function tableRowClassName({ row }) {
  if (row.status === '0') return 'row-pending'
  return ''
}

getList()
</script>

<style scoped>
.page-shell {
  background: #f5f7fb;
}

.hero-card,
.panel-card {
  border: none;
  border-radius: 16px;
}

.hero-card {
  margin-bottom: 16px;
  background: linear-gradient(135deg, #5a3f13 0%, #a97a1f 58%, #f0d28c 100%);
}

.hero-card__body {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: center;
  color: #fff;
}

.hero-card__eyebrow {
  margin-bottom: 10px;
  font-size: 13px;
  letter-spacing: 1px;
  opacity: 0.85;
}

.hero-card__title {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.hero-card__desc {
  margin-top: 12px;
  max-width: 620px;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.92;
}

.hero-card__meta {
  min-width: 180px;
  padding: 20px 24px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(6px);
}

.hero-card__meta-label {
  font-size: 13px;
  opacity: 0.88;
}

.hero-card__meta-value {
  margin-top: 10px;
  font-size: 34px;
  font-weight: 700;
}

.search-card {
  margin-bottom: 16px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.overview-card {
  padding: 18px 20px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.overview-card__label {
  font-size: 13px;
  color: #6b7280;
}

.overview-card__value {
  margin-top: 14px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.overview-card__hint {
  margin-top: 12px;
  font-size: 12px;
  line-height: 1.6;
  color: #6b7280;
}

.overview-card--blue {
  border-top: 4px solid #2e86de;
}

.overview-card--red {
  border-top: 4px solid #dc2626;
}

.overview-card--green {
  border-top: 4px solid #16a34a;
}

.overview-card--slate {
  border-top: 4px solid #64748b;
}

.table-card__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.table-card__tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.table-card__tag {
  cursor: pointer;
}

.main-cell__title {
  font-weight: 600;
  color: #1f2937;
  line-height: 1.5;
}

.main-cell__sub {
  margin-top: 6px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}

.detail-banner {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  padding: 18px 20px;
  border-radius: 14px;
  background: linear-gradient(135deg, #fff9eb 0%, #fffdf6 100%);
}

.detail-banner__title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
}

.detail-banner__sub {
  margin-top: 8px;
  font-size: 13px;
  color: #64748b;
}

.detail-banner__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-section {
  height: 100%;
}

.detail-section__title {
  font-weight: 600;
  color: #1f2937;
}

.handle-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.handle-panel__item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
}

.handle-panel__label {
  font-size: 12px;
  color: #64748b;
}

.handle-panel__value {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.7;
  color: #1f2937;
}

.handle-form {
  padding: 14px 16px 2px;
  border-radius: 12px;
  background: #f8fafc;
}

.handle-panel__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

:deep(.el-table .row-pending) {
  --el-table-tr-bg-color: #fff8eb;
}

@media (max-width: 1200px) {
  .overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-card__body,
  .table-card__header,
  .detail-banner {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-card__meta {
    width: 100%;
  }

  .overview-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
