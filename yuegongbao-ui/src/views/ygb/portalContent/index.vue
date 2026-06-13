<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="门户" prop="portalCode">
        <el-select v-model="queryParams.portalCode" clearable style="width: 120px">
          <el-option label="粤工保" value="ygb" />
          <el-option label="安责保" value="azb" />
        </el-select>
      </el-form-item>
      <el-form-item label="栏目" prop="sectionCode">
        <el-select v-model="queryParams.sectionCode" clearable style="width: 180px">
          <el-option v-for="item in sectionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="contentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="contentId" width="80" />
      <el-table-column label="门户" prop="portalCode" width="80" />
      <el-table-column label="栏目" prop="sectionCode" width="150" />
      <el-table-column label="分类" prop="categoryCode" width="140" />
      <el-table-column label="标题" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="来源" prop="sourceName" width="140" show-overflow-tooltip />
      <el-table-column label="排序" prop="sortOrder" width="70" />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '发布' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" prop="publishTime" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ygb:portalContent:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ygb:portalContent:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="780px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="门户" prop="portalCode">
              <el-select v-model="form.portalCode">
                <el-option label="粤工保" value="ygb" />
                <el-option label="安责保" value="azb" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栏目" prop="sectionCode">
              <el-select v-model="form.sectionCode">
                <el-option v-for="item in sectionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryCode">
              <el-input v-model="form.categoryCode" placeholder="如 national / slide / main" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="摘要" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正文" prop="content">
              <Editor v-model="form.content" :min-height="220" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源" prop="sourceName">
              <el-input v-model="form.sourceName" />
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
          <el-col :span="24">
            <el-form-item label="封面/二维码" prop="coverUrl">
              <ImageUpload v-model="form.coverUrl" :limit="1" :drag="false" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="isDownloadSection ? '附件/下载链接' : '链接'" prop="linkUrl">
              <FileUpload
                v-if="isDownloadSection"
                v-model="form.linkUrl"
                :limit="1"
                :file-type="['pdf', 'doc', 'docx', 'zip', 'rar']"
              />
              <el-input v-else v-model="form.linkUrl" placeholder="外链地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="扩展JSON" prop="extraJson">
              <el-input v-model="form.extraJson" type="textarea" :rows="4" placeholder='如 {"icon":"book","features":[]}' />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker v-model="form.publishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbPortalContent">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute } from 'vue-router'
import { listPortalContent, getPortalContent, addPortalContent, updatePortalContent, delPortalContent } from '@/api/ygb/portalContent'

const route = useRoute()
const { proxy } = getCurrentInstance()

const contentList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const sectionOptions = [
  { label: '站点配置', value: 'site_config' },
  { label: '广告轮播', value: 'banner' },
  { label: '平台简介', value: 'intro' },
  { label: '最新动态', value: 'news' },
  { label: '政策法规', value: 'policy' },
  { label: '解决方案', value: 'solution' },
  { label: '工会服务', value: 'union' },
  { label: '服务指南', value: 'guide' },
  { label: '关于我们', value: 'about' },
  { label: '合作单位', value: 'partner' },
  { label: '下载中心', value: 'download' },
  { label: '快速入口', value: 'quick_entry' },
  { label: '求职指南', value: 'job_guide' },
  { label: '暖新地图', value: 'warm_map' },
  { label: '培训课程', value: 'training_course' },
  { label: '法规库', value: 'law_library' },
  { label: '互助区', value: 'mutual_help' },
  { label: '招聘市场', value: 'recruit_market' }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    portalCode: 'ygb',
    sectionCode: undefined,
    title: undefined
  },
  rules: {
    portalCode: [{ required: true, message: '门户不能为空', trigger: 'change' }],
    sectionCode: [{ required: true, message: '栏目不能为空', trigger: 'change' }],
    title: [{ required: true, message: '标题不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)
const isDownloadSection = computed(() => form.value.sectionCode === 'download')

function defaultPortalCode() {
  return route.query.portalCode || 'ygb'
}

function defaultSectionCode() {
  return route.query.sectionCode || 'news'
}

function applyRouteQuery() {
  if (route.query.portalCode) {
    queryParams.value.portalCode = route.query.portalCode
  }
  if (route.query.sectionCode) {
    queryParams.value.sectionCode = route.query.sectionCode
  }
  if (route.query.title) {
    queryParams.value.title = route.query.title
  }
}

function getList() {
  loading.value = true
  listPortalContent(queryParams.value).then(response => {
    contentList.value = response.rows || []
    total.value = response.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    contentId: undefined,
    portalCode: defaultPortalCode(),
    sectionCode: defaultSectionCode(),
    categoryCode: '',
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
  proxy.resetForm('formRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    portalCode: defaultPortalCode(),
    sectionCode: undefined,
    title: undefined
  }
  applyRouteQuery()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.contentId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增门户内容'
}

function handleUpdate(row) {
  reset()
  const contentId = row?.contentId || ids.value[0]
  getPortalContent(contentId).then(response => {
    form.value = response.data || {}
    open.value = true
    title.value = '修改门户内容'
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const request = form.value.contentId ? updatePortalContent(form.value) : addPortalContent(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.contentId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const contentIds = row?.contentId || ids.value
  proxy.$modal.confirm(`是否确认删除门户内容编号为 "${contentIds}" 的数据项？`).then(() => {
    return delPortalContent(contentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

applyRouteQuery()
reset()
getList()
</script>
