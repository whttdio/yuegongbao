<template>
  <CitizenContentManager :config="config" />
</template>

<script setup name="YgbCitizenServiceLawLibrary">
import CitizenContentManager from '../shared/CitizenContentManager.vue'

const lawCategoryOptions = [
  { label: '劳动合同', value: 'contract' },
  { label: '工资支付', value: 'salary' },
  { label: '社会保险', value: 'social' },
  { label: '工伤保险', value: 'injury' },
  { label: '个人所得税', value: 'tax' },
  { label: '投诉举报', value: 'complaint' },
  { label: '法律咨询', value: 'legal' }
]

const config = {
  title: '权益法规内容库',
  description: '维护移动端法律咨询、权益查询和法规学习可消费的内容，覆盖合同、工资、社保、工伤、个税和投诉咨询场景。',
  sectionCode: 'law_library',
  itemName: '法规内容',
  tableTitle: '法规与权益内容',
  tableDesc: '内容可被移动端法律咨询、法规库和权益服务入口复用',
  miniappEntry: '/pages/legal/index',
  tone: 'indigo',
  categoryOptions: lawCategoryOptions,
  tableFields: [
    { key: 'lawType', label: '法规类型', width: 130 },
    { key: 'sourceOrg', label: '发布机关', width: 160 },
    { key: 'effectiveDate', label: '生效日期', width: 120 }
  ],
  detailFields: [
    { key: 'lawType', label: '法规类型' },
    { key: 'sourceOrg', label: '发布机关' },
    { key: 'effectiveDate', label: '生效日期' }
  ],
  formFields: [
    { key: 'lawType', label: '法规类型', placeholder: '如 条例、办法、问答、案例' },
    { key: 'sourceOrg', label: '发布机关', placeholder: '如 广东省人社厅' },
    { key: 'effectiveDate', label: '生效日期', placeholder: '如 2026-01-01' },
    { key: 'miniappVisible', label: '移动端展示', type: 'switch', defaultValue: '1' },
    { key: 'miniappPath', label: '移动端路径', span: 24, defaultValue: '/pages/legal/index' }
  ]
}
</script>
