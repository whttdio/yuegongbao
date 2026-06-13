import { getActivePortalConfig } from '@/utils/portal'

const portal = getActivePortalConfig()

export default {
  title: portal.appTitle,
  sideTheme: 'theme-dark',
  showSettings: false,
  navType: 1,
  tagsView: true,
  tagsViewPersist: false,
  tagsIcon: false,
  tagsViewStyle: 'card',
  fixedHeader: true,
  sidebarLogo: true,
  dynamicTitle: false,
  footerVisible: false,
  footerContent: portal.footerContent
}
