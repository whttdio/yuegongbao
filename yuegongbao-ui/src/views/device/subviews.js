import router from '@/router'
import { isResolvedRouteAvailable } from '@/utils/portal'

const DEVICE_SUBVIEW_DEFINITIONS = Object.freeze([
  {
    key: 'attendance',
    viewCode: 'ATTENDANCE',
    title: '考勤设备',
    desc: '按考勤设备视角查看在线、授权和接入台账。',
    actionText: '进入考勤设备',
    ygb: {
      routeName: 'YgbDeviceAttendance',
      path: '/ygb-safety/deviceAttendance',
      component: 'ygb/deviceAttendance/index'
    },
    azb: {
      routeName: 'AzbDeviceAttendance',
      path: '/ygb-safety/deviceAttendance',
      component: 'azb/deviceAttendance/index'
    }
  },
  {
    key: 'chip',
    viewCode: 'CHIP',
    title: '芯片设备',
    desc: '聚焦芯片设备、物联卡和授权状态。',
    actionText: '进入芯片设备',
    ygb: {
      routeName: 'YgbDeviceChip',
      path: '/ygb-safety/deviceChip',
      component: 'ygb/deviceChip/index'
    },
    azb: {
      routeName: 'AzbDeviceChip',
      path: '/ygb-safety/deviceChip',
      component: 'azb/deviceChip/index'
    }
  },
  {
    key: 'ai',
    viewCode: 'AI',
    title: 'AI 设备',
    desc: '查看 AI 设备在线、授权和异常留痕基础台账。',
    actionText: '进入 AI 设备',
    ygb: {
      routeName: 'YgbDeviceAi',
      path: '/ygb-safety/deviceAi',
      component: 'ygb/deviceAi/index'
    },
    azb: {
      routeName: 'AzbDeviceAi',
      path: '/ygb-safety/deviceAi',
      component: 'azb/deviceAi/index'
    }
  },
  {
    key: 'iotCard',
    title: '物联卡',
    desc: '查看设备物联卡号、归属企业和在线状态。',
    actionText: '进入物联卡台账',
    ygb: {
      routeName: 'YgbDeviceIotCard',
      path: '/ygb-safety/deviceIotCard',
      component: 'ygb/deviceIotCard/index'
    },
    azb: {
      routeName: 'AzbDeviceIotCard',
      path: '/ygb-safety/deviceIotCard',
      component: 'azb/deviceIotCard/index'
    }
  },
  {
    key: 'chipInventory',
    title: '芯片库存',
    desc: '查看芯片编号、库存归属和授权状态。',
    actionText: '进入芯片库存',
    ygb: {
      routeName: 'YgbDeviceChipInventory',
      path: '/ygb-safety/deviceChipInventory',
      component: 'ygb/deviceChipInventory/index'
    },
    azb: {
      routeName: 'AzbDeviceChipInventory',
      path: '/ygb-safety/deviceChipInventory',
      component: 'azb/deviceChipInventory/index'
    }
  },
  {
    key: 'geofence',
    title: '电子围栏',
    desc: '维护围栏名称、区域和关联企业范围。',
    actionText: '进入电子围栏',
    ygb: {
      routeName: 'YgbDeviceGeofence',
      path: '/ygb-safety/deviceGeofence',
      component: 'ygb/deviceGeofence/index'
    },
    azb: {
      routeName: 'AzbDeviceGeofence',
      path: '/ygb-safety/deviceGeofence',
      component: 'azb/deviceGeofence/index'
    }
  },
  {
    key: 'uninstallAlert',
    title: '拆卸报警',
    desc: '集中查看拆卸、拆改和围栏破坏相关报警。',
    actionText: '进入拆卸报警',
    ygb: {
      routeName: 'YgbDeviceUninstallAlert',
      path: '/ygb-safety/deviceUninstallAlert',
      component: 'ygb/deviceUninstallAlert/index'
    },
    azb: {
      routeName: 'AzbDeviceUninstallAlert',
      path: '/ygb-safety/deviceUninstallAlert',
      component: 'azb/deviceUninstallAlert/index'
    }
  }
])

function buildResolvedRoute(target) {
  if (!target?.routeName && !target?.path) {
    return null
  }

  if (target.routeName) {
    try {
      const resolvedByName = router.resolve({ name: target.routeName })
      if (isResolvedRouteAvailable(resolvedByName)) {
        return { route: resolvedByName, source: 'name' }
      }
    } catch (error) {
      // Fall through to path-based resolution when the named route is not yet registered.
    }
  }

  if (target.path) {
    try {
      const resolvedByPath = router.resolve({ path: target.path })
      if (isResolvedRouteAvailable(resolvedByPath)) {
        return { route: resolvedByPath, source: 'path' }
      }
    } catch (error) {
      return null
    }
  }

  return null
}

export function buildDeviceSubviewEntries(portalCode, queryBuilder = () => ({})) {
  return DEVICE_SUBVIEW_DEFINITIONS
    .map(item => {
      const target = item[portalCode]
      const resolved = buildResolvedRoute(target)
      if (!resolved) {
        return null
      }
      return {
        key: item.key,
        title: item.title,
        desc: item.desc,
        actionText: item.actionText,
        routeName: target.routeName,
        path: resolved.route.path || target.path,
        query: queryBuilder(item) || {},
        viewCode: item.viewCode,
        component: target.component
      }
    })
    .filter(Boolean)
}

export function openDeviceSubview(proxy, entry) {
  if (!entry?.path) {
    return false
  }
  proxy?.$tab?.openPage?.(entry.title, entry.path, entry.query || {})
  return true
}
