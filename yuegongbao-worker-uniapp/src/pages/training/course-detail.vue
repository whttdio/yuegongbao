<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view class="worker-subtitle">{{ detail.summary || '暂无课程摘要' }}</view>
      <view class="course-meta">
        <view class="worker-tag">时长 {{ detail.durationText || '00:00' }}</view>
        <view class="worker-tag">已学 {{ detail.studiedSeconds || 0 }} 秒</view>
      </view>
      <view v-for="(item, index) in outlineList" :key="`${index}-${item}`" class="content-row">
        {{ index + 1 }}. {{ item }}
      </view>
      <view class="progress-actions">
        <button class="worker-button worker-button--secondary" @click="saveProgress(120)">记录 2 分钟</button>
        <button class="worker-button" @click="saveProgress(detail.totalSeconds || 0)">
          {{ detail.completed ? '已学完' : '标记学完' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getTrainingCourseDetail, saveTrainingStudyProgress } from '../../api/worker'

const detail = ref({})
const courseDetailLastLoadedAt = ref('')
const courseDetailLastSavedAt = ref('')
const courseDetailLastActionAt = ref('')
const courseDetailLastMessage = ref('')

const outlineList = computed(() => {
  if (Array.isArray(detail.value?.outlineList) && detail.value.outlineList.length) {
    return detail.value.outlineList
  }
  return ['当前课程暂无详细提纲，可先记录学习进度。']
})
const courseDetailSummaryText = computed(() => {
  return `${detail.value.title || '-'} / ${detail.value.durationText || '00:00'} / ${detail.value.completed ? '已完成' : '学习中'} / ${detail.value.studiedSeconds || 0} 秒`
})
const courseDetailLinkageText = computed(() => {
  return detail.value.completed
    ? '课程完成后，应回到培训首页核对本月进度，并继续检查首页、工作台、工资页和打卡页的解锁刷新'
    : '课程学习中时，学习记录页与培训首页应同步显示已学秒数和未完成状态'
})
const courseDetailSnapshotText = computed(() => {
  return [
    '## 课程详情验收摘要',
    `- 最近加载：${courseDetailLastLoadedAt.value || '-'}`,
    `- 最近保存：${courseDetailLastSavedAt.value || '-'}`,
    `- 最近联动：${courseDetailLastActionAt.value || '-'}`,
    `- 课程摘要：${courseDetailSummaryText.value}`,
    `- 提纲数量：${outlineList.value.length} 条`,
    `- 进度联动：${courseDetailLinkageText.value}`,
    `- 说明：${courseDetailLastMessage.value || '-'}`,
    '- 链路关联：培训首页 / 课程详情 / 学习进度保存'
  ].join('\n')
})

async function loadData(courseKey) {
  if (!courseKey) {
    uni.showToast({ title: '缺少课程标识', icon: 'none' })
    return
  }
  try {
    detail.value = await getTrainingCourseDetail(courseKey)
    courseDetailLastLoadedAt.value = new Date().toLocaleString()
    courseDetailLastMessage.value = '课程详情已加载，可核对提纲和学习进度'
  } catch (error) {
    courseDetailLastLoadedAt.value = new Date().toLocaleString()
    courseDetailLastMessage.value = error.message || '加载课程失败'
    uni.showToast({ title: error.message || '加载课程失败', icon: 'none' })
  }
}

function recordCourseDetailAction(action, detailText) {
  courseDetailLastActionAt.value = new Date().toLocaleString()
  courseDetailLastMessage.value = detailText ? `${action} / ${detailText}` : action
}

async function saveProgress(seconds) {
  if (!detail.value.courseKey) {
    return
  }
  const nextSeconds = Math.max(Number(detail.value.studiedSeconds || 0), Number(seconds || 0))
  try {
    const result = await saveTrainingStudyProgress(detail.value.courseKey, nextSeconds)
    detail.value = {
      ...detail.value,
      studiedSeconds: result?.studiedSeconds || nextSeconds,
      totalSeconds: result?.totalSeconds || detail.value.totalSeconds,
      completed: !!result?.completed
    }
    courseDetailLastSavedAt.value = new Date().toLocaleString()
    recordCourseDetailAction(
      detail.value.completed ? '课程进度已保存' : '学习进度已保存',
      detail.value.completed
        ? '当前已完成学习，可回培训首页核对解锁进度'
        : `已学习 ${detail.value.studiedSeconds || nextSeconds} 秒，可继续回学习记录页核对`
    )
    uni.showToast({ title: detail.value.completed ? '课程已完成' : '学习进度已保存', icon: 'none' })
  } catch (error) {
    courseDetailLastSavedAt.value = new Date().toLocaleString()
    recordCourseDetailAction('保存进度失败', error.message || '保存进度失败')
    uni.showToast({ title: error.message || '保存进度失败', icon: 'none' })
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
  loadData(options?.courseKey)
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

.course-meta {
  display: flex;
  gap: 16rpx;
  margin-top: 18rpx;
}

.content-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #36506b;
}

.progress-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}

.progress-actions button {
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
  line-height: 1.7;
  color: #16324f;
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
