<template>
  <!-- ??????????????? /app/screen/** -->
  <view class="worker-page worker-page--screen-detail">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title">{{ detail.salaryMonth || '-' }} 工资详情</view>
        <view class="worker-tag">{{ detail.payStatusText || '-' }}</view>
      </view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">金额明细</view>
      </view>
      <view class="detail-grid">
        <view class="detail-item">
          <view class="detail-item__label">应发工资</view>
          <view class="detail-item__value">{{ detail.shouldAmount || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">实发工资</view>
          <view class="detail-item__value">{{ detail.realAmount || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">扣减金额</view>
          <view class="detail-item__value">{{ detail.deductionAmount || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">出勤天数</view>
          <view class="detail-item__value">{{ detail.attendanceDays || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">累计工时</view>
          <view class="detail-item__value">{{ detail.totalHours || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">合同编号</view>
          <view class="detail-item__value detail-item__value--small">{{ detail.contractNo || '-' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">发放信息</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">工资批次</view>
        <view class="detail-row__value">{{ detail.batchNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">开户名</view>
        <view class="detail-row__value">{{ detail.bankAccountName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">银行卡号</view>
        <view class="detail-row__value">{{ detail.bankAccountNoMasked || detail.bankAccountNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">银行流水</view>
        <view class="detail-row__value">{{ detail.bankSerialNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">发放时间</view>
        <view class="detail-row__value">{{ detail.paidTime || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">扣款明细</view>
        <view class="worker-tag">{{ deductionItems.length }} 项</view>
      </view>
      <view v-if="deductionItems.length">
        <view v-for="(item, index) in deductionItems" :key="`${item.itemName}-${index}`" class="deduction-row">
          <view>
            <view class="deduction-row__title">{{ item.itemName || '-' }}</view>
            <view class="deduction-row__desc">{{ item.summary || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.amount || 0 }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">本月暂无扣款明细</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">备注说明</view>
      </view>
      <view class="detail-block">
        {{ detail.failReason || detail.remark || '本月暂无额外备注。' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getSalaryDetail } from '../../api/screen'

const detail = ref({})
const salaryDetailLastLoadedAt = ref('')
const salaryDetailLastMessage = ref('')

const deductionItems = computed(() => detail.value.deductionItems || [])
const salarySummaryText = computed(() => {
  return `${detail.value.salaryMonth || '-'} / 应发 ${detail.value.shouldAmount || 0} / 实发 ${detail.value.realAmount || 0} / ${detail.value.payStatusText || '-'}`
})
const salaryPaySummaryText = computed(() => {
  return `${detail.value.batchNo || '-'} / ${detail.value.paidTime || '-'} / ${detail.value.bankAccountNoMasked || detail.value.bankAccountNo || '-'}`
})
const salaryDetailSnapshotText = computed(() => {
  return [
    '## 工资详情验收摘要',
    `- 最近加载：${salaryDetailLastLoadedAt.value || '-'}`,
    `- 工资摘要：${salarySummaryText.value}`,
    `- 发放摘要：${salaryPaySummaryText.value}`,
    `- 扣款数量：${deductionItems.value.length} 项`,
    `- 说明：${salaryDetailLastMessage.value || '-'}`,
    '- 链路关联：工资列表 / 工资详情 / 培训解锁校验'
  ].join('\n')
})

async function loadData(month) {
  if (!month) {
    uni.showToast({ title: '缺少工资月份', icon: 'none' })
    return
  }
  try {
    detail.value = await getSalaryDetail(month)
    salaryDetailLastLoadedAt.value = new Date().toLocaleString()
    salaryDetailLastMessage.value = '工资详情已加载，可核对发放信息和扣款明细'
  } catch (error) {
    salaryDetailLastLoadedAt.value = new Date().toLocaleString()
    salaryDetailLastMessage.value = error.message || '加载工资详情失败'
    uni.showToast({ title: error.message || '加载工资详情失败', icon: 'none' })
  }
}

function copyText(content, successTitle) {
  if (!content) {
    uni.showToast({ title: '暂无可复制内容', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: content,
    success: () => uni.showToast({ title: successTitle, icon: 'none' }),
    fail: () => uni.showToast({ title: '复制失败，请改用截图', icon: 'none' })
  })
}

onLoad((options) => {
  loadData(options?.month)
})
</script>
