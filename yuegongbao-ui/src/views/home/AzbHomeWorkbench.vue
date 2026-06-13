<template>
  <HomeWorkbenchShell
    v-bind="props"
    portal-summary-text="按应急治理语义、角色分工与真实摘要重排默认工作台"
    playbook-title="当前角色风险处置路径"
    playbook-hint="按当前账号角色和可见模块组织风险治理、协同和现场闭环顺序"
    focus-title="当前风险态势"
    focus-hint="从共享后台的真实摘要接口装配应急侧风险、设备、安责险和协同概览"
    module-title="当前治理域拆分"
    module-hint="按安责保前端语义组织设备、安责险、技术防范与协同边界"
    task-title="高频治理路径"
    task-hint="围绕应急监管、保险协同、企业现场负责人的实际处置顺序组织"
    stack-title="前端架构层次"
    stack-hint="安责保独立壳层演进，共享后台与粤工保共底座"
    policy-title="平台治理原则"
    policy-hint="保证安责保前端聚焦风险治理，但企业、人员、预警和高危作业仍共底座"
    @open-action="$emit('open-action', $event)"
  />
</template>

<script setup>
import HomeWorkbenchShell from './HomeWorkbenchShell.vue'
import { homeWorkbenchProps } from './homeWorkbenchProps'

defineOptions({
  name: 'AzbHomeWorkbench'
})

defineEmits(['open-action'])

const props = defineProps(homeWorkbenchProps)
</script>
