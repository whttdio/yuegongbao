<template>
  <div class="portal-detail">
    <header class="portal-detail__header">
      <div class="portal-detail__container">
        <button type="button" class="portal-detail__back" @click="goBack">← 返回官网</button>
        <span class="portal-detail__badge">招聘岗位</span>
        <h1>{{ detail.title || '岗位详情' }}</h1>
        <div class="portal-detail__meta">
          <span v-if="detail.company">企业：{{ detail.company }}</span>
          <span v-if="detail.salary">薪资：{{ detail.salary }}</span>
          <span v-if="detail.location">地点：{{ detail.location }}</span>
        </div>
      </div>
    </header>
    <main class="portal-detail__main">
      <div class="portal-detail__container">
        <section class="portal-detail__card">
          <h3>岗位信息</h3>
          <ul class="portal-detail__list">
            <li v-if="detail.jobType"><strong>工种：</strong>{{ detail.jobType }}</li>
            <li v-if="detail.recruitCount"><strong>招聘人数：</strong>{{ detail.recruitCount }} 人</li>
            <li v-if="publishTimeText"><strong>发布时间：</strong>{{ publishTimeText }}</li>
          </ul>
        </section>
        <section v-if="detail.description" class="portal-detail__card">
          <h3>岗位描述</h3>
          <p>{{ detail.description }}</p>
        </section>
        <section v-if="detail.requirementText" class="portal-detail__card">
          <h3>任职要求</h3>
          <p>{{ detail.requirementText }}</p>
        </section>
        <section v-if="detail.contactName || detail.contactMobile" class="portal-detail__card">
          <h3>联系方式</h3>
          <p>{{ detail.contactName }} {{ detail.contactMobile }}</p>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPortalJobDetail } from '@/api/portal/public'
import { resolveOfficialPortalRoute } from '@/utils/portal'

const route = useRoute()
const router = useRouter()
const detail = ref({})

const portalCode = computed(() => route.query.portalCode || 'ygb')
const publishTimeText = computed(() => {
  if (!detail.value.publishTime) return ''
  return String(detail.value.publishTime).slice(0, 10)
})

const goBack = () => {
  const { route } = resolveOfficialPortalRoute(portalCode.value)
  router.push({ path: route, query: { standalone: '1', anchor: 'recruitment' } })
}

onMounted(async () => {
  try {
    const res = await getPortalJobDetail(route.params.jobId)
    if (!res.data || !res.data.title) {
      ElMessage.error('岗位不存在或已下线')
      goBack()
      return
    }
    detail.value = res.data
  } catch (error) {
    console.error(error)
    ElMessage.error('岗位加载失败')
  }
})
</script>

<style scoped lang="scss">
.portal-detail {
  min-height: 100vh;
  background: #f4f7fb;
  color: #18354d;

  &__container {
    width: min(960px, calc(100% - 32px));
    margin: 0 auto;
  }

  &__header {
    padding: 28px 0 24px;
    background: linear-gradient(135deg, #0a3d7a 0%, #0f5ea8 100%);
    color: #fff;
  }

  &__back {
    margin-bottom: 16px;
    padding: 0;
    border: none;
    background: transparent;
    color: rgba(255, 255, 255, 0.88);
    cursor: pointer;
  }

  &__badge {
    display: inline-block;
    margin-bottom: 10px;
    padding: 4px 10px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.16);
    font-size: 12px;
  }

  h1 {
    margin: 0;
    font-size: 28px;
    line-height: 1.4;
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-top: 12px;
    color: rgba(255, 255, 255, 0.82);
    font-size: 13px;
  }

  &__main {
    padding: 28px 0 48px;
  }

  &__card {
    margin-bottom: 16px;
    padding: 20px 22px;
    background: #fff;
    border-radius: 12px;

    h3 {
      margin: 0 0 12px;
      font-size: 18px;
    }

    p {
      margin: 0;
      line-height: 1.8;
      white-space: pre-wrap;
    }
  }

  &__list {
    margin: 0;
    padding: 0;
    list-style: none;

    li {
      margin-bottom: 8px;
      line-height: 1.7;
    }
  }
}
</style>
