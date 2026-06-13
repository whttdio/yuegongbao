function Replace-RegexOnce {
  param(
    [string]$Text,
    [string]$Pattern,
    [string]$Replacement
  )
  $regex = [regex]::new($Pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
  if (-not $regex.IsMatch($Text)) {
    throw "Pattern not found: $Pattern"
  }
  return $regex.Replace($Text, $Replacement, 1)
}

$p = 'D:\javaproject\yuegongbao\YueGongBao-Vue\yuegongbao-ui\src\views\ygb\aiReport\index.vue'
$c = Get-Content -LiteralPath $p -Raw -Encoding UTF8

$header = @'
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">AI 鐩戞祴鎶ュ憡</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          绮ゅ伐淇濅晶閲嶇偣涓嶆槸鍙湅鍒嗘暟锛岃€屾槸鎶?AI 鎶ュ憡鐢熸垚銆侀珮椋庨櫓鎵挎帴銆侀瑙堝綊妗ｅ拰鑱斿姩鏁存敼鐪熸涓叉垚涓€鏉¤繛缁姙鐞嗛摼銆?
        </p>
      </div>
      <div class="ygb-page__tip">
        <div class="ygb-page__tip-item">褰撳墠瑙嗚锛?{{ roleBadge }}</div>
        <div class="ygb-page__tip-item">{{ roleTip }}</div>
        <div class="ygb-page__tip-item">妯″瀷鐗堟湰 {{ currentConfig.version || 'DEFAULT-STUB' }}锛屽綋鍓嶇户缁鐢ㄧ粺涓€ AI 鎶ュ憡鍜屼华琛ㄧ洏鎺ュ彛锛屼笉鎷嗙浜屽鍚庣鏈嶅姟銆?</div>
      </div>
    </section>
'@
$c = Replace-RegexOnce $c '<section class="gov-page-header ygb-page__header">[\s\S]*?</section>' $header

$focusBlock1 = @'
    <div class="ygb-focus-grid">
      <el-card class="ygb-panel-card" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div>
              <div class="ygb-card-head__title">鍔炵悊鎵挎帴鐒︾偣</div>
              <div class="ygb-card-head__desc">{{ roleFocusTip }}</div>
            </div>
          </div>
        </template>
        <div class="ygb-focus-list ygb-focus-list--single">
          <button
            v-for="item in focusQueues"
            :key="item.key"
            type="button"
            class="ygb-focus-queue"
            :class="{ 'is-active': item.key === activeFocus?.key }"
            @click="handleSelectFocus(item)"
          >
            <div class="ygb-focus-queue__main">
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
            <div class="ygb-focus-queue__side">
              <span class="ygb-focus-queue__count">{{ item.count }}{{ item.unit }}</span>
              <span class="ygb-focus-queue__action">{{ item.actionText }}</span>
            </div>
          </button>
        </div>
      </el-card>

      <el-card class="ygb-panel-card" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div>
              <div class="ygb-card-head__title">褰撳墠閫変腑鎶ュ憡</div>
              <div class="ygb-card-head__desc">鐐瑰嚮鎶ュ憡鍚庣洿鎺ユ壙鎺ュ尯鍩熴€佸懆鏈熴€侀闄╃瓑绾у拰妯″瀷鐗堟湰锛屽噺灏戝弽澶嶆墦寮€璇︽儏纭瀵硅薄銆?</div>
            </div>
          </div>
        </template>
        <div class="ygb-source-list">
          <div v-for="item in selectedReportOverview" :key="item.label" class="ygb-source-item">
            <div class="ygb-source-item__label">{{ item.label }}</div>
            <div class="ygb-source-item__value">{{ item.value }}</div>
          </div>
        </div>
        <div class="ygb-recommend-panel">
          <div class="ygb-recommend-panel__title">褰撳墠鍔炵悊寤鸿</div>
          <div class="ygb-recommend-panel__summary">{{ currentActionSummary }}</div>
          <div class="ygb-tag-list">
            <el-tag v-for="item in currentActionTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
        <div class="ygb-focus-actions">
          <el-button type="primary" :disabled="primaryAction.action !== 'generate' && !selectedReport" @click="handlePrimaryAction">
            {{ primaryAction.label }}
          </el-button>
          <el-button plain :disabled="!selectedReport" @click="openDetail(selectedReport)">鏌ョ湅璇︽儏</el-button>
          <el-button plain @click="openModule(secondaryAction.path)">{{ secondaryAction.label }}</el-button>
          <el-button plain :disabled="!canExport" @click="handleExport">瀵煎嚭褰撳墠鏉′欢</el-button>
        </div>
      </el-card>
    </div>
'@
$c = Replace-RegexOnce $c '<div class="ygb-focus-grid">[\s\S]*?(?=\s*<div class="ygb-focus-grid">)' $focusBlock1

$focusBlock2 = @'
    <div class="ygb-focus-grid">
      <el-card class="ygb-panel-card" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">鎶ュ憡鍔炵悊璺緞</div>
            <div class="ygb-card-head__desc">鎸夌菠宸ヤ繚鍔炵悊閾捐矾鎶?AI 鐩戞祴鎶ュ憡鍥哄寲涓?鈥滈攣瀹氭牱鏈? 鐢熸垚鎶ュ憡 / 鎵挎帴椋庨櫓 / 棰勮褰掓。鈥濄€?</div>
          </div>
        </template>
        <div class="ygb-step-list">
          <div v-for="(item, index) in workflowSteps" :key="item.label" class="ygb-step-item">
            <span class="ygb-step-item__index">{{ String(index + 1).padStart(2, '0') }}</span>
            <div class="ygb-step-item__body">
              <strong>{{ item.label }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="ygb-panel-card" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">鎶ュ憡鍔炵悊鎻愮ず</div>
            <div class="ygb-card-head__desc">鍩轰簬褰撳墠鐒︾偣銆侀€変腑鎶ュ憡鍜屾ā鍨嬪彛寰勭粰鍑烘渶搴斿厛澶勭悊鐨勬彁绀恒€?</div>
          </div>
        </template>
        <div class="ygb-tag-list">
          <el-tag v-for="item in pageHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
        </div>
      </el-card>
    </div>

    <el-alert
      v-if="isReadOnlyRole"
      :title="`${readOnlyRoleLabel}锛氬綋鍓嶉〉闈粎淇濈暀鎽樿銆佽鎯呫€侀瑙堝拰瀵煎嚭`"
      :description="readOnlyRoleDescription || '鐢熸垚銆侀噸鐢熸垚绛夊姙鐞嗗姩浣滃凡鑷姩鏀跺彛銆?'"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 18px"
    />
'@
$c = Replace-RegexOnce $c '<div class="ygb-focus-grid">[\s\S]*?(?=\s*<div class="ai-report-layout">)' $focusBlock2

$toolbarButtons = @'
            <el-form-item>
              <el-button icon="Refresh" @click="resetQuery">閲嶇疆</el-button>
              <el-button v-if="canGenerate" type="primary" plain icon="MagicStick" @click="openGenerateDialog()">
                鐢熸垚鎶ュ憡
              </el-button>
              <el-button v-if="canExport" type="warning" plain icon="Download" @click="handleExport">
                瀵煎嚭鍒楄〃
              </el-button>
            </el-form-item>
'@
$c = Replace-RegexOnce $c '<el-form-item>\s*<el-button icon="Refresh" @click="resetQuery">[\s\S]*?</el-form-item>' $toolbarButtons

$c = $c.Replace(':data="reportList"', ':data="visibleReportList"')
$c = Replace-RegexOnce $c '<el-table-column label="[^"]*" width="220" align="center">[\s\S]*?</el-table-column>' @'
            <el-table-column label="鎿嶄綔" :width="tableActionWidth" align="center">
              <template #default="scope">
                <el-button link type="primary" icon="View" @click.stop="openDetail(scope.row)">璇︽儏</el-button>
                <el-button link type="primary" icon="Document" @click.stop="selectAndPreview(scope.row)">棰勮</el-button>
                <el-button v-if="canGenerate" link type="primary" icon="RefreshRight" @click.stop="openGenerateDialog(scope.row)">閲嶇敓鎴?</el-button>
              </template>
            </el-table-column>
'@

$c = Replace-RegexOnce $c '</el-descriptions>\s*' @'
</el-descriptions>

        <div class="ygb-detail-block">
          <h3>褰撳墠瑙嗚閲嶇偣</h3>
          <p class="ygb-detail-focus">{{ detailFocusText }}</p>
        </div>

'@

$c = $c.Replace("import { ElMessage } from 'element-plus'", "import { ElMessage } from 'element-plus'`r`nimport { useRouter } from 'vue-router'`r`nimport useUserStore from '@/store/modules/user'`r`nimport { useRoleViewMode } from '@/utils/roleView'")

$helperBlock = @'
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')

function hasPermissionPrefix(permissions, prefixes) {
  if (!Array.isArray(permissions)) {
    return false
  }
  if (permissions.includes('*:*:*')) {
    return true
  }
  return permissions.some(permission => prefixes.some(prefix => permission === prefix || permission.startsWith(prefix)))
}

function hasPermission(permissions, targets) {
  if (!Array.isArray(permissions)) {
    return false
  }
  if (permissions.includes('*:*:*')) {
    return true
  }
  return targets.some(target => {
    if (permissions.includes(target)) {
      return true
    }
    const segments = target.split(':')
    if (segments.length === 3) {
      return permissions.includes(`${segments[0]}:${segments[1]}:*`)
    }
    return false
  })
}

function isFinanceView(roles, permissions) {
  if (roles.includes('ygb_hrss_supervisor') || roles.includes('ygb_enterprise_operator')) {
    return false
  }
  const financePrefixes = ['ygb:salaryBatch', 'ygb:salaryDetail', 'ygb:socialPayment', 'ygb:socialBaseCompare', 'ygb:taxCompare']
  return hasPermissionPrefix(permissions, financePrefixes)
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

function uniqueTags(tags = []) {
  const deduped = new Map()
  tags.forEach(tag => {
    if (tag?.label && !deduped.has(tag.label)) {
      deduped.set(tag.label, tag)
    }
  })
  return [...deduped.values()]
}
'@
$c = $c.Replace("const { proxy } = getCurrentInstance()", "const { proxy } = getCurrentInstance()`r`n`r`n$helperBlock")

$usePageBlock = @'
} = useAiReportPage({
  exportFilePrefix: 'ai_report',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction(actionLabel) {
    const roleLabel = readOnlyRoleLabel.value || '褰撳墠瑙掕壊'
    proxy?.$modal?.msgWarning?.(`${roleLabel}涓嶈兘${actionLabel}`)
  },
  afterLoad() {
    syncActiveFocus()
  }
})
'@
$c = Replace-RegexOnce $c '\} = useAiReportPage\(\{[\s\S]*?\}\)' $usePageBlock

$selectedBlock = @'
const permissions = computed(() => userStore.permissions || [])
const roleView = computed(() => {
  if (isReadOnlyRole.value) {
    return 'readonly'
  }
  const roles = userStore.roles || []
  const currentPermissions = permissions.value || []
  if (roles.includes('ygb_hrss_supervisor')) {
    return 'hrss'
  }
  if (roles.includes('ygb_enterprise_operator')) {
    return 'operator'
  }
  if (isFinanceView(roles, currentPermissions)) {
    return 'finance'
  }
  if (roles.includes('ygb_enterprise_admin')) {
    return 'admin'
  }
  return 'default'
})
const canGenerate = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:aiReport:generate']))
const canExport = computed(() => hasPermission(permissions.value, ['ygb:aiReport:export']))
const selectedReport = computed(() => defaultSelectedReport.value)
const averageScore = computed(() => formatDecimal(dashboard.averageScore))
const scoreCards = computed(() => dashboard.scoreCards || [])
const topRankingList = computed(() => dashboard.topRankingList || [])
const bottomRankingList = computed(() => dashboard.bottomRankingList || [])
const highRiskList = computed(() => dashboard.highRiskList || [])
const trendPoints = computed(() => dashboard.trendPoints || [])

function countRows(predicate) {
  return reportList.value.filter(predicate).length
}

function matchWorkbenchFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'all') {
    return true
  }
  if (focusKey === 'highRisk' || focusKey === 'lowScore') {
    return matchReportFocus(row, focusKey, dashboard.averageScore)
  }
  if (focusKey === 'bottom') {
    return bottomRankingList.value.some(item => item.reportId === row.reportId)
  }
  if (focusKey === 'preview') {
    return !String(row.reportPdfUrl || '').trim()
  }
  return false
}

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

