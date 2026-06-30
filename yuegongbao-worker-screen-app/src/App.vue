<script>
import { safeHideTabBar } from './utils/navigation'
import { initWorkerPushListener, syncWorkerPushRegistration } from './utils/push'
import { consumePendingWorkerJumpTarget } from './utils/worker-jump'

export default {
  onLaunch() {
    initWorkerPushListener()
    syncWorkerPushRegistration().catch(() => null)
  },
  onShow() {
    syncWorkerPushRegistration().catch(() => null)
    safeHideTabBar()
    setTimeout(() => {
      consumePendingWorkerJumpTarget()
    }, 80)
  },
  onHide() {}
}
</script>

<style>
page {
  background: #f3f7fb;
  color: #16324f;
  font-family: 'Microsoft YaHei', sans-serif;
}

.worker-page {
  min-height: 100vh;
  padding: 24rpx;
  box-sizing: border-box;
}

.worker-card {
  background: #ffffff;
  border-radius: 28rpx;
  padding: 28rpx;
  box-shadow: 0 14rpx 34rpx rgba(15, 41, 77, 0.08);
}

.worker-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #0e2d4e;
}

.worker-subtitle {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #6b819b;
  line-height: 1.6;
}

.worker-button {
  height: 84rpx;
  border: none;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #1f6fd6 0%, #0f95e8 100%);
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
}

.worker-button--secondary {
  background: #e9f2ff;
  color: #1f6fd6;
}

.worker-tag {
  display: inline-flex;
  align-items: center;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: #eef6ff;
  color: #1f6fd6;
  font-size: 22rpx;
}

.worker-empty {
  padding: 48rpx 24rpx;
  text-align: center;
  color: #7890aa;
  font-size: 24rpx;
}
</style>
