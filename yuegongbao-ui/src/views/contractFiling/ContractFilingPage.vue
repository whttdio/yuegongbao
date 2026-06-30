<template>
  <div class="app-container contract-filing-page" :class="portalClass">
    <section class="contract-hero">
      <div>
        <p class="contract-hero__eyebrow">{{ copy.eyebrow }}</p>
        <h1>{{ copy.title }}</h1>
        <p>{{ copy.description }}</p>
      </div>
      <div class="contract-hero__facts">
        <span v-for="item in copy.facts" :key="item">{{ item }}</span>
      </div>
    </section>

    <div class="contract-summary">
      <div v-for="item in summaryCards" :key="item.key" class="contract-summary__item" :class="item.tone">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.note }}</small>
      </div>
    </div>

    <el-card class="contract-panel" shadow="never">
      <el-form :model="queryParams" :inline="true" label-width="86px">
        <el-form-item v-if="mode !== 'template'" label="合同编号">
          <el-input v-model="queryParams.contractNo" placeholder="输入合同编号" clearable style="width: 190px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="mode !== 'template'" label="劳动者">
          <el-input v-model="queryParams.personKeyword" placeholder="姓名/身份证" clearable style="width: 190px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="mode !== 'template'" label="企业">
          <el-input v-model="queryParams.enterpriseKeyword" placeholder="派遣/用工单位" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="mode !== 'template'" label="合同类型">
          <el-select v-model="queryParams.contractType" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="item in contractTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="mode === 'list'" label="备案状态">
          <el-select v-model="queryParams.contractStatus" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="item in contractStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="mode === 'list'" label="区块链">
          <el-select v-model="queryParams.hasBlockchain" placeholder="全部" clearable style="width: 140px">
            <el-option label="已存证" value="1" />
            <el-option label="未存证" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="mode === 'expiry'" label="到期日期">
          <el-date-picker
            v-model="expiryRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item v-if="mode === 'template'" label="模板名称">
          <el-input v-model="templateQuery.templateName" placeholder="输入模板名称" clearable style="width: 210px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="mode === 'template'" label="模板类型">
          <el-select v-model="templateQuery.templateType" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="item in contractTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="mode === 'template'" label="审核状态">
          <el-select v-model="templateQuery.reviewStatus" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="item in reviewStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="contract-panel" shadow="never">
      <div class="contract-toolbar">
        <div>
          <el-button v-if="mode === 'list' && isYgb" type="primary" icon="Plus" v-hasPermi="['ygb:contract:add']" @click="openForm()">新增合同</el-button>
          <el-button v-if="mode === 'expiry'" type="warning" icon="Bell" :disabled="multiple" v-hasPermi="[perms.remind]" @click="batchRemind">批量提醒</el-button>
          <el-button v-if="mode === 'unfiled'" type="danger" icon="Warning" :disabled="multiple" v-hasPermi="[perms.warn]" @click="batchWarn">生成预警</el-button>
          <el-button v-if="mode === 'template' && isYgb" type="primary" icon="Upload" v-hasPermi="['ygb:contractTemplate:add']" @click="openTemplateForm()">上传模板</el-button>
          <el-button icon="Download" plain v-hasPermi="[exportPerm]" @click="handleExport">导出</el-button>
        </div>
        <span>当前 {{ tableTotal }} 条</span>
      </div>

      <el-table v-if="mode !== 'template'" v-loading="loading" :data="contractRows" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="46" />
        <el-table-column label="合同编号" prop="contractNo" min-width="160" show-overflow-tooltip />
        <el-table-column label="劳动者" prop="personName" width="110" />
        <el-table-column label="合同类型" width="110">
          <template #default="{ row }">
            <el-tag effect="plain">{{ labelOf(contractTypeOptions, row.contractType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="派遣单位" prop="dispatchEnterpriseName" min-width="190" show-overflow-tooltip />
        <el-table-column label="用工单位" prop="employerEnterpriseName" min-width="190" show-overflow-tooltip />
        <el-table-column v-if="mode === 'expiry'" label="到期" width="150">
          <template #default="{ row }">
            <div class="contract-cell-stack">
              <strong>{{ parseTime(row.endDate, '{y}-{m}-{d}') || '-' }}</strong>
              <span>{{ expiryText(row) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-if="mode === 'unfiled'" label="风险原因" prop="riskReason" min-width="210" show-overflow-tooltip />
        <el-table-column v-if="mode === 'list'" label="备案状态" width="110">
          <template #default="{ row }">
            <el-tag :type="contractStatusTone(row.contractStatus)">{{ labelOf(contractStatusOptions, row.contractStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="区块链存证" width="130">
          <template #default="{ row }">
            <el-tag :type="row.blockchainHash ? 'success' : 'info'" effect="plain">{{ row.blockchainHash ? '已存证' : '未存证' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="mode === 'expiry'" label="提醒状态" width="130">
          <template #default="{ row }">{{ row.lastRemindTime ? parseTime(row.lastRemindTime, '{m}-{d} {h}:{i}') : '未提醒' }}</template>
        </el-table-column>
        <el-table-column v-if="mode === 'unfiled'" label="预警状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.warningId ? 'warning' : 'info'">{{ row.warningId ? '已生成' : '未生成' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="210">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="openDetail(row)">详情</el-button>
            <el-button v-if="mode === 'list' && isYgb" link type="primary" icon="Edit" v-hasPermi="['ygb:contract:edit']" @click="openForm(row)">修改</el-button>
            <el-button v-if="row.blockchainHash" link type="success" icon="Link" v-hasPermi="[perms.evidence]" @click="copyEvidence(row)">存证</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-table v-else v-loading="loading" :data="templateRows" @selection-change="handleSelectionChange">
        <el-table-column label="模板编码" prop="templateCode" width="150" />
        <el-table-column label="模板名称" prop="templateName" min-width="180" show-overflow-tooltip />
        <el-table-column label="版本" prop="templateVersion" width="100" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">{{ labelOf(contractTypeOptions, row.templateType) }}</template>
        </el-table-column>
        <el-table-column label="适用范围" prop="applicableScope" min-width="160" show-overflow-tooltip />
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="reviewStatusTone(row.reviewStatus)">{{ labelOf(reviewStatusOptions, row.reviewStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启停" width="130">
          <template #default="{ row }">
            <el-switch
              v-if="isYgb"
              :model-value="row.status"
              active-value="1"
              inactive-value="0"
              :disabled="row.status !== '1' && row.reviewStatus !== 'approved'"
              v-hasPermi="['ygb:contractTemplate:status']"
              @change="value => changeTemplateStatus(row, value)"
            />
            <el-tag class="template-status-tag" :type="row.status === '1' ? 'success' : 'info'">{{ row.status === '1' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="openTemplateDetail(row)">详情</el-button>
            <el-button v-if="isYgb" link type="primary" icon="Edit" v-hasPermi="['ygb:contractTemplate:edit']" @click="openTemplateForm(row)">修改</el-button>
            <el-button v-if="isYgb" link type="warning" icon="Promotion" :disabled="!canSubmitTemplate(row)" v-hasPermi="['ygb:contractTemplate:submit']" @click="submitTemplate(row)">提交</el-button>
            <el-button link type="success" icon="CircleCheck" :disabled="!canReviewTemplate(row)" v-hasPermi="[perms.templateReview]" @click="openReview(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="mode !== 'template' && tableTotal > 0"
        :total="tableTotal"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
      <pagination
        v-if="mode === 'template' && tableTotal > 0"
        :total="tableTotal"
        v-model:page="templateQuery.pageNum"
        v-model:limit="templateQuery.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-drawer v-model="detailOpen" :title="detailTitle" size="680px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="合同编号">{{ detail.contractNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合同类型">{{ labelOf(contractTypeOptions, detail.contractType) }}</el-descriptions-item>
          <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ detail.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位" :span="2">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用工单位" :span="2">{{ detail.employerEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="签订日期">{{ parseTime(detail.signDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="终止日期">{{ parseTime(detail.endDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备案编号">{{ detail.filingNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备案时间">{{ parseTime(detail.filingTime, '{y}-{m}-{d} {h}:{i}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区块链哈希" :span="2">{{ detail.blockchainHash || '未存证' }}</el-descriptions-item>
          <el-descriptions-item label="合同文件" :span="2">
            <el-link v-if="detail.contractFileUrl" type="primary" href="javascript:void(0)" @click="openResourceFile(detail.contractFileUrl)">打开合同文件</el-link>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="办理提示" :span="2">{{ detailHint }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>

    <el-dialog v-model="formOpen" :title="form.contractId ? '修改合同备案' : '新增合同备案'" width="760px" append-to-body>
      <el-form ref="contractRef" :model="form" :rules="contractRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="合同编号" prop="contractNo"><el-input v-model="form.contractNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="合同类型" prop="contractType"><el-select v-model="form.contractType" style="width: 100%"><el-option v-for="item in contractTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="派遣单位" prop="dispatchEnterpriseId"><el-select v-model="form.dispatchEnterpriseId" filterable style="width: 100%"><el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="用工单位" prop="employerEnterpriseId"><el-select v-model="form.employerEnterpriseId" filterable style="width: 100%"><el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" /></el-select></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="劳动者" prop="personId">
              <el-select
                v-model="form.personId"
                filterable
                remote
                reserve-keyword
                clearable
                :remote-method="searchPersons"
                :loading="personLoading"
                placeholder="输入姓名检索后选择"
                style="width: 100%"
              >
                <el-option v-for="item in personOptions" :key="item.personId" :label="personLabel(item)" :value="item.personId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="备案状态" prop="contractStatus"><el-select v-model="form.contractStatus" style="width: 100%"><el-option v-for="item in contractStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="签订日期"><el-date-picker v-model="form.signDate" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="终止日期"><el-date-picker v-model="form.endDate" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="备案编号"><el-input v-model="form.filingNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="月工资"><el-input-number v-model="form.monthlyWage" :min="0" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="合同文件" prop="contractFileUrl"><file-upload v-model="form.contractFileUrl" :limit="1" :file-size="20" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="formOpen = false">取消</el-button>
        <el-button type="primary" @click="submitContract">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="templateFormOpen" :title="templateForm.templateId ? '修改合同模板' : '上传合同模板'" width="680px" append-to-body>
      <el-form ref="templateRef" :model="templateForm" :rules="templateRules" label-width="110px">
        <el-form-item label="模板编码" prop="templateCode"><el-input v-model="templateForm.templateCode" /></el-form-item>
        <el-form-item label="模板名称" prop="templateName"><el-input v-model="templateForm.templateName" /></el-form-item>
        <el-form-item label="模板版本"><el-input v-model="templateForm.templateVersion" /></el-form-item>
        <el-form-item label="合同类型" prop="templateType"><el-select v-model="templateForm.templateType" style="width: 100%"><el-option v-for="item in contractTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="适用范围"><el-input v-model="templateForm.applicableScope" /></el-form-item>
        <el-form-item label="模板文件" prop="templateFileUrl"><file-upload v-model="templateForm.templateFileUrl" :limit="1" :file-size="20" /></el-form-item>
        <el-form-item label="模板说明"><el-input v-model="templateForm.contentText" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateFormOpen = false">取消</el-button>
        <el-button type="primary" @click="submitTemplateForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="templateDetailOpen" title="合同模板详情" width="680px" append-to-body>
      <el-descriptions v-if="templateDetail" :column="2" border>
        <el-descriptions-item label="模板编码">{{ templateDetail.templateCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模板版本">{{ templateDetail.templateVersion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模板名称" :span="2">{{ templateDetail.templateName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">{{ labelOf(reviewStatusOptions, templateDetail.reviewStatus) }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ templateDetail.reviewBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ templateDetail.reviewRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="模板文件" :span="2">
          <el-link v-if="templateDetail.templateFileUrl" href="javascript:void(0)" type="primary" @click="openResourceFile(templateDetail.templateFileUrl)">打开模板文件</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="reviewOpen" title="模板审核" width="520px" append-to-body>
      <el-form :model="reviewForm" label-width="90px">
        <el-form-item label="审核结论">
          <el-radio-group v-model="reviewForm.reviewStatus">
            <el-radio-button label="approved">通过</el-radio-button>
            <el-radio-button label="rejected">驳回</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.reviewRemark" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewOpen = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, ref, watch } from 'vue'
import FileUpload from '@/components/FileUpload/index.vue'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { listPerson } from '@/api/ygb/person'
import {
  addContract,
  getContract,
  getContractExpirySummary,
  getContractSummary,
  getContractUnfiledSummary,
  listContract,
  listContractExpiry,
  listContractUnfiled,
  remindContractExpiry,
  updateContract,
  warnContractUnfiled
} from '@/api/ygb/contract'
import {
  addContractTemplate,
  changeContractTemplateStatus,
  getContractTemplate,
  listContractTemplate,
  reviewContractTemplate,
  submitContractTemplate,
  updateContractTemplate
} from '@/api/ygb/contractTemplate'
import { ElMessage } from 'element-plus'

const props = defineProps({
  portal: { type: String, default: 'ygb' },
  mode: { type: String, default: 'list' }
})

const { proxy } = getCurrentInstance()
const isYgb = computed(() => props.portal === 'ygb')
const mode = computed(() => props.mode)
const portalClass = computed(() => (isYgb.value ? 'contract-filing-page--ygb' : 'contract-filing-page--azb'))

const pageCopies = {
  ygb: {
    list: {
      eyebrow: '合同备案',
      title: '合同列表与区块链存证',
      description: '围绕劳动合同、派遣协议和用工协议做查询筛选、备案维护、文件留痕和区块链存证查看。',
      facts: ['监管维护', '备案闭环', '存证可追溯']
    },
    expiry: {
      eyebrow: '合同备案',
      title: '30日合同到期批量提醒',
      description: '默认聚焦未来30日到期合同，批量记录提醒动作，推动企业提前续签、归档或解除。',
      facts: ['30日窗口', '批量提醒', '动作留痕']
    },
    unfiled: {
      eyebrow: '合同备案',
      title: '未备案企业人员预警',
      description: '识别未备案、备案编号缺失、备案时间缺失的合同对象，并生成去重预警工单。',
      facts: ['风险原因', '预警去重', '闭环处置']
    },
    template: {
      eyebrow: '合同模板库',
      title: '标准合同模板上传审核管理',
      description: '标准模板上传后进入待审核，审核通过并启用后才能被合同备案使用。',
      facts: ['上传审核', '版本管理', '启停控制']
    }
  },
  azb: {
    list: {
      eyebrow: '安责保合同协同',
      title: '合同备案核验与存证查看',
      description: '从安责保视角查看人员合同备案、区块链存证和高危岗位治理关联，不承接监管专属维护动作。',
      facts: ['协同查看', '风险核验', '保险联动']
    },
    expiry: {
      eyebrow: '安责保合同协同',
      title: '到期合同协同提醒',
      description: '聚焦高危岗位人员合同临期风险，记录协同提醒，辅助保险、企业和应急侧提前核验。',
      facts: ['临期核验', '协同提醒', '共享底座']
    },
    unfiled: {
      eyebrow: '安责保合同协同',
      title: '未备案合同风险核验',
      description: '把合同未备案风险纳入安责保治理视图，支撑人员风险复核和企业整改协同。',
      facts: ['风险核验', '工单去重', '应急协同']
    },
    template: {
      eyebrow: '安责保合同协同',
      title: '合同模板库协同审核',
      description: '查看标准合同模板和审核状态，按安责保协同角色参与模板审核与下载核验。',
      facts: ['只读优先', '审核协同', '模板追溯']
    }
  }
}

const copy = computed(() => pageCopies[props.portal][props.mode])
const perms = computed(() => ({
  remind: `${props.portal}:contract:remind`,
  warn: `${props.portal}:contract:warn`,
  evidence: `${props.portal}:contract:evidence`,
  templateReview: `${props.portal}:contractTemplate:review`
}))
const exportPerm = computed(() => (mode.value === 'template' ? `${props.portal}:contractTemplate:export` : `${props.portal}:contract:export`))

const contractTypeOptions = [
  { label: '劳动合同', value: '1' },
  { label: '派遣协议', value: '2' },
  { label: '用工协议', value: '3' }
]
const contractStatusOptions = [
  { label: '草稿', value: '0' },
  { label: '待备案', value: '1' },
  { label: '已备案', value: '2' },
  { label: '驳回', value: '3' },
  { label: '已到期', value: '4' },
  { label: '已解除', value: '5' }
]
const reviewStatusOptions = [
  { label: '待审核', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已驳回', value: 'rejected' }
]

const loading = ref(false)
const contractRows = ref([])
const templateRows = ref([])
const tableTotal = ref(0)
const summary = ref({})
const ids = ref([])
const multiple = computed(() => ids.value.length === 0)
const enterpriseOptions = ref([])
const personOptions = ref([])
const personLoading = ref(false)
const expiryRange = ref([])

const queryParams = ref(baseContractQuery())
const templateQuery = ref(baseTemplateQuery())

const detailOpen = ref(false)
const detail = ref(null)
const formOpen = ref(false)
const form = ref(baseContractForm())
const templateFormOpen = ref(false)
const templateForm = ref(baseTemplateForm())
const templateDetailOpen = ref(false)
const templateDetail = ref(null)
const reviewOpen = ref(false)
const reviewingTemplate = ref(null)
const reviewForm = ref({ reviewStatus: 'approved', reviewRemark: '' })

const contractRules = {
  contractNo: [{ required: true, message: '合同编号不能为空', trigger: 'blur' }],
  contractType: [{ required: true, message: '合同类型不能为空', trigger: 'change' }],
  dispatchEnterpriseId: [{ required: true, message: '派遣单位不能为空', trigger: 'change' }],
  employerEnterpriseId: [{ required: true, message: '用工单位不能为空', trigger: 'change' }],
  personId: [{ required: true, message: '劳动者不能为空', trigger: 'change' }],
  contractStatus: [{ required: true, message: '备案状态不能为空', trigger: 'change' }],
  contractFileUrl: [{ required: true, message: '合同文件不能为空', trigger: 'change' }]
}
const templateRules = {
  templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
  templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  templateType: [{ required: true, message: '合同类型不能为空', trigger: 'change' }],
  templateFileUrl: [{ required: true, message: '模板文件不能为空', trigger: 'change' }]
}

const summaryCards = computed(() => {
  if (mode.value === 'template') {
    const rows = templateRows.value
    return [
      { key: 'all', label: '模板总数', value: tableTotal.value, note: '当前筛选', tone: '' },
      { key: 'pending', label: '待审核', value: rows.filter(item => item.reviewStatus === 'pending').length, note: '需处理', tone: 'warning' },
      { key: 'approved', label: '已通过', value: rows.filter(item => item.reviewStatus === 'approved').length, note: '可启用', tone: 'success' },
      { key: 'rejected', label: '已驳回', value: rows.filter(item => item.reviewStatus === 'rejected').length, note: '需修订', tone: 'danger' }
    ]
  }
  return [
    { key: 'total', label: '合同总数', value: summary.value.totalCount || tableTotal.value || 0, note: '当前范围', tone: '' },
    { key: 'filed', label: '已备案', value: summary.value.filedCount || 0, note: '主链完成', tone: 'success' },
    { key: 'expiring', label: '30日到期', value: summary.value.expiringSoonCount || 0, note: '需提醒', tone: 'warning' },
    { key: 'unfiled', label: '未备案', value: summary.value.unfiledCount || 0, note: '需预警', tone: 'danger' }
  ]
})

const detailTitle = computed(() => (detail.value ? `${detail.value.contractNo || '合同'}详情` : '合同详情'))
const detailHint = computed(() => {
  if (!detail.value) return '-'
  if (!detail.value.blockchainHash) return '该合同尚未完成区块链存证，请优先核对合同文件和备案状态。'
  if (['0', '1'].includes(String(detail.value.contractStatus))) return '该合同仍未完成备案，请补齐备案编号和备案时间。'
  return isYgb.value ? '合同备案链路完整，可继续用于考勤、工资和社保税务联动核验。' : '合同备案链路可作为安责保人员风险核验和保险协同依据。'
})

watch(expiryRange, value => {
  queryParams.value.endDateBegin = value?.[0]
  queryParams.value.endDateEnd = value?.[1]
})

function baseContractQuery() {
  return { pageNum: 1, pageSize: 10, contractNo: undefined, personKeyword: undefined, enterpriseKeyword: undefined, contractType: undefined, contractStatus: undefined, hasBlockchain: undefined, endDateBegin: undefined, endDateEnd: undefined }
}

function baseTemplateQuery() {
  return { pageNum: 1, pageSize: 10, templateName: undefined, templateType: undefined, reviewStatus: undefined }
}

function baseContractForm() {
  return { contractId: undefined, contractNo: '', contractType: '1', dispatchEnterpriseId: undefined, employerEnterpriseId: undefined, personId: undefined, contractStatus: '1', signDate: undefined, endDate: undefined, monthlyWage: undefined, filingNo: '', contractFileUrl: '', remark: '' }
}

function baseTemplateForm() {
  return { templateId: undefined, templateCode: '', templateName: '', templateVersion: 'V1.0', templateType: '1', applicableScope: '', templateFileUrl: '', contentText: '', status: '0', reviewStatus: 'pending' }
}

async function getList() {
  loading.value = true
  try {
    if (mode.value === 'template') {
      const res = await listContractTemplate(templateQuery.value)
      templateRows.value = res.rows || []
      tableTotal.value = res.total || 0
      return
    }
    const loader = mode.value === 'expiry' ? listContractExpiry : mode.value === 'unfiled' ? listContractUnfiled : listContract
    const res = await loader(queryParams.value)
    contractRows.value = res.rows || []
    tableTotal.value = res.total || 0
    await getSummary()
  } finally {
    loading.value = false
  }
}

async function getSummary() {
  if (mode.value === 'template') return
  const summaryLoader = mode.value === 'expiry' ? getContractExpirySummary : mode.value === 'unfiled' ? getContractUnfiledSummary : getContractSummary
  const res = await summaryLoader(queryParams.value)
  summary.value = res.data || {}
}

function handleQuery() {
  if (mode.value === 'template') {
    templateQuery.value.pageNum = 1
  } else {
    queryParams.value.pageNum = 1
  }
  getList()
}

function resetQuery() {
  queryParams.value = baseContractQuery()
  templateQuery.value = baseTemplateQuery()
  expiryRange.value = []
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.contractId || item.templateId).filter(Boolean)
}

async function openDetail(row) {
  const res = await getContract(row.contractId)
  detail.value = res.data || row
  detailOpen.value = true
}

async function openForm(row) {
  if (row?.contractId) {
    const res = await getContract(row.contractId)
    form.value = { ...baseContractForm(), ...(res.data || row) }
    ensureSelectedPersonOption(form.value)
  } else {
    form.value = baseContractForm()
    personOptions.value = []
  }
  formOpen.value = true
}

function submitContract() {
  proxy.$refs.contractRef.validate(async valid => {
    if (!valid) return
    if (form.value.dispatchEnterpriseId === form.value.employerEnterpriseId) {
      ElMessage.error('派遣单位和用工单位不能相同')
      return
    }
    if (form.value.contractId) await updateContract(form.value)
    else await addContract(form.value)
    ElMessage.success('合同备案已保存')
    formOpen.value = false
    getList()
  })
}

async function batchRemind() {
  const res = await remindContractExpiry({ contractIds: ids.value, query: queryParams.value, portalScope: props.portal })
  ElMessage.success(`已记录 ${res.data?.handled || 0} 条提醒`)
  getList()
}

async function batchWarn() {
  const res = await warnContractUnfiled({ contractIds: ids.value, query: queryParams.value, portalScope: props.portal })
  ElMessage.success(`已生成 ${res.data?.handled || 0} 条预警，跳过 ${res.data?.skipped || 0} 条`)
  getList()
}

function handleExport() {
  if (mode.value === 'template') {
    proxy.download('ygb/contract/template/export', templateQuery.value, `contract_template_${Date.now()}.xlsx`)
  } else if (mode.value === 'expiry') {
    proxy.download('ygb/contract/expiry/export', queryParams.value, `contract_expiry_${Date.now()}.xlsx`)
  } else if (mode.value === 'unfiled') {
    proxy.download('ygb/contract/unfiled/export', queryParams.value, `contract_unfiled_${Date.now()}.xlsx`)
  } else {
    proxy.download('ygb/contract/export', queryParams.value, `contract_${Date.now()}.xlsx`)
  }
}

function copyEvidence(row) {
  navigator.clipboard?.writeText(row.blockchainHash)
  ElMessage.success('区块链存证哈希已复制')
}

function normalizeResourceFileUrl(fileUrl) {
  const value = String(fileUrl || '').trim()
  if (!value) return ''
  if (/^https?:\/\//i.test(value)) {
    try {
      const url = new URL(value)
      const profileIndex = url.pathname.indexOf('/profile/')
      return profileIndex >= 0 ? decodeURIComponent(url.pathname.slice(profileIndex)) : value
    } catch (error) {
      return value
    }
  }
  if (value.startsWith('/prod-api/profile/')) return value.substring('/prod-api'.length)
  if (value.startsWith('/dev-api/profile/')) return value.substring('/dev-api'.length)
  if (value.startsWith('profile/')) return `/${value}`
  if (value.startsWith('/upload/')) return `/profile${value}`
  if (value.startsWith('upload/')) return `/profile/${value}`
  return value
}

function openResourceFile(fileUrl) {
  const resource = normalizeResourceFileUrl(fileUrl)
  if (!resource) {
    ElMessage.warning('附件地址为空')
    return
  }
  if (/^https?:\/\/stub\//i.test(resource) || /^https?:\/\/[^/]*\.local\//i.test(resource)) {
    ElMessage.warning('当前为模拟附件地址，请上传真实合同文件后再打开')
    return
  }
  if (/^https?:\/\//i.test(resource)) {
    window.open(resource, '_blank', 'noopener,noreferrer')
    return
  }
  if (!resource.startsWith('/profile/')) {
    ElMessage.error('附件地址格式异常，请重新上传文件')
    return
  }
  proxy.$download.resource(resource)
}

async function openTemplateDetail(row) {
  const res = await getContractTemplate(row.templateId)
  templateDetail.value = res.data || row
  templateDetailOpen.value = true
}

async function openTemplateForm(row) {
  if (row?.templateId) {
    const res = await getContractTemplate(row.templateId)
    templateForm.value = { ...baseTemplateForm(), ...(res.data || row) }
  } else {
    templateForm.value = baseTemplateForm()
  }
  templateFormOpen.value = true
}

function submitTemplateForm() {
  proxy.$refs.templateRef.validate(async valid => {
    if (!valid) return
    if (templateForm.value.templateId) await updateContractTemplate(templateForm.value)
    else await addContractTemplate(templateForm.value)
    ElMessage.success('合同模板已保存')
    templateFormOpen.value = false
    getList()
  })
}

function canSubmitTemplate(row) {
  return row?.reviewStatus !== 'pending'
}

function canReviewTemplate(row) {
  return row?.reviewStatus === 'pending'
}

async function changeTemplateStatus(row, status) {
  if (status === '1' && row.reviewStatus !== 'approved') {
    ElMessage.warning('只有审核通过的模板才能启用')
    return
  }
  await changeContractTemplateStatus(row.templateId, { status })
  ElMessage.success(status === '1' ? '模板已启用' : '模板已停用')
  getList()
}

async function submitTemplate(row) {
  if (!canSubmitTemplate(row)) {
    ElMessage.warning('模板已处于待审核状态，无需重复提交')
    return
  }
  await submitContractTemplate(row.templateId, { portalScope: props.portal })
  ElMessage.success('模板已提交审核')
  getList()
}

function openReview(row) {
  if (!canReviewTemplate(row)) {
    ElMessage.warning('只有待审核模板才能审核')
    return
  }
  reviewingTemplate.value = row
  reviewForm.value = { reviewStatus: 'approved', reviewRemark: '' }
  reviewOpen.value = true
}

async function submitReview() {
  if (!canReviewTemplate(reviewingTemplate.value)) {
    ElMessage.warning('只有待审核模板才能审核')
    return
  }
  await reviewContractTemplate(reviewingTemplate.value.templateId, { ...reviewForm.value, portalScope: props.portal })
  ElMessage.success('模板审核已提交')
  reviewOpen.value = false
  getList()
}

function labelOf(options, value) {
  return options.find(item => String(item.value) === String(value))?.label || '-'
}

function contractStatusTone(value) {
  if (String(value) === '2') return 'success'
  if (['0', '1'].includes(String(value))) return 'warning'
  if (String(value) === '3') return 'danger'
  return 'info'
}

function reviewStatusTone(value) {
  if (value === 'approved') return 'success'
  if (value === 'rejected') return 'danger'
  return 'warning'
}

function expiryText(row) {
  if (row.daysToExpire === undefined || row.daysToExpire === null) return '-'
  if (row.daysToExpire < 0) return '已过期'
  return `${row.daysToExpire}天后到期`
}

function personLabel(item) {
  return [item.personName, item.idCard, item.enterpriseName].filter(Boolean).join(' / ')
}

function ensureSelectedPersonOption(contract) {
  if (!contract?.personId) return
  if (personOptions.value.some(item => String(item.personId) === String(contract.personId))) return
  personOptions.value = [{
    personId: contract.personId,
    personName: contract.personName,
    idCard: contract.idCard,
    enterpriseName: contract.dispatchEnterpriseName || contract.employerEnterpriseName
  }, ...personOptions.value]
}

async function searchPersons(keyword) {
  if (!keyword || keyword.trim().length < 1) {
    personOptions.value = form.value.personId ? personOptions.value.filter(item => String(item.personId) === String(form.value.personId)) : []
    return
  }
  personLoading.value = true
  try {
    const enterpriseId = form.value.dispatchEnterpriseId || form.value.employerEnterpriseId || undefined
    const res = await listPerson({ pageNum: 1, pageSize: 20, personName: keyword.trim(), enterpriseId })
    personOptions.value = res.rows || []
    ensureSelectedPersonOption(form.value)
  } finally {
    personLoading.value = false
  }
}

async function loadOptions() {
  const enterpriseRes = await optionselectEnterprise()
  enterpriseOptions.value = enterpriseRes.data || []
}

onMounted(() => {
  loadOptions()
  getList()
})
</script>

<style lang="scss" scoped>
.contract-filing-page {
  background: #f6f8fb;
}

.contract-hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 22px 24px;
  margin-bottom: 14px;
  border: 1px solid #d9e4f2;
  background: #fff;
}

.contract-hero__eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
}

.contract-hero h1 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #0f172a;
}

.contract-hero p {
  margin: 0;
  max-width: 820px;
  color: #526071;
  line-height: 1.7;
}

.contract-hero__facts {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  justify-content: flex-end;
  gap: 8px;
}

.contract-hero__facts span {
  padding: 7px 10px;
  border: 1px solid #d9e4f2;
  background: #f8fbff;
  color: #31506f;
  font-size: 12px;
}

.contract-filing-page--azb .contract-hero__eyebrow {
  color: #0e7490;
}

.contract-filing-page--azb .contract-hero__facts span {
  border-color: #cce7ea;
  background: #f4fbfb;
  color: #0f6571;
}

.contract-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.contract-summary__item {
  padding: 16px;
  border: 1px solid #dfe7f0;
  background: #fff;
}

.contract-summary__item span,
.contract-summary__item small {
  display: block;
  color: #64748b;
}

.contract-summary__item strong {
  display: block;
  margin: 7px 0;
  font-size: 26px;
  color: #0f172a;
}

.contract-summary__item.success strong {
  color: #15803d;
}

.contract-summary__item.warning strong {
  color: #b45309;
}

.contract-summary__item.danger strong {
  color: #b91c1c;
}

.contract-panel {
  margin-bottom: 14px;
  border-radius: 4px;
}

.contract-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.contract-toolbar span {
  color: #64748b;
  font-size: 13px;
}

.contract-cell-stack {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contract-cell-stack span {
  color: #64748b;
  font-size: 12px;
}

.template-status-tag {
  margin-left: 8px;
}

@media (max-width: 1100px) {
  .contract-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .contract-hero {
    flex-direction: column;
  }

  .contract-hero__facts {
    justify-content: flex-start;
  }
}
</style>
