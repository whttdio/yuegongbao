<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">集体合同查阅</view>
      <view class="worker-subtitle">劳动者可在此查看当前可公开的集体合同与协商备忘。</view>
      <view v-if="contracts.length">
        <view
          v-for="item in contracts"
          :key="item.contractKey"
          class="list-row"
          @click="openContract(item)"
        >
          <view class="list-row__title">{{ item.title }}</view>
          <view class="list-row__meta">{{ item.enterpriseName }}</view>
          <view class="list-row__meta">{{ item.period }}</view>
          <view class="list-row__subtitle">{{ item.summary }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可查阅合同</view>
        <view class="worker-empty__desc">如集体合同暂未同步，可先查看我的劳动合同摘要，或进入工会服务页继续关注合同协同与通知更新。</view>
        <view class="contract-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goLaborContracts">我的合同</button>
          <button class="worker-button" @click="goUnionHome">工会服务</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLaborContractList, getUnionContracts } from '../../api/worker'

const EXPECTED_UNION_CONTRACT_CHAIN_PAGES = ['工会服务', '集体合同', '我的劳动合同']
const contracts = ref([])
const laborContracts = ref([])
const contractLastLoadedAt = ref('')
const contractLastActionAt = ref('')
const contractLastMessage = ref('')
const contractChainCoverageText = computed(() => EXPECTED_UNION_CONTRACT_CHAIN_PAGES.join(' / '))
const enterpriseSummaryText = computed(() => {
  const names = new Set(
    contracts.value
      .map((item) => String(item?.enterpriseName || '').trim())
      .filter(Boolean)
  )
  return `${names.size} 家`
})
const laborContractSummaryText = computed(() => `${laborContracts.value.length} 份`)
const contractConsistencyText = computed(() => {
  if (!contracts.value.length && !laborContracts.value.length) {
    return '公开集体合同与个人劳动合同均为空，需结合真实接口复核'
  }
  return `公开合同 ${contracts.value.length} 份 / 个人合同 ${laborContracts.value.length} 份`
})
const contractSnapshotText = computed(() => {
  return [
    '## 集体合同验收摘要',
    `- 链路核对：${contractChainCoverageText.value}`,
    `- 最近联动：${contractLastActionAt.value || '-'}`,
    `- 最近加载：${contractLastLoadedAt.value || '-'}`,
    `- 集体合同数：${contracts.value.length} 份`,
    `- 覆盖企业数：${enterpriseSummaryText.value}`,
    `- 我的劳动合同：${laborContractSummaryText.value}`,
    '- 口径说明：当前页为公开集体合同，个人劳动合同需进入“我的劳动合同”单独查看',
    `- 双口径核对：${contractConsistencyText.value}`,
    `- 说明：${contractLastMessage.value || '-'}`
  ].join('\n')
})

async function loadData() {
  try {
    const [contractResult, laborResult] = await Promise.allSettled([
      getUnionContracts(),
      getLaborContractList()
    ])
    if (contractResult.status !== 'fulfilled') {
      throw contractResult.reason
    }
    contracts.value = contractResult.value?.rows || []
    laborContracts.value = laborResult.status === 'fulfilled' ? laborResult.value?.rows || [] : []
    contractLastLoadedAt.value = new Date().toLocaleString()
    const messageParts = []
    if (!contracts.value.length) {
      messageParts.push('当前暂无集体合同数据')
    }
    if (laborResult.status !== 'fulfilled') {
      messageParts.push(`我的劳动合同快照异常：${laborResult.reason?.message || '请复核接口'}`)
    }
    if (!messageParts.length) {
      messageParts.push('集体合同与个人劳动合同快照已同步加载，可核对双口径差异')
    }
    contractLastMessage.value = messageParts.join('；')
  } catch (error) {
    contractLastLoadedAt.value = new Date().toLocaleString()
    contracts.value = []
    laborContracts.value = []
    contractLastMessage.value = error.message || '加载集体合同失败'
    uni.showToast({ title: error.message || '加载集体合同失败', icon: 'none' })
  }
}

function openContract(item) {
  contractLastActionAt.value = new Date().toLocaleString()
  contractLastMessage.value = `已打开集体合同详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/union/contract-detail?contractKey=${item.contractKey}` })
}

function goLaborContracts() {
  contractLastActionAt.value = new Date().toLocaleString()
  contractLastMessage.value = '已前往我的劳动合同页'
  uni.navigateTo({ url: '/pages/profile/labor-contracts' })
}

function goUnionHome() {
  contractLastActionAt.value = new Date().toLocaleString()
  contractLastMessage.value = '已返回工会服务首页'
  uni.navigateTo({ url: '/pages/union/index' })
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

onShow(loadData)
</script>

<style lang="scss">
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.list-row {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.list-row__meta {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #5f7893;
}

.list-row__subtitle {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #7890aa;
  line-height: 1.6;
}

.contract-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.contract-empty-actions button {
  flex: 1;
}

.worker-title--small {
  font-size: 28rpx;
}

.section-head--sub {
  margin-top: 20rpx;
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

.result-block {
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 22rpx;
  color: #7890aa;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #16324f;
  white-space: pre-wrap;
  word-break: break-all;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}
</style>
