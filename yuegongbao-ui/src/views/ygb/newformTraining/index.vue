<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addNewformTraining,
  delNewformTraining,
  getNewformTraining,
  getNewformTrainingSummary,
  listNewformTraining,
  updateNewformTraining
} from '@/api/ygb/newformWorker'

const config = {
  title: '培训管理',
  description: '复用扩展台账对象维护新业态培训计划、课程安排、学时留痕和闭环状态。',
  permPrefix: 'ygb:newformTraining',
  filePrefix: 'newform_training',
  defaultCategoryCode: 'training',
  defaultWorkflowStatus: 'pending',
  defaultSourceLabel: '新业态监管',
  recordNameLabel: '培训主题',
  recordNamePlaceholder: '请输入培训主题',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'platformName', 'recordName', 'workflowStatus', 'status'],
  routeQueryFields: ['statMonth', 'regionCode', 'enterpriseId', 'platformName'],
  listApi: query => listNewformTraining(query),
  summaryApi: query => getNewformTrainingSummary(query),
  detailApi: id => getNewformTraining(id),
  addApi: data => addNewformTraining(data),
  updateApi: data => updateNewformTraining(data),
  deleteApi: ids => delNewformTraining(ids),
  exportUrl: 'ygb/newform/training/export'
}
</script>
