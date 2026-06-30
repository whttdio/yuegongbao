<template>
  <div class="app-container citizen-page">
    <section class="citizen-hero" :class="`citizen-hero--${config.tone || 'blue'}`">
      <div>
        <p class="citizen-hero__eyebrow">便民服务运营台</p>
        <h1 class="citizen-hero__title">{{ config.title }}</h1>
        <p class="citizen-hero__desc">{{ config.description }}</p>
      </div>
      <div class="citizen-hero__meta">
        <span>移动端入口</span>
        <strong>{{ config.miniappEntry }}</strong>
      </div>
    </section>

    <div class="citizen-stat-grid">
      <div v-for="item in stats" :key="item.key" class="citizen-stat-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.hint }}</small>
      </div>
    </div>

    <el-card shadow="never" class="citizen-panel">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="queryParams.title" :placeholder="`请输入${config.itemName}名称`" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryCode">
          <el-select v-model="queryParams.categoryCode" clearable style="width: 180px">
            <el-option v-for="item in config.categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" clearable style="width: 120px">
            <el-option label="上架" value="0" />
            <el-option label="下架" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="citizen-panel">
      <template #header>
        <div class="citizen-table-head">
          <div>
            <strong>{{ config.tableTitle }}</strong>
            <span>{{ config.tableDesc }}</span>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </template>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:portalContent:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:portalContent:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:portalContent:remove']">删除</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="contentList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" prop="contentId" width="80" />
        <el-table-column :label="config.itemName" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="main-cell">
              <strong>{{ row.title || '-' }}</strong>
              <span>{{ row.summary || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="130">
          <template #default="{ row }">{{ categoryLabel(row.categoryCode) }}</template>
        </el-table-column>
        <el-table-column
          v-for="field in config.tableFields"
          :key="field.key"
          :label="field.label"
          :width="field.width"
          :min-width="field.minWidth"
          show-overflow-tooltip
        >
          <template #default="{ row }">{{ formatExtraValue(row, field) }}</template>
        </el-table-column>
        <el-table-column label="移动端展示" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="isMiniappVisible(row) ? 'success' : 'info'">{{ isMiniappVisible(row) ? '展示' : '隐藏' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="publishTime" width="170" />
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['ygb:portalContent:edit']">修改</el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(row)" v-hasPermi="['ygb:portalContent:remove']">删除</el-button>
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

    <el-dialog :title="dialogTitle" v-model="open" width="900px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="112px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="`${config.itemName}名称`" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryCode">
              <el-select v-model="form.categoryCode" style="width: 100%">
                <el-option v-for="item in config.categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="摘要" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
          <el-col v-for="field in config.formFields" :key="field.key" :span="field.span || 12">
            <el-form-item :label="field.label">
              <el-input-number
                v-if="field.type === 'number'"
                v-model="extraForm[field.key]"
                :precision="field.precision ?? 0"
                :step="field.step || 1"
                style="width: 100%"
              />
              <el-switch
                v-else-if="field.type === 'switch'"
                v-model="extraForm[field.key]"
                active-value="1"
                inactive-value="0"
              />
              <el-select v-else-if="field.type === 'select'" v-model="extraForm[field.key]" clearable style="width: 100%">
                <el-option v-for="item in field.options || []" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
              <el-input
                v-else
                v-model="extraForm[field.key]"
                :type="field.type === 'textarea' ? 'textarea' : 'text'"
                :rows="field.rows || 3"
                :placeholder="field.placeholder || ''"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正文说明" prop="content">
              <Editor v-model="form.content" :min-height="220" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源/责任单位" prop="sourceName">
              <el-input v-model="form.sourceName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">上架</el-radio>
                <el-radio value="1">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker v-model="form.publishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面/二维码" prop="coverUrl">
              <ImageUpload v-model="form.coverUrl" :limit="1" :drag="false" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="外部链接" prop="linkUrl">
              <el-input v-model="form.linkUrl" placeholder="可填公开详情页、视频地址或移动端落地页" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" :title="`${config.itemName}详情`" width="780px" append-to-body>
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="名称">{{ detail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ categoryLabel(detail.categoryCode) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.status === '0' ? '上架' : '下架' }}</el-descriptions-item>
          <el-descriptions-item label="移动端展示">{{ isMiniappVisible(detail) ? '展示' : '隐藏' }}</el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">{{ detail.summary || '-' }}</el-descriptions-item>
          <el-descriptions-item v-for="field in config.detailFields" :key="field.key" :label="field.label">
            {{ formatExtraValue(detail, field) }}
          </el-descriptions-item>
          <el-descriptions-item label="移动端路径" :span="2">{{ readExtra(detail).miniappPath || config.miniappEntry }}</el-descriptions-item>
          <el-descriptions-item label="正文" :span="2">
            <div class="detail-content" v-html="detail.content || detail.summary || '-'" />
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addCitizenPortalContent,
  delCitizenPortalContent,
  getCitizenPortalContent,
  listCitizenPortalContent,
  updateCitizenPortalContent
} from '@/api/ygb/citizenService'

const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const contentList = ref([])
const total = ref(0)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const detailOpen = ref(false)
const dialogTitle = ref('')
const detail = ref(null)
const extraForm = ref({})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    portalCode: 'ygb',
    sectionCode: props.config.sectionCode,
    categoryCode: undefined,
    title: undefined,
    status: undefined
  },
  form: {},
  rules: {
    title: [{ required: true, message: `${props.config.itemName}名称不能为空`, trigger: 'blur' }],
    categoryCode: [{ required: true, message: '分类不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const stats = computed(() => {
  const rows = contentList.value || []
  const published = rows.filter(item => item.status === '0').length
  const visible = rows.filter(isMiniappVisible).length
  const categoryCount = new Set(rows.map(item => item.categoryCode).filter(Boolean)).size
  return [
    { key: 'total', label: `${props.config.itemName}总数`, value: total.value, hint: '当前筛选范围' },
    { key: 'published', label: '已上架', value: published, hint: '当前页已发布到移动端' },
    { key: 'visible', label: '移动端展示', value: visible, hint: '当前页展示开关开启' },
    { key: 'category', label: '分类覆盖', value: categoryCount, hint: '当前页去重分类' }
  ]
})

function getList() {
  loading.value = true
  listCitizenPortalContent(queryParams.value).then(response => {
    contentList.value = response.rows || []
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
  queryParams.value.portalCode = 'ygb'
  queryParams.value.sectionCode = props.config.sectionCode
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.contentId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function reset() {
  form.value = {
    contentId: undefined,
    portalCode: 'ygb',
    sectionCode: props.config.sectionCode,
    categoryCode: props.config.categoryOptions?.[0]?.value || '',
    title: '',
    summary: '',
    content: '',
    coverUrl: '',
    linkUrl: '',
    sourceName: '',
    publishTime: undefined,
    sortOrder: 0,
    status: '0',
    extraJson: ''
  }
  extraForm.value = buildDefaultExtra()
  proxy.resetForm('formRef')
}

function handleAdd() {
  reset()
  open.value = true
  dialogTitle.value = `新增${props.config.itemName}`
}

function handleUpdate(row) {
  reset()
  const contentId = row?.contentId || ids.value[0]
  getCitizenPortalContent(contentId).then(response => {
    form.value = response.data || {}
    extraForm.value = { ...buildDefaultExtra(), ...parseExtra(form.value.extraJson) }
    open.value = true
    dialogTitle.value = `修改${props.config.itemName}`
  })
}

function handleDetail(row) {
  getCitizenPortalContent(row.contentId).then(response => {
    detail.value = response.data || row
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    form.value.extraJson = JSON.stringify(extraForm.value || {})
    const request = form.value.contentId ? updateCitizenPortalContent(form.value) : addCitizenPortalContent(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.contentId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const contentIds = row?.contentId || ids.value
  proxy.$modal.confirm(`是否确认删除${props.config.itemName}编号为 "${contentIds}" 的数据项？`).then(() => {
    return delCitizenPortalContent(contentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function cancel() {
  open.value = false
  reset()
}

function buildDefaultExtra() {
  const extra = {
    miniappVisible: '1',
    miniappPath: props.config.miniappEntry
  }
  for (const field of props.config.formFields || []) {
    if (field.defaultValue !== undefined) {
      extra[field.key] = field.defaultValue
    } else if (field.type === 'switch') {
      extra[field.key] = '0'
    } else {
      extra[field.key] = ''
    }
  }
  return extra
}

function parseExtra(value) {
  if (!value) return {}
  try {
    return JSON.parse(value)
  } catch {
    return {}
  }
}

function readExtra(row) {
  return row?.extra || parseExtra(row?.extraJson)
}

function formatExtraValue(row, field) {
  const value = readExtra(row)[field.key]
  if (field.formatter) return field.formatter(value, row)
  if (field.type === 'switch') return value === '1' ? '是' : '否'
  return value === undefined || value === null || value === '' ? '-' : value
}

function isMiniappVisible(row) {
  const extra = readExtra(row)
  return row?.status === '0' && extra.miniappVisible !== '0'
}

function categoryLabel(value) {
  return props.config.categoryOptions?.find(item => item.value === value)?.label || value || '-'
}

reset()
getList()
</script>

<style scoped>
.citizen-page {
  background: #f6f8fb;
}

.citizen-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
  padding: 24px 28px;
  border-radius: 8px;
  color: #fff;
}

.citizen-hero--blue {
  background: linear-gradient(135deg, #155e75 0%, #0f766e 100%);
}

.citizen-hero--green {
  background: linear-gradient(135deg, #166534 0%, #15803d 100%);
}

.citizen-hero--indigo {
  background: linear-gradient(135deg, #3730a3 0%, #1d4ed8 100%);
}

.citizen-hero__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  opacity: 0.82;
}

.citizen-hero__title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
}

.citizen-hero__desc {
  max-width: 720px;
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.92;
}

.citizen-hero__meta {
  min-width: 210px;
  padding: 16px 18px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.14);
}

.citizen-hero__meta span,
.citizen-hero__meta strong {
  display: block;
}

.citizen-hero__meta span {
  margin-bottom: 8px;
  font-size: 12px;
  opacity: 0.82;
}

.citizen-hero__meta strong {
  font-size: 14px;
  word-break: break-all;
}

.citizen-stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.citizen-stat-card,
.citizen-panel {
  border: none;
  border-radius: 8px;
  background: #fff;
}

.citizen-stat-card {
  padding: 18px 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.citizen-stat-card span,
.citizen-stat-card small {
  display: block;
  color: #64748b;
}

.citizen-stat-card strong {
  display: block;
  margin: 10px 0 8px;
  color: #0f172a;
  font-size: 28px;
  line-height: 1;
}

.citizen-panel {
  margin-bottom: 16px;
}

.citizen-table-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.citizen-table-head strong,
.citizen-table-head span {
  display: block;
}

.citizen-table-head span {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.main-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.main-cell strong {
  color: #1f2937;
}

.main-cell span {
  color: #64748b;
  font-size: 12px;
  line-height: 1.5;
}

.detail-content {
  max-height: 260px;
  overflow: auto;
  line-height: 1.7;
}

@media (max-width: 1200px) {
  .citizen-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .citizen-hero,
  .citizen-table-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .citizen-hero__meta {
    width: 100%;
  }

  .citizen-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
