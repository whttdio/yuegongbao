<template>
  <div class="app-container">
    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input
          v-model="queryParams.menuName"
          placeholder="请输入菜单名称"
          clearable
          style="width: 220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="前端归属" prop="portalScope">
        <el-select v-model="queryParams.portalScope" placeholder="请选择前端归属" clearable style="width: 220px">
          <el-option
            v-for="item in portalScopeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 220px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:menu:add']"
        >
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Check"
          @click="handleSaveSort"
          v-hasPermi="['system:menu:edit']"
        >
          保存排序
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">
          展开/折叠
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="menuList"
      row-key="menuId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="menuName" label="菜单名称" min-width="220" :show-overflow-tooltip="true">
        <template #default="{ row }">
          <svg-icon :icon-class="row.icon || 'tree'" />
          <span class="ml5">{{ row.menuName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="menuType" label="类型" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.menuType === 'M'" type="primary" size="small">目录</el-tag>
          <el-tag v-else-if="row.menuType === 'C'" type="success" size="small">菜单</el-tag>
          <el-tag v-else type="warning" size="small">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="160">
        <template #default="{ row }">
          <el-input-number v-model="row.orderNum" controls-position="right" :min="0" style="width: 88px" />
        </template>
      </el-table-column>
      <el-table-column prop="perms" label="权限标识" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column prop="component" label="组件路径" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column prop="portalScope" label="前端归属" width="120">
        <template #default="{ row }">
          <el-tag :type="portalScopeTagMap[normalizePortalScope(row.portalScope)]" size="small">
            {{ portalScopeLabelMap[normalizePortalScope(row.portalScope)] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column class-name="table-fill-column" min-width="1" />

      <el-table-column label="操作" fixed="right" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['system:menu:edit']">
            修改
          </el-button>
          <el-button link type="success" icon="Plus" @click="handleAdd(row)" v-hasPermi="['system:menu:add']">
            新增
          </el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['system:menu:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="menuRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
                value-key="menuId"
                placeholder="请选择上级菜单"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">菜单</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="菜单图标" prop="icon">
              <el-popover placement="bottom-start" :width="540" trigger="click">
                <template #reference>
                  <el-input v-model="form.icon" placeholder="点击选择图标" readonly @focus="showSelectIcon" />
                </template>
                <icon-select ref="iconSelectRef" :active-icon="form.icon" @selected="selected" />
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="前端归属" prop="portalScope">
              <el-select v-model="form.portalScope" placeholder="请选择前端归属">
                <el-option
                  v-for="item in portalScopeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="路由名称" prop="routeName">
              <el-input v-model="form.routeName" placeholder="不填则默认与路由地址一致" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="是否外链">
              <el-radio-group v-model="form.isFrame">
                <el-radio value="0">是</el-radio>
                <el-radio value="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="如 system/user/index" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'M'">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" maxlength="100" placeholder="如 system:user:list" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="路由参数" prop="query">
              <el-input v-model="form.query" maxlength="255" placeholder='如 {"id":1}' />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="是否缓存">
              <el-radio-group v-model="form.isCache">
                <el-radio value="0">缓存</el-radio>
                <el-radio value="1">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="显示状态">
              <el-radio-group v-model="form.visible">
                <el-radio v-for="dict in sys_show_hide" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Menu">
import { getCurrentInstance, nextTick, reactive, ref, toRefs } from 'vue'
import { addMenu, delMenu, getMenu, listMenu, updateMenu, updateMenuSort } from '@/api/system/menu'
import IconSelect from '@/components/IconSelect'

const { proxy } = getCurrentInstance()
const { sys_show_hide, sys_normal_disable } = useDict('sys_show_hide', 'sys_normal_disable')

const portalScopeOptions = [
  { label: '双前端共用', value: 'both' },
  { label: '粤工保', value: 'ygb' },
  { label: '安责保', value: 'azb' }
]

const portalScopeLabelMap = {
  both: '双端',
  ygb: '粤工保',
  azb: '安责保'
}

const portalScopeTagMap = {
  both: 'info',
  ygb: 'primary',
  azb: 'success'
}

const menuList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const title = ref('')
const menuOptions = ref([])
const isExpandAll = ref(false)
const refreshTable = ref(true)
const iconSelectRef = ref()
const originalOrders = ref({})

const data = reactive({
  form: {},
  queryParams: {
    menuName: undefined,
    portalScope: undefined,
    status: undefined
  },
  rules: {
    menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
    orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
    path: [{
      validator: (_rule, value, callback) => {
        if (form.value.menuType !== 'F' && !value) {
          callback(new Error('路由地址不能为空'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }],
    portalScope: [{ required: true, message: '前端归属不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function normalizePortalScope(scope) {
  return scope || 'both'
}

function getList() {
  loading.value = true
  listMenu(queryParams.value).then(response => {
    menuList.value = proxy.handleTree(response.data, 'menuId')
    originalOrders.value = {}
    recordOriginalOrders(menuList.value)
    loading.value = false
  })
}

function getTreeselect() {
  return listMenu().then(response => {
    const root = { menuId: 0, menuName: '主类目', children: [] }
    root.children = proxy.handleTree(response.data, 'menuId')
    menuOptions.value = [root]
  })
}

function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    icon: undefined,
    menuType: 'M',
    orderNum: 0,
    isFrame: '1',
    isCache: '0',
    portalScope: 'both',
    visible: '0',
    status: '0',
    perms: undefined,
    path: undefined,
    component: undefined,
    query: undefined,
    routeName: undefined
  }
  proxy.resetForm('menuRef')
}

function cancel() {
  open.value = false
  reset()
}

function showSelectIcon() {
  iconSelectRef.value?.reset()
}

function selected(name) {
  form.value.icon = name
}

function handleQuery() {
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleAdd(row) {
  reset()
  getTreeselect().then(() => {
    form.value.parentId = row?.menuId || 0
    open.value = true
    title.value = '新增菜单'
  })
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleUpdate(row) {
  reset()
  getTreeselect().then(() => {
    getMenu(row.menuId).then(response => {
      form.value = {
        ...response.data,
        portalScope: normalizePortalScope(response.data.portalScope)
      }
      open.value = true
      title.value = '修改菜单'
    })
  })
}

function submitForm() {
  proxy.$refs.menuRef.validate(valid => {
    if (!valid) {
      return
    }
    const request = form.value.menuId ? updateMenu(form.value) : addMenu(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.menuId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function recordOriginalOrders(list) {
  list.forEach(item => {
    originalOrders.value[item.menuId] = item.orderNum
    if (item.children?.length) {
      recordOriginalOrders(item.children)
    }
  })
}

function handleSaveSort() {
  const changedMenuIds = []
  const changedOrderNums = []

  const collectChanged = list => {
    list.forEach(item => {
      if (String(originalOrders.value[item.menuId]) !== String(item.orderNum)) {
        changedMenuIds.push(item.menuId)
        changedOrderNums.push(item.orderNum)
      }
      if (item.children?.length) {
        collectChanged(item.children)
      }
    })
  }

  collectChanged(menuList.value)
  if (changedMenuIds.length === 0) {
    proxy.$modal.msgWarning('未检测到排序修改')
    return
  }
  updateMenuSort({
    menuIds: changedMenuIds.join(','),
    orderNums: changedOrderNums.join(',')
  }).then(() => {
    proxy.$modal.msgSuccess('排序保存成功')
    originalOrders.value = {}
    recordOriginalOrders(menuList.value)
  })
}

function handleDelete(row) {
  proxy.$modal.confirm(`是否确认删除名称为“${row.menuName}”的数据项？`).then(() => {
    return delMenu(row.menuId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

reset()
getList()
</script>
