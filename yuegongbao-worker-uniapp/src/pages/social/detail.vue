<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.insuredMonth || '-' }} 社保详情</view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '-' }}</view>
      <view v-if="items.length">
        <view v-for="item in items" :key="item.itemName" class="list-row">
          <view>
            <view class="list-row__title">{{ item.itemName }}</view>
            <view class="list-row__subtitle">缴费基数 {{ item.baseAmount ?? '-' }}</view>
            <view class="list-row__subtitle">个人缴费 {{ item.personalAmount ?? '-' }}</view>
            <view class="list-row__subtitle">单位缴费 {{ item.companyAmount ?? '-' }}</view>
            <view class="list-row__subtitle">{{ item.personalAmountText || '来源已提供拆分金额' }}</view>
          </view>
          <view class="worker-tag">总额 {{ item.paidAmount ?? '-' }}</view>
        </view>
      </view>
      <view class="worker-subtitle">状态：{{ detail.statusText || '-' }}</view>
      <view class="worker-subtitle social-hint">{{ detail.amountHint || '' }}</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">来源信息</view>
      <view class="detail-row">
        <view class="detail-row__label">来源流水</view>
        <view class="detail-row__value">{{ detail.sourceSerialNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">来源状态</view>
        <view class="detail-row__value">{{ detail.sourceStatusText || '-' }}</view>
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
import { getSocialSecurityDetail } from '../../api/worker'

const detail = ref({})
const socialDetailLastLoadedAt = ref('')
const socialDetailLastMessage = ref('')

const items = computed(() => detail.value.insuranceItems || [])
const socialSummaryText = computed(() => {
  return `${detail.value.insuredMonth || '-'} / ${detail.value.statusText || '-'} / ${items.value.length} 项险种`
})
const socialSourceSummaryText = computed(() => {
  return `${detail.value.sourceSerialNo || '-'} / ${detail.value.sourceStatusText || '-'} / ${detail.value.callbackTime || '-'}`
})
const socialDetailConsistencyText = computed(() => {
  if (!items.value.length) {
    return '明细险种为空，需结合列表摘要和真实接口复核'
  }
  return `${detail.value.statusText || '-'} / 险种 ${items.value.length} 项 / 来源 ${detail.value.sourceStatusText || '-'}`
})
const socialDetailSnapshotText = computed(() => {
  return [
    '## 社保详情验收摘要',
    `- 最近加载：${socialDetailLastLoadedAt.value || '-'}`,
    `- 社保摘要：${socialSummaryText.value}`,
    `- 险种数量：${items.value.length} 项`,
    `- 来源摘要：${socialSourceSummaryText.value}`,
    `- 详情核对：${socialDetailConsistencyText.value}`,
    `- 说明：${socialDetailLastMessage.value || '-'}`,
    '- 链路关联：社保列表 / 社保详情 / 列表摘要一致性'
  ].join('\n')
})

async function loadData(month) {
  if (!month) {
    uni.showToast({ title: '缺少社保月份', icon: 'none' })
    return
  }
  try {
    detail.value = await getSocialSecurityDetail(month)
    socialDetailLastLoadedAt.value = new Date().toLocaleString()
    socialDetailLastMessage.value = '社保详情已加载，可核对险种明细和来源信息'
  } catch (error) {
    socialDetailLastLoadedAt.value = new Date().toLocaleString()
    socialDetailLastMessage.value = error.message || '加载社保详情失败'
    uni.showToast({ title: error.message || '加载社保详情失败', icon: 'none' })
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

.list-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 30rpx;
  font-weight: 600;
  color: #16324f;
}

.list-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
}

.social-hint {
  margin-top: 18rpx;
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
