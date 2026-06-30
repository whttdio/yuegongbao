<template>
  <view class="worker-page">
    <view v-if="loading" class="worker-card worker-empty worker-empty--panel">
      <view class="worker-empty__title">记录加载中...</view>
    </view>

    <template v-else-if="loadError">
      <view class="worker-card worker-empty worker-empty--panel">
        <view class="worker-empty__title">记录加载失败</view>
        <view class="worker-empty__desc">{{ loadError }}</view>
        <view class="worker-empty__actions">
          <button class="worker-button" @click="retryLoad">重新加载</button>
        </view>
      </view>
    </template>

    <template v-else>
      <view class="worker-card worker-hero">
        <view class="worker-title worker-title--display">{{ monthLabel }}</view>
        <view class="worker-subtitle">查看当月培训答题与课程学习情况</view>
        <view class="hero-stat-grid">
          <view class="hero-stat">
            <view class="hero-stat__value">{{ progressText }}</view>
            <view class="hero-stat__label">答题进度</view>
          </view>
          <view class="hero-stat">
            <view class="hero-stat__value">{{ completedQuestionCount }}</view>
            <view class="hero-stat__label">已完成题</view>
          </view>
          <view class="hero-stat">
            <view class="hero-stat__value">{{ detail.passed ? '已通过' : '进行中' }}</view>
            <view class="hero-stat__label">培训状态</view>
          </view>
        </view>
      </view>

      <view class="worker-card">
        <view class="section-head">
          <view class="worker-title">答题明细</view>
          <view class="worker-tag">{{ completedQuestionCount }}/{{ questionRows.length }}</view>
        </view>
        <view v-if="questionRows.length">
          <view v-for="item in questionRows" :key="item.questionId" class="list-row">
            <view>
              <view class="list-row__title">{{ item.title }}</view>
              <view class="list-row__subtitle">{{ item.completed ? '本月已完成' : '本月未完成' }}</view>
            </view>
            <view class="worker-tag">{{ item.completed ? '已完成' : '未完成' }}</view>
          </view>
        </view>
        <view v-else class="worker-empty worker-empty--panel">
          <view class="worker-empty__title">暂无题目记录</view>
        </view>
      </view>

      <view class="worker-card">
        <view class="section-head">
          <view class="worker-title">课程学习</view>
        </view>
        <view v-if="courseRows.length">
          <view v-for="item in courseRows" :key="item.courseKey" class="list-row" @click="openCourse(item)">
            <view>
              <view class="list-row__title">{{ item.title }}</view>
              <view class="list-row__subtitle">已学 {{ item.studiedSeconds || 0 }} 秒 / {{ item.durationText || '-' }}</view>
            </view>
            <view class="worker-tag">{{ item.completed ? '已完成' : '继续学习' }}</view>
          </view>
        </view>
        <view v-else class="worker-empty worker-empty--panel">
          <view class="worker-empty__title">暂无课程记录</view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
// 培训历史详情接口联调快照：/app/worker/training/history/detail
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getTrainingHistoryDetail } from '../../api/screen'
import { formatTrainingMonth } from '../../utils/training-format'

const detail = ref({})
const monthRef = ref('')
const loading = ref(true)
const loadError = ref('')

const questionRows = computed(() => detail.value.questions || [])
const courseRows = computed(() => detail.value.courses || [])
const monthLabel = computed(() => detail.value.monthLabel || formatTrainingMonth(detail.value.month || monthRef.value))
const progressText = computed(() => `${detail.value.completed || 0}/${detail.value.total || 0}`)
const completedQuestionCount = computed(() => questionRows.value.filter((item) => item.completed).length)

async function loadData(month) {
  if (!month) {
    loading.value = false
    loadError.value = '缺少月份参数'
    return
  }
  monthRef.value = month
  loading.value = true
  loadError.value = ''
  try {
    detail.value = await getTrainingHistoryDetail(month)
  } catch (error) {
    loadError.value = error.message || '加载记录失败'
    uni.showToast({ title: loadError.value, icon: 'none' })
  } finally {
    loading.value = false
  }
}

function retryLoad() {
  loadData(monthRef.value)
}

function openCourse(item) {
  if (!item?.courseKey) {
    return
  }
  uni.navigateTo({ url: `/pages/training/course-detail?courseKey=${item.courseKey}` })
}

onLoad((options) => {
  loadData(options?.month)
})
</script>

<style lang="scss">
.worker-empty__actions button {
  flex: 1;
}
</style>
