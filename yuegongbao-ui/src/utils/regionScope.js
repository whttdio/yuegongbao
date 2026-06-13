import { computed, unref } from 'vue'
import useUserStore from '@/store/modules/user'

function toRegionPrefix(code) {
  if (!code) {
    return ''
  }
  if (code.endsWith('0000')) {
    return code.slice(0, 2)
  }
  if (code.endsWith('00')) {
    return code.slice(0, 4)
  }
  return code
}

export function isRegionAllowed(code, allowedCodes) {
  if (!code) {
    return true
  }
  if (!Array.isArray(allowedCodes) || allowedCodes.length === 0 || allowedCodes.includes('440000')) {
    return true
  }
  const regionPrefix = toRegionPrefix(code)
  return allowedCodes.some(scopeCode => {
    const scopePrefix = toRegionPrefix(scopeCode)
    return regionPrefix.startsWith(scopePrefix)
  })
}

export function filterAuthorizedRegionOptions(options, allowedCodes) {
  const source = Array.isArray(options) ? options : []
  return source.filter(item => isRegionAllowed(item.value, allowedCodes))
}

export function useAuthorizedRegionOptions(options) {
  const userStore = useUserStore()
  return computed(() => filterAuthorizedRegionOptions(unref(options), userStore.allowedRegionCodes))
}

export function authorizedDefaultRegionCode(fallback = '440000') {
  const userStore = useUserStore()
  const allowedCodes = userStore.allowedRegionCodes
  if (!Array.isArray(allowedCodes) || allowedCodes.length === 0 || allowedCodes.includes('440000')) {
    return fallback
  }
  return allowedCodes[0] || fallback
}
