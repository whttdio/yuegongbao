<template>
  <component :is="type" v-bind="linkProps()">
    <slot />
  </component>
</template>

<script setup>
import { isExternal } from '@/utils/validate'
import { PORTAL_CODES } from '@/config/portal'
import {
  AZB_OFFICIAL_PORTAL_ALIAS,
  isOfficialPortalPath,
  resolveOfficialPortalHref,
  YGB_OFFICIAL_PORTAL_ALIAS
} from '@/utils/portal'

const props = defineProps({
  to: {
    type: [String, Object],
    required: true
  },
  linkMeta: {
    type: Object,
    default: () => ({})
  }
})

const router = useRouter()

const isExt = computed(() => {
  return isExternal(props.to)
})

const opensInNewWindow = computed(() => {
  return Boolean(props.linkMeta?.openInNewWindow && props.linkMeta?.cockpitScreenPath)
})

const isOfficialPortal = computed(() => {
  return isOfficialPortalPath(props.to)
})

const type = computed(() => {
  if (isExt.value || isOfficialPortal.value || opensInNewWindow.value) {
    return 'a'
  }
  return 'router-link'
})

function resolveOfficialPortalCodeFromTarget(target) {
  const path = typeof target === 'string' ? target : target?.path || ''
  const normalized = String(path).replace(/^\//, '')
  if (normalized === YGB_OFFICIAL_PORTAL_ALIAS.replace(/^\//, '') || normalized === 'ygbOfficialSite' || normalized === 'portal/ygb-official') {
    return PORTAL_CODES.ygb
  }
  if (normalized === AZB_OFFICIAL_PORTAL_ALIAS.replace(/^\//, '') || normalized === 'officialSite' || normalized === 'portal/official') {
    return PORTAL_CODES.azb
  }
  return undefined
}

function linkProps() {
  const officialPortalCode = resolveOfficialPortalCodeFromTarget(props.to)
  const officialHref = resolveOfficialPortalHref(router, officialPortalCode)
  if (opensInNewWindow.value) {
    const path = props.linkMeta.cockpitScreenPath
    return {
      href: router.resolve(path).href,
      target: '_blank',
      rel: 'noopener noreferrer'
    }
  }
  if (isExt.value) {
    return {
      href: props.to,
      target: '_blank',
      rel: 'noopener'
    }
  }
  if (isOfficialPortal.value) {
    return {
      href: officialHref,
      target: '_blank',
      rel: 'noopener'
    }
  }
  return {
    to: props.to
  }
}
</script>
