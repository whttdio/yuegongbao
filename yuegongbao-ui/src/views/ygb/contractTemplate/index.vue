<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>合同模板库</span>
          <div>
            <el-button type="primary" @click="handleAdd" v-hasPermi="['ygb:contractTemplate:add']">新增模板</el-button>
            <el-button type="warning" @click="handleExport" v-hasPermi="['ygb:contractTemplate:export']">导出</el-button>
          </div>
        </div>
      </template>

      <el-form :model="queryParams" :inline="true" ref="queryRef">
        <el-form-item label="模板名称">
          <el-input v-model="queryParams.templateName" clearable @keyup.enter="getList" />
        </el-form-item>
        <el-form-item label="合同类型">
          <el-select v-model="queryParams.templateType" clearable style="width: 160px">
            <el-option v-for="item in templateTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.reviewStatus" clearable style="width: 160px">
            <el-option v-for="item in reviewStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="启停状态">
          <el-select v-model="queryParams.status" clearable style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="模板编码" prop="templateCode" min-width="140" />
        <el-table-column label="模板名称" prop="templateName" min-width="180" />
        <el-table-column label="版本" prop="templateVersion" width="100" />
        <el-table-column label="合同类型" width="120">
          <template #default="scope">{{ templateTypeLabel(scope.row.templateType) }}</template>
        </el-table-column>
        <el-table-column label="适用范围" prop="applicableScope" min-width="180" show-overflow-tooltip />
        <el-table-column label="审核状态" width="120">
          <template #default="scope">
            <dict-tag :options="reviewStatusOptions" :value="scope.row.reviewStatus" />
          </template>
        </el-table-column>
        <el-table-column label="启停状态" width="120">
          <template #default="scope">
            <el-switch
              :model-value="scope.row.status === '1'"
              @change="value => handleStatusChange(scope.row, value)"
              v-hasPermi="['ygb:contractTemplate:status']"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['ygb:contractTemplate:edit']">修改</el-button>
            <el-button link type="info" @click="handleView(scope.row)" v-hasPermi="['ygb:contractTemplate:query']">详情</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['ygb:contractTemplate:remove']">删除</el-button>
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

    <el-dialog v-model="open" :title="title" width="760px">
      <el-form ref="templateRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="模板编码" prop="templateCode">
              <el-input v-model="form.templateCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="form.templateName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板版本" prop="templateVersion">
              <el-input v-model="form.templateVersion" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="templateType">
              <el-select v-model="form.templateType">
                <el-option v-for="item in templateTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核状态" prop="reviewStatus">
              <el-select v-model="form.reviewStatus">
                <el-option v-for="item in reviewStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启停状态" prop="status">
              <el-select v-model="form.status">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="适用范围" prop="applicableScope">
          <el-input v-model="form.applicableScope" placeholder="如：派遣企业 / 用工单位 / 高风险岗位" />
        </el-form-item>
        <el-form-item label="模板文件" prop="templateFileUrl">
          <file-upload v-model="form.templateFileUrl" :limit="1" :file-size="20" />
        </el-form-item>
        <el-form-item label="模板说明" prop="contentText">
          <el-input v-model="form.contentText" type="textarea" :rows="5" />
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

    <el-dialog v-model="detailOpen" title="模板详情" width="720px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="模板编码">{{ detail.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ detail.templateName }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ detail.templateVersion }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">{{ templateTypeLabel(detail.templateType) }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <dict-tag :options="reviewStatusOptions" :value="detail.reviewStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="启停状态">
          <dict-tag :options="statusOptions" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="适用范围" :span="2">{{ detail.applicableScope || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模板文件" :span="2">{{ detail.templateFileUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模板说明" :span="2">{{ detail.contentText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="YgbContractTemplate">
import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import FileUpload from '@/components/FileUpload/index.vue'
import { addContractTemplate, changeContractTemplateStatus, delContractTemplate, getContractTemplate, listContractTemplate, updateContractTemplate } from '@/api/ygb/contractTemplate'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const templateList = ref([])
const ids = ref([])
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref(null)

const templateTypeOptions = [
  { label: '劳动合同', value: '1' },
  { label: '派遣协议', value: '2' },
  { label: '用工协议', value: '3' }
]

const reviewStatusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '待审核', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已驳回', value: 'rejected' }
]

const statusOptions = [
  { label: '停用', value: '0' },
  { label: '启用', value: '1' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    templateName: undefined,
    templateType: undefined,
    reviewStatus: undefined,
    status: undefined
  },
  form: {},
  rules: {
    templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
    templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
    templateType: [{ required: true, message: '请选择合同类型', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function templateTypeLabel(value) {
  return templateTypeOptions.find(item => item.value === value)?.label || '-'
}

function reset() {
  form.value = {
    templateId: undefined,
    templateCode: undefined,
    templateName: undefined,
    templateVersion: 'V1.0',
    templateType: '1',
    applicableScope: undefined,
    reviewStatus: 'approved',
    status: '1',
    templateFileUrl: undefined,
    contentText: undefined,
    remark: undefined
  }
  proxy.resetForm('templateRef')
}

function getList() {
  loading.value = true
  listContractTemplate(queryParams.value).then(response => {
    templateList.value = response.rows || []
    total.value = response.total || 0
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
  ids.value = selection.map(item => item.templateId)
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增模板'
}

function handleUpdate(row) {
  reset()
  getContractTemplate(row.templateId).then(response => {
    form.value = response.data || {}
    open.value = true
    title.value = '修改模板'
  })
}

function handleView(row) {
  getContractTemplate(row.templateId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.templateRef.validate(valid => {
    if (!valid) {
      return
    }
    const request = form.value.templateId ? updateContractTemplate(form.value) : addContractTemplate(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const templateIds = row.templateId || ids.value
  proxy.$modal.confirm('确认删除所选模板吗？').then(() => {
    return delContractTemplate(templateIds)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  })
}

function handleStatusChange(row, value) {
  changeContractTemplateStatus(row.templateId, { status: value ? '1' : '0' }).then(() => {
    proxy.$modal.msgSuccess('状态已更新')
    getList()
  })
}

function handleExport() {
  proxy.download('/ygb/contract/template/export', { ...queryParams.value }, `contract_template_${Date.now()}.xlsx`)
}

getList()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
