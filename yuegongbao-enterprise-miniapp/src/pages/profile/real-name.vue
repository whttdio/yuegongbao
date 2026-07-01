<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">实名认证协同</view>
      <view class="worker-subtitle">{{ detail.statusHint || '企业端仅查看实名状态和资料完整度，不代员工提交实名申请。' }}</view>
      <view class="status-panel">
        <view class="status-panel__title">{{ detail.applyStatusText || detail.realNameStatusText || '未提交申请' }}</view>
        <view class="status-panel__desc">{{ detail.rejectReason ? `驳回原因：${detail.rejectReason}` : '员工本人提交后等待审核' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">实名认证提交依赖员工本人证件照片、身份确认和个人授权，企业端不再代员工发起或重提申请。</view>
        <view class="enterprise-notice__reason">当前仅保留实名状态、资料字段和图片占位查看，便于企业解释实名进度和提醒员工补齐材料。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="openPage('/pages/profile/help')">查看帮助</button>
          <button class="worker-button" @click="openPage('/pages/workbench/index')">返回工作台</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="detail-grid">
        <view class="detail-item">
          <view class="detail-item__label">姓名</view>
          <view class="detail-item__value">{{ detail.personName || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">手机号</view>
          <view class="detail-item__value">{{ detail.mobile || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">身份证号</view>
          <view class="detail-item__value">{{ detail.idCard || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">当前账号</view>
          <view class="detail-item__value">{{ detail.userName || '-' }}</view>
        </view>
      </view>
      <view class="enterprise-record-meta">
        <view class="enterprise-record-meta__item">最近申请 {{ detail.applyTime || '-' }}</view>
        <view class="enterprise-record-meta__item">最近更新 {{ detail.lastUpdateTime || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">资料图片</view>
          <view class="enterprise-list-head__meta">仅展示已回写图片地址的占位情况，企业端不再上传或替换实名材料。</view>
        </view>
      </view>
      <view class="record-row">
        <view class="record-row__main">
          <view class="record-row__title">身份证人像面</view>
          <view class="record-row__subtitle">{{ detail.idCardFrontUrl ? '已回写图片地址' : '暂未上传' }}</view>
        </view>
      </view>
      <view class="record-row">
        <view class="record-row__main">
          <view class="record-row__title">身份证国徽面</view>
          <view class="record-row__subtitle">{{ detail.idCardBackUrl ? '已回写图片地址' : '暂未上传' }}</view>
        </view>
      </view>
      <view class="record-row">
        <view class="record-row__main">
          <view class="record-row__title">本人免冠照片</view>
          <view class="record-row__subtitle">{{ detail.selfieUrl ? '已回写图片地址' : '暂未上传' }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerRealnameDetail } from '../../api/enterprise-service'
import { openPage } from '../../utils/navigation'

const detail = reactive({})

async function loadDetail() {
  const data = await getWorkerRealnameDetail()
  Object.keys(detail).forEach((key) => delete detail[key])
  Object.assign(detail, data || {})
}

onShow(() => {
  loadDetail().catch((error) => {
    uni.showToast({ title: error.message || '加载实名信息失败', icon: 'none' })
  })
})
</script>
