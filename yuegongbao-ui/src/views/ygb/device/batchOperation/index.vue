<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">设备管理</p>
        <h1 class="ygb-page__title">批量操作</h1>
        <p class="ygb-page__desc">对设备执行批量锁定、解锁、授权等操作，提升运维效率。</p>
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编号" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="queryParams.deviceType" placeholder="请选择设备类型" clearable style="width: 150px">
            <el-option label="考勤机" value="1" />
            <el-option label="芯片设备" value="2" />
            <el-option label="AI摄像头" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Lock" @click="batchLock" v-hasPermi="['ygb:device:lock']">批量锁定</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Unlock" @click="batchUnlock" v-hasPermi="['ygb:device:unlock']">批量解锁</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="CircleCheck" @click="batchAuthorize" v-hasPermi="['ygb:device:authorize']">批量授权</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:device:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">批量操作台账</div>
            <div class="ygb-card-head__desc">集中办理设备启停、参数下发、批量巡检和操作结果回写。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="deviceId" width="80" />
        <el-table-column label="设备编号" align="center" prop="deviceCode" width="160" />
        <el-table-column label="设备名称" align="center" prop="deviceName" min-width="180" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" />
        <el-table-column label="设备类型" align="center" prop="deviceType" width="120" />
        <el-table-column label="设备状态" align="center" prop="deviceStatus" width="100">
          <template #default="scope">
            <dict-tag :options="[{ label: '离线', value: '0' }, { label: '在线', value: '1' }, { label: '锁定', value: '2' }, { label: '故障', value: '3' }]" :value="scope.row.deviceStatus" />
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="120" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="批量操作详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备编号" :span="1">{{ detail.deviceCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备名称" :span="1">{{ detail.deviceName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备类型" :span="1">{{ detail.deviceType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备状态" :span="1">{{ detail.deviceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="安装位置" :span="2">{{ detail.installLocation || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>

    <el-dialog title="批量授权" v-model="authorizeOpen" width="520px" append-to-body>
      <el-form :model="authorizeForm" label-width="90px">
        <el-form-item label="授权人员" required>
          <el-select v-model="authorizeForm.personId" placeholder="请选择授权人员" clearable filterable style="width: 100%">
            <el-option v-for="item in personOptions" :key="item.personId" :label="item.personName" :value="item.personId" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件号码">
          <el-input v-model="authorizeForm.certNo" placeholder="默认使用人员档案证件号" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="authorizeOpen = false">取消</el-button>
          <el-button type="primary" @click="submitBatchAuthorize">确认授权</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbDeviceBatchOperation">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listDevice, getDeviceSummary, batchLockDevice, batchUnlockDevice, batchAuthorizeDevice } from '@/api/ygb/device'
import { ElMessage, ElMessageBox } from 'element-plus'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { optionselectPerson } from '@/api/ygb/person'

function formatMoney(value) {
  if (value === undefined || value === null || value === '') return '-'
  return '¥ ' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])
const { proxy } = getCurrentInstance()
const ids = ref([])
const selectedRows = ref([])
const authorizeOpen = ref(false)
const personOptions = ref([])
const authorizeForm = ref({
  personId: undefined,
  certNo: undefined
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  deviceCode: undefined,
  enterpriseId: undefined,
  deviceType: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listDevice(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function batchLock() {
  if (!ids.value.length) return ElMessage.warning('请选择设备')
  const invalidRows = selectedRows.value.filter(item => String(item.deviceStatus) !== '1')
  if (invalidRows.length) return ElMessage.warning('批量锁定只能选择在线设备')
  await ElMessageBox.confirm('确认锁定已选设备？', '提示', { type: 'warning' })
  await batchLockDevice({ deviceIds: ids.value })
  ElMessage.success('批量锁定成功')
  getList()
}
async function batchUnlock() {
  if (!ids.value.length) return ElMessage.warning('请选择设备')
  const invalidRows = selectedRows.value.filter(item => String(item.deviceStatus) !== '2')
  if (invalidRows.length) return ElMessage.warning('批量解锁只能选择锁定设备')
  await ElMessageBox.confirm('确认解锁已选设备？', '提示', { type: 'warning' })
  await batchUnlockDevice({ deviceIds: ids.value })
  ElMessage.success('批量解锁成功')
  getList()
}
async function batchAuthorize() {
  if (!ids.value.length) return ElMessage.warning('请选择设备')
  const invalidRows = selectedRows.value.filter(item => ['2', '3'].includes(String(item.deviceStatus)))
  if (invalidRows.length) return ElMessage.warning('锁定或故障设备不能批量授权')
  authorizeForm.value = {
    personId: undefined,
    certNo: undefined
  }
  await loadPersonOptions()
  authorizeOpen.value = true
}

async function submitBatchAuthorize() {
  if (!authorizeForm.value.personId) return ElMessage.warning('请选择授权人员')
  await batchAuthorizeDevice({
    deviceIds: ids.value,
    personId: authorizeForm.value.personId,
    certNo: authorizeForm.value.certNo
  })
  ElMessage.success('批量授权成功')
  authorizeOpen.value = false
  getList()
  getSummary()
}

async function getSummary() {
  try {
    const res = await getDeviceSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalDevice', label: '设备总数', value: valueOrDefault(data.totalCount, total.value), unit: '台', note: '当前筛选范围', cardClass: '' },
      { key: 'onlineCount', label: '在线设备', value: valueOrDefault(data.onlineCount, 0), unit: '台', note: '可远程操作', cardClass: 'success' },
      { key: 'selectedCount', label: '已选设备', value: ids.value.length, unit: '台', note: '待批量操作', cardClass: 'primary' }
    ].filter(Boolean)
  } catch (e) {
    console.error(e)
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  getSummary()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
  deviceCode: undefined,
  enterpriseId: undefined,
  deviceType: undefined
  }
  getList()
  getSummary()
}

function handleSelectionChange(selection) {
  selectedRows.value = selection
  ids.value = selection.map(item => item.deviceId).filter(Boolean)
  getSummary()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/device/export', { ...queryParams.value }, `device_batch_operation_${Date.now()}.xlsx`)
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (e) {}
}

async function loadPersonOptions() {
  try {
    const res = await optionselectPerson({ enterpriseId: queryParams.value.enterpriseId })
    personOptions.value = res.data || []
  } catch (e) {
    personOptions.value = []
  }
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
  getSummary()
})
</script>
