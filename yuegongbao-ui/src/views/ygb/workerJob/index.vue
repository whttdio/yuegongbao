<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="岗位名称" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入岗位名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="企业名称" prop="enterpriseName">
        <el-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable style="width: 120px">
          <el-option label="发布" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:workerJob:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:workerJob:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:workerJob:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="jobList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="jobId" width="70" />
      <el-table-column label="岗位名称" prop="title" min-width="160" show-overflow-tooltip />
      <el-table-column label="企业" prop="enterpriseName" min-width="160" show-overflow-tooltip />
      <el-table-column label="工种" prop="jobType" width="120" />
      <el-table-column label="薪资" prop="salaryText" width="140" />
      <el-table-column label="地点" prop="workAddress" min-width="160" show-overflow-tooltip />
      <el-table-column label="经纬度" min-width="180">
        <template #default="scope">
          <span>{{ formatCoordinate(scope.row.longitude, scope.row.latitude) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="招聘人数" prop="recruitCount" width="90" />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '发布' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" prop="publishTime" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ygb:workerJob:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ygb:workerJob:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业名称" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工种" prop="jobType">
              <el-input v-model="form.jobType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点" prop="workAddress">
              <el-input v-model="form.workAddress" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位经度" prop="longitude">
              <el-input-number v-model="form.longitude" :precision="6" :step="0.000001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位纬度" prop="latitude">
              <el-input-number v-model="form.latitude" :precision="6" :step="0.000001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最低薪资" prop="salaryMin">
              <el-input-number v-model="form.salaryMin" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最高薪资" prop="salaryMax">
              <el-input-number v-model="form.salaryMax" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="薪资文案" prop="salaryText">
              <el-input v-model="form.salaryText" placeholder="如 7000-9000 元/月" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="招聘人数" prop="recruitCount">
              <el-input-number v-model="form.recruitCount" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactMobile">
              <el-input v-model="form.contactMobile" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">发布</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker v-model="form.publishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="岗位描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任职要求" prop="requirementText">
              <el-input v-model="form.requirementText" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbWorkerJob">
import { listWorkerJob, getWorkerJob, addWorkerJob, updateWorkerJob, delWorkerJob } from '@/api/ygb/workerJob'

const { proxy } = getCurrentInstance()

const jobList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    enterpriseName: undefined,
    status: undefined
  },
  rules: {
    title: [{ required: true, message: '岗位名称不能为空', trigger: 'blur' }],
    enterpriseName: [{ required: true, message: '企业名称不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listWorkerJob(queryParams.value).then(response => {
    jobList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    jobId: undefined,
    enterpriseId: undefined,
    enterpriseName: '',
    title: '',
    jobType: '',
    workAddress: '',
    longitude: undefined,
    latitude: undefined,
    salaryMin: undefined,
    salaryMax: undefined,
    salaryText: '',
    recruitCount: 1,
    contactName: '',
    contactMobile: '',
    description: '',
    requirementText: '',
    status: '0',
    publishTime: undefined
  }
  proxy.resetForm('formRef')
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
  ids.value = selection.map(item => item.jobId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增招聘岗位'
}

function handleUpdate(row) {
  reset()
  const jobId = row?.jobId || ids.value[0]
  getWorkerJob(jobId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改招聘岗位'
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const request = form.value.jobId ? updateWorkerJob(form.value) : addWorkerJob(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.jobId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const jobIds = row?.jobId || ids.value
  proxy.$modal.confirm(`是否确认删除招聘岗位编号为 "${jobIds}" 的数据项？`).then(() => {
    return delWorkerJob(jobIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function formatCoordinate(longitude, latitude) {
  if (longitude == null || latitude == null || longitude === '' || latitude === '') {
    return '-'
  }
  return `${longitude}, ${latitude}`
}

getList()
</script>
