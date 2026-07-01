<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">法律咨询协同</view>
      <view class="worker-subtitle">企业端保留法律知识、热线和历史记录查看；员工本人发起咨询的动作已关闭。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--info">{{ hotlineText }}</view>
        <view class="worker-tag">{{ rows.length }} 条历史记录</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">法律咨询提交依赖员工本人身份、联系信息和个人附件材料，企业端不再代员工发起咨询。</view>
        <view class="enterprise-notice__reason">当前页面保留 FAQ、热线和既有记录查看，推荐通过帮助中心或员工本人入口继续处理。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="goArticleList">查看法律讲座</button>
          <button class="worker-button" @click="goUnion">查看工会服务</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">常见法律问题</view>
          <view class="enterprise-list-head__meta">企业端可检索法律 FAQ 和知识文章，供管理员向员工解释流程。</view>
        </view>
        <view class="worker-tag">{{ faqRows.length }} 条</view>
      </view>
      <view class="form-field">
        <view class="form-field__label">关键字</view>
        <input v-model="faqKeyword" class="form-input" placeholder="输入关键字检索常见问题" @confirm="handleFaqSearch" />
      </view>
      <button class="worker-button worker-button--secondary worker-button--compact" @click="handleFaqSearch">检索 FAQ</button>
      <view v-if="faqRows.length">
        <view v-for="item in faqRows" :key="item.faqKey" class="list-row" @click="openFaq(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.category || '-' }}</view>
          </view>
          <view class="worker-tag">查看</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无匹配的常见问题</view>
        <view class="worker-empty__desc">可改用其他关键字，或进入法律讲座和帮助中心查看通用说明。</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">历史咨询记录</view>
          <view class="enterprise-list-head__meta">仅作企业协同参考，不代表企业可以继续代办个人咨询。</view>
        </view>
        <view class="worker-tag">{{ rows.length }} 条</view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.consultId" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.consultType || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.statusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无咨询记录</view>
        <view class="worker-empty__desc">员工本人提交过咨询后，企业端可在这里查看历史状态和处理结果。</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLegalConsultList, getLegalFaqList, getLegalHotline } from '../../api/enterprise-service'
import { sanitizeFaqRows } from '../../utils/faq-sanitize'

const rows = ref([])
const faqRows = ref([])
const faqKeyword = ref('')
const hotline = ref({})

const hotlineText = computed(() => hotline.value?.displayText || '工会法律服务热线 12351')

async function loadFaqs() {
  const faqs = await getLegalFaqList(faqKeyword.value.trim())
  faqRows.value = sanitizeFaqRows(faqs?.rows || [])
}

async function loadData() {
  const [consults, phone] = await Promise.all([getLegalConsultList(), getLegalHotline()])
  rows.value = consults?.rows || []
  hotline.value = phone || {}
  await loadFaqs()
}

function handleFaqSearch() {
  loadFaqs().catch((error) => {
    uni.showToast({ title: error.message || '加载 FAQ 失败', icon: 'none' })
  })
}

function openFaq(item) {
  if (!item?.faqKey) {
    return
  }
  uni.navigateTo({ url: `/pages/legal/faq-detail?faqKey=${encodeURIComponent(item.faqKey)}` })
}

function openDetail(item) {
  if (!item?.consultId) {
    return
  }
  uni.navigateTo({ url: `/pages/legal/detail?consultId=${item.consultId}` })
}

function goArticleList() {
  uni.navigateTo({ url: '/pages/legal/article-list' })
}

function goUnion() {
  uni.navigateTo({ url: '/pages/union/index' })
}

onShow(() => {
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载法律协同信息失败', icon: 'none' })
  })
})
</script>
