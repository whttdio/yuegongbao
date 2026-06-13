<template>
  <div class="app-container page-shell">
    <el-card shadow="never" class="hero-card">
      <div class="hero-card__body">
        <div>
          <div class="hero-card__eyebrow">用工宝 / 劳动者服务</div>
          <div class="hero-card__title">活动参与处理</div>
          <div class="hero-card__desc">
            查看活动报名、开奖和发放状态，支持直接完成状态处理与记录回写。
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
        <el-form-item label="活动标识" prop="activityKey">
          <el-input
            v-model="queryParams.activityKey"
            placeholder="请输入活动标识"
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
        <el-form-item label="手机号" prop="mobile">
          <el-input
            v-model="queryParams.mobile"
            placeholder="请输入手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
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
        :data="activityList"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="参与ID" prop="joinId" width="90" align="center" />
        <el-table-column label="活动信息" min-width="250">
          <template #default="{ row }">
            <div class="main-cell">
              <div class="main-cell__title">{{ row.activityTitle || '-' }}</div>
              <div class="main-cell__sub">
                <span>标识：{{ row.activityKey || '-' }}</span>
                <span>订单号：{{ row.orderNo || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="劳动者" min-width="180">
          <template #default="{ row }">
            <div class="main-cell">
              <div class="main-cell__title">{{ row.personName || '-' }}</div>
              <div class="main-cell__sub">
                <span>手机号：{{ row.mobile || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="处理状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开奖状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="lotteryTagType(row.lotteryStatus)">
              {{ row.lotteryStatusText || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发放状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="deliveryTagType(row.deliveryStatus)">
              {{ row.deliveryStatusText || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="奖励信息" prop="rewardTitle" min-width="180" show-overflow-tooltip />
        <el-table-column label="参与时间" prop="joinTime" width="168" />
        <el-table-column label="最近更新" prop="updateTime" width="168" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button
              link
              type="primary"
              icon="Edit"
              v-hasPermi="['ygb:workerActivity:handle']"
              @click="handleProcess(row)"
            >
              处理
            </el-button>
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

    <el-dialog v-model="detailOpen" title="活动参与详情" width="920px" append-to-body>
      <template v-if="detail">
        <div class="detail-banner">
          <div>
            <div class="detail-banner__title">{{ detail.activityTitle || '-' }}</div>
            <div class="detail-banner__sub">
              订单号：{{ detail.orderNo || '-' }} | 劳动者：{{ detail.personName || '-' }}
            </div>
          </div>
          <div class="detail-banner__tags">
            <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
            <el-tag :type="lotteryTagType(detail.lotteryStatus)">{{ detail.lotteryStatusText || '-' }}</el-tag>
            <el-tag :type="deliveryTagType(detail.deliveryStatus)">{{ detail.deliveryStatusText || '-' }}</el-tag>
          </div>
        </div>

        <el-row :gutter="16">
          <el-col :span="14">
            <el-card shadow="never" class="detail-section">
              <template #header>
                <div class="detail-section__title">基础信息</div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="参与ID">{{ detail.joinId }}</el-descriptions-item>
                <el-descriptions-item label="活动标识">{{ detail.activityKey || '-' }}</el-descriptions-item>
                <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ detail.mobile || '-' }}</el-descriptions-item>
                <el-descriptions-item label="订单号">{{ detail.orderNo || '-' }}</el-descriptions-item>
                <el-descriptions-item label="参与渠道">{{ detail.paymentChannel || '-' }}</el-descriptions-item>
                <el-descriptions-item label="资格时间">{{ detail.qualifiedTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="参与时间">{{ detail.joinTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="开奖时间">{{ detail.lotteryTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="发放时间">{{ detail.deliveryTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="奖励信息" :span="2">{{ detail.rewardTitle || '-' }}</el-descriptions-item>
                <el-descriptions-item label="资格说明" :span="2">{{ detail.eligibilityText || '-' }}</el-descriptions-item>
                <el-descriptions-item label="发放说明" :span="2">{{ detail.deliveryText || '-' }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          <el-col :span="10">
            <el-card shadow="never" class="detail-section">
              <template #header>
                <div class="detail-section__title">状态时间线</div>
              </template>
              <el-timeline v-if="statusTimeline.length">
                <el-timeline-item
                  v-for="(item, index) in statusTimeline"
                  :key="`${index}-${item.title || item.label || 'timeline'}`"
                  :timestamp="item.time || item.timestamp || ''"
                >
                  <div class="timeline-item-title">{{ item.title || item.label || '-' }}</div>
                  <div class="timeline-item-desc">{{ item.desc || item.description || item.statusText || '-' }}</div>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-else description="暂无状态时间线" :image-size="88" />
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-dialog>

    <el-dialog v-model="processOpen" title="处理活动状态" width="620px" append-to-body>
      <el-alert
        class="process-alert"
        type="info"
        :closable="false"
        show-icon
        :title="processRuleText"
      />
      <el-form ref="processRef" :model="processForm" :rules="rules" label-width="100px">
        <el-form-item label="开奖状态" prop="lotteryStatus">
          <el-select
            v-model="processForm.lotteryStatus"
            style="width: 100%"
            @change="handleLotteryStatusChange"
          >
            <el-option
              v-for="item in lotteryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发放状态" prop="deliveryStatus">
          <el-select
            v-model="processForm.deliveryStatus"
            style="width: 100%"
            @change="handleDeliveryStatusChange"
          >
            <el-option
              v-for="item in availableDeliveryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="奖励标题" prop="rewardTitle">
          <el-input v-model="processForm.rewardTitle" placeholder="请输入奖励标题" />
        </el-form-item>
        <el-form-item label="发放时间" prop="deliveryTimeText">
          <el-input v-model="processForm.deliveryTimeText" placeholder="例如 2026-06-09 15:30" />
        </el-form-item>
        <el-form-item label="状态说明" prop="statusMessage">
          <el-input
            v-model="processForm.statusMessage"
            type="textarea"
            :rows="4"
            placeholder="请输入处理说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitProcess">确定</el-button>
        <el-button @click="processOpen = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbWorkerActivity">
import { computed } from 'vue'
import { getWorkerActivity, listWorkerActivity, updateWorkerActivityStatus } from '@/api/ygb/workerActivity'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const activityList = ref([])
const detail = ref(null)
const detailOpen = ref(false)
const processOpen = ref(false)
const currentJoinId = ref(undefined)

const statusOptions = [
  { label: '待处理', value: '0' },
  { label: '已中奖', value: '1' },
  { label: '已发放', value: '2' },
  { label: '未中奖', value: '3' }
]

const statusFilters = [
  { label: '全部', value: undefined, type: 'info' },
  { label: '待处理', value: '0', type: 'warning' },
  { label: '已中奖', value: '1', type: 'success' },
  { label: '已发放', value: '2', type: 'success' },
  { label: '未中奖', value: '3', type: 'info' }
]

const lotteryOptions = [
  { label: '待开奖', value: 'PENDING' },
  { label: '已中奖', value: 'WON' },
  { label: '未中奖', value: 'LOST' }
]

const deliveryOptions = [
  { label: '待发放', value: 'PENDING' },
  { label: '已发放', value: 'DELIVERED' },
  { label: '发放失败', value: 'FAILED' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    activityKey: undefined,
    personName: undefined,
    mobile: undefined,
    status: undefined
  },
  processForm: {
    lotteryStatus: 'PENDING',
    deliveryStatus: 'PENDING',
    rewardTitle: '',
    deliveryTimeText: '',
    statusMessage: ''
  },
  rules: {
    lotteryStatus: [{ required: true, message: '开奖状态不能为空', trigger: 'change' }],
    deliveryStatus: [{ required: true, message: '发放状态不能为空', trigger: 'change' }],
    statusMessage: [{ required: true, message: '状态说明不能为空', trigger: 'blur' }]
  }
})

const { queryParams, processForm, rules } = toRefs(data)

const statusTimeline = computed(() => detail.value?.statusTimeline || [])

const overviewCards = computed(() => {
  const rows = activityList.value || []
  const pendingCount = rows.filter(item => item.status === '0').length
  const wonPendingCount = rows.filter(item => item.lotteryStatus === 'WON' && item.deliveryStatus !== 'DELIVERED').length
  const deliveredCount = rows.filter(item => item.deliveryStatus === 'DELIVERED').length
  const failedCount = rows.filter(item => item.deliveryStatus === 'FAILED' || item.status === '3').length
  return [
    { key: 'all', label: '筛选结果', value: total.value, hint: '当前条件下的活动参与记录', tone: 'blue' },
    { key: 'pending', label: '待处理', value: pendingCount, hint: '待开奖或待发放记录', tone: 'amber' },
    { key: 'won', label: '中奖待发放', value: wonPendingCount, hint: '已中奖但尚未发放完成', tone: 'green' },
    { key: 'failed', label: '异常 / 未中奖', value: failedCount, hint: '发放失败或未中奖记录', tone: 'slate' }
  ]
})

const availableDeliveryOptions = computed(() => {
  if (processForm.value.lotteryStatus === 'PENDING') {
    return deliveryOptions.filter(item => item.value === 'PENDING')
  }
  if (processForm.value.lotteryStatus === 'LOST') {
    return deliveryOptions.filter(item => item.value !== 'DELIVERED')
  }
  return deliveryOptions
})

const processRuleText = computed(() => {
  if (processForm.value.lotteryStatus === 'PENDING') {
    return '待开奖记录只允许保持“待发放”，用于保留后续开奖处理空间。'
  }
  if (processForm.value.lotteryStatus === 'LOST') {
    return '未中奖记录不能标记为已发放，如需结束处理请保留“待发放”或改为“发放失败”。'
  }
  if (processForm.value.deliveryStatus === 'DELIVERED') {
    return '已发放会视为处理闭环，请确认奖励标题、发放时间和状态说明已经补齐。'
  }
  return '可以先更新开奖结果，再根据发放情况补录发放状态和说明。'
})

function getList() {
  loading.value = true
  listWorkerActivity(queryParams.value).then(response => {
    activityList.value = response.rows || []
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

function handleDetail(row) {
  getWorkerActivity(row.joinId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function handleProcess(row) {
  currentJoinId.value = row.joinId
  processForm.value = {
    lotteryStatus: row.lotteryStatus || 'PENDING',
    deliveryStatus: row.deliveryStatus || 'PENDING',
    rewardTitle: row.rewardTitle || '',
    deliveryTimeText: row.deliveryTime || '',
    statusMessage: row.deliveryText || ''
  }
  normalizeProcessForm()
  processOpen.value = true
  nextTick(() => {
    proxy.$refs.processRef?.clearValidate()
  })
}

function handleLotteryStatusChange() {
  normalizeProcessForm()
}

function handleDeliveryStatusChange() {
  if (processForm.value.deliveryStatus === 'DELIVERED' && processForm.value.lotteryStatus !== 'WON') {
    processForm.value.lotteryStatus = 'WON'
  }
  normalizeProcessForm()
}

function normalizeProcessForm() {
  if (processForm.value.lotteryStatus === 'PENDING') {
    processForm.value.deliveryStatus = 'PENDING'
    return
  }
  if (processForm.value.lotteryStatus === 'LOST' && processForm.value.deliveryStatus === 'DELIVERED') {
    processForm.value.deliveryStatus = 'FAILED'
  }
}

function submitProcess() {
  proxy.$refs.processRef.validate(valid => {
    if (!valid || !currentJoinId.value) return
    updateWorkerActivityStatus(currentJoinId.value, processForm.value).then(() => {
      proxy.$modal.msgSuccess('活动状态已更新')
      processOpen.value = false
      getList()
    })
  })
}

function statusLabel(value) {
  return statusOptions.find(item => item.value === value)?.label || value || '-'
}

function statusTagType(value) {
  if (value === '2') return 'success'
  if (value === '1') return 'warning'
  if (value === '3') return 'info'
  return 'danger'
}

function lotteryTagType(value) {
  if (value === 'WON') return 'success'
  if (value === 'LOST') return 'info'
  if (value === 'NOT_JOINED') return 'danger'
  return 'warning'
}

function deliveryTagType(value) {
  if (value === 'DELIVERED') return 'success'
  if (value === 'FAILED') return 'danger'
  if (value === 'NOT_JOINED') return 'info'
  return 'warning'
}

function tableRowClassName({ row }) {
  if (row.status === '0') return 'row-pending'
  if (row.deliveryStatus === 'FAILED') return 'row-failed'
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
  background: linear-gradient(135deg, #0f5aa6 0%, #2e86de 62%, #8ec5ff 100%);
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

.overview-card--amber {
  border-top: 4px solid #f59e0b;
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
  background: linear-gradient(135deg, #eff6ff 0%, #f8fbff 100%);
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

.timeline-item-title {
  font-weight: 600;
  color: #1f2937;
}

.timeline-item-desc {
  margin-top: 4px;
  line-height: 1.7;
  color: #64748b;
}

.process-alert {
  margin-bottom: 16px;
}

:deep(.el-table .row-pending) {
  --el-table-tr-bg-color: #fff8eb;
}

:deep(.el-table .row-failed) {
  --el-table-tr-bg-color: #fff2f0;
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
