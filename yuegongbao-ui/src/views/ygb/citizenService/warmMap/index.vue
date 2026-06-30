<template>
  <CitizenContentManager :config="config" />
</template>

<script setup name="YgbCitizenServiceWarmMap">
import CitizenContentManager from '../shared/CitizenContentManager.vue'

const serviceTypeOptions = [
  { label: '暖新驿站', value: 'station' },
  { label: '法律服务点', value: 'legal' },
  { label: '工会服务点', value: 'union' },
  { label: '就业服务点', value: 'employment' },
  { label: '培训服务点', value: 'training' },
  { label: '应急/AED/换电', value: 'emergency' }
]

const config = {
  title: '暖新地图服务点管理',
  description: '维护移动端可查询的驿站、法律、工会、就业、培训和应急服务点，确保劳动者能就近找到真实可用的服务。',
  sectionCode: 'warm_map',
  itemName: '服务点',
  tableTitle: '服务点台账',
  tableDesc: '与移动端暖新地图、服务点导航和权益入口保持一致',
  miniappEntry: '/pages/job/map',
  tone: 'blue',
  categoryOptions: serviceTypeOptions,
  tableFields: [
    { key: 'address', label: '服务地址', minWidth: 180 },
    { key: 'serviceHours', label: '服务时间', width: 150 },
    { key: 'contactMobile', label: '联系电话', width: 130 }
  ],
  detailFields: [
    { key: 'address', label: '服务地址' },
    { key: 'longitude', label: '经度' },
    { key: 'latitude', label: '纬度' },
    { key: 'serviceHours', label: '服务时间' },
    { key: 'contactName', label: '联系人' },
    { key: 'contactMobile', label: '联系电话' },
    { key: 'serviceTags', label: '服务标签' }
  ],
  formFields: [
    { key: 'address', label: '服务地址', span: 24, placeholder: '请输入详细地址' },
    { key: 'longitude', label: '经度', type: 'number', precision: 6, step: 0.000001 },
    { key: 'latitude', label: '纬度', type: 'number', precision: 6, step: 0.000001 },
    { key: 'serviceHours', label: '服务时间', placeholder: '如 工作日 09:00-18:00' },
    { key: 'contactName', label: '联系人' },
    { key: 'contactMobile', label: '联系电话' },
    { key: 'serviceTags', label: '服务标签', span: 24, placeholder: '如 饮水、休息、法律咨询、换电' },
    { key: 'miniappVisible', label: '移动端展示', type: 'switch', defaultValue: '1' },
    { key: 'miniappPath', label: '移动端路径', span: 24, defaultValue: '/pages/job/map' }
  ]
}
</script>
