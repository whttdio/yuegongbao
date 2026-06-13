<template>
  <HomeWorkbenchShell
    v-bind="props"
    portal-summary-text="按门户、角色与真实摘要重排默认工作台"
    playbook-title="当前角色工作路径"
    playbook-hint="按当前账号角色和可见模块自动给出建议顺序"
    focus-title="当前真实态势"
    focus-hint="从共享后台的真实摘要接口直接装配，不再只展示静态说明"
    module-title="当前业务拆分"
    module-hint="按门户语义和共享后台能力组织模块边界"
    task-title="高频使用路径"
    task-hint="围绕企业、工地、财务和监管的实际操作顺序组织"
    stack-title="前端架构层次"
    stack-hint="门户独立演进，共享后台不重复建设"
    policy-title="平台治理原则"
    policy-hint="保证双门户差异明确，但企业、人员、预警和高危作业仍共底座"
    @open-action="$emit('open-action', $event)"
  />
</template>

<script setup>
import HomeWorkbenchShell from './HomeWorkbenchShell.vue'
import { homeWorkbenchProps } from './homeWorkbenchProps'

defineOptions({
  name: 'YgbHomeWorkbench'
})

defineEmits(['open-action'])

const props = defineProps(homeWorkbenchProps)
</script>
