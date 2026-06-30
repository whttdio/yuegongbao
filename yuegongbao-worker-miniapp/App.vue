<script>
import { initWorkerPushListener, syncWorkerPushRegistration } from './utils/push'
import { consumePendingWorkerJumpTarget } from './utils/worker-jump'

export default {
  onLaunch() {
    initWorkerPushListener()
    syncWorkerPushRegistration().catch(() => null)
  },
  onShow() {
    syncWorkerPushRegistration().catch(() => null)
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
