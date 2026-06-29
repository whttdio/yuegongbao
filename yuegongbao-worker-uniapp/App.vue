<script>
import { initWorkerPushListener, syncWorkerPushRegistration } from './utils/push'
import { consumePendingWorkerJumpTarget } from './utils/worker-jump'

export default {
  methods: {
    applyKioskMode() {
      if (typeof uni.setKeepScreenOn === 'function') {
        uni.setKeepScreenOn({ keepScreenOn: true })
      }

      if (typeof plus !== 'undefined' && plus?.screen?.lockOrientation) {
        try {
          plus.screen.lockOrientation('portrait')
        } catch (error) {
          try {
            plus.screen.lockOrientation('portrait-primary')
          } catch (lockError) {
            void lockError
          }
        }
      }
    }
  },
  onLaunch() {
    initWorkerPushListener()
    syncWorkerPushRegistration().catch(() => null)
    this.applyKioskMode()
  },
  onShow() {
    syncWorkerPushRegistration().catch(() => null)
    this.applyKioskMode()
    setTimeout(() => {
      consumePendingWorkerJumpTarget()
    }, 80)
  },
  onHide() {}
}
</script>

<style lang="scss">
@import './styles/worker-ui.scss';
</style>
