import {
  addLedgerSubmodule,
  delLedgerSubmodule,
  getLedgerSubmodule,
  getLedgerSubmoduleSummary,
  listLedgerSubmodule,
  updateLedgerSubmodule
} from '@/api/ygb/ledger'

export function createLedgerPageConfig(options) {
  const {
    submodule,
    title,
    description,
    recordNameLabel = '台账名称',
    recordNamePlaceholder = '请输入台账名称',
    defaultCategoryCode = submodule,
    defaultSourceLabel = '监管联动',
    filters = ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'recordName', 'workflowStatus', 'status']
  } = options

  const permPrefix = `ygb:${submodule}`
  const resolvedTitle = isBrokenText(title) ? submodule : title
  const resolvedDescription = isBrokenText(description)
    ? `围绕${resolvedTitle}维护台账记录、办理状态、企业人员关联和导出归档。`
    : description
  const resolvedRecordNameLabel = isBrokenText(recordNameLabel) ? '台账名称' : recordNameLabel
  const resolvedRecordNamePlaceholder = isBrokenText(recordNamePlaceholder) ? `请输入${resolvedTitle}名称` : recordNamePlaceholder
  const resolvedDefaultSourceLabel = isBrokenText(defaultSourceLabel) ? '监管联动' : defaultSourceLabel

  return {
    title: resolvedTitle,
    description: resolvedDescription,
    permPrefix,
    filePrefix: `ledger_${submodule}`,
    defaultCategoryCode,
    defaultSourceLabel: resolvedDefaultSourceLabel,
    recordNameLabel: resolvedRecordNameLabel,
    recordNamePlaceholder: resolvedRecordNamePlaceholder,
    filters,
    routeQueryFields: filters,
    listApi: query => listLedgerSubmodule(submodule, query),
    summaryApi: query => getLedgerSubmoduleSummary(submodule, query),
    detailApi: id => getLedgerSubmodule(submodule, id),
    addApi: data => addLedgerSubmodule(submodule, data),
    updateApi: data => updateLedgerSubmodule(submodule, data),
    deleteApi: ids => delLedgerSubmodule(submodule, ids),
    exportUrl: `ygb/ledger/${submodule}/export`
  }
}

function isBrokenText(value) {
  return typeof value !== 'string' ||
    value.trim() === '' ||
    /\?{2,}/.test(value) ||
    /[�锟閸欑挧閹哄☉閺傜粙缁€瀹告导娴滄妞嬮悽閺侀惄閼卞畝鐎筣]/.test(value)
}