const roleBadge = computed(() => {
  if (isInsurerRole.value) return '淇濋櫓鍙鍗忓悓'
  if (isBankRole.value) return '閾惰鍙鐪嬫澘'
  if (roleView.value === 'hrss') return '浜虹ぞ鐩戠瑙嗚'
  if (roleView.value === 'operator') return '浼佷笟缁忓姙瑙嗚'
  if (roleView.value === 'finance') return '璐㈠姟缁忓姙瑙嗚'
  if (roleView.value === 'admin') return '浼佷笟绠＄悊鍛樿瑙?'
  return '缁煎悎鍔炵悊瑙嗚'
})

const roleTitle = computed(() => {
  if (isInsurerRole.value) return 'AI 椋庨櫓鎽樿鍗忓悓鐪嬫澘'
  if (isBankRole.value) return 'AI 椋庨櫓涓庝俊鐢ㄨ仈鍔ㄧ湅鏉?'
  if (roleView.value === 'hrss') return '鍖哄煙 AI 鐩戞祴澶嶆牳涓庣洃绠″彴璐?'
  if (roleView.value === 'operator') return 'AI 鎶ュ憡鐢熸垚涓庡洖鍐欏彴璐?'
  if (roleView.value === 'finance') return 'AI 浣庡垎鏍锋湰鍥炴煡涓庤储鍔¤仈鍔ㄥ彴璐?'
  if (roleView.value === 'admin') return 'AI 鐩戞祴鎶ュ憡闂幆宸ヤ綔鍙?'
  return 'AI 鐩戞祴鎶ュ憡鍔炵悊鍙拌处'
})

