<template>
  <view class="worker-page">
    <view v-if="locked" class="worker-card">
      <view class="worker-title">工资查询未解锁</view>
      <view class="worker-subtitle">{{ lockReason }}</view>
      <button class="worker-button" @click="goTraining">去完成培训</button>
    </view>

    <view v-else class="worker-card">
      <view class="worker-title">工资列表</view>
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
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getSalaryList } from '../../api/worker'

const salaryData = ref({})
const salaryLastLoadedAt = ref('')
const salaryLastMessage = ref('')

const locked = computed(() => !!salaryData.value.locked)
const lockReason = computed(() => salaryData.value.lockReason || '请先完成培训')
const months = computed(() => salaryData.value.months || [])
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

.worker-empty--panel {
  padding: 24rpx 0;
}

.worker-empty__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.worker-empty__desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #7890aa;
}

.worker-empty__actions {
  display: flex;
  gap: 18rpx;
  margin-top: 22rpx;
}

.worker-empty__actions button {
  flex: 1;
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
