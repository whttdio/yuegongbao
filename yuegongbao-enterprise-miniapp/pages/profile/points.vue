<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page points-page">
    <view class="points-hero worker-card worker-hero">
      <view class="points-hero__top">
        <view>
          <view class="points-hero__label">我的积分</view>
          <view class="points-score">{{ currentPoints }}</view>
        </view>
        <view class="points-hero__badge">积分商城</view>
      </view>
      <view class="points-hero__stats">
        <view class="points-stat">
          <view class="points-stat__value">{{ rewards.length }}</view>
          <view class="points-stat__label">可兑商品</view>
        </view>
        <view class="points-stat">
          <view class="points-stat__value">{{ exchangeRows.length }}</view>
          <view class="points-stat__label">兑换记录</view>
        </view>
        <view class="points-stat">
          <view class="points-stat__value">{{ rows.length }}</view>
          <view class="points-stat__label">积分流水</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">可兑换商品</view>
        <view v-if="rewards.length" class="worker-tag worker-tag--info">{{ rewards.length }} 件</view>
      </view>

      <view v-if="rewards.length" class="reward-list">
        <view v-for="item in rewards" :key="item.goodsKey" class="reward-item">
          <view class="reward-item__icon" :class="'entry-item__icon--' + getGoodsIcon(item).tone">
            <text class="entry-item__glyph">{{ getGoodsIcon(item).glyph }}</text>
          </view>
          <view class="reward-item__main">
            <view class="reward-item__title">{{ item.title }}</view>
            <view class="reward-item__desc">{{ item.desc }}</view>
            <view class="reward-item__meta">
              库存 {{ item.stockCount }} · {{ goodsTypeText(item.goodsType) }}
            </view>
            <view v-if="item.disabledReason" class="reward-item__tip">{{ item.disabledReason }}</view>
          </view>
          <view class="reward-item__side">
            <view class="reward-item__score">{{ item.score }} 积分</view>
            <button
              class="worker-button worker-button--small reward-item__button"
              :class="{ 'reward-item__button--disabled': !item.canExchange }"
              :disabled="!item.canExchange || exchanging"
              @click="exchangeGoods(item)"
            >
              {{ exchanging ? '处理中' : '立即兑换' }}
            </button>
          </view>
        </view>
      </view>

      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可兑换商品</view>
        <view class="worker-empty__desc">可先参加福利活动、完成培训或关注平台通知，积分商品上架后再回来兑换。</view>
        <view class="points-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goActivity">福利活动</button>
          <button class="worker-button" @click="goTraining">去培训</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">兑换记录</view>
        <view v-if="exchangeRows.length" class="worker-tag worker-tag--info">{{ exchangeRows.length }} 条</view>
      </view>

      <view v-if="exchangeRows.length" class="record-list">
        <view v-for="item in exchangeRows" :key="item.exchangeId" class="record-item">
          <view class="record-item__main">
            <view class="record-item__title">{{ item.goodsName }}</view>
            <view class="record-item__desc">{{ item.deliveryRemark || '兑换申请已提交' }}</view>
            <view class="record-item__time">{{ formatDateTime(item.createTime) }}</view>
          </view>
          <view class="record-item__side">
            <view class="worker-tag" :class="getExchangeStatusClass(item.exchangeStatusText)">
              {{ item.exchangeStatusText || '-' }}
            </view>
            <view class="record-item__cost">-{{ item.scoreCost }}</view>
          </view>
        </view>
      </view>

      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无兑换记录</view>
        <view class="worker-empty__desc">兑换完成后，可在这里查看发放状态和处理结果。</view>
        <view class="points-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goActivity">去赚积分</button>
          <button class="worker-button" @click="goHelp">帮助中心</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">积分流水</view>
        <view v-if="rows.length" class="worker-tag worker-tag--info">{{ rows.length }} 条</view>
      </view>

      <view v-if="rows.length" class="ledger-list">
        <view v-for="item in rows" :key="item.ledgerId" class="ledger-item">
          <view class="ledger-item__main">
            <view class="ledger-item__title">{{ item.title }}</view>
            <view class="ledger-item__desc">{{ item.summary || '-' }}</view>
            <view class="ledger-item__time">{{ formatDateTime(item.createTime) }}</view>
          </view>
          <view
            class="ledger-item__delta"
            :class="Number(item.scoreDelta) >= 0 ? 'ledger-item__delta--plus' : 'ledger-item__delta--minus'"
          >
            {{ formatScoreDelta(item.scoreDelta) }}
          </view>
        </view>
      </view>

      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无积分流水</view>
        <view class="worker-empty__desc">平台奖励、活动参与或兑换处理后，相关积分变动会在这里展示。</view>
        <view class="points-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goNotice">查看通知</button>
          <button class="worker-button" @click="goActivity">参与活动</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { exchangeWorkerPointGoods, getWorkerPointsAccount } from '../../api/enterprise-service'
import { openPage } from '../../utils/navigation'

