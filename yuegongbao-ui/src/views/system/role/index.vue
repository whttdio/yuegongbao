<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      label-width="68px"
      v-show="showSearch"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="请输入权限字符"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="角色状态" clearable style="width: 240px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:role:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['system:role:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:role:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:role:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="角色编号" prop="roleId" width="120" />
      <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="180" />
      <el-table-column label="显示顺序" prop="roleSort" width="100" />
      <el-table-column label="前端范围" width="120">
        <template #default="scope">
          <el-tag size="small" :type="getPortalScopeTagType(scope.row.allowedPortalScope)">
            {{ getPortalScopeLabel(scope.row.allowedPortalScope) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="默认前端" width="120">
        <template #default="scope">
          <el-tag size="small" effect="plain">
            {{ getPortalCodeLabel(scope.row.defaultPortalCode) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="table-fill-column" min-width="1" />

      <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-tooltip content="修改" placement="top" v-if="scope.row.roleId !== 1">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:role:edit']" />
          </el-tooltip>
          <el-tooltip content="删除" placement="top" v-if="scope.row.roleId !== 1">
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:role:remove']" />
          </el-tooltip>
          <el-tooltip content="数据权限" placement="top" v-if="scope.row.roleId !== 1">
            <el-button link type="primary" icon="CircleCheck" @click="handleDataScope(scope.row)" v-hasPermi="['system:role:edit']" />
          </el-tooltip>
          <el-tooltip content="分配用户" placement="top" v-if="scope.row.roleId !== 1">
            <el-button link type="primary" icon="User" @click="handleAuthUser(scope.row)" v-hasPermi="['system:role:edit']" />
          </el-tooltip>
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

    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="roleRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item prop="roleKey">
          <template #label>
            <span>
              <el-tooltip content="控制器中定义的权限字符，例如：@PreAuthorize(`@ss.hasRole('admin')`)" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              权限字符
            </span>
          </template>
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="前端范围" prop="allowedPortalScope">
          <el-select
            v-model="form.allowedPortalScope"
            placeholder="请选择前端范围"
            :disabled="isFixedPortalRuleRole"
            @change="handleAllowedPortalScopeChange"
          >
            <el-option
              v-for="item in rolePortalScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="默认前端" prop="defaultPortalCode">
          <el-select
            v-model="form.defaultPortalCode"
            placeholder="请选择默认前端"
            :disabled="isFixedPortalRuleRole"
          >
            <el-option
              v-for="item in filteredDefaultPortalOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="fixedRolePortalHint" label="门户规则">
          <div class="fixed-role-tip">
            {{ fixedRolePortalHint }}
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <div class="menu-scope-toolbar">
            <el-select v-model="menuPortalScope" placeholder="请选择授权视角" style="width: 180px" @change="handleMenuPortalScopeChange">
              <el-option
                v-for="item in filteredMenuPortalScopeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span class="menu-scope-tip">
              当前保存只覆盖所选视角。`双前端共用` 用于维护两端共享授权，`粤工保视角/安责保视角` 只维护对应门户授权。
            </span>
          </div>
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')">父子联动</el-checkbox>
          <el-tree
            ref="menuRef"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="title" v-model="openDataScope" width="500px" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="form.roleKey" disabled />
        </el-form-item>
        <el-form-item label="权限范围">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange">
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据权限" v-show="form.dataScope == 2">
          <el-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event, 'dept')">展开/折叠</el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event, 'dept')">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.deptCheckStrictly" @change="handleCheckedTreeConnect($event, 'dept')">父子联动</el-checkbox>
          <el-tree
            ref="deptRef"
            class="tree-border"
            :data="deptOptions"
            show-checkbox
            default-expand-all
            node-key="id"
            :check-strictly="!form.deptCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDataScope">确定</el-button>
          <el-button @click="cancelDataScope">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Role">
import { computed } from "vue"
import {
  addRole,
  changeRoleStatus,
  dataScope,
  delRole,
  deptTreeSelect,
  getRole,
  listRole,
  updateRole
} from "@/api/system/role"
import { roleMenuTreeselect, treeselect as menuTreeselect } from "@/api/system/menu"
import { getActivePortalCode, resolveFixedRolePortalRule } from "@/utils/portal"

const router = useRouter()
const { proxy } = getCurrentInstance()
const { sys_normal_disable } = useDict("sys_normal_disable")

const defaultMenuPortalScope = getActivePortalCode()
const menuPortalScopeOptions = [
  { value: "ygb", label: "粤工保视角" },
  { value: "azb", label: "安责保视角" },
  { value: "both", label: "双前端共用" }
]
const rolePortalScopeOptions = [
  { value: "both", label: "双前端" },
  { value: "ygb", label: "仅粤工保" },
  { value: "azb", label: "仅安责保" }
]
const roleDefaultPortalOptions = [
  { value: "ygb", label: "粤工保" },
  { value: "azb", label: "安责保" }
]
const dataScopeOptions = [
  { value: "1", label: "全部数据权限" },
  { value: "2", label: "自定数据权限" },
  { value: "3", label: "本部门数据权限" },
  { value: "4", label: "本部门及以下数据权限" },
  { value: "5", label: "仅本人数据权限" }
]

const roleList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const dateRange = ref([])
const menuOptions = ref([])
const menuExpand = ref(false)
const menuNodeAll = ref(false)
const deptExpand = ref(true)
const deptNodeAll = ref(false)
const deptOptions = ref([])
const openDataScope = ref(false)
const menuRef = ref(null)
const deptRef = ref(null)
const menuPortalScope = ref(defaultMenuPortalScope)
const roleMenuCheckedKeys = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleKey: undefined,
    status: undefined
  },
  rules: {
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }],
    roleKey: [{ required: true, message: "权限字符不能为空", trigger: "blur" }],
    roleSort: [{ required: true, message: "角色顺序不能为空", trigger: "blur" }],
    allowedPortalScope: [{ required: true, message: "前端范围不能为空", trigger: "change" }],
    defaultPortalCode: [{ required: true, message: "默认前端不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const filteredDefaultPortalOptions = computed(() => {
  if (form.value.allowedPortalScope === "ygb") {
    return roleDefaultPortalOptions.filter(item => item.value === "ygb")
  }
  if (form.value.allowedPortalScope === "azb") {
    return roleDefaultPortalOptions.filter(item => item.value === "azb")
  }
  return roleDefaultPortalOptions
})

const fixedRolePortalRule = computed(() => resolveFixedRolePortalRule(form.value))
const isFixedPortalRuleRole = computed(() => Boolean(fixedRolePortalRule.value))
const fixedRolePortalHint = computed(() => {
  if (!fixedRolePortalRule.value) {
    return ""
  }
  const scopeLabel = getPortalScopeLabel(fixedRolePortalRule.value.allowedPortalScope)
  const portalLabel = getPortalCodeLabel(fixedRolePortalRule.value.defaultPortalCode)
  const readOnlyHint = fixedRolePortalRule.value.readOnly ? "，并保持只读视图" : ""
  return `该固定角色按双门户实施方案锁定为“${scopeLabel} / 默认 ${portalLabel}”${readOnlyHint}。`
})

const filteredMenuPortalScopeOptions = computed(() => {
  if (form.value.allowedPortalScope === "ygb") {
    return menuPortalScopeOptions.filter(item => item.value === "ygb")
  }
  if (form.value.allowedPortalScope === "azb") {
    return menuPortalScopeOptions.filter(item => item.value === "azb")
  }
  return menuPortalScopeOptions
})

function getPortalScopeLabel(scope) {
  const normalized = String(scope || "both").toLowerCase()
  return rolePortalScopeOptions.find(item => item.value === normalized)?.label || "双前端"
}

function getPortalScopeTagType(scope) {
  const normalized = String(scope || "both").toLowerCase()
  if (normalized === "ygb") {
    return "success"
  }
  if (normalized === "azb") {
    return "warning"
  }
  return "info"
}

function getPortalCodeLabel(code) {
  const normalized = String(code || "ygb").toLowerCase()
  return roleDefaultPortalOptions.find(item => item.value === normalized)?.label || "粤工保"
}

function applyFixedRolePortalRule(role = form.value) {
  const fixedRule = resolveFixedRolePortalRule(role)
  if (!fixedRule) {
    return false
  }
  form.value.allowedPortalScope = fixedRule.allowedPortalScope
  form.value.defaultPortalCode = fixedRule.defaultPortalCode
  if (fixedRule.allowedPortalScope === "ygb" || fixedRule.allowedPortalScope === "azb") {
    menuPortalScope.value = fixedRule.allowedPortalScope
  }
  return true
}

function getList() {
  loading.value = true
  listRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    roleList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.roleId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleStatusChange(row) {
  const text = row.status === "0" ? "启用" : "停用"
  proxy.$modal.confirm(`确认要${text}“${row.roleName}”角色吗？`).then(() => {
    return changeRoleStatus(row.roleId, row.status)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(() => {
    row.status = row.status === "0" ? "1" : "0"
  })
}

function handleDelete(row) {
  const roleIds = row.roleId || ids.value
  proxy.$modal.confirm(`是否确认删除角色编号为“${roleIds}”的数据项？`).then(() => {
    return delRole(roleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download("system/role/export", { ...queryParams.value }, `role_${new Date().getTime()}.xlsx`)
}

function handleAuthUser(row) {
  router.push("/system/role-auth/user/" + row.roleId)
}

function buildMenuPortalScopeQuery() {
  return menuPortalScope.value ? { portalScope: menuPortalScope.value } : {}
}

function getMenuTreeselect() {
  return menuTreeselect(buildMenuPortalScopeQuery()).then(response => {
    menuOptions.value = response.data
    roleMenuCheckedKeys.value = []
    applyRoleMenuCheckedKeys()
    return response
  })
}

function getRoleMenuTreeselect(roleId) {
  return roleMenuTreeselect(roleId, buildMenuPortalScopeQuery()).then(response => {
    menuOptions.value = response.menus
    roleMenuCheckedKeys.value = response.checkedKeys || []
    applyRoleMenuCheckedKeys()
    return response
  })
}

function getDeptTree(roleId) {
  return deptTreeSelect(roleId).then(response => {
    deptOptions.value = response.depts
    return response
  })
}

function applyRoleMenuCheckedKeys() {
  nextTick(() => {
    if (!menuRef.value) {
      return
    }
    menuRef.value.setCheckedKeys([])
    roleMenuCheckedKeys.value.forEach(menuId => {
      menuRef.value.setChecked(menuId, true, false)
    })
  })
}

function getMenuAllCheckedKeys() {
  const checkedKeys = menuRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuRef.value?.getHalfCheckedKeys() || []
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
  return checkedKeys
}

function getDeptAllCheckedKeys() {
  const checkedKeys = deptRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = deptRef.value?.getHalfCheckedKeys() || []
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
  return checkedKeys
}

function buildRoleMenuSubmitKeys() {
  return getMenuAllCheckedKeys()
}

function handleCheckedTreeExpand(value, type) {
  const treeRef = type === "menu" ? menuRef.value : deptRef.value
  const treeList = type === "menu" ? menuOptions.value : deptOptions.value
  treeList.forEach(item => {
    if (treeRef?.store?.nodesMap?.[item.id]) {
      treeRef.store.nodesMap[item.id].expanded = value
    }
  })
}

function handleCheckedTreeNodeAll(value, type) {
  if (type === "menu") {
    menuRef.value?.setCheckedNodes(value ? menuOptions.value : [])
  } else {
    deptRef.value?.setCheckedNodes(value ? deptOptions.value : [])
  }
}

function handleCheckedTreeConnect(value, type) {
  if (type === "menu") {
    form.value.menuCheckStrictly = !!value
  } else {
    form.value.deptCheckStrictly = !!value
  }
}

function ensureMenuPortalScopeWithinAllowed(allowedPortalScope = form.value.allowedPortalScope) {
  if (allowedPortalScope === "ygb" || allowedPortalScope === "azb") {
    menuPortalScope.value = allowedPortalScope
    return
  }
  if (!["ygb", "azb", "both"].includes(menuPortalScope.value)) {
    menuPortalScope.value = defaultMenuPortalScope
  }
}

function handleAllowedPortalScopeChange(value) {
  if (applyFixedRolePortalRule()) {
    return
  }
  if (value === "ygb" || value === "azb") {
    form.value.defaultPortalCode = value
  } else if (!form.value.defaultPortalCode) {
    form.value.defaultPortalCode = "ygb"
  }
  ensureMenuPortalScopeWithinAllowed(value)
}

function normalizeRoleForm(role = {}) {
  const fixedRule = resolveFixedRolePortalRule(role)
  const allowedPortalScope = fixedRule?.allowedPortalScope || role.allowedPortalScope || "both"
  const defaultPortalCode = fixedRule?.defaultPortalCode || (
    allowedPortalScope === "azb"
      ? "azb"
      : allowedPortalScope === "ygb"
        ? "ygb"
        : role.defaultPortalCode || "ygb"
  )
  return {
    ...role,
    roleSort: Number(role.roleSort || 0),
    allowedPortalScope,
    defaultPortalCode,
    menuCheckStrictly: role.menuCheckStrictly !== false,
    deptCheckStrictly: role.deptCheckStrictly !== false
  }
}

function reset() {
  menuRef.value?.setCheckedKeys([])
  deptRef.value?.setCheckedKeys([])
  menuExpand.value = false
  menuNodeAll.value = false
  deptExpand.value = true
  deptNodeAll.value = false
  menuPortalScope.value = defaultMenuPortalScope
  menuOptions.value = []
  deptOptions.value = []
  roleMenuCheckedKeys.value = []
  form.value = normalizeRoleForm({
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: "0",
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: undefined
  })
  proxy.resetForm("roleRef")
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增角色"
  applyFixedRolePortalRule()
  ensureMenuPortalScopeWithinAllowed(form.value.allowedPortalScope)
  getMenuTreeselect()
}

function handleUpdate(row) {
  reset()
  const roleId = row.roleId || ids.value
  title.value = "修改角色"
  getRole(roleId).then(response => {
    form.value = normalizeRoleForm(response.data)
    ensureMenuPortalScopeWithinAllowed(form.value.allowedPortalScope)
    open.value = true
    getRoleMenuTreeselect(roleId)
  })
}

function handleMenuPortalScopeChange() {
  menuNodeAll.value = false
  menuExpand.value = false
  if (form.value.roleId != undefined) {
    getRoleMenuTreeselect(form.value.roleId)
    return
  }
  getMenuTreeselect()
}

function submitForm() {
  proxy.$refs["roleRef"].validate(valid => {
    if (!valid) {
      return
    }
    applyFixedRolePortalRule()
    const submitData = {
      ...form.value,
      menuIds: buildRoleMenuSubmitKeys(),
      menuPortalScope: menuPortalScope.value || "both"
    }
    const request = form.value.roleId != undefined ? updateRole(submitData) : addRole(submitData)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.roleId != undefined ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function cancel() {
  open.value = false
  reset()
}

function dataScopeSelectChange(value) {
  if (value !== "2") {
    deptRef.value?.setCheckedKeys([])
  }
}

function handleDataScope(row) {
  reset()
  const deptTree = getDeptTree(row.roleId)
  getRole(row.roleId).then(response => {
    form.value = normalizeRoleForm(response.data)
    openDataScope.value = true
    nextTick(() => {
      deptTree.then(res => {
        deptRef.value?.setCheckedKeys(res.checkedKeys || [])
      })
    })
  })
  title.value = "分配数据权限"
}

function submitDataScope() {
  if (form.value.roleId == undefined) {
    return
  }
  form.value.deptIds = getDeptAllCheckedKeys()
  dataScope(form.value).then(() => {
    proxy.$modal.msgSuccess("修改成功")
    openDataScope.value = false
    getList()
  })
}

function cancelDataScope() {
  openDataScope.value = false
  reset()
}

getList()
</script>

<style scoped>
.menu-scope-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.menu-scope-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.fixed-role-tip {
  font-size: 12px;
  line-height: 1.5;
  color: #606266;
}
</style>
