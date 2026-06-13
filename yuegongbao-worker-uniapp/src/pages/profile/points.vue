<template>
  <view class="worker-page">
    <view class="worker-card points-hero">
      <view class="worker-title points-title">积分商城</view>
      <view class="worker-subtitle points-subtitle">展示当前积分余额、可兑换商品、兑换记录与积分流水。</view>
      <view class="points-score">当前积分 {{ currentPoints }}</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">可兑换商品</view>
      <view v-if="rewards.length">
        <view v-for="item in rewards" :key="item.goodsKey" class="reward-card">
          <view>
            <view class="reward-row__title">{{ item.title }}</view>
            <view class="reward-row__subtitle">{{ item.desc }}</view>
            <view class="reward-row__subtitle">库存 {{ item.stockCount }} / 类型 {{ goodsTypeText(item.goodsType) }}</view>
            <view v-if="item.disabledReason" class="reward-row__tip">{{ item.disabledReason }}</view>
          </view>
          <view class="reward-card__action">
            <view class="worker-tag">{{ item.score }} 积分</view>
            <button
              class="worker-button worker-button--small"
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
        <view class="worker-empty__desc">可先参加福利活动、学习培训或关注平台通知，后续积分商品上架后再回来兑换。</view>
        <view class="points-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goActivity">福利活动</button>
          <button class="worker-button" @click="goTraining">去培训</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">兑换记录</view>
      <view v-if="exchangeRows.length">
        <view v-for="item in exchangeRows" :key="item.exchangeId" class="reward-row">
          <view>
            <view class="reward-row__title">{{ item.goodsName }}</view>
            <view class="reward-row__subtitle">
              {{ item.deliveryRemark || '' }}{{ item.createTime ? ' / ' + item.createTime : '' }}
            </view>
          </view>
          <view class="reward-card__action">
            <view class="worker-tag">{{ item.exchangeStatusText }}</view>
            <view class="reward-row__cost">-{{ item.scoreCost }}</view>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无兑换记录</view>
        <view class="worker-empty__desc">如暂未发起兑换，可先积累积分；兑换完成后，可回到这里查看发放状态和处理结果。</view>
        <view class="points-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goActivity">去赚积分</button>
          <button class="worker-button" @click="goHelp">帮助中心</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">积分流水</view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.ledgerId" class="reward-row">
          <view>
            <view class="reward-row__title">{{ item.title }}</view>
            <view class="reward-row__subtitle">
              {{ item.summary }}{{ item.createTime ? ' / ' + item.createTime : '' }}
            </view>
          </view>
          <view class="worker-tag" :class="{ 'worker-tag--minus': Number(item.scoreDelta) < 0 }">
            {{ Number(item.scoreDelta) > 0 ? '+' : '' }}{{ item.scoreDelta }}
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无积分流水</view>
        <view class="worker-empty__desc">平台奖励、活动参与、讲座报名或兑换处理后，相关积分变动会在这里持续累积展示。</view>
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
import { exchangeWorkerPointGoods, getWorkerPointsAccount } from '../../api/worker'
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
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.worker-card + .worker-card {
  margin-top: 24rpx;
}

.points-hero {
  background: linear-gradient(145deg, #0f4078 0%, #1d6ecf 72%, #48a3f0 100%);
  color: #fff;
}

.points-title,
.points-subtitle {
  color: #fff;
}

.points-score {
  margin-top: 20rpx;
  font-size: 48rpx;
  font-weight: 700;
}

.reward-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.reward-card:last-child {
  border-bottom: none;
}

.reward-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.reward-row:last-child {
  border-bottom: none;
}

.reward-row__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.reward-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
  line-height: 1.6;
}

.reward-row__tip {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #dc3545;
}

.reward-row__cost {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #dc3545;
  text-align: right;
}

.reward-card__action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12rpx;
}

.worker-button--small {
  min-width: 150rpx;
  height: 64rpx;
  line-height: 64rpx;
  padding: 0 20rpx;
  font-size: 24rpx;
}

.worker-tag--minus {
  background: #fdecef;
  color: #dc3545;
}

.points-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.points-empty-actions button {
  flex: 1;
}

.section-head--sub {
  margin-top: 20rpx;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}

.worker-title--small {
  font-size: 28rpx;
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
</style>