const EXPECTED_POINTS_CHAIN_PAGES = ['积分商城', '商品兑换', '兑换记录', '积分流水']
const account = ref({})
const rewards = ref([])
const rows = ref([])
const exchangeRows = ref([])
const exchanging = ref(false)
const pointsLastLoadedAt = ref('')
const pointsLastExchangedAt = ref('')
const pointsLastActionAt = ref('')
const pointsLastMessage = ref('')

const currentPoints = computed(() => account.value.currentPoints || 0)
const pointsChainCoverageText = computed(() => EXPECTED_POINTS_CHAIN_PAGES.join(' / '))
const pointsGoodsSummaryText = computed(() => `当前积分 ${currentPoints.value} / 商品 ${rewards.value.length} 个`)
const pointsRecordsSummaryText = computed(() => `兑换 ${exchangeRows.value.length} 条 / 流水 ${rows.value.length} 条`)
const pointsConsistencyText = computed(() => {
  if (!rewards.value.length && !exchangeRows.value.length && !rows.value.length) {
    return '当前无商品、兑换记录和流水，需结合真实接口复核'
  }
  return `商品 ${rewards.value.length} 个 / 兑换 ${exchangeRows.value.length} 条 / 流水 ${rows.value.length} 条`
})
const latestExchangeSummaryText = computed(() => {
  if (!exchangeRows.value.length) {
    return '暂无兑换记录'
  }
  const latest = exchangeRows.value[0] || {}
  return `${latest.goodsName || '-'} / ${latest.exchangeStatusText || '-'} / -${latest.scoreCost || 0}`
})
const pointsSnapshotText = computed(() => {
  return [
    '## 积分商城验收摘要',
    `- 链路核对：${pointsChainCoverageText.value}`,
    `- 最近联动：${pointsLastActionAt.value || '-'}`,
    `- 最近加载：${pointsLastLoadedAt.value || '-'}`,
    `- 最近兑换：${pointsLastExchangedAt.value || '-'}`,
    `- 积分与商品：${pointsGoodsSummaryText.value}`,
    `- 兑换与流水：${pointsRecordsSummaryText.value}`,
    `- 最近一条兑换：${latestExchangeSummaryText.value}`,
    `- 数据核对：${pointsConsistencyText.value}`,
    `- 说明：${pointsLastMessage.value || '-'}`,
    '- 链路关联：积分商城 / 商品兑换 / 兑换记录 / 积分流水'
  ].join('\n')
})

function formatDateTime(value) {
  if (!value) {
    return '-'
  }
  const text = String(value).trim()
  const normalized = text
    .replace('T', ' ')
    .replace(/\.\d+(Z|[+-]\d{2}:?\d{2})?$/, '')
    .replace(/\+08:00$/, '')
    .trim()
  if (normalized.length >= 16) {
    return normalized.slice(0, 16)
  }
  return normalized
}

function formatScoreDelta(value) {
  const score = Number(value || 0)
  if (score > 0) {
    return `+${score}`
  }
  return String(score)
}

function getGoodsIcon(item = {}) {
  const type = item.goodsType
  if (type === 'coupon') {
    return { glyph: '券', tone: 'gold' }
  }
  if (type === 'service') {
    return { glyph: '服', tone: 'blue' }
  }
  if (type === 'package') {
    return { glyph: '礼', tone: 'rose' }
  }
  return { glyph: '兑', tone: 'orange' }
}

function getExchangeStatusClass(statusText) {
  const text = String(statusText || '')
  if (text.includes('待') || text.includes('处理') || text.includes('配货')) {
    return 'worker-tag--warning'
  }
  if (text.includes('已') || text.includes('完成') || text.includes('成功')) {
    return 'worker-tag--success'
  }
  if (text.includes('失败') || text.includes('取消') || text.includes('驳回')) {
    return 'worker-tag--danger'
  }
  return 'worker-tag--info'
}

async function loadData() {
  try {
    const data = await getWorkerPointsAccount()
    account.value = data || {}
    rewards.value = data?.rewards || []
    rows.value = data?.rows || []
    exchangeRows.value = data?.exchangeRows || []
    pointsLastLoadedAt.value = new Date().toLocaleString()
    pointsLastMessage.value = rewards.value.length || exchangeRows.value.length || rows.value.length
      ? '积分账户已加载，可核对余额、商品、兑换记录和流水'
      : '当前暂无可兑换商品和积分流水，可先参与活动或培训'
  } catch (error) {
    pointsLastLoadedAt.value = new Date().toLocaleString()
    pointsLastMessage.value = error.message || '加载积分失败'
    uni.showToast({ title: error.message || '加载积分失败', icon: 'none' })
  }
}

function goodsTypeText(type) {
  if (type === 'coupon') {
    return '福利券'
  }
  if (type === 'service') {
    return '服务权益'
  }
  if (type === 'package') {
    return '资料礼包'
  }
  return '其他'
}

