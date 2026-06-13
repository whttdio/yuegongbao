import useSettingsStore from '@/store/modules/settings'
import { getActivePortalConfig } from '@/utils/portal'

/**
 * 动态修改标题
 */
export function useDynamicTitle() {
  const settingsStore = useSettingsStore()
  const portal = getActivePortalConfig()
  if (settingsStore.dynamicTitle) {
    document.title = settingsStore.title + ' - ' + portal.appTitle
  } else {
    document.title = portal.appTitle
  }
}
