<script>
import { safeHideTabBar } from './utils/navigation'

export default {
  methods: {
    applyKioskMode() {
      safeHideTabBar()

      if (typeof uni.setKeepScreenOn === 'function') {
        try {
          const result = uni.setKeepScreenOn({ keepScreenOn: true })
          if (result && typeof result.catch === 'function') {
            result.catch(() => null)
          }
        } catch {
          // H5 may expose the API but reject it as unsupported.
        }
      }

      if (typeof plus !== 'undefined' && plus?.screen?.lockOrientation) {
        try {
          plus.screen.lockOrientation('portrait')
        } catch (error) {
          try {
            plus.screen.lockOrientation('portrait-primary')
          } catch (lockError) {
            void error
            void lockError
          }
        }
      }
    }
  },
  onLaunch() {
    this.applyKioskMode()
  },
  onShow() {
    this.applyKioskMode()
  },
  onHide() {}
}
</script>

<style lang="scss">
@import './uni.scss';
@import './styles/worker-ui.scss';
</style>