async function exchangeGoods(item) {
  if (!item?.goodsKey || exchanging.value) {
    return
  }
  exchanging.value = true
  try {
    pointsLastExchangedAt.value = new Date().toLocaleString()
    pointsLastActionAt.value = pointsLastExchangedAt.value
    const result = await exchangeWorkerPointGoods({ goodsKey: item.goodsKey })
    pointsLastMessage.value = result?.exchangeStatusText || '兑换成功'
    uni.showToast({ title: result?.exchangeStatusText || '兑换成功', icon: 'none' })
    await loadData()
  } catch (error) {
    pointsLastExchangedAt.value = new Date().toLocaleString()
    pointsLastMessage.value = error.message || '兑换失败'
    uni.showToast({ title: error.message || '兑换失败', icon: 'none' })
  } finally {
    exchanging.value = false
  }
}

function goActivity() {
  pointsLastActionAt.value = new Date().toLocaleString()
  pointsLastMessage.value = '已前往福利活动，待补充空态引导验收'
  uni.navigateTo({ url: '/pages/activity/detail' })
}

function goTraining() {
  pointsLastActionAt.value = new Date().toLocaleString()
  pointsLastMessage.value = '已前往培训页，待补充空态引导验收'
  openPage('/pages/training/index')
}

function goHelp() {
  pointsLastActionAt.value = new Date().toLocaleString()
  pointsLastMessage.value = '已前往帮助中心，待补充空态引导验收'
  uni.navigateTo({ url: '/pages/profile/help' })
}

function goNotice() {
  pointsLastActionAt.value = new Date().toLocaleString()
  pointsLastMessage.value = '已前往通知列表，待补充空态引导验收'
  uni.navigateTo({ url: '/pages/notice/list' })
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
.points-hero__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.points-hero__label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.84);
}

.points-score {
  margin-top: 10rpx;
  font-size: 72rpx;
  font-weight: 800;
  line-height: 1.1;
  color: #fff;
  letter-spacing: -0.02em;
}

.points-hero__badge {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  font-size: 22rpx;
  font-weight: 650;
}

.points-hero__stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14rpx;
  margin-top: 28rpx;
}

.points-stat {
  padding: 18rpx 12rpx;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.14);
  border: 1rpx solid rgba(255, 255, 255, 0.18);
  text-align: center;
}

.points-stat__value {
  font-size: 32rpx;
  font-weight: 750;
  color: #fff;
  line-height: 1.2;
}

.points-stat__label {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.82);
}

.reward-list,
.record-list,
.ledger-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.reward-item,
.record-item,
.ledger-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 22rpx 20rpx;
  border-radius: 20rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f7fafb 100%);
  border: 1rpx solid $ygb-border-light;
  box-shadow: 0 4rpx 12rpx rgba(24, 50, 71, 0.04);
}

.reward-item__icon {
  width: 72rpx;
  height: 72rpx;
  margin-bottom: 0;
  flex-shrink: 0;
}

.reward-item__main,
.record-item__main,
.ledger-item__main {
  flex: 1;
  min-width: 0;
}

.reward-item__title,
.record-item__title,
.ledger-item__title {
  font-size: 28rpx;
  font-weight: 650;
  color: $ygb-text-body;
  line-height: 1.45;
}

.reward-item__desc,
.record-item__desc,
.ledger-item__desc {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: $ygb-text-tertiary;
  line-height: 1.55;
}

.reward-item__meta,
.record-item__time,
.ledger-item__time {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: $ygb-text-muted;
}

.reward-item__tip {
  display: inline-flex;
  margin-top: 10rpx;
  padding: 4rpx 12rpx;
  border-radius: 999rpx;
  background: $ygb-danger-soft;
  color: $ygb-danger;
  font-size: 20rpx;
}

.reward-item__side,
.record-item__side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12rpx;
  flex-shrink: 0;
}

.reward-item__score {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: $ygb-primary-soft;
  color: #0b6b64;
  font-size: 22rpx;
  font-weight: 650;
  white-space: nowrap;
}

.reward-item__button {
  min-width: 148rpx;
  min-height: 64rpx;
  line-height: 64rpx;
  padding: 0 18rpx;
  font-size: 24rpx;
  box-shadow: 0 8rpx 18rpx rgba(15, 118, 110, 0.18);
}

.reward-item__button--disabled,
.reward-item__button[disabled] {
  background: #d7e2e8;
  color: #7b909e;
  box-shadow: none;
}

.record-item__cost {
  font-size: 28rpx;
  font-weight: 700;
  color: $ygb-danger;
}

.ledger-item__delta {
  flex-shrink: 0;
  min-width: 88rpx;
  padding: 10rpx 14rpx;
  border-radius: 16rpx;
  font-size: 28rpx;
  font-weight: 700;
  text-align: center;
}

.ledger-item__delta--plus {
  background: $ygb-success-soft;
  color: $ygb-success;
}

.ledger-item__delta--minus {
  background: $ygb-danger-soft;
  color: $ygb-danger;
}

.points-empty-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.points-empty-actions .worker-button {
  flex: 1;
}
</style>
