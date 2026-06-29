<template>
  <div class="app-container">
    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="劳动者" prop="personName">
        <el-input v-model="queryParams.personName" placeholder="请输入劳动者姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="鍒嗙被缂栫爜" prop="categoryCode">
        <el-input v-model="queryParams.categoryCode" placeholder="请输入分类编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="鍒嗙被鍚嶇О" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="请输入分类名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="鏉ユ簮妯″潡" prop="sourceModule">
        <el-input v-model="queryParams.sourceModule" placeholder="请输入来源模块" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="文件名" prop="fileName">
        <el-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">鎼滅储</el-button>
        <el-button icon="Refresh" @click="resetQuery">閲嶇疆</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recordList">
      <el-table-column label="ID" prop="uploadId" width="90" />
      <el-table-column label="劳动者" prop="personName" width="120" />
      <el-table-column label="鍒嗙被缂栫爜" prop="categoryCode" width="140" />
      <el-table-column label="鍒嗙被鍚嶇О" prop="categoryName" width="160" />
      <el-table-column label="文件名" prop="fileName" min-width="180" show-overflow-tooltip />
      <el-table-column label="原始文件名" prop="originalFilename" min-width="180" show-overflow-tooltip />
      <el-table-column label="鏉ユ簮妯″潡" prop="sourceModule" width="120" />
      <el-table-column label="鏂囦欢澶у皬" width="120">
        <template #default="scope">
          {{ formatFileSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column label="涓婁紶鏃堕棿" prop="createTime" width="170" />
      <el-table-column label="鎿嶄綔" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">璇︽儏</el-button>
          <el-button v-if="scope.row.fileUrl" link type="primary" icon="Link" @click="handleOpen(scope.row)">鎵撳紑鏂囦欢</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog v-model="detailOpen" title="涓婁紶褰掓。璇︽儏" width="760px" append-to-body>
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="褰掓。ID">{{ detail.uploadId }}</el-descriptions-item>
        <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鍒嗙被缂栫爜">{{ detail.categoryCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鍒嗙被鍚嶇О">{{ detail.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="文件名">{{ detail.fileName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原始文件名">{{ detail.originalFilename || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鏉ユ簮妯″潡">{{ detail.sourceModule || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鍐呭绫诲瀷">{{ detail.contentType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鏂囦欢澶у皬">{{ formatFileSize(detail.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="涓婁紶鏃堕棿">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鏂囦欢鍦板潃" :span="2">{{ detail.fileUrl || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button v-if="detail?.fileUrl" type="primary" @click="handleOpen(detail)">鎵撳紑鏂囦欢</el-button>
        <el-button @click="detailOpen = false">鍏抽棴</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbWorkerUploadRecord">
import { getWorkerUploadRecord, listWorkerUploadRecord } from '@/api/ygb/workerMessage'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const recordList = ref([])
const detail = ref(null)
const detailOpen = ref(false)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    personName: undefined,
    categoryCode: undefined,
    categoryName: undefined,
    sourceModule: undefined,
    fileName: undefined
  }
})

const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listWorkerUploadRecord(queryParams.value).then(response => {
    recordList.value = response.rows || []
    total.value = response.total || 0
    loading.value = false
  }).catch(() => {
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

function handleDetail(row) {
  getWorkerUploadRecord(row.uploadId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function handleOpen(row) {
  if (!row?.fileUrl) return
  window.open(row.fileUrl, '_blank')
}

function formatFileSize(value) {
  const size = Number(value || 0)
  if (!size) return '-'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(2)} KB`
  return `${(size / 1024 / 1024).toFixed(2)} MB`
}

getList()
</script>

