<template>
  <!-- ??????????????? /app/screen/** -->
  <view class="worker-page worker-page--screen-detail">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title">{{ detail.insuredMonth || '-' }} 社保详情</view>
        <view class="worker-tag">{{ detail.statusText || '-' }}</view>
      </view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">险种明细</view>
        <view class="worker-tag">{{ items.length }} 项</view>
      </view>
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
      <view v-else class="worker-empty">本月暂无险种明细</view>
      <view v-if="detail.amountHint" class="worker-subtitle social-hint">{{ detail.amountHint }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">来源信息</view>
      </view>
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
      <view class="section-head">
        <view class="worker-title">备注说明</view>
      </view>
      <view class="detail-block">{{ detail.remark || '本月暂无额外说明。' }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getSocialSecurityDetail } from '../../api/screen'

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
.social-hint {
  margin-top: 18rpx;
}
</style>
