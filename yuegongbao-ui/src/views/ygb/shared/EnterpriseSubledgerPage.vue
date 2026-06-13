<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <div class="card-header__title">{{ config.title }}</div>
            <div class="card-header__tip">{{ config.description }}</div>
          </div>
          <el-tag effect="plain" type="primary">{{ typeLabel }}</el-tag>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-card__label">{{ item.label }}</div>
          <div class="summary-card__value">{{ item.value }}</div>
        </div>
      </div>

      <el-alert
        v-if="isReadOnlyRole"
        :title="`${readOnlyRoleLabel}：当前页面仅保留查询、详情和导出`"
        :description="readOnlyRoleDescription || '当前角色不展示新增、修改和删除操作。'"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 16px"
      />

      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="queryParams.enterpriseName" clearable placeholder="请输入企业名称" style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统一代码" prop="enterpriseCode">
          <el-input v-model="queryParams.enterpriseCode" clearable placeholder="请输入统一社会信用代码" style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="同步状态" prop="syncStatus">
          <el-select v-model="queryParams.syncStatus" clearable style="width: 160px">
            <el-option v-for="item in syncStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option v-for="item in sys_normal_disable" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:enterprise:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:enterprise:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:enterprise:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:enterprise:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="loading" :data="enterpriseList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="企业ID" prop="enterpriseId" width="96" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="统一社会信用代码" prop="enterpriseCode" width="190" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="法定代表人" prop="legalPerson" width="120" />
        <el-table-column label="联系人" prop="contactPerson" width="120" />
        <el-table-column label="联系电话" prop="contactPhone" width="130" />
        <el-table-column label="同步状态" prop="syncStatus" width="110">
          <template #default="scope">
            <dict-tag :options="syncStatusOptions" :value="scope.row.syncStatus" />
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="成立日期" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.establishedDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:enterprise:edit']">修改</el-button>
            <el-button v-if="!isReadOnlyRole" link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:enterprise:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="enterpriseRef" :model="form" :rules="rules" label-width="120px">
        <div class="form-grid">
          <el-form-item label="企业名称" prop="enterpriseName">
            <el-input v-model="form.enterpriseName" placeholder="请输入企业名称" />
          </el-form-item>
          <el-form-item label="统一社会信用代码" prop="enterpriseCode">
            <el-input v-model="form.enterpriseCode" placeholder="请输入统一社会信用代码" />
          </el-form-item>
          <el-form-item label="企业类型">
            <el-input :model-value="typeLabel" disabled />
          </el-form-item>
          <el-form-item label="区域编码" prop="regionCode">
            <el-input v-model="form.regionCode" placeholder="请输入 6 位区域编码" maxlength="6" />
          </el-form-item>
          <el-form-item label="法定代表人" prop="legalPerson">
            <el-input v-model="form.legalPerson" placeholder="请输入法定代表人" />
          </el-form-item>
          <el-form-item label="联系人" prop="contactPerson">
            <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
          </el-form-item>
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="成立日期" prop="establishedDate">
            <el-date-picker
              v-model="form.establishedDate"
              type="date"
              placeholder="请选择成立日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="同步状态" prop="syncStatus">
            <el-radio-group v-model="form.syncStatus">
              <el-radio v-for="item in syncStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="item in sys_normal_disable" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="企业地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入企业地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="企业详情" v-model="detailOpen" width="760px">
      <el-descriptions v-if="detailEnterprise" :column="2" border>
        <el-descriptions-item label="企业名称">{{ detailEnterprise.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="企业类型">{{ typeLabel }}</el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码" :span="2">{{ detailEnterprise.enterpriseCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ formatRegionName(detailEnterprise.regionCode) }}</el-descriptions-item>
        <el-descriptions-item label="同步状态">{{ optionLabel(syncStatusOptions, detailEnterprise.syncStatus) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ optionLabel(sys_normal_disable, detailEnterprise.status) }}</el-descriptions-item>
        <el-descriptions-item label="成立日期">{{ parseTime(detailEnterprise.establishedDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="法定代表人">{{ detailEnterprise.legalPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailEnterprise.contactPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailEnterprise.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="企业地址" :span="2">{{ detailEnterprise.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailEnterprise.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance } from 'vue'
import { useRoute } from 'vue-router'
import { useRoleViewMode } from '@/utils/roleView'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  enterpriseTypeOptions,
  formatRegionName,
  optionLabel,
  regionOptions as allRegionOptions,
  syncStatusOptions,
  useEnterprisePage
} from '@/views/enterprise/useEnterprisePage'

const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

const route = useRoute()
const { proxy } = getCurrentInstance()
const { sys_normal_disable } = useDict('sys_normal_disable')
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)

function buildInitialQuery() {
  const initialQuery = {
    enterpriseType: props.config.fixedEnterpriseType
  }
  const routeFields = props.config.routeQueryFields || ['regionCode', 'enterpriseName', 'enterpriseCode', 'syncStatus', 'status']
  routeFields.forEach(field => {
    if (route.query[field] !== undefined) {
      initialQuery[field] = route.query[field]
    }
  })
  return initialQuery
}

function blockReadOnlyAction(actionLabel) {
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留查询、详情和导出，不能${actionLabel}`)
}

const page = useEnterprisePage({
  exportFilePrefix: props.config.filePrefix || 'ygb_enterprise_subledger',
  initialQueryParams: buildInitialQuery(),
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  actionText: {
    add: `新增${props.config.title}`,
    edit: `修改${props.config.title}`,
    delete: `删除${props.config.title}`
  },
  dialogTitle: {
    add: `新增${props.config.title}`,
    edit: `修改${props.config.title}`
  },
  messages: {
    addSuccess: '新增成功',
    editSuccess: '修改成功',
    deleteSuccess: '删除成功',
    deleteConfirm: enterpriseNames => `是否确认删除 ${enterpriseNames} 的数据项？`
  }
})

const {
  showSearch,
  loading,
  total,
  open,
  detailOpen,
  title,
  enterpriseList,
  detailEnterprise,
  summaryData,
  single,
  multiple,
  queryParams,
  form,
  rules,
  getList,
  cancel,
  handleQuery,
  handleSelectionChange,
  handleRowClick,
  handleUpdate,
  openDetail,
  handleDelete,
  handleExport,
  handleAdd: baseHandleAdd,
  submitForm: baseSubmitForm
} = page

const typeLabel = computed(() => optionLabel(enterpriseTypeOptions, props.config.fixedEnterpriseType, '企业'))

const summaryCards = computed(() => ([
  { key: 'total', label: `${props.config.title}总量`, value: summaryData.value.totalCount ?? total.value ?? 0 },
  { key: 'normal', label: '正常单位', value: summaryData.value.normalCount ?? 0 },
  { key: 'syncError', label: '同步异常', value: summaryData.value.syncErrorCount ?? 0 },
  { key: 'disabled', label: '停用单位', value: summaryData.value.disabledCount ?? 0 }
]))

function handleAdd() {
  baseHandleAdd()
  form.value.enterpriseType = props.config.fixedEnterpriseType
}

function submitForm() {
  form.value.enterpriseType = props.config.fixedEnterpriseType
  baseSubmitForm()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseName: undefined,
    enterpriseCode: undefined,
    enterpriseType: props.config.fixedEnterpriseType,
    syncStatus: undefined,
    status: undefined
  })
  const routeFields = props.config.routeQueryFields || ['regionCode', 'enterpriseName', 'enterpriseCode', 'syncStatus', 'status']
  routeFields.forEach(field => {
    if (route.query[field] !== undefined) {
      queryParams.value[field] = route.query[field]
    }
  })
  getList()
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.card-header__title {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
}

.card-header__tip {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.summary-card {
  padding: 14px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef4fb 100%);
  border: 1px solid #e1eaf4;
}

.summary-card__label {
  color: #627486;
  font-size: 13px;
}

.summary-card__value {
  margin-top: 10px;
  color: #13243a;
  font-size: 26px;
  font-weight: 700;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
