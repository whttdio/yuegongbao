<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.taxMonth || '-' }} 个税详情</view>
      <view class="detail-grid">
        <view class="detail-item">
          <view class="detail-item__label">所得类型</view>
          <view class="detail-item__value">{{ detail.incomeType || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">申报收入</view>
          <view class="detail-item__value">{{ detail.incomeAmount ?? '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">工资实发</view>
          <view class="detail-item__value">{{ detail.salaryAmount ?? '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">实缴个税</view>
          <view class="detail-item__value">{{ detail.taxAmount ?? '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">比对结果</view>
          <view class="detail-item__value">{{ detail.compareResultText || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">预警状态</view>
          <view class="detail-item__value">{{ detail.warningStatusText || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">差异</view>
          <view class="detail-item__value">{{ detail.diffRatio || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">累计申报收入</view>
          <view class="detail-item__value detail-item__value--small">{{ detail.cumulativeIncome || 0 }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">累计工资实发</view>
          <view class="detail-item__value detail-item__value--small">{{ detail.cumulativeSalary ?? '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">累计实缴个税</view>
          <view class="detail-item__value detail-item__value--small">{{ detail.cumulativeTax ?? '-' }}</view>
        </view>
      </view>
      <view class="worker-subtitle tax-hint">
        {{ detail.amountHint || detail.taxAmountText || detail.cumulativeTaxText || '' }}
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">申报来源</view>
      <view class="detail-row">
        <view class="detail-row__label">来源流水</view>
        <view class="detail-row__value">{{ detail.sourceSerialNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">来源状态</view>
        <view class="detail-row__value">{{ detail.sourceStatusText || detail.sourceStatus || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">回调时间</view>
        <view class="detail-row__value">{{ detail.callbackTime || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">备注说明</view>
      <view class="detail-block">{{ detail.remark || '本月暂无额外说明。' }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getTaxDetail } from '../../api/worker'

const detail = ref({})
const taxDetailLastLoadedAt = ref('')
const taxDetailLastMessage = ref('')

const taxSummaryText = computed(() => {
  return `${detail.value.taxMonth || '-'} / ${detail.value.compareResultText || '-'} / 预警 ${detail.value.warningStatusText || '-'}`
})
const taxCumulativeSummaryText = computed(() => {
  return `收入 ${detail.value.cumulativeIncome || 0} / 工资 ${detail.value.cumulativeSalary ?? '-'} / 个税 ${detail.value.cumulativeTax ?? '-'}`
})
const taxSourceSummaryText = computed(() => {
  return `${detail.value.sourceSerialNo || '-'} / ${detail.value.sourceStatusText || detail.value.sourceStatus || '-'} / ${detail.value.callbackTime || '-'}`
})
const taxDetailConsistencyText = computed(() => {
  return `${detail.value.compareResultText || '-'} / 预警 ${detail.value.warningStatusText || '-'} / 差异 ${detail.value.diffRatio || 0}`
})
const taxDetailSnapshotText = computed(() => {
  return [
    '## 个税详情验收摘要',
    `- 最近加载：${taxDetailLastLoadedAt.value || '-'}`,
    `- 个税摘要：${taxSummaryText.value}`,
    `- 累计摘要：${taxCumulativeSummaryText.value}`,
    `- 来源摘要：${taxSourceSummaryText.value}`,
    `- 详情核对：${taxDetailConsistencyText.value}`,
    `- 说明：${taxDetailLastMessage.value || '-'}`,
    '- 链路关联：个税列表 / 个税详情 / 工资税务比对'
  ].join('\n')
})

async function loadData(month) {
  if (!month) {
    uni.showToast({ title: '缺少个税月份', icon: 'none' })
    return
  }
  try {
    detail.value = await getTaxDetail(month)
    taxDetailLastLoadedAt.value = new Date().toLocaleString()
    taxDetailLastMessage.value = '个税详情已加载，可核对比对结果、预警状态和累计数据'
  } catch (error) {
    taxDetailLastLoadedAt.value = new Date().toLocaleString()
    taxDetailLastMessage.value = error.message || '加载个税详情失败'
    uni.showToast({ title: error.message || '加载个税详情失败', icon: 'none' })
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

.tax-hint {
  margin-top: 8rpx;
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
