<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '合同详情' }}</view>
      <view class="worker-subtitle">{{ detail.contractStatusText || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">合同信息</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">合同编号</view>
        <view class="detail-row__value">{{ detail.contractNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">合同类型</view>
        <view class="detail-row__value">{{ detail.contractTypeText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">派遣单位</view>
        <view class="detail-row__value">{{ detail.dispatchEnterpriseName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">用工单位</view>
        <view class="detail-row__value">{{ detail.employerEnterpriseName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">签订日期</view>
        <view class="detail-row__value">{{ detail.signDate || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">合同期限</view>
        <view class="detail-row__value">{{ detail.period || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">月工资标准</view>
        <view class="detail-row__value">{{ detail.monthlyWage || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">备案编号</view>
        <view class="detail-row__value">{{ detail.filingNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">OCR 状态</view>
        <view class="detail-row__value">{{ detail.ocrStatusText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">条款校验</view>
        <view class="detail-row__value">{{ detail.clauseCheckStatusText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">区块链哈希</view>
        <view class="detail-row__value detail-row__value--break">{{ detail.blockchainHash || '-' }}</view>
      </view>
      <view class="worker-subtitle contract-hint">{{ detail.viewTip || '' }}</view>
      <view class="contract-actions">
        <button class="worker-button" :disabled="!detail.contractFileUrl" @click="openOriginalFile">
          {{ detail.contractFileUrl ? '查看电子版原件' : '原件暂未同步' }}
        </button>
        <button class="worker-button worker-button--secondary" @click="openPreview">
          查看合同摘要
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getLaborContractDetail } from '../../api/enterprise-service'

const detail = ref({})
const contractId = ref()
const contractDetailLastLoadedAt = ref('')
const contractDetailLastActionAt = ref('')
const contractDetailLastMessage = ref('')

const contractDetailSummaryText = computed(() => {
  return `${detail.value.contractNo || '-'} / ${detail.value.contractTypeText || '-'} / ${detail.value.contractStatusText || '-'}`
})
const contractActionSummaryText = computed(() => {
  return `${detail.value.contractFileUrl ? '原件可看' : '原件未同步'} / 摘要可看`
})
const contractDetailSnapshotText = computed(() => {
  return [
    '## 合同详情验收摘要',
    `- 最近加载：${contractDetailLastLoadedAt.value || '-'}`,
    `- 最近动作：${contractDetailLastActionAt.value || '-'}`,
    `- 合同摘要：${contractDetailSummaryText.value}`,
    `- 原件与摘要：${contractActionSummaryText.value}`,
    `- 说明：${contractDetailLastMessage.value || '-'}`,
    '- 链路关联：我的劳动合同 / 合同详情 / 合同摘要预览'
  ].join('\n')
})

async function loadData() {
  try {
    detail.value = await getLaborContractDetail(contractId.value)
    contractDetailLastLoadedAt.value = new Date().toLocaleString()
    contractDetailLastMessage.value = '合同详情已加载，可核对原件、摘要和校验状态'
  } catch (error) {
    contractDetailLastLoadedAt.value = new Date().toLocaleString()
    contractDetailLastMessage.value = error.message || '加载合同详情失败'
    uni.showToast({ title: error.message || '加载合同详情失败', icon: 'none' })
  }
}

function openOriginalFile() {
  const fileUrl = detail.value?.contractFileUrl
  if (!fileUrl) {
    return
  }
  contractDetailLastActionAt.value = new Date().toLocaleString()
  contractDetailLastMessage.value = '已尝试打开合同电子版原件'
  // #ifdef H5
  window.open(fileUrl, '_blank')
  // #endif
  // #ifndef H5
  uni.downloadFile({
    url: fileUrl,
    success: (res) => {
      if (res.statusCode === 200) {
        uni.openDocument({
          filePath: res.tempFilePath,
          showMenu: true,
          fail: () => {
            uni.setClipboardData({ data: fileUrl })
          }
        })
        return
      }
      uni.showToast({ title: '打开电子版失败', icon: 'none' })
    },
    fail: () => {
      uni.setClipboardData({ data: fileUrl })
    }
  })
  // #endif
}

function openPreview() {
  if (!contractId.value) {
    return
  }
  contractDetailLastActionAt.value = new Date().toLocaleString()
  contractDetailLastMessage.value = '已打开合同摘要预览'
  uni.navigateTo({ url: `/pages/profile/labor-contract-preview?contractId=${contractId.value}` })
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
  contractId.value = options?.contractId
  loadData()
})
</script>

<style lang="scss">
.detail-row:last-of-type {
  border-bottom: none;
}

.detail-row__value--break {
  word-break: break-all;
}

.contract-hint {
  margin: 20rpx 0 24rpx;
}

.contract-actions {
  display: flex;
  gap: 18rpx;
}

.contract-actions button {
  flex: 1;
}
</style>
