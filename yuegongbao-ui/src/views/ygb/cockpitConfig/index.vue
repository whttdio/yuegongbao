<template>
  <div class="app-container">
    <div class="summary-grid">
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">配置总数</div>
        <div class="summary-value">{{ summary.totalCount || 0 }}</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">启用配置</div>
        <div class="summary-value">{{ summary.activeCount || 0 }}</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">自定义地图中心</div>
        <div class="summary-value">{{ summary.customCenterCount || 0 }}</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">自定义卡片布局</div>
        <div class="summary-value">{{ summary.customCardCount || 0 }}</div>
      </el-card>
      <el-card shadow="never" class="summary-card">
        <div class="summary-label">启用轮播/刷新</div>
        <div class="summary-value">{{ summary.autoRefreshCount || 0 }}</div>
      </el-card>
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>驾驶舱配置</span>
          <div>
            <el-button type="primary" @click="handleAdd" v-hasPermi="['ygb:cockpitConfig:add']">新增配置</el-button>
            <el-button type="warning" @click="handleExport" v-hasPermi="['ygb:cockpitConfig:export']">导出</el-button>
          </div>
        </div>
      </template>

      <el-form ref="queryRef" :model="queryParams" :inline="true">
        <el-form-item label="配置编码">
          <el-input v-model="queryParams.configCode" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="配置名称">
          <el-input v-model="queryParams.configName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="配置编码" prop="configCode" min-width="140" />
        <el-table-column label="配置名称" prop="configName" min-width="160" />
        <el-table-column label="所属区域" min-width="120">
          <template #default="scope">{{ regionLabel(scope.row.regionCode) }}</template>
        </el-table-column>
        <el-table-column label="默认区域" min-width="120">
          <template #default="scope">{{ regionLabel(scope.row.defaultRegionCode) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <dict-tag :options="statusOptions" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="轮播/刷新" min-width="140">
          <template #default="scope">{{ scope.row.rotateSeconds || 0 }}s / {{ scope.row.refreshSeconds || 0 }}s</template>
        </el-table-column>
        <el-table-column label="地图中心" min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ mapCenterText(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="默认焦点" prop="defaultFocusKey" width="120" />
        <el-table-column label="更新时间" width="170">
          <template #default="scope">{{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['ygb:cockpitConfig:edit']">修改</el-button>
            <el-button link type="info" @click="handleView(scope.row)" v-hasPermi="['ygb:cockpitConfig:query']">详情</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['ygb:cockpitConfig:remove']">删除</el-button>
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

    <el-dialog v-model="open" :title="title" width="900px">
      <el-form ref="configRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配置编码" prop="configCode">
              <el-input v-model="form.configCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配置名称" prop="configName">
              <el-input v-model="form.configName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="regionCode">
              <el-select v-model="form.regionCode" style="width: 100%">
                <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认区域" prop="defaultRegionCode">
              <el-select v-model="form.defaultRegionCode" style="width: 100%">
                <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="轮播秒数" prop="rotateSeconds">
              <el-input-number v-model="form.rotateSeconds" :min="0" :step="5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="刷新秒数" prop="refreshSeconds">
              <el-input-number v-model="form.refreshSeconds" :min="0" :step="5" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中心经度" prop="mapCenterLng">
              <el-input-number v-model="form.mapCenterLng" :precision="6" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中心纬度" prop="mapCenterLat">
              <el-input-number v-model="form.mapCenterLat" :precision="6" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地图缩放" prop="mapZoom">
              <el-input-number v-model="form.mapZoom" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认焦点" prop="defaultFocusKey">
              <el-select v-model="form.defaultFocusKey" clearable style="width: 100%">
                <el-option v-for="item in focusKeyOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源模式" prop="sourceMode">
              <el-select v-model="form.sourceMode" style="width: 100%">
                <el-option label="manual" value="manual" />
                <el-option label="stub" value="stub" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="卡片显隐/排序" prop="summaryCardConfig">
          <el-input
            v-model="form.summaryCardConfig"
            type="textarea"
            :rows="4"
            placeholder='示例: [{"key":"warning","visible":true,"order":1}]'
          />
        </el-form-item>
        <el-form-item label="焦点队列配置" prop="focusQueueConfig">
          <el-input
            v-model="form.focusQueueConfig"
            type="textarea"
            :rows="4"
            placeholder='示例: [{"key":"salary","visible":true,"order":1}]'
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="驾驶舱配置详情" width="820px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="配置编码">{{ detail.configCode }}</el-descriptions-item>
        <el-descriptions-item label="配置名称">{{ detail.configName }}</el-descriptions-item>
        <el-descriptions-item label="所属区域">{{ regionLabel(detail.regionCode) }}</el-descriptions-item>
        <el-descriptions-item label="默认区域">{{ regionLabel(detail.defaultRegionCode) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="statusOptions" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="默认焦点">{{ detail.defaultFocusKey || '-' }}</el-descriptions-item>
        <el-descriptions-item label="轮播/刷新">{{ detail.rotateSeconds || 0 }}s / {{ detail.refreshSeconds || 0 }}s</el-descriptions-item>
        <el-descriptions-item label="地图中心">{{ mapCenterText(detail) }}</el-descriptions-item>
        <el-descriptions-item label="卡片显隐/排序" :span="2">{{ detail.summaryCardConfig || '-' }}</el-descriptions-item>
        <el-descriptions-item label="焦点队列配置" :span="2">{{ detail.focusQueueConfig || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="YgbCockpitConfig">
import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addCockpitConfig,
  delCockpitConfig,
  getCockpitConfig,
  getCockpitConfigSummary,
  listCockpitConfig,
  updateCockpitConfig
} from '@/api/ygb/cockpitConfig'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const configList = ref([])
const ids = ref([])
const summary = ref({})
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref(null)

const regionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市顺德区', value: '440606' }
]

const statusOptions = [
  { label: '停用', value: '0' },
  { label: '启用', value: '1' }
]

const focusKeyOptions = [
  { label: '预警', value: 'warning' },
  { label: '合同', value: 'contract' },
  { label: '考勤', value: 'attendance' },
  { label: '工资', value: 'salary' },
  { label: '设备', value: 'device' },
  { label: '工伤', value: 'injury' },
  { label: '联动月报', value: 'socialTax' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    configCode: undefined,
    configName: undefined,
    regionCode: undefined,
    status: undefined
  },
  form: {},
  rules: {
    configCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
    configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
    regionCode: [{ required: true, message: '请选择所属区域', trigger: 'change' }],
    defaultRegionCode: [{ required: true, message: '请选择默认区域', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function regionLabel(value) {
  return regionOptions.find(item => item.value === value)?.label || value || '-'
}

function mapCenterText(row = {}) {
  if (row.mapCenterLng == null || row.mapCenterLat == null) {
    return '-'
  }
  return `${row.mapCenterLng}, ${row.mapCenterLat} / Z${row.mapZoom || '-'}`
}

function reset() {
  form.value = {
    configId: undefined,
    configCode: undefined,
    configName: undefined,
    status: '1',
    regionCode: '440000',
    defaultRegionCode: '440000',
    summaryCardConfig: '[]',
    focusQueueConfig: '[]',
    rotateSeconds: 0,
    refreshSeconds: 30,
    mapCenterLng: undefined,
    mapCenterLat: undefined,
    mapZoom: 11,
    defaultFocusKey: undefined,
    sourceMode: 'manual',
    remark: undefined
  }
  proxy.resetForm('configRef')
}

function getList() {
  loading.value = true
  Promise.all([
    listCockpitConfig(queryParams.value),
    getCockpitConfigSummary(queryParams.value)
  ]).then(([listResponse, summaryResponse]) => {
    configList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summary.value = summaryResponse.data || {}
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.configId)
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增驾驶舱配置'
}

function handleUpdate(row) {
  reset()
  getCockpitConfig(row.configId).then(response => {
    form.value = response.data || {}
    open.value = true
    title.value = '修改驾驶舱配置'
  })
}

function handleView(row) {
  getCockpitConfig(row.configId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.configRef.validate(valid => {
    if (!valid) {
      return
    }
    const request = form.value.configId ? updateCockpitConfig(form.value) : addCockpitConfig(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const configIds = row.configId || ids.value
  proxy.$modal.confirm('确认删除所选驾驶舱配置吗？').then(() => {
    return delCockpitConfig(configIds)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  })
}

function handleExport() {
  proxy.download('/ygb/cockpit/config/export', { ...queryParams.value }, `cockpit_config_${Date.now()}.xlsx`)
}

getList()
</script>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.summary-card {
  min-height: 96px;
}

.summary-label {
  color: #64748b;
  font-size: 13px;
}

.summary-value {
  margin-top: 12px;
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@media (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
