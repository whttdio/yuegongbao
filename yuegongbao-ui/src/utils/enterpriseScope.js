import useUserStore from '@/store/modules/user'

const ENTERPRISE_ROLE_KEYS = ['ygb_enterprise_admin', 'ygb_enterprise_operator']

export function isEnterpriseScopedUser(roles = null) {
  const userStore = useUserStore()
  const roleList = Array.isArray(roles) ? roles : userStore.roles
  if (!Array.isArray(roleList) || roleList.length === 0) {
    return false
  }
  return roleList.some(role => ENTERPRISE_ROLE_KEYS.includes(role))
}

export function lockedEnterpriseId() {
  const userStore = useUserStore()
  if (!isEnterpriseScopedUser()) {
    return undefined
  }
  const enterpriseId = userStore.enterpriseId
  return enterpriseId == null || enterpriseId === '' ? undefined : Number(enterpriseId)
}

export function isEnterpriseFilterLocked() {
  return lockedEnterpriseId() != null
}

export function filterAuthorizedEnterpriseOptions(options, enterpriseId = lockedEnterpriseId()) {
  const source = Array.isArray(options) ? options : []
  if (enterpriseId == null) {
    return source
  }
  return source.filter(item => Number(item.enterpriseId) === Number(enterpriseId))
}

export function applyLockedEnterpriseQuery(query = {}) {
  const enterpriseId = lockedEnterpriseId()
  if (enterpriseId == null) {
    return { ...query }
  }
  return {
    ...query,
    enterpriseId
  }
}
