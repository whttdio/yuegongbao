<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view v-if="locked" class="worker-card">
      <view class="worker-title">工资查询未解锁</view>
      <view class="worker-subtitle">{{ lockReason }}</view>
      <button class="worker-button" @click="goTraining">去完成培训</button>
    </view>

    <template v-else>
      <view class="worker-card worker-hero">
        <view class="worker-title worker-title--display">工资查询</view>
        <view class="worker-subtitle">查看每月应发、实发与发放状态</view>
        <view class="hero-stat-grid">
          <view class="hero-stat">
            <view class="hero-stat__value">{{ months.length }}</view>
            <view class="hero-stat__label">可查月份</view>
          </view>
          <view class="hero-stat">
            <view class="hero-stat__value">{{ latestMonth || '-' }}</view>
            <view class="hero-stat__label">最近月份</view>
          </view>
          <view class="hero-stat">
            <view class="hero-stat__value">{{ latestRealAmount }}</view>
            <view class="hero-stat__label">最近实发</view>
          </view>
        </view>
      </view>

      <view class="worker-card">
        <view class="section-head">
          <view class="worker-title">工资列表</view>
        </view>
      <view v-if="months.length">
        <view v-for="item in months" :key="item.salaryMonth" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.salaryMonth }}</view>
            <view class="list-row__subtitle">应发 {{ item.shouldAmount || 0 }} / 实发 {{ item.realAmount || 0 }}</view>
          </view>
          <view class="worker-tag">{{ item.payStatusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无工资记录</view>
        <view class="worker-empty__desc">
          如本月刚完成培训或工资尚未同步，可先核对考勤记录；如长期未显示，可进入帮助中心或法律咨询继续处理。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goAttendance">查看考勤</button>
          <button class="worker-button" @click="goHelp">帮助中心</button>
        </view>
      </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getSalaryList } from '../../api/enterprise-service'

const salaryData = ref({})
const salaryLastLoadedAt = ref('')
const salaryLastMessage = ref('')

const locked = computed(() => !!salaryData.value.locked)
const lockReason = computed(() => salaryData.value.lockReason || '请先完成培训')
const months = computed(() => salaryData.value.months || [])
const latestMonth = computed(() => months.value[0]?.salaryMonth || '')
const latestRealAmount = computed(() => months.value[0]?.realAmount ?? '-')
const salaryAccessSummaryText = computed(() => (locked.value ? `未解锁 / ${lockReason.value}` : '已解锁'))
const salarySnapshotText = computed(() => {
  const monthLabels = months.value.map((item) => item.salaryMonth).join('、') || '-'
  return [
    '## 工资验收摘要',
    `- 最近加载：${salaryLastLoadedAt.value || '-'}`,
    `- 查询状态：${salaryAccessSummaryText.value}`,
    `- 月份数量：${months.value.length}`,
    `- 月份列表：${monthLabels}`,
    `- 加载说明：${salaryLastMessage.value || '-'}`
  ].join('\n')
})

async function loadData() {
  try {
    salaryData.value = await getSalaryList(new Date().getFullYear().toString())
    salaryLastLoadedAt.value = new Date().toLocaleString()
    salaryLastMessage.value = locked.value ? lockReason.value : `已返回 ${months.value.length} 条工资记录`
  } catch (error) {
    salaryLastLoadedAt.value = new Date().toLocaleString()
    salaryLastMessage.value = error.message || '加载工资失败'
    uni.showToast({ title: error.message || '加载工资失败', icon: 'none' })
  }
}

function goTraining() {
  uni.navigateTo({ url: '/pages/training/index' })
}

function openDetail(item) {
  if (!item?.salaryMonth) {
    return
  }
  uni.navigateTo({ url: `/pages/salary/detail?month=${item.salaryMonth}` })
}

function goAttendance() {
  uni.navigateTo({ url: '/pages/attendance/checkin' })
}

function goHelp() {
  uni.navigateTo({ url: '/pages/profile/help' })
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
.worker-empty__actions button {
  flex: 1;
}
</style>
