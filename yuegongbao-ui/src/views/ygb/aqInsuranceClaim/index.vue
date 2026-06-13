<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addAqInsuranceClaim,
  delAqInsuranceClaim,
  getAqInsuranceClaim,
  getAqInsuranceClaimSummary,
  listAqInsuranceClaim,
  updateAqInsuranceClaim
} from '@/api/ygb/aqInsurance'

const config = {
  title: '赔付率监控',
  description: '在安责险主链下补齐赔付率监控对象、状态流转和导出能力。',
  permPrefix: 'ygb:aqInsuranceClaim',
  filePrefix: 'aq_insurance_claim',
  defaultCategoryCode: 'claim_rate',
  defaultWorkflowStatus: 'pending',
  defaultSourceLabel: '安责险监管',
  recordNameLabel: '监控标题',
  recordNamePlaceholder: '请输入监控标题',
  listApi: query => listAqInsuranceClaim(query),
  summaryApi: query => getAqInsuranceClaimSummary(query),
  detailApi: id => getAqInsuranceClaim(id),
  addApi: data => addAqInsuranceClaim(data),
  updateApi: data => updateAqInsuranceClaim(data),
  deleteApi: ids => delAqInsuranceClaim(ids),
  exportUrl: 'ygb/aqInsurance/claim/export'
}
</script>