const roleDescription = computed(() => {
  if (isInsurerRole.value) {
    return '闈㈠悜淇濋櫓鍗忓悓瑙嗚缁熶竴鏌ョ湅 AI 鎶ュ憡鎽樿銆侀珮椋庨櫓瀵硅薄鍜屾ā鍨嬪彛寰勶紝鏂逛究淇濆崟椋庨櫓澶嶆牳銆?'
  }
  if (isBankRole.value) {
    return '闈㈠悜閾惰鍙瑙嗚缁熶竴鏌ョ湅 AI 椋庨櫓鎽樿銆佸熬閮ㄦ牱鏈拰鍚庣画淇＄敤鑱斿姩鍙傝€冦€?'
  }
  if (roleView.value === 'hrss') {
    return '闈㈠悜浜虹ぞ鐩戠缁忓姙缁熶竴鎵挎帴鍖哄煙楂橀闄╂牱鏈€佸熬閮ㄦ帓鍚嶅拰 AI 缁撹锛岄噸鐐规槸鎶婃姤鍛婄粨鏋滃洖钀藉埌鍖哄煙鐩戠鍜屾湀鎶ヨВ閲娿€?'
  }
  if (roleView.value === 'operator') {
    return '闈㈠悜浼佷笟缁忓姙缁熶竴璺熻繘 AI 鎶ュ憡鐢熸垚缁撴灉锛岄噸鐐规槸鍏堣ˉ鐢熸垚褰撳墠鍛ㄦ湡鎴栧尯鍩熺殑鎶ュ憡锛屽啀鍥炲啓瑙勫垯璇存槑鍜岀暀鐥曘€?'
  }
  if (roleView.value === 'finance') {
    return '闈㈠悜璐㈠姟缁忓姙缁熶竴鍥炵湅浣庡垎鎴栭珮椋庨櫓 AI 鎶ュ憡锛岄噸鐐规槸鎶婃姤鍛婄粨璁虹洿鎺ヤ覆鍥炲伐璧勩€佺ぞ淇濄€佷釜绋庡拰淇＄敤鏁存敼涓婚摼銆?'
  }
  if (roleView.value === 'admin') {
    return '闈㈠悜浼佷笟绠＄悊鍛樼粺涓€鎵挎帴 AI 鎶ュ憡鐢熸垚銆侀珮椋庨櫓鎵挎帴銆佹ā鍨嬪彛寰勫榻愬拰棰勮褰掓。鍔ㄤ綔銆?'
  }
  return '闈㈠悜浼佷笟绠＄悊鍛樸€佽储鍔″拰鐩戠缁忓姙缁熶竴鏌ョ湅 AI 鐩戞祴鎶ュ憡缁撴灉銆?'
})

const roleTip = computed(() => {
  if (isReadOnlyRole.value) {
    return '褰撳墠鍙瑙嗚浼樺厛澶嶆牳鎶ュ憡缁撹銆侀珮椋庨櫓瀵硅薄鍜屾ā鍨嬪彛寰勶紝鍐嶈浆浜ょ粰涓氬姟瑙掕壊澶勭悊銆?'
  }
  if (roleView.value === 'hrss') {
    return '浼樺厛閿佸畾楂橀闄╂牱鏈拰灏鹃儴鎶ュ憡锛屽噺灏戝湪鎶ヨ〃銆侀璀﹀拰 AI 缁撹涔嬮棿鏉ュ洖鍒囨崲銆?'
  }
  if (roleView.value === 'operator') {
    return '浼樺厛琛ョ敓鎴愬綋鍓嶅懆鏈熸姤鍛婂拰棰勮鐣欑棔锛屼笉璁╅〉闈㈠仠鐣欏湪鍙湁琛ㄦ牸鐨勯潤鎬佸垪琛ㄣ€?'
  }
  if (roleView.value === 'finance') {
    return '浼樺厛鍥炵湅浣庡垎鏍锋湰銆侀珮椋庨櫓瀵硅薄鍜屽悗浣嶆帓鍚嶏紝鍑忓皯璐㈠姟鍦?AI 鎶ュ憡銆佺ぞ淇濆拰涓◣閾捐矾闂存潵鍥炲垏鎹?'
  }
  if (roleView.value === 'admin') {
    return '浼樺厛缁熺浼氱粙鍏ラ璀﹀拰淇＄敤涓婚摼鐨勫叧閿姤鍛婏紝鍐嶅喅瀹氭槸鍚︾珛鍗抽噸鐢熸垚鎴栬繘鍏ヨ︽儏鎵挎帴銆?'
  }
  return '鍚庣画濡傛嫓鎺ョ湡瀹?PDF 鎴栫绔犲嚟璇侊紝浠嶄互鏈〉浣滀负缁熶竴鎶ュ憡鍔炵悊鍏ュ彛銆?'
})

