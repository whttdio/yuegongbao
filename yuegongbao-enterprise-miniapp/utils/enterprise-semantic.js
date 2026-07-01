import { openPage } from './navigation'

export const ENTERPRISE_PAGE_MODE = {
  MANAGE: 'manage',
  COLLABORATION: 'collaboration',
  READONLY: 'readonly',
  COMMON: 'common'
}

export function buildEnterpriseReadonlyState(options = {}) {
  return {
    mode: options.mode || ENTERPRISE_PAGE_MODE.READONLY,
    badge: options.badge || '企业侧只读参考',
    title: options.title || '当前页面仅提供企业协同查看',
    desc: options.desc || '这项能力依赖员工本人账号或个人身份校验，企业端当前不直接代办。',
    reason: options.reason || '避免企业端触发与个人 worker 语义冲突的提交动作。',
    actionText: options.actionText || '返回企业工作台',
    actionPath: options.actionPath || '/pages/workbench/index',
    secondaryText: options.secondaryText || '',
    secondaryPath: options.secondaryPath || '',
    tips: Array.isArray(options.tips) ? options.tips : []
  }
}

export function openEnterpriseReadonlyState(state) {
  if (state?.actionPath) {
    openPage(state.actionPath)
  }
}

export function openEnterpriseSecondaryAction(state) {
  if (state?.secondaryPath) {
    openPage(state.secondaryPath)
  }
}

export function notifyEnterpriseReadonly(message) {
  uni.showToast({
    title: message || '当前页面为企业侧只读参考',
    icon: 'none'
  })
}

export function buildEnterpriseHealthCards({ homeHero, warnings, quickEntries, sections } = {}) {
  const warningCount = Array.isArray(warnings) ? warnings.length : 0
  const quickCount = Array.isArray(quickEntries) ? quickEntries.length : 0
  const sectionCount = Array.isArray(sections) ? sections.length : 0
  return [
    {
      label: '企业健康度',
      value: homeHero?.healthScore || '--',
      desc: `待办 ${homeHero?.todoCount || 0} / 预警 ${homeHero?.warningCount || 0}`
    },
    {
      label: '主链覆盖',
      value: quickCount ? `${quickCount} 项` : '--',
      desc: `工作台分组 ${sectionCount || 0} 个`
    },
    {
      label: '风险提示',
      value: String(warningCount),
      desc: warningCount ? '需优先跟进企业主链任务' : '当前未发现高优先级提醒'
    }
  ]
}

export function buildEnterpriseScopeText(profile = {}) {
  const parts = [
    profile.enterpriseName || '企业范围未识别',
    profile.jobType || '',
    profile.employmentStatus || '',
    profile.insuranceStatus || ''
  ].filter(Boolean)
  return parts.join(' / ')
}
