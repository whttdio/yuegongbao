<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>工资拖欠预警</span>
          <div>
            <el-button plain @click="handleBackToBatch" v-if="hasBatchBackLink">返回工资批次</el-button>
            <el-button type="warning" @click="handleExport" v-hasPermi="['ygb:salaryBatchArrears:export']">导出</el-button>
          </div>
        </div>
      </template>

      <el-form :model="queryParams" :inline="true" ref="queryRef">
        <el-form-item label="批次号">
          <el-input v-model="queryParams.batchNo" clearable @keyup.enter="getList" />
        </el-form-item>
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" />
        </el-form-item>
        <el-form-item label="派遣企业">
          <el-input v-model="queryParams.dispatchEnterpriseId" clearable />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="queryParams.regionCode" clearable />
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="queryParams.handleStatus" clearable style="width: 160px">
            <el-option v-for="item in handleStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="arrearsList">
        <el-table-column label="批次号" prop="batchNo" min-width="160" />
        <el-table-column label="派遣企业" prop="dispatchEnterpriseName" min-width="220" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="应发人数" prop="totalPersonCount" width="100" />
        <el-table-column label="应发金额" prop="totalPayableAmount" width="120" />
        <el-table-column label="已发金额" prop="totalPaidAmount" width="120" />
        <el-table-column label="到账缺口" prop="accountGapAmount" width="120" />
        <el-table-column label="拖欠金额" prop="arrearsAmount" width="120" />
        <el-table-column label="逾期天数" prop="overdueDays" width="100" />
        <el-table-column label="处置状态" width="120">
          <template #default="scope">
            <dict-tag :options="handleStatusOptions" :value="scope.row.handleStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预警ID" prop="warningId" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleFollow(scope.row)" v-hasPermi="['ygb:salaryBatchArrears:handle']">处置</el-button>
            <el-button link type="info" @click="handleView(scope.row)" v-hasPermi="['ygb:salaryBatchArrears:query']">详情</el-button>
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

    <el-dialog v-model="open" title="拖欠处置" width="640px">
      <el-form ref="arrearsRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="处置状态" prop="handleStatus">
          <el-select v-model="form.handleStatus">
            <el-option v-for="item in handleStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进" prop="nextFollowTime">
          <el-date-picker v-model="form.nextFollowTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="处置结果" prop="handleResult">
          <el-input v-model="form.handleResult" type="textarea" :rows="4" />
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

    <el-dialog v-model="detailOpen" title="拖欠详情" width="720px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="批次号">{{ detail.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="统计月份">{{ detail.statMonth }}</el-descriptions-item>
        <el-descriptions-item label="派遣企业">{{ detail.dispatchEnterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="应发人数">{{ detail.totalPersonCount }}</el-descriptions-item>
        <el-descriptions-item label="应发金额">{{ detail.totalPayableAmount }}</el-descriptions-item>
        <el-descriptions-item label="已发金额">{{ detail.totalPaidAmount }}</el-descriptions-item>
        <el-descriptions-item label="到账金额">{{ detail.accountReceivedAmount }}</el-descriptions-item>
        <el-descriptions-item label="到账缺口">{{ detail.accountGapAmount }}</el-descriptions-item>
        <el-descriptions-item label="拖欠金额">{{ detail.arrearsAmount }}</el-descriptions-item>
        <el-descriptions-item label="逾期天数">{{ detail.overdueDays }}</el-descriptions-item>
        <el-descriptions-item label="处置状态">
          <dict-tag :options="handleStatusOptions" :value="detail.handleStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="预警ID">{{ detail.warningId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="跟进结果" :span="2">{{ detail.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="YgbSalaryArrears">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSalaryArrears, handleSalaryArrears, listSalaryArrears } from '@/api/ygb/salaryArrears'

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const total = ref(0)
const arrearsList = ref([])
const open = ref(false)
const detailOpen = ref(false)
const detail = ref(null)
const hasBatchBackLink = computed(() => Boolean(route.query.statMonth || route.query.dispatchEnterpriseId || route.query.regionCode))

const handleStatusOptions = [
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已闭环', value: 'closed' },
  { label: '已驳回', value: 'rejected' },
  { label: '已逾期', value: 'overdue' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    batchNo: undefined,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    regionCode: undefined,
    handleStatus: undefined
  },
  form: {},
  rules: {
    handleStatus: [{ required: true, message: '请选择处置状态', trigger: 'change' }],
    handleResult: [{ required: true, message: '请输入处置结果', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function reset() {
  form.value = {
    batchId: undefined,
    handleStatus: 'processing',
    nextFollowTime: undefined,
    handleResult: undefined,
    remark: undefined
  }
  proxy.resetForm('arrearsRef')
}

function getList() {
  loading.value = true
  listSalaryArrears(queryParams.value).then(response => {
    arrearsList.value = response.rows || []
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
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    batchNo: undefined,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    regionCode: undefined,
    handleStatus: undefined
  })
  applyRouteQuery()
  handleQuery()
}

function handleFollow(row) {
  reset()
  getSalaryArrears(row.batchId).then(response => {
    form.value = {
      batchId: row.batchId,
      handleStatus: response.data?.handleStatus || 'processing',
      nextFollowTime: response.data?.nextFollowTime,
      handleResult: response.data?.handleResult,
      remark: response.data?.remark
    }
    open.value = true
  })
}

function handleView(row) {
  getSalaryArrears(row.batchId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.arrearsRef.validate(valid => {
    if (!valid) {
      return
    }
    handleSalaryArrears(form.value).then(() => {
      proxy.$modal.msgSuccess('处置成功')
      open.value = false
      getList()
    })
  })
}

function handleExport() {
  proxy.download('/ygb/salary/batch/arrears/export', { ...queryParams.value }, `salary_arrears_${Date.now()}.xlsx`)
}

function applyRouteQuery() {
  if (route.query.statMonth) {
    queryParams.value.statMonth = route.query.statMonth
  }
  if (route.query.dispatchEnterpriseId) {
    queryParams.value.dispatchEnterpriseId = route.query.dispatchEnterpriseId
  }
  if (route.query.regionCode) {
    queryParams.value.regionCode = route.query.regionCode
  }
  if (route.query.handleStatus) {
    queryParams.value.handleStatus = route.query.handleStatus
  }
}

function handleBackToBatch() {
  router.push({
    path: "/ygb/salaryBatch",
    query: {
      statMonth: queryParams.value.statMonth,
      dispatchEnterpriseId: queryParams.value.dispatchEnterpriseId,
      regionCode: queryParams.value.regionCode
    }
  })
}

applyRouteQuery()
getList()
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
