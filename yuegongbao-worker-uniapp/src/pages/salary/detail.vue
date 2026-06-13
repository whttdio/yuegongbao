<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.salaryMonth || '-' }} 工资详情</view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '-' }}</view>
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
      <view class="worker-subtitle">状态：{{ detail.payStatusText || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">发放信息</view>
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
      <view class="worker-title">备注说明</view>
      <view class="detail-block">
        {{ detail.failReason || detail.remark || '本月暂无额外备注。' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getSalaryDetail } from '../../api/worker'

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

<style lang="scss">
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin: 24rpx 0;
}

.detail-item {
  padding: 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.detail-item__label {
  font-size: 22rpx;
  color: #7890aa;
}

.detail-item__value {
  margin-top: 10rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #16324f;
}

.detail-item__value--small {
  font-size: 22rpx;
  line-height: 1.6;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row__label {
  font-size: 26rpx;
  color: #5f7893;
}

.detail-row__value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #16324f;
  line-height: 1.6;
  word-break: break-all;
}

.deduction-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.deduction-row:last-child {
  border-bottom: none;
}

.deduction-row__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.deduction-row__desc {
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7890aa;
}

.detail-block {
  margin-top: 20rpx;
  padding: 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  font-size: 28rpx;
  color: #16324f;
  line-height: 1.7;
}

.result-block {
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 24rpx;
  color: #5f7893;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.8;
  color: #36506b;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