const roleFocusTip = computed(() => {
  if (roleView.value === 'hrss') return '鍏堢湅鍖哄煙楂橀闄╁拰灏鹃儴鎶ュ憡锛屽啀鍐冲畾鏄惁杩涘叆棰勮銆佹湀鎶ユ垨鍖哄煙鐩戠鍙拌处銆?'
  if (roleView.value === 'operator') return '鍏堣ˉ褰撳墠鍛ㄦ湡鏈敓鎴?PDF 鎴栨湭棰勮鐨勬姤鍛婏紝鍐嶈繘琛屽洖鍐欏拰鐣欑棔銆?'
  if (roleView.value === 'finance') return '鍏堥攣瀹氫綆鍒嗐€侀珮椋庨櫓鍜屽熬閮ㄦ牱鏈紝蹇€熷垽鏂摢浜涚粨璁洪渶瑕佸洖鍒拌储鍔℃暣鏀逛富閾俱€?'
  if (roleView.value === 'admin') return '鍏堢湅褰撳墠鍛ㄦ湡鏍锋湰瑙勬ā銆侀珮椋庨櫓瀵硅薄鍜屾湭棰勮鎶ュ憡锛屽啀鍐冲畾鏄惁绔嬪嵆閲嶇敓鎴愭垨鎵挎帴銆?'
  return '鍏堢湅褰撳墠鍛ㄦ湡鏍锋湰瑙勬ā銆侀珮椋庨櫓瀵硅薄鍜屽悗浣嶆牱鏈紝鍐嶅喅瀹氭槸鍚︾珛鍗宠ˉ鐢熸垚銆佸鏍告垨涓撻」璺熻繘銆?'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('lowScore', '浣庡垎鏍锋湰', countRows(row => matchWorkbenchFocus(row, 'lowScore')), '浠?, '浼樺厛鍥炵湅浼氱壍鍔ㄥ伐璧勩€佺ぞ淇濆拰涓◣鏁存敼鐨勪綆鍒嗘姤鍛娿€?', 'ygb-summary-card--primary'),
      summaryCard('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '閫傚悎鐩存帴鎺ュ洖淇＄敤璇勫垎鍜岀ぞ绋庡紓甯歌仈鍔ㄩ摼銆?', 'ygb-summary-card--warning'),
      summaryCard('bottom', '灏鹃儴鏍锋湰', bottomRankingList.value.length, '浠?, '渚夸簬璐㈠姟蹇€熷垽鏂摢浜涙姤鍛婇渶瑕佸厛鍥炴煡鍙ｅ緞銆?'),
      summaryCard('configVersion', '妯″瀷鐗堟湰', currentConfig.value.version || 'DEFAULT-STUB', '', '纭繚璐㈠姟鍥炴煡銆佹姤琛ㄥ拰 AI 瑙ｉ噴浣跨敤鍚屼竴鍙ｅ緞銆?', 'ygb-summary-card--neutral')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('sampleCount', '鎶ュ憡鏍锋湰', dashboard.totalCount || total.value, '浠?, '褰撳墠鍖哄煙鍛ㄦ湡涓嬪凡鍙洃绠″鏍哥殑鎶ュ憡鏍锋湰銆?', 'ygb-summary-card--primary'),
      summaryCard('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '闇€瑕佷紭鍏堢撼鍏ラ璀﹀拰鍖哄煙鐩戠鎵挎帴鐨勫璞°€?', 'ygb-summary-card--warning'),
      summaryCard('bottom', '灏鹃儴鎶ュ憡', bottomRankingList.value.length, '浠?, '渚夸簬浜虹ぞ鐩戠閿佸畾鍚勫急缁村害鍜屽尯鍩熷樊寮傘€?'),
      summaryCard('configVersion', '妯″瀷鐗堟湰', currentConfig.value.version || 'DEFAULT-STUB', '', '鍖哄煙瑙ｉ噴銆佹湀鎶ュ拰椋庨櫓鐮斿垽鍏辩敤褰撳墠鍙ｅ緞銆?', 'ygb-summary-card--neutral')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('sampleCount', '褰撳墠鏍锋湰', dashboard.totalCount || total.value, '浠?, '鍏堝垽鏂褰撳墠鍛ㄦ湡鏄惁宸插舰鎴愬彲鐢ㄦ姤鍛婂簳鏁般€?', 'ygb-summary-card--primary'),
      summaryCard('preview', '寰呴瑙堟姤鍛?, countRows(row => matchWorkbenchFocus(row, 'preview')), '浠?, '閫傚悎浼佷笟缁忓姙鍏堣ˉ棰勮鐣欑棔鍜屾満鏋勮鏄庛€?', 'ygb-summary-card--warning'),
      summaryCard('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '浼佷笟缁忓姙闇€瑕佸厛琛ラ綈璇存槑鍜屾潗鏂欑殑瀵硅薄銆?'),
      summaryCard('configVersion', '妯″瀷鐗堟湰', currentConfig.value.version || 'DEFAULT-STUB', '', '棰勭暀鍚庣画涓庢ā鍨嬮厤缃€丅I 鎶ヨ〃鐣欑棔鑱斿姩銆?', 'ygb-summary-card--neutral')
    ]
  }
  return [
    summaryCard('sampleCount', '鎶ュ憡鏍锋湰', dashboard.totalCount || total.value, '浠?, '褰撳墠绛涢€夋潯浠朵笅鍙敤浜庡姣斿垎鏋愮殑鏍锋湰鏁伴噺銆?', 'ygb-summary-card--primary'),
    summaryCard('avgScore', '骞冲潎寰楀垎', averageScore.value, '鍒?, '鐢ㄤ簬鍒ゆ柇褰撳墠鍛ㄦ湡鏁翠綋椋庨櫓姘村钩鍜屾ā鍨嬭緭鍑鸿川閲忋€?', 'ygb-summary-card--success'),
    summaryCard('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '闇€瑕佷紭鍏堣仈鍔ㄥ鏍稿拰鍚庣画娌荤悊鐨勯噸鐐瑰璞°€?', 'ygb-summary-card--warning'),
    summaryCard('configVersion', '妯″瀷鐗堟湰', currentConfig.value.version || 'DEFAULT-STUB', '', '褰撳墠缁熶竴璇勫垎鍙ｅ緞锛屼緵鍚勭骇绠＄悊鍜屽姣斾娇鐢ㄣ€?', 'ygb-summary-card--neutral')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('lowScore', '浣庡垎鏍锋湰', countRows(row => matchWorkbenchFocus(row, 'lowScore')), '浠?, '鍏堥攣瀹氫綆浜庡綋鏈熷钩鍧囧垎鐨勬姤鍛婏紝鍑忓皯璐㈠姟鍦?AI 銆佺ぞ淇濆拰涓◣鍙拌处涔嬮棿鏉ュ洖鍒囨崲銆?', '鍥炵湅浣庡垎鏍锋湰'),
      focusQueue('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '浼樺厛鎵挎帴鐜扮孩鑹查闄╂牱鏈紝蹇€熷垽鏂槸鍚﹂渶瑕佺粰绀句繚銆佺◣鍔℃垨淇＄敤涓婚摼鍥炲啓銆?', '鑱斿姩璐㈠姟鏁存敼'),
      focusQueue('bottom', '灏鹃儴鏍锋湰', bottomRankingList.value.length, '浠?, '鎶?Top / Bottom 瀵规瘮鐩存帴鎷夊洖璐㈠姟瑙嗚锛屽厛鐪嬫渶寮辩殑鎶ュ憡鏍锋湰銆?', '鍥炵湅灏鹃儴鎺掑悕'),
      focusQueue('all', '鍏ㄩ儴鏍锋湰', dashboard.totalCount || total.value, '浠?, '鍦ㄤ綆鍒嗗拰楂橀闄╀箣澶栵紝缁熶竴鍥炵湅褰撳墠鍛ㄦ湡鎶ュ憡鍙拌处銆?', '鏌ョ湅鍏ㄩ儴鏍锋湰')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('highRisk', '鍖哄煙楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '鍏堢湅鍖哄煙鐩戠鏈€搴旇鎵挎帴鐨勯珮椋庨櫓鎶ュ憡锛屽啀鍐冲畾鏄惁杩涘叆棰勮鎴栨湀鎶ュ綊妗ｃ€?', '鎵挎帴楂橀闄╁璞?'),
      focusQueue('bottom', '灏鹃儴鎺掑悕', bottomRankingList.value.length, '浠?, '鍏堥攣瀹氬熬閮ㄦ姤鍛婏紝蹇€熷鏍稿悇寮辩淮搴﹀拰鍖哄煙宸紓銆?', '鍥炵湅灏鹃儴鏍锋湰'),
      focusQueue('preview', '鏃犲嚟璇佹姤鍛?, countRows(row => matchWorkbenchFocus(row, 'preview')), '浠?, '渚夸簬鐩戠鍏堢‘璁ゆ槸鍚﹂渶瑕佽ˉ棰勮銆佽ˉ鐣欑棔鎴栬ˉ璇存槑銆?', '鏌ョ湅棰勮鐣欑棔'),
      focusQueue('all', '鍏ㄩ儴鏍锋湰', dashboard.totalCount || total.value, '浠?, '鍦ㄥ畬鎴愰珮椋庨櫓鍜屽熬閮ㄦ牱鏈鏍稿悗锛屽啀缁熶竴鍥炵湅鍖哄煙鍛ㄦ湡鍙拌处銆?', '鏌ョ湅鍏ㄩ儴鏍锋湰')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('preview', '寰呴瑙堟姤鍛?, countRows(row => matchWorkbenchFocus(row, 'preview')), '浠?, '浼佷笟缁忓姙鍏堣ˉ褰撳墠鍛ㄦ湡鏈瑙堛€乸df 鍗犱綅鎴栫暀鐥曚俊鎭笉瀹屾暣鐨勬姤鍛娿€?', '琛ラ瑙堢暀鐥?'),
      focusQueue('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '鍏堜负楂橀闄╂姤鍛婅ˉ鍏呰鏄庡拰闄勪欢锛屼究浜庡悗缁鐞嗗憳鎴栫洃绠℃壙鎺ャ€?', '琛ヨ鏄庢潗鏂?'),
      focusQueue('all', '鍏ㄩ儴鏍锋湰', dashboard.totalCount || total.value, '浠?, '鎶?AI 鎶ュ憡鍙拌处浣滀负鍛ㄦ湡鍔炵悊鏍锋湰搴曠锛岀粺涓€鍥炵湅銆?', '鏌ョ湅鍏ㄩ儴鏍锋湰')
    ]
  }
  return [
    focusQueue('highRisk', '楂橀闄╂牱鏈?, dashboard.highRiskCount || 0, '浠?, '鍏堥攣瀹氶珮椋庨櫓 AI 鎶ュ憡锛屽啀鍐冲畾鏄惁闇€瑕佽仈鍔ㄩ璀﹀拰淇＄敤涓婚摼銆?', '鎵挎帴楂橀闄╁璞?'),
    focusQueue('lowScore', '浣庡垎鏍锋湰', countRows(row => matchWorkbenchFocus(row, 'lowScore')), '浠?, '鎶婁綆浜庡钩鍧囧垎鐨勬姤鍛婁紭鍏堋搁《锛屽揩閫熷垽鏂摢浜涢渶瑕佸洖鍒拌储鍔℃垨棰勮涓婚摼銆?', '鍥炵湅浣庡垎鏍锋湰'),
    focusQueue('preview', '寰呴瑙堟姤鍛?, countRows(row => matchWorkbenchFocus(row, 'preview')), '浠?, '鍏堣ˉ鏈畬鎴愰瑙堢暀鐥曟垨 PDF 鍗犱綅鐨勬姤鍛婏紝閬垮厤褰掓。鏂。銆?', '琛ラ瑙堢暀鐥?'),
    focusQueue('all', '鍏ㄩ儴鏍锋湰', dashboard.totalCount || total.value, '浠?, '鍦ㄥ畬鎴愰珮椋庨櫓銆佷綆鍒嗗拰棰勮鐒︾偣鍚庯紝鍐嶇粺涓€鍥炵湅褰撳墠鍛ㄦ湡鍙拌处銆?', '鏌ョ湅鍏ㄩ儴鏍锋湰')
  ]
})

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visibleReportList = computed(() => {
  if (!activeFocus.value) {
    return reportList.value
  }
  return prioritizeFocusRows(reportList.value, row => matchWorkbenchFocus(row, activeFocus.value.key))
})

const selectedReportOverview = computed(() => {
  const current = selectedReport.value
  if (!current) {
    return [
      { label: '褰撳墠鐒︾偣', value: activeFocus.value?.title || '鏆傛棤' },
      { label: '鐒︾偣鏁伴噺', value: activeFocus.value ? `${activeFocus.value.count}${activeFocus.value.unit}` : '-' },
      { label: '褰撳墠鍙ｅ緞', value: `${regionNameMap[queryParams.regionCode] || queryParams.regionCode || '-'} / ${reportTypeLabel(queryParams.reportType)}` },
      { label: '妯″瀷鐗堟湰', value: currentConfig.value.version || 'DEFAULT-STUB' }
    ]
  }
  return [
    { label: '褰撳墠鎶ュ憡', value: reportDisplayName(current) },
    { label: '鍛ㄦ湡', value: `${formatDate(current.periodStart)} 鑷?${formatDate(current.periodEnd)}` },
    { label: '椋庨櫓 / 鎺掑悕', value: `${riskLevelLabel(current.riskLevel)} / #${current.rankingNo || '-'}` },
    { label: '妯″瀷鐗堟湰', value: current.configVersion || currentConfig.value.version || 'DEFAULT-STUB' }
  ]
})

const workflowSteps = computed(() => {
  if (roleView.value === 'finance') {
    return [
      { label: '鍏堥攣瀹氫綆鍒嗗拰楂橀闄╂姤鍛?', desc: '浼樺厛鍦?AI 鎶ュ憡鍙拌处閲岄攣瀹氫綆浜庡钩鍧囧垎銆佺孩鑹查闄╁拰灏鹃儴鏍锋湰銆?' },
      { label: '鍐嶅洖鍒拌储鍔℃暣鏀归摼', desc: '鎶婃姤鍛婄粨璁虹洿鎺ヤ覆鍥炲伐璧勩€佺ぞ淇濄€佷釜绋庡拰淇＄敤璇勫垎鍙拌处锛岄伩鍏嶆眹鎬荤粨鏋滃仠鍦ㄦ姤鍛婂眰銆?' },
      { label: '鏈€鍚庨瑙堝綊妗?', desc: '鍦ㄥ洖鏌ュ彛寰勫拰鑱斿姩鏁存敼瀹屾垚鍚庯紝鍐嶉瑙堟姤鍛婂苟浣滀负鏈堝害搴曠褰掓。銆?' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '鍏堢湅鍖哄煙楂橀闄╁璞?', desc: '鎶婂綋鍓嶅尯鍩熸渶搴旇鍏虫敞鐨勯珮椋庨櫓鎶ュ憡鎻愬埌鍓嶅垪銆?' },
      { label: '鍐嶆壙鎺ュ熬閮ㄦ牱鏈鏍?', desc: '閫愭潯鍥炵湅灏鹃儴鎶ュ憡鐨勫急缁村害鍜岄闄╄В閲婏紝鍒ゆ柇鏄惁闇€瑕佽繘鍏ラ璀﹀缃垨鍖哄煙鐫ｅ姙銆?' },
      { label: '鏈€鍚庤繘鍏ユ湀鎶ュ綊妗?', desc: '灏嗗洖鏌ョ粨鏋滄矇娣€鍒板尯鍩熺洃绠℃湀鎶ュ拰鍚庣画鐣欑棔瑙ｉ噴閾捐矾銆?' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '鍏堣ˉ鎶ュ憡鐢熸垚', desc: '鍏堣ˉ褰撳墠鍛ㄦ湡鏈敓鎴愭垨鏈畬鎴愰瑙堢暀鐥曠殑鎶ュ憡銆?' },
      { label: '鍐嶅洖鍐欒鏄庣暀鐥?', desc: '鎶婄敓鎴愭潵婧愩€佽鏄庡拰棰勮鐣欑棔鍥炲啓鍒版姤鍛婂彴璐︼紝鏂逛究鍚庣画绠＄悊鍜岀洃绠℃壙鎺ャ€?' },
      { label: '鏈€鍚庝氦鎺ユ暣鏀?/ 褰掓。', desc: '鍦ㄨˉ榻愮暀鐥曞悗锛屽啀浜ょ敱绠＄悊鍛樻垨鐩戠缁忓姙鎵挎帴鍚庣画椋庨櫓鍔ㄤ綔銆?' }
    ]
  }
  return [
    { label: '閿佸畾鏍锋湰鑼冨洿', desc: `鍏堟寜 ${reportTypeLabel(queryParams.reportType)} 鍛ㄦ湡鍜?${regionNameMap[queryParams.regionCode] || queryParams.regionCode || '-'} 鍖哄煙閿佸畾褰撳墠闇€瑕佸垎鏋愮殑鏍锋湰闆嗗悎銆?` },
    { label: '鐢熸垚鍜屽鏍告姤鍛?', desc: '瀵规柊鍛ㄦ湡鎴栧叧閿尯鍩熷厛鐢熸垚 AI 鎶ュ憡锛屽啀鐢ㄧ淮搴﹀緱鍒嗗拰缁撹鍒ゆ柇鎶ュ憡璐ㄩ噺銆?' },
    { label: '鎵挎帴楂橀闄╁璞?', desc: '缁撳悎灏鹃儴鎺掑悕鍜岄珮椋庨櫓娓呭崟锛屽皢浣庡垎鏍锋湰銆佺孩鑹查闄╁璞＄撼鍏ョ洃绠℃壙鎺ュ彴璐︺€?' },
    { label: '棰勮褰掓。杈撳嚭', desc: '鍦ㄨ鎯呫€侀瑙堝拰瀵煎嚭鍓嶏紝鍏堢‘璁ゆā鍨嬭鏄庛€佹牱鏈彛寰勫拰鍏抽敭缁撹銆?' }
  ]
})

const activeReportSummary = computed(() => buildReportSummary(selectedReport.value))
'@
$c = Replace-RegexOnce $c 'const selectedReport = computed\(\(\) => defaultSelectedReport\.value\)[\s\S]*?const activeReportSummary = computed\(\(\) => buildReportSummary\(selectedReport\.value\)\)' $selectedBlock

$tagBlock = @'
const currentActionSummary = computed(() => {
  if (!selectedReport.value) {
    return activeFocus.value
      ? `褰撳墠宸叉寜鈥?${activeFocus.value.title}鈥濋噸鎺掓姤鍛婂彴璐︼紝寤鸿浼樺厛澶勭悊琛ㄦ牸鍓嶅垪鏍锋湰锛屽啀鍐冲畾鏄惁杩涘叆棰勮銆侀噸鐢熸垚鎴栬仈鍔ㄦ暣鏀归摼銆?`
      : '褰撳墠鏆傛棤 AI 鎶ュ憡鏍锋湰銆?'
  }
  if (isReadOnlyRole.value) {
    return '褰撳墠瑙掕壊浠呬繚鐣欐煡鐪嬨€侀瑙堝拰瀵煎嚭锛屽缓璁紭鍏堝鏍告姤鍛婄粨璁恒€侀珮椋庨櫓瀵硅薄鍜屾ā鍨嬪彛寰勶紝鍐嶈浆浜ょ粰涓氬姟瑙掕壊澶勭悊銆?'
  }
  if (roleView.value === 'finance') {
    if (matchWorkbenchFocus(selectedReport.value, 'highRisk')) {
      return '璇ユ姤鍛婂綋鍓嶅凡灞炰簬楂橀闄╁璞★紝寤鸿浼樺厛鑱斿姩淇＄敤璇勫垎銆佺ぞ淇濆熀鏁板拰涓◣姣斿鍙拌处锛岀‘璁よ储鍔℃暣鏀归」鏄惁宸茬湡姝ｅ洖钀姐€?'
    }
    if (matchWorkbenchFocus(selectedReport.value, 'lowScore')) {
      return '璇ユ姤鍛婁綆浜庡綋鏈熷钩鍧囧垎锛屽缓璁厛鍥炵湅宸ヨ祫鍙戞斁銆佺ぞ淇濄€佷釜绋庡拰棰勮闂幆缁村害锛岄伩鍏嶈储鍔″洖鏌ヤ粛鍋滅暀鍦ㄦ眹鎬婚潰銆?'
    }
    return '褰撳墠鎶ュ憡鍙綔涓鸿储鍔′笌 AI 鑱斿姩鐨勫簳绋匡紝寤鸿鍏堥瑙堢‘璁よВ閲婂彛寰勶紝鍐嶅喅瀹氭槸鍚﹀洖鍒扮ぞ淇濄€佷釜绋庢垨淇＄敤鍙拌处缁х画澶勭悊銆?'
  }
  if (roleView.value === 'hrss') {
    if (matchWorkbenchFocus(selectedReport.value, 'highRisk')) {
      return '璇ユ姤鍛婂綋鍓嶅凡灞炰簬鍖哄煙楂橀闄╂牱鏈紝寤鸿浼樺厛鎻愬彇寮辩淮搴︾粨璁哄拰绠＄悊寤鸿锛屽啀杩涘叆棰勮銆佹湀鎶ュ拰鐩戠鐣欑棔閾捐矾銆?'
    }
    return '褰撳墠鎶ュ憡鍙户缁敤浜庡尯鍩熺洃绠℃煡鐪嬪拰鏈堟姤瑙ｉ噴锛屽缓璁厛纭妯″瀷鐗堟湰銆佽В閲婂彛寰勫拰鍏抽敭缁撹鏄惁瀹屾暣銆?'
  }
  if (roleView.value === 'operator') {
    if (matchWorkbenchFocus(selectedReport.value, 'preview')) {
      return '璇ユ姤鍛婂綋鍓嶄粛缂哄皯棰勮鐣欑棔鎴?PDF 鍗犱綅锛屽缓璁厛棰勮纭锛屽啀琛ュ厖璇存槑鍜屽洖鍐欎俊鎭€?'
    }
    return '褰撳墠鎶ュ憡鏇撮€傚悎鍏堣ˉ璇存槑鍜岄瑙堢暀鐥曪紝鍐嶇敱绠＄悊鍛樻垨鐩戠缁忓姙鎵挎帴鍚庣画椋庨櫓鍔ㄤ綔銆?'
  }
  if (matchWorkbenchFocus(selectedReport.value, 'highRisk')) {
    return '璇ユ姤鍛婂綋鍓嶅凡灞炰簬楂橀闄╂牱鏈紝寤鸿浼樺厛杩涘叆璇︽儏澶嶆牳缁村害鍜?AI 缁撹锛屽啀鎶婂姩浣滃洖钀藉埌棰勮鎴栦俊鐢ㄥ彴璐︺€?'
  }
  return '褰撳墠鎶ュ憡鍙綔涓哄綋鍓嶅懆鏈熺殑 AI 鍔炵悊鏍锋湰锛屽缓璁厛棰勮纭缁撹銆佸悗鍐嶆牴鎹粨鏋滃喅瀹氭槸鍚﹂噸鐢熸垚鎴栬繘鍏ヨ仈鍔ㄦ暣鏀归摼銆?'
})

const currentActionTags = computed(() => {
  const tags = buildBaseAiReportHintTags(selectedReport.value, {
    items: selectedDimensionItems.value,
    averageScore: dashboard.averageScore,
    highRiskCount: dashboard.highRiskCount,
    currentConfigVersion: currentConfig.value.version
  })
  if (activeFocus.value?.title) {
    tags.unshift({ label: `褰撳墠鐒︾偣锛?${activeFocus.value.title}`, type: 'info' })
  }
  if (roleView.value === 'finance') {
    tags.push({ label: '璐㈠姟瑙嗚浼樺厛鍥炵湅宸ヨ祫銆佺ぞ淇濄€佷釜绋庡拰淇＄敤涓婚摼鏄惁涓?AI 鎶ュ憡缁撹瀵归綈銆?', type: 'info' })
  }
  if (roleView.value === 'hrss') {
    tags.push({ label: '浜虹ぞ瑙嗚浼樺厛纭楂橀闄╂牱鏈槸鍚︽彁鍓嶆帴鍏ラ璀﹀拰鍖哄煙鐩戠鐣欑棔銆?', type: 'info' })
  }
  if (roleView.value === 'operator') {
    tags.push({ label: '浼佷笟缁忓姙瑙嗚浼樺厛琛ラ綈棰勮鐣欑棔銆佺敓鎴愯鏄庡拰鍙ｅ緞澶囨敞銆?', type: 'info' })
  }
  return uniqueTags(tags).slice(0, 5)
})

const pageHintTags = computed(() => {
  const tags = [...currentActionTags.value]
  if (dashboard.highRiskCount > 0) {
    tags.push({ label: `褰撳墠浠嶆湁 ${dashboard.highRiskCount} 涓珮椋庨櫓鏍锋湰寰呮壙鎺ワ紝寤鸿浼樺厛鍥炵湅鍓嶅垪鏍锋湰銆?`, type: 'warning' })
  }
  if (countRows(row => matchWorkbenchFocus(row, 'preview')) > 0) {
    tags.push({ label: `褰撳墠浠嶆湁 ${countRows(row => matchWorkbenchFocus(row, 'preview'))} 浠芥姤鍛婄己灏?PDF 鎴栭瑙堢暀鐥曪紝寤鸿鍏堣ˉ榻愬綊妗ｄ緷鎹€?`, type: 'info' })
  }
  if (roleView.value === 'finance' && countRows(row => matchWorkbenchFocus(row, 'lowScore')) > 0) {
    tags.push({ label: '浣庡垎鏍锋湰宸茶嚜鍔ㄩ《鍒板墠鍒楋紝鏂逛究璐㈠姟鐩存帴鍥炲埌绀剧◣鏁存敼涓婚摼銆?', type: 'primary' })
  }
  return uniqueTags(tags).slice(0, 5)
})

const detailFocusText = computed(() => {
  if (isReadOnlyRole.value) {
    return '褰撳墠鍙瑙嗚閲嶇偣鏌ョ湅 AI 缁撹銆侀珮椋庨櫓鍘熷洜銆佹ā鍨嬪彛寰勫拰鍏抽敭缁村害鏄惁瀹屾暣銆?'
  }
  if (roleView.value === 'finance') {
    return '閲嶇偣鐪嬪摢浜涚淮搴﹀拰缁撹浼氱洿鎺ヤ綆璐㈠姟鍚堣鍒嗗拰淇＄敤璇勫垎锛屽啀鍐冲畾鏄惁鍥炲埌绀句繚銆佷釜绋庢垨宸ヨ祫鍙版煡鐪嬨€?'
  }
  if (roleView.value === 'hrss') {
    return '閲嶇偣鐪嬪綋鍓嶅尯鍩熼闄╃瓑绾с€佸熬閮ㄦ帓鍚嶅拰鏁存敼寤鸿鏄惁瓒充互鏀拺鍖哄煙鐩戠瑙ｉ噴銆?'
  }
  if (roleView.value === 'operator') {
    return '閲嶇偣鐪嬫槸鍚﹁繕缂洪瑙堢暀鐥曘€佽鏄庢潗鏂欏拰鐢熸垚鍙ｅ緞澶囨敞锛岄伩鍏嶆姤鍛婂彧鍋滅暀鍦ㄩ潤鎬佸垪琛ㄥ眰銆?'
  }
  return '閲嶇偣鐪嬫姤鍛婄粨璁恒€侀珮椋庨櫓鍘熷洜鍜屽叧閿淮搴﹀緱鍒嗘槸鍚﹁兘鐩存帴鎸囧悜涓嬩竴姝ュ姩浣溿€?'
})

const detailHintTags = computed(() => uniqueTags([
  { label: `褰撳墠瑙嗚閲嶇偣锛?${detailFocusText.value}`, type: 'info' },
  ...buildBaseAiReportHintTags(detailDisplayReport.value, {
    items: detailDimensionItems.value,
    detailMode: true,
    averageScore: dashboard.averageScore,
    currentConfigVersion: currentConfig.value.version
  })
]).slice(0, 5))

const primaryAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: '鏌ョ湅璇︽儏', action: 'detail' }
  }
  if (!selectedReport.value) {
    return canGenerate.value ? { label: '鐢熸垚鎶ュ憡', action: 'generate' } : { label: '鏌ョ湅鎽樿', action: 'detail' }
  }
  if (roleView.value === 'operator') {
    return canGenerate.value ? { label: '閲嶇敓鎴愭姤鍛?', action: 'regenerate' } : { label: '棰勮鎶ュ憡', action: 'preview' }
  }
  if (roleView.value === 'finance') {
    return { label: '棰勮骞跺洖鏌?', action: 'preview' }
  }
  if (roleView.value === 'hrss') {
    return { label: '鏌ョ湅璇︽儏', action: 'detail' }
  }
  if (canGenerate.value && (matchWorkbenchFocus(selectedReport.value, 'highRisk') || matchWorkbenchFocus(selectedReport.value, 'preview'))) {
    return { label: '閲嶇敓鎴愭姤鍛?', action: 'regenerate' }
  }
  return { label: '棰勮鎶ュ憡', action: 'preview' }
})

const secondaryAction = computed(() => {
  if (roleView.value === 'finance') {
    if (activeFocus.value?.key === 'highRisk') {
      return { label: '鏌ョ湅淇＄敤璇勫垎', path: '/ygb/creditScore' }
    }
    return { label: '鏌ョ湅涓◣姣斿', path: '/ygb/taxCompare' }
  }
  if (roleView.value === 'hrss') {
    if (activeFocus.value?.key === 'highRisk') {
      return { label: '鏌ョ湅棰勮涓績', path: '/ygb/warning' }
    }
    return { label: '鏌ョ湅缁熻鎶ヨ〃', path: '/ygb/statReport' }
  }
  if (roleView.value === 'operator') {
    return { label: '鏌ョ湅妯″瀷閰嶇疆', path: '/ygb/aiReportConfig' }
  }
  if (activeFocus.value?.key === 'preview') {
    return { label: '鏌ョ湅妯″瀷閰嶇疆', path: '/ygb/aiReportConfig' }
  }
  if (activeFocus.value?.key === 'highRisk') {
    return { label: '鏌ョ湅棰勮涓績', path: '/ygb/warning' }
  }
  return { label: '鏌ョ湅淇＄敤璇勫垎', path: '/ygb/creditScore' }
})

const tableActionWidth = computed(() => (canGenerate.value ? 220 : 150))

const standardRows = computed(() => {
'@
$c = Replace-RegexOnce $c 'const reportHintTags = computed\(\(\) => buildBaseAiReportHintTags\(selectedReport\.value, \{[\s\S]*?const standardRows = computed\(\(\) => \{' $tagBlock

$actionFns = @'
async function selectAndPreview(row) {
  activeReportId.value = row.reportId
  await loadDashboard(row.reportId)
  await handlePreviewReport()
}

async function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  const matched = visibleReportList.value.find(row => matchWorkbenchFocus(row, item.key))
  if (matched?.reportId) {
    activeReportId.value = matched.reportId
    await loadDashboard(matched.reportId)
    await nextTick()
    renderCharts()
  }
}

function openModule(path) {
  if (!path) return
  router.push(path)
}

async function handlePrimaryAction() {
  if (primaryAction.value.action === 'generate') {
    openGenerateDialog()
    return
  }
  if (!selectedReport.value) {
    return
  }
  if (primaryAction.value.action === 'regenerate') {
    openGenerateDialog(selectedReport.value)
    return
  }
  if (primaryAction.value.action === 'preview') {
    await selectAndPreview(selectedReport.value)
    return
  }
  await openDetail(selectedReport.value)
}
'@
$c = Replace-RegexOnce $c 'async function selectAndPreview\(row\) \{[\s\S]*?\n\}' $actionFns

$c = Replace-RegexOnce $c '\.header-badge-panel \{[\s\S]*?\.header-badge--muted \{[\s\S]*?\}' @'
.ygb-panel-card {
  height: 100%;
}
'@
$c = $c.Replace(".ygb-summary-card,`r`n.ygb-focus-card,`r`n.report-card {", ".ygb-summary-card,`r`n.ygb-panel-card,`r`n.report-card {")
$c = $c.Replace('.ygb-focus-card :deep(.el-card__header)', '.ygb-panel-card :deep(.el-card__header)')
$c = $c.Replace('.ygb-focus-card :deep(.el-card__body)', '.ygb-panel-card :deep(.el-card__body)')

$cssInsert = @'
.ygb-focus-list--single {
  gap: 12px;
}

.ygb-focus-queue {
  width: 100%;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #0f5ea8;
  box-shadow: 0 10px 24px rgba(15, 94, 168, 0.08);
}

.ygb-focus-queue__main strong {
  display: block;
  margin-bottom: 6px;
  color: #13243a;
}

.ygb-focus-queue__main p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #5f6f80;
}

.ygb-focus-queue__side {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
}

.ygb-focus-queue__count {
  font-size: 20px;
  font-weight: 700;
  color: #0f5ea8;
}

.ygb-focus-queue__action {
  font-size: 12px;
  color: #1d4ed8;
}

.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-source-item {
  padding: 14px 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-source-item__label {
  font-size: 12px;
  color: #7b8da1;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #13243a;
  font-size: 16px;
  font-weight: 600;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(15, 94, 168, 0.08), rgba(29, 78, 216, 0.05));
  border: 1px solid rgba(15, 94, 168, 0.15);
}

.ygb-recommend-panel__title {
  color: #0f5ea8;
  font-size: 13px;
  font-weight: 600;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 12px;
  color: #1f2937;
  font-size: 14px;
  line-height: 1.7;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

'@
$c = $c.Replace(".ygb-tag-list {`r`n  display: flex;", "${cssInsert}.ygb-tag-list {")
$c = $c.Replace('.detail-html {', ".ygb-detail-focus {`r`n  margin: 0;`r`n  padding: 14px 16px;`r`n  border-radius: 14px;`r`n  background: #f8fbfe;`r`n  border: 1px solid #e3ebf3;`r`n  color: #5f6f80;`r`n  line-height: 1.8;`r`n}`r`n`r`.detail-html {")
$c = $c.Replace("  .overview-hero,`r`n  .chart-section,`r`n  .ranking-grid,`r`n  .ranking-summary-strip,`r`n  .risk-focus-grid,`r`n  .score-grid,`r`n  .ygb-focus-grid {", "  .overview-hero,`r`n  .chart-section,`r`n  .ranking-grid,`r`n  .ranking-summary-strip,`r`n  .risk-focus-grid,`r`n  .score-grid,`r`n  .ygb-focus-grid,`r`n  .ygb-source-list {")
$c = $c.Replace("  .ygb-summary-grid {`r`n    grid-template-columns: 1fr;`r`n  }", "  .ygb-summary-grid {`r`n    grid-template-columns: 1fr;`r`n  }`r`n`r`n  .ygb-focus-queue {`r`n    flex-direction: column;`r`n  }`r`n`r`n  .ygb-focus-queue__side {`r`n    min-width: 0;`r`n    align-items: flex-start;`r`n  }")

Set-Content -LiteralPath $p -Value $c -Encoding UTF8
