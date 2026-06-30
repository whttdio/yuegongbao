<template>
  <CitizenContentManager :config="config" />
</template>

<script setup name="YgbCitizenServiceTrainingCourse">
import CitizenContentManager from '../shared/CitizenContentManager.vue'

const trainingCategoryOptions = [
  { label: '安全技能', value: 'safety' },
  { label: '权益保护', value: 'rights' },
  { label: '职业发展', value: 'career' },
  { label: '高危作业', value: 'high_risk' },
  { label: '新业态专题', value: 'newform' }
]

const workerTypeOptions = [
  { label: '通用劳动者', value: 'all' },
  { label: '建筑工人', value: 'construction' },
  { label: '快递/外卖骑手', value: 'rider' },
  { label: '家政服务', value: 'domestic' },
  { label: '高危岗位', value: 'high_risk' }
]

const config = {
  title: '培训课程运营',
  description: '维护移动端培训课程的分类、学时、适用对象和上架状态，支撑劳动者培训解锁、学习记录和课程详情。',
  sectionCode: 'training_course',
  itemName: '课程',
  tableTitle: '移动端课程清单',
  tableDesc: '与工人端 /pages/training/index 和课程详情保持一致',
  miniappEntry: '/pages/training/index',
  tone: 'green',
  categoryOptions: trainingCategoryOptions,
  tableFields: [
    { key: 'courseKey', label: '课程编码', width: 140 },
    { key: 'durationMinutes', label: '学时/分钟', width: 110 },
    { key: 'targetWorkerType', label: '适用对象', width: 130, formatter: value => workerTypeOptions.find(item => item.value === value)?.label || value || '-' }
  ],
  detailFields: [
    { key: 'courseKey', label: '课程编码' },
    { key: 'durationMinutes', label: '学时/分钟' },
    { key: 'targetWorkerType', label: '适用对象', formatter: value => workerTypeOptions.find(item => item.value === value)?.label || value || '-' },
    { key: 'requiredFlag', label: '是否必修', formatter: value => value === '1' ? '必修' : '选修' }
  ],
  formFields: [
    { key: 'courseKey', label: '课程编码', placeholder: '如 safety-basic-001' },
    { key: 'durationMinutes', label: '学时/分钟', type: 'number', defaultValue: 30 },
    { key: 'targetWorkerType', label: '适用对象', type: 'select', options: workerTypeOptions, defaultValue: 'all' },
    { key: 'requiredFlag', label: '是否必修', type: 'switch', defaultValue: '0' },
    { key: 'miniappVisible', label: '移动端展示', type: 'switch', defaultValue: '1' },
    { key: 'miniappPath', label: '移动端路径', span: 24, defaultValue: '/pages/training/index' }
  ]
}
</script>
