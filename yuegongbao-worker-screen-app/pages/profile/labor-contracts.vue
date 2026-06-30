<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">我的劳动合同</view>
      <view class="worker-subtitle">查看劳动合同电子版、备案状态和到期提醒</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ rows.length }}</view>
          <view class="hero-stat__label">合同数量</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ activeCount }}</view>
          <view class="hero-stat__label">生效中</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ expiringCount }}</view>
          <view class="hero-stat__label">即将到期</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">合同列表</view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.contractId" class="list-row" @click="openDetail(item)">
          <view class="list-row__title">{{ item.title }}</view>
          <view class="list-row__meta">{{ item.dispatchEnterpriseName || '-' }}</view>
          <view class="list-row__meta">{{ item.employerEnterpriseName || '-' }}</view>
          <view class="list-row__meta">{{ item.period || '-' }}</view>
          <view class="list-row__footer">
            <text class="worker-tag">{{ item.contractStatusText || '-' }}</text>
            <text class="list-row__expire">{{ item.expireText || '-' }}</text>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可查看的劳动合同</view>
        <view class="worker-empty__desc">
          如个人劳动合同暂未同步，可先查看工会公开集体合同，或返回工会服务页关注通知与协同进展。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="openUnionContracts">集体合同</button>
          <button class="worker-button" @click="openUnionHome">工会服务</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLaborContractList, getUnionContracts } from '../../api/screen'

const EXPECTED_LABOR_CONTRACT_CHAIN_PAGES = ['我的劳动合同', '劳动合同详情', '集体合同']
const rows = ref([])
const activeCount = computed(() => rows.value.filter((item) => String(item.contractStatusText || '').includes('生效')).length)
const expiringCount = computed(() => rows.value.filter((item) => String(item.expireText || '').includes('即将')).length)
const unionContracts = ref([])
const laborContractLastLoadedAt = ref('')
const laborContractLastActionAt = ref('')
const laborContractLastMessage = ref('')
const laborContractChainCoverageText = computed(() => EXPECTED_LABOR_CONTRACT_CHAIN_PAGES.join(' / '))
const contractStatusSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无劳动合同记录'
  }
  const statusMap = rows.value.reduce((result, item) => {
    const key = item?.contractStatusText || '未知状态'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(statusMap).map((key) => `${key} ${statusMap[key]} 份`).join(' / ')
})
const unionContractSummaryText = computed(() => `${unionContracts.value.length} 份`)
const laborContractConsistencyText = computed(() => {
  if (!rows.value.length && !unionContracts.value.length) {
    return '个人劳动合同与集体合同均为空，需结合真实接口复核'
  }
  return `个人合同 ${rows.value.length} 份 / 集体合同 ${unionContracts.value.length} 份`
})
const laborContractSnapshotText = computed(() => {
  return [
    '## 劳动合同验收摘要',
    `- 链路核对：${laborContractChainCoverageText.value}`,
    `- 最近联动：${laborContractLastActionAt.value || '-'}`,
    `- 最近加载：${laborContractLastLoadedAt.value || '-'}`,
    `- 劳动合同数：${rows.value.length} 份`,
    `- 状态分布：${contractStatusSummaryText.value}`,
    `- 集体合同快照：${unionContractSummaryText.value}`,
    '- 口径说明：当前页为本人劳动合同，工会页展示的是公开集体合同',
    `- 双口径核对：${laborContractConsistencyText.value}`,
    `- 说明：${laborContractLastMessage.value || '-'}`
  ].join('\n')
})

async function loadData() {
  try {
    const [laborResult, unionResult] = await Promise.allSettled([
      getLaborContractList(),
      getUnionContracts()
    ])
    if (laborResult.status !== 'fulfilled') {
      throw laborResult.reason
    }
    rows.value = laborResult.value?.rows || []
    unionContracts.value = unionResult.status === 'fulfilled' ? unionResult.value?.rows || [] : []
    laborContractLastLoadedAt.value = new Date().toLocaleString()
    const messageParts = []
    if (!rows.value.length) {
      messageParts.push('当前暂无个人劳动合同数据')
    }
    if (unionResult.status !== 'fulfilled') {
      messageParts.push(`集体合同快照异常：${unionResult.reason?.message || '请复核接口'}`)
    }
    if (!messageParts.length) {
      messageParts.push('个人劳动合同与集体合同快照已同步加载，可核对双口径差异')
    }
    laborContractLastMessage.value = messageParts.join('；')
  } catch (error) {
    laborContractLastLoadedAt.value = new Date().toLocaleString()
    rows.value = []
    unionContracts.value = []
    laborContractLastMessage.value = error.message || '加载劳动合同失败'
    uni.showToast({ title: error.message || '加载劳动合同失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.contractId) {
    return
  }
  laborContractLastActionAt.value = new Date().toLocaleString()
  laborContractLastMessage.value = `已打开劳动合同详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/profile/labor-contract-detail?contractId=${item.contractId}` })
}

function openUnionContracts() {
  laborContractLastActionAt.value = new Date().toLocaleString()
  laborContractLastMessage.value = '已前往集体合同页'
  uni.navigateTo({ url: '/pages/union/contracts' })
}

function openUnionHome() {
  laborContractLastActionAt.value = new Date().toLocaleString()
  laborContractLastMessage.value = '已返回工会服务首页'
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
.list-row__meta {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #607789;
}

.list-row__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12rpx;
}

.list-row__expire {
  font-size: 22rpx;
  color: #d66b1f;
}

.worker-empty__actions button {
  flex: 1;
}
</style>
