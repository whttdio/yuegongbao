import { computed } from 'vue'
import useUserStore from '@/store/modules/user'

export const READ_ONLY_ROLE_KEYS = Object.freeze(['ygb_insurer', 'ygb_bank'])

const READ_ONLY_ROLE_META = Object.freeze({
  ygb_insurer: {
    roleKey: 'ygb_insurer',
    label: '保险只读协同',
    description: '保留总览、明细和导出入口，不展示录入、生成和治理动作。'
  },
  ygb_bank: {
    roleKey: 'ygb_bank',
    label: '银行只读看板',
    description: '聚焦信用画像和区域报表，不展示生成类和治理录入动作。'
  }
})

export function resolveReadOnlyRoleMeta(roles = []) {
  const matchedRole = roles.find(role => READ_ONLY_ROLE_KEYS.includes(role))
  return matchedRole ? READ_ONLY_ROLE_META[matchedRole] : null
}

export function useRoleViewMode() {
  const userStore = useUserStore()

  const readOnlyMeta = computed(() => resolveReadOnlyRoleMeta(userStore.roles || []))
  const isReadOnlyRole = computed(() => Boolean(readOnlyMeta.value))
  const isInsurerRole = computed(() => readOnlyMeta.value?.roleKey === 'ygb_insurer')
  const isBankRole = computed(() => readOnlyMeta.value?.roleKey === 'ygb_bank')
  const readOnlyRoleLabel = computed(() => readOnlyMeta.value?.label || '只读角色')
  const readOnlyRoleDescription = computed(() => readOnlyMeta.value?.description || '')

  return {
    readOnlyMeta,
    isReadOnlyRole,
    isInsurerRole,
    isBankRole,
    readOnlyRoleLabel,
    readOnlyRoleDescription
  }
}
