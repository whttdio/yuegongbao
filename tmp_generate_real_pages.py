import os
from pathlib import Path

UI_DIR = Path(r'D:\JAVA\YueGongBao-Vue\YueGongBao-Vue\yuegongbao-ui\src\views\ygb')

BASE_TEMPLATE = """<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">{eyebrow}</p>
        <h1 class="ygb-page__title">{title}</h1>
        <p class="ygb-page__desc">{desc}</p>
      </div>
    </section>

{summary_section}

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
{search_form}
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
{toolbar}
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">{table_title}</div>
            <div class="ygb-card-head__desc">{table_desc}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{{{ total }}}} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
{table_columns}
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="120" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
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
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="{detail_title}" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
{detail_items}
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="{route_name}">
{imports}
import {{ parseTime }} from '@/utils/yuegongbao'
import {{ optionselectEnterprise }} from '@/api/ygb/enterprise'
import {{ ElMessage }} from 'element-plus'

function formatMoney(value) {{
  if (value === undefined || value === null || value === '') return '-'
  return '¥ ' + Number(value).toLocaleString('zh-CN', {{ minimumFractionDigits: 2, maximumFractionDigits: 2 }})
}}
function valueOrDefault(value, fallback = 0) {{
  return value === undefined || value === null ? fallback : value
}}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])

const queryParams = ref({{
  pageNum: 1,
  pageSize: 10,
{query_defaults}
}})

const summaryCards = ref([])

async function getList() {{
  loading.value = true
  try {{
    const res = await {list_call}(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  }} catch (e) {{
    console.error(e)
  }} finally {{
    loading.value = false
  }}
}}

{extra_script}

async function getSummary() {{
  try {{
    const res = await {summary_call}
    const data = res.data || res || {{}}
    summaryCards.value = [
{summary_builders}
    ].filter(Boolean)
  }} catch (e) {{
    console.error(e)
  }}
}}

function handleQuery() {{
  queryParams.value.pageNum = 1
  getList()
}}

function resetQuery() {{
  queryParams.value = {{
    pageNum: 1,
    pageSize: 10,
{query_defaults}
  }}
  getList()
}}

function handleSelectionChange(selection) {{
}}

function openDetail(row) {{
  detail.value = row
  detailOpen.value = true
}}

function handleExport() {{
  ElMessage.info('导出功能开发中')
}}

async function loadEnterpriseOptions() {{
  try {{
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  }} catch (e) {{}}
}}

onMounted(() => {{
  loadEnterpriseOptions()
  getList()
  getSummary()
}})
</script>
"""

SUMMARY_SECTION = """    <div class="ygb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>"""


def render_field_input(item):
    t = item.get('type', 'input')
    w = item.get('width', 200)
    prop = item['prop']
    label = item['label']
    if t == 'select':
        opts = '\n'.join([f"            <el-option label=\"{o['label']}\" value=\"{o['value']}\" />" for o in item.get('options', [])])
        return f"        <el-form-item label=\"{label}\" prop=\"{prop}\">\n          <el-select v-model=\"queryParams.{prop}\" placeholder=\"请选择{label}\" clearable style=\"width: {w}px\">\n{opts}\n          </el-select>\n        </el-form-item>"
    if t == 'enterprise':
        return f"        <el-form-item label=\"{label}\" prop=\"{prop}\">\n          <el-select v-model=\"queryParams.{prop}\" placeholder=\"请选择{label}\" clearable filterable style=\"width: {w}px\">\n            <el-option v-for=\"item in enterpriseOptions\" :key=\"item.enterpriseId\" :label=\"item.enterpriseName\" :value=\"item.enterpriseId\" />\n          </el-select>\n        </el-form-item>"
    if t == 'month':
        return f"        <el-form-item label=\"{label}\" prop=\"{prop}\">\n          <el-date-picker v-model=\"queryParams.{prop}\" type=\"month\" placeholder=\"请选择{label}\" format=\"YYYY-MM\" value-format=\"YYYY-MM\" style=\"width: {w}px\" />\n        </el-form-item>"
    return f"        <el-form-item label=\"{label}\" prop=\"{prop}\">\n          <el-input v-model=\"queryParams.{prop}\" placeholder=\"请输入{label}\" clearable style=\"width: {w}px\" @keyup.enter=\"handleQuery\" />\n        </el-form-item>"


def render_column(col):
    if col.get('type') == 'selection':
        return "        <el-table-column type=\"selection\" width=\"55\" align=\"center\" />"
    wa = col.get('width_attr', '')
    if col.get('dict'):
        opts = ', '.join([f"{{ label: '{o['label']}', value: '{o['value']}' }}" for o in col['dict']])
        return f"        <el-table-column label=\"{col['label']}\" align=\"center\" prop=\"{col['prop']}\" {wa}>\n          <template #default=\"scope\">\n            <dict-tag :options=\"[{opts}]\" :value=\"scope.row.{col['prop']}\" />\n          </template>\n        </el-table-column>"
    if col.get('format') == 'date':
        return f"        <el-table-column label=\"{col['label']}\" align=\"center\" {wa}>\n          <template #default=\"scope\">\n            <span>{{{{ parseTime(scope.row.{col['prop']}, '{{y}}-{{m}}-{{d}}') }}}}</span>\n          </template>\n        </el-table-column>"
    if col.get('format') == 'datetime':
        return f"        <el-table-column label=\"{col['label']}\" align=\"center\" {wa}>\n          <template #default=\"scope\">\n            <span>{{{{ parseTime(scope.row.{col['prop']}, '{{y}}-{{m}}-{{d}} {{h}}:{{i}}:{{s}}') }}}}</span>\n          </template>\n        </el-table-column>"
    if col.get('format') == 'money':
        return f"        <el-table-column label=\"{col['label']}\" align=\"center\" {wa}>\n          <template #default=\"scope\">\n            <span>{{{{ formatMoney(scope.row.{col['prop']}) }}}}</span>\n          </template>\n        </el-table-column>"
    return f"        <el-table-column label=\"{col['label']}\" align=\"center\" prop=\"{col['prop']}\" {wa} />"


def render_detail_item(item):
    span = item.get('span', 1)
    if item.get('format') == 'date':
        return f"          <el-descriptions-item label=\"{item['label']}\" :span=\"{span}\">{{{{ parseTime(detail.{item['prop']}, '{{y}}-{{m}}-{{d}}') || '-' }}}}</el-descriptions-item>"
    if item.get('format') == 'datetime':
        return f"          <el-descriptions-item label=\"{item['label']}\" :span=\"{span}\">{{{{ parseTime(detail.{item['prop']}, '{{y}}-{{m}}-{{d}} {{h}}:{{i}}:{{s}}') || '-' }}}}</el-descriptions-item>"
    if item.get('format') == 'money':
        return f"          <el-descriptions-item label=\"{item['label']}\" :span=\"{span}\">{{{{ formatMoney(detail.{item['prop']}) }}}}</el-descriptions-item>"
    return f"          <el-descriptions-item label=\"{item['label']}\" :span=\"{span}\">{{{{ detail.{item['prop']} || '-' }}}}</el-descriptions-item>"


def build_summary_card(card):
    key = card['key']
    expr = card.get('expr', f"data.{key}")
    return f"      {{ key: '{key}', label: '{card['label']}', value: valueOrDefault({expr}, 0), unit: '{card.get('unit', '')}', note: '{card.get('note', '')}', cardClass: '{card.get('cardClass', '')}' }}"


def build_query_defaults(fields):
    lines = []
    for f in fields:
        d = f.get('default')
        if d is None:
            lines.append(f"  {f['prop']}: undefined")
        elif isinstance(d, str):
            lines.append(f"  {f['prop']}: '{d}'")
        else:
            lines.append(f"  {f['prop']}: {d}")
    return ',\n'.join(lines)


def render_page(cfg):
    fields = cfg.get('search_fields', [])
    search_form = '\n'.join([render_field_input(f) for f in fields])
    toolbar = "        <el-col :span=\"1.5\">\n          <el-button type=\"warning\" plain icon=\"Download\" @click=\"handleExport\">导出</el-button>\n        </el-col>"
    if cfg.get('extra_toolbar'):
        toolbar = cfg['extra_toolbar'] + '\n' + toolbar
    table_cols = '\n'.join([render_column(c) for c in cfg.get('table_columns', [])])
    detail_items = '\n'.join([render_detail_item(i) for i in cfg.get('detail_items', [])])
    qd = build_query_defaults(fields)
    summary = ',\n'.join([build_summary_card(c) for c in cfg.get('summary_cards', [])]) if cfg.get('summary_cards') else ''
    imports = cfg.get('imports', [])
    if 'ref, computed, onMounted' not in ''.join(imports):
        imports.insert(0, "import { ref, onMounted } from 'vue'")

    return BASE_TEMPLATE.format(
        eyebrow=cfg['eyebrow'],
        title=cfg['title'],
        desc=cfg['desc'],
        summary_section=SUMMARY_SECTION if cfg.get('summary_cards') else '',
        search_form=search_form,
        toolbar=toolbar,
        table_title=cfg.get('table_title', cfg['title'] + '台账'),
        table_desc=cfg.get('table_desc', '按文档要求组织的台账数据'),
        table_columns=table_cols,
        detail_title=cfg.get('detail_title', cfg['title'] + '详情'),
        detail_items=detail_items,
        route_name=cfg['route_name'],
        imports='\n'.join(imports),
        query_defaults=qd,
        summary_builders=summary,
        list_call=cfg['list_call'],
        summary_call=cfg.get('summary_call', 'Promise.resolve({ data: {} })'),
        extra_script=cfg.get('extra_script', ''),
    )


ENTERPRISE_OPT = "            <el-option v-for=\"item in enterpriseOptions\" :key=\"item.enterpriseId\" :label=\"item.enterpriseName\" :value=\"item.enterpriseId\" />"

RISK_LEVEL_DICT = [{"label": "高", "value": "1"}, {"label": "中", "value": "2"}, {"label": "低", "value": "3"}]
WARN_STATUS_DICT = [{"label": "待处置", "value": "0"}, {"label": "处置中", "value": "1"}, {"label": "已办结", "value": "2"}]
WARN_LEVEL_DICT = [{"label": "红", "value": "1"}, {"label": "黄", "value": "2"}, {"label": "蓝", "value": "3"}]
CODE_COLOR_DICT = [{"label": "红码", "value": "red"}, {"label": "黄码", "value": "yellow"}, {"label": "绿码", "value": "green"}]
CONTRACT_STATUS_DICT = [{"label": "未备案", "value": "0"}, {"label": "备案中", "value": "1"}, {"label": "已备案", "value": "2"}]
ATTENDANCE_STATUS_DICT = [{"label": "正常", "value": "0"}, {"label": "异常", "value": "1"}, {"label": "缺卡", "value": "2"}]
BATCH_STATUS_DICT = [{"label": "草稿", "value": "0"}, {"label": "已提交", "value": "1"}, {"label": "发放中", "value": "2"}, {"label": "已完成", "value": "3"}]
ACCOUNT_STATUS_DICT = [{"label": "未到账", "value": "0"}, {"label": "部分到账", "value": "1"}, {"label": "已到账", "value": "2"}]
INJURY_STATUS_DICT = [{"label": "待认定", "value": "0"}, {"label": "认定中", "value": "1"}, {"label": "已认定", "value": "2"}, {"label": "不认定", "value": "3"}]
DEVICE_STATUS_DICT = [{"label": "在线", "value": "1"}, {"label": "离线", "value": "0"}]

PAGES = [
    {
        "path": "cockpit/map/index.vue",
        "route_name": "YgbCockpitMap",
        "eyebrow": "驾驶舱",
        "title": "地图可视化",
        "desc": "以地图方式展示企业分布、风险点位与预警分布，支持区域钻取。",
        "imports": ["import { getCockpitMap, getCockpitIndicators } from '@/api/ygb/cockpit'"],
        "list_call": "getCockpitMap",
        "summary_call": "getCockpitIndicators()",
        "search_fields": [{"label": "区域", "prop": "regionCode", "type": "input", "width": 160}, {"label": "企业", "prop": "enterpriseId", "type": "enterprise"}],
        "table_columns": [
            {"label": "区域", "prop": "regionName", "width_attr": "width=\"140\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "经度", "prop": "longitude", "width_attr": "width=\"120\""},
            {"label": "纬度", "prop": "latitude", "width_attr": "width=\"120\""},
            {"label": "风险等级", "prop": "riskLevel", "width_attr": "width=\"110\"", "dict": RISK_LEVEL_DICT},
            {"label": "预警数", "prop": "warningCount", "width_attr": "width=\"100\""},
        ],
        "detail_items": [{"label": "区域", "prop": "regionName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "经度", "prop": "longitude"}, {"label": "纬度", "prop": "latitude"}, {"label": "风险等级", "prop": "riskLevel"}, {"label": "预警数", "prop": "warningCount"}],
        "summary_cards": [
            {"key": "totalEnterprise", "label": "企业总数", "unit": "家", "note": "地图覆盖范围"},
            {"key": "warningEnterprise", "label": "预警企业", "unit": "家", "note": "存在风险信号", "cardClass": "warning"},
            {"key": "highRiskCount", "label": "高风险点位", "unit": "个", "note": "需重点关注", "cardClass": "danger"},
            {"key": "onlineRate", "label": "设备在线率", "unit": "%", "note": "实时均值"},
        ],
    },
    {
        "path": "cockpit/warningStream/index.vue",
        "route_name": "YgbCockpitWarningStream",
        "eyebrow": "驾驶舱",
        "title": "实时预警流",
        "desc": "滚动展示平台实时产生的预警工单，支持按级别、来源、状态快速筛选。",
        "imports": ["import { listWarning, getWarningSummary } from '@/api/ygb/warning'"],
        "list_call": "listWarning",
        "summary_call": "getWarningSummary()",
        "search_fields": [
            {"label": "预警级别", "prop": "warnLevel", "type": "select", "width": 140, "options": WARN_LEVEL_DICT},
            {"label": "来源模块", "prop": "sourceModule", "type": "select", "width": 150, "options": [{"label": "社保", "value": "social"}, {"label": "工资", "value": "salary"}, {"label": "税务", "value": "tax"}, {"label": "设备", "value": "device"}]},
            {"label": "工单状态", "prop": "warnStatus", "type": "select", "width": 150, "options": WARN_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "预警ID", "prop": "warnId", "width_attr": "width=\"90\""},
            {"label": "级别", "prop": "warnLevel", "width_attr": "width=\"90\"", "dict": WARN_LEVEL_DICT},
            {"label": "类型", "prop": "warnType", "width_attr": "min-width=\"160\""},
            {"label": "来源", "prop": "sourceModule", "width_attr": "width=\"110\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "内容", "prop": "content", "width_attr": "min-width=\"260\""},
            {"label": "状态", "prop": "warnStatus", "width_attr": "width=\"100\"", "dict": WARN_STATUS_DICT},
            {"label": "创建时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "预警ID", "prop": "warnId"}, {"label": "级别", "prop": "warnLevel"}, {"label": "类型", "prop": "warnType"}, {"label": "来源模块", "prop": "sourceModule"},
            {"label": "企业", "prop": "enterpriseName"}, {"label": "区域", "prop": "regionName"}, {"label": "状态", "prop": "warnStatus"}, {"label": "创建时间", "prop": "createTime", "format": "datetime"},
            {"label": "预警内容", "prop": "content", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalWarning", "label": "预警总数", "unit": "条", "note": "累计产生"},
            {"key": "pendingWarning", "label": "待处置", "unit": "条", "note": "需尽快闭环", "cardClass": "warning"},
            {"key": "highRiskWarning", "label": "红码预警", "unit": "条", "note": "高风险信号", "cardClass": "danger"},
            {"key": "resolvedWarning", "label": "已办结", "unit": "条", "note": "本周办结", "cardClass": "success"},
        ],
    },
    {
        "path": "cockpit/enterpriseCode/index.vue",
        "route_name": "YgbCockpitEnterpriseCode",
        "eyebrow": "驾驶舱",
        "title": "红黄绿码企业分类",
        "desc": "基于信用评分与预警数据对企业进行红黄绿码分类，直观展示风险分布。",
        "imports": ["import { listCreditScore, getCreditScoreSummary } from '@/api/ygb/creditScore'"],
        "list_call": "listCreditScore",
        "summary_call": "getCreditScoreSummary()",
        "search_fields": [
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "码色", "prop": "codeColor", "type": "select", "width": 140, "options": CODE_COLOR_DICT},
        ],
        "table_columns": [
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "信用分", "prop": "score", "width_attr": "width=\"110\""},
            {"label": "码色", "prop": "codeColor", "width_attr": "width=\"100\"", "dict": CODE_COLOR_DICT},
            {"label": "预警数", "prop": "warningCount", "width_attr": "width=\"100\""},
            {"label": "所属区域", "prop": "regionName", "width_attr": "width=\"150\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [{"label": "企业", "prop": "enterpriseName"}, {"label": "信用分", "prop": "score"}, {"label": "码色", "prop": "codeColor"}, {"label": "预警数", "prop": "warningCount"}, {"label": "所属区域", "prop": "regionName"}, {"label": "更新时间", "prop": "updateTime", "format": "datetime"}],
        "summary_cards": [
            {"key": "redCount", "label": "红码企业", "unit": "家", "note": "高风险", "cardClass": "danger"},
            {"key": "yellowCount", "label": "黄码企业", "unit": "家", "note": "中风险", "cardClass": "warning"},
            {"key": "greenCount", "label": "绿码企业", "unit": "家", "note": "低风险", "cardClass": "success"},
            {"key": "avgScore", "label": "平均信用分", "unit": "分", "note": "平台均值"},
        ],
    },
    {
        "path": "aiReport/ranking/index.vue",
        "route_name": "YgbAiReportRanking",
        "eyebrow": "AI监测报告",
        "title": "风险评分与排名",
        "desc": "展示 AI 监测报告的风险评分与企业/项目排名，辅助监管聚焦重点对象。",
        "imports": ["import { listAiReport, getAiReportDashboard } from '@/api/ygb/aiReport'"],
        "list_call": "listAiReport",
        "summary_call": "getAiReportDashboard()",
        "search_fields": [
            {"label": "报告名称", "prop": "reportName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "风险等级", "prop": "riskLevel", "type": "select", "width": 140, "options": RISK_LEVEL_DICT},
        ],
        "table_columns": [
            {"label": "报告ID", "prop": "reportId", "width_attr": "width=\"90\""},
            {"label": "报告名称", "prop": "reportName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "风险分", "prop": "riskScore", "width_attr": "width=\"100\""},
            {"label": "风险等级", "prop": "riskLevel", "width_attr": "width=\"110\"", "dict": RISK_LEVEL_DICT},
            {"label": "排名", "prop": "rankNo", "width_attr": "width=\"80\""},
            {"label": "生成时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "报告ID", "prop": "reportId"}, {"label": "报告名称", "prop": "reportName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "风险分", "prop": "riskScore"},
            {"label": "风险等级", "prop": "riskLevel"}, {"label": "排名", "prop": "rankNo"}, {"label": "生成时间", "prop": "createTime", "format": "datetime"}, {"label": "监测建议", "prop": "suggestion", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalReport", "label": "报告总数", "unit": "份", "note": "累计生成"},
            {"key": "highRiskCount", "label": "高风险报告", "unit": "份", "note": "需重点关注", "cardClass": "danger"},
            {"key": "avgRiskScore", "label": "平均风险分", "unit": "分", "note": "平台均值"},
            {"key": "improvedCount", "label": "改善企业", "unit": "家", "note": "评分上升", "cardClass": "success"},
        ],
    },
    {
        "path": "aiReport/filter/index.vue",
        "route_name": "YgbAiReportFilter",
        "eyebrow": "AI监测报告",
        "title": "多维筛选与穿透",
        "desc": "支持按区域、行业、指标维度对 AI 监测报告进行多维筛选与下钻穿透。",
        "imports": ["import { listAiReport } from '@/api/ygb/aiReport'"],
        "list_call": "listAiReport",
        "search_fields": [
            {"label": "报告名称", "prop": "reportName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "维度", "prop": "dimension", "type": "select", "width": 140, "options": [{"label": "区域", "value": "region"}, {"label": "行业", "value": "industry"}, {"label": "指标", "value": "indicator"}]},
            {"label": "风险等级", "prop": "riskLevel", "type": "select", "width": 140, "options": RISK_LEVEL_DICT},
        ],
        "table_columns": [
            {"label": "报告ID", "prop": "reportId", "width_attr": "width=\"90\""},
            {"label": "报告名称", "prop": "reportName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "维度", "prop": "dimension", "width_attr": "width=\"120\""},
            {"label": "风险分", "prop": "riskScore", "width_attr": "width=\"100\""},
            {"label": "风险等级", "prop": "riskLevel", "width_attr": "width=\"110\"", "dict": RISK_LEVEL_DICT},
            {"label": "生成时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "报告ID", "prop": "reportId"}, {"label": "报告名称", "prop": "reportName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "维度", "prop": "dimension"},
            {"label": "风险分", "prop": "riskScore"}, {"label": "风险等级", "prop": "riskLevel"}, {"label": "生成时间", "prop": "createTime", "format": "datetime"}, {"label": "详细指标", "prop": "indicatorJson", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalReport", "label": "报告总数", "unit": "份", "note": "当前筛选范围"},
            {"key": "dimensionCount", "label": "维度数", "unit": "个", "note": "已覆盖维度"},
            {"key": "highRiskCount", "label": "高风险", "unit": "份", "note": "需下钻", "cardClass": "danger"},
            {"key": "drillCount", "label": "已穿透", "unit": "份", "note": "生成任务", "cardClass": "primary"},
        ],
    },
    {
        "path": "contract/expiry/index.vue",
        "route_name": "YgbContractExpiry",
        "eyebrow": "合同备案",
        "title": "合同到期提醒",
        "desc": "集中展示即将到期或已到期合同，便于提前发起续签与备案更新。",
        "imports": ["import { listContract, getContractSummary } from '@/api/ygb/contract'"],
        "list_call": "listContract",
        "summary_call": "getContractSummary()",
        "search_fields": [
            {"label": "合同编号", "prop": "contractNo", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "到期天数", "prop": "expiryDays", "type": "select", "width": 150, "options": [{"label": "7天内", "value": "7"}, {"label": "30天内", "value": "30"}, {"label": "已过期", "value": "-1"}]},
        ],
        "table_columns": [
            {"label": "合同ID", "prop": "contractId", "width_attr": "width=\"80\""},
            {"label": "合同编号", "prop": "contractNo", "width_attr": "width=\"180\""},
            {"label": "劳动者", "prop": "personName", "width_attr": "width=\"120\""},
            {"label": "派遣单位", "prop": "dispatchEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "用工单位", "prop": "employerEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "合同类型", "prop": "contractType", "width_attr": "width=\"110\""},
            {"label": "到期日期", "prop": "endDate", "width_attr": "width=\"120\"", "format": "date"},
            {"label": "到期状态", "prop": "expiryStatus", "width_attr": "width=\"100\"", "dict": [{"label": "已过期", "value": "expired"}, {"label": "即将到期", "value": "soon"}]},
        ],
        "detail_items": [
            {"label": "合同编号", "prop": "contractNo"}, {"label": "劳动者", "prop": "personName"}, {"label": "派遣单位", "prop": "dispatchEnterpriseName"}, {"label": "用工单位", "prop": "employerEnterpriseName"},
            {"label": "合同类型", "prop": "contractType"}, {"label": "到期日期", "prop": "endDate", "format": "date"}, {"label": "月工资标准", "prop": "monthlyWage", "format": "money"}, {"label": "备注", "prop": "remark", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalContract", "label": "合同总数", "unit": "份", "note": "当前范围"},
            {"key": "expiredSoon", "label": "7天内到期", "unit": "份", "note": "需续签", "cardClass": "warning"},
            {"key": "alreadyExpired", "label": "已过期", "unit": "份", "note": "需处理", "cardClass": "danger"},
            {"key": "renewedCount", "label": "已续签", "unit": "份", "note": "本月", "cardClass": "success"},
        ],
    },
    {
        "path": "contract/unfiled/index.vue",
        "route_name": "YgbContractUnfiled",
        "eyebrow": "合同备案",
        "title": "未备案合同预警",
        "desc": "识别并预警尚未完成备案的合同，督促企业尽快补充备案信息。",
        "imports": ["import { listContract, getContractSummary } from '@/api/ygb/contract'"],
        "list_call": "listContract",
        "summary_call": "getContractSummary()",
        "search_fields": [
            {"label": "合同编号", "prop": "contractNo", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "备案状态", "prop": "contractStatus", "type": "select", "width": 150, "options": CONTRACT_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "合同ID", "prop": "contractId", "width_attr": "width=\"80\""},
            {"label": "合同编号", "prop": "contractNo", "width_attr": "width=\"180\""},
            {"label": "劳动者", "prop": "personName", "width_attr": "width=\"120\""},
            {"label": "派遣单位", "prop": "dispatchEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "用工单位", "prop": "employerEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "合同类型", "prop": "contractType", "width_attr": "width=\"110\""},
            {"label": "备案状态", "prop": "contractStatus", "width_attr": "width=\"100\"", "dict": CONTRACT_STATUS_DICT},
            {"label": "创建时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "合同编号", "prop": "contractNo"}, {"label": "劳动者", "prop": "personName"}, {"label": "派遣单位", "prop": "dispatchEnterpriseName"}, {"label": "用工单位", "prop": "employerEnterpriseName"},
            {"label": "备案状态", "prop": "contractStatus"}, {"label": "创建时间", "prop": "createTime", "format": "datetime"}, {"label": "月工资标准", "prop": "monthlyWage", "format": "money"}, {"label": "备注", "prop": "remark", "span": 2},
        ],
        "summary_cards": [
            {"key": "unfiledCount", "label": "未备案", "unit": "份", "note": "需督促", "cardClass": "danger"},
            {"key": "filingCount", "label": "备案中", "unit": "份", "note": "正在办理", "cardClass": "warning"},
            {"key": "filedCount", "label": "已备案", "unit": "份", "note": "合规完成", "cardClass": "success"},
            {"key": "totalContract", "label": "合同总数", "unit": "份", "note": "当前范围"},
        ],
    },
    {
        "path": "attendance/overview/index.vue",
        "route_name": "YgbAttendanceOverview",
        "eyebrow": "用工考勤",
        "title": "考勤总览",
        "desc": "汇总展示考勤上报情况、出勤率、异常率等核心指标，支持按企业与月份下钻。",
        "imports": ["import { listAttendanceRaw, getAttendanceRawOverview } from '@/api/ygb/attendanceRaw'"],
        "list_call": "listAttendanceRaw",
        "summary_call": "getAttendanceRawOverview()",
        "search_fields": [
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "考勤状态", "prop": "attendanceStatus", "type": "select", "width": 140, "options": ATTENDANCE_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "ID", "prop": "attendanceId", "width_attr": "width=\"80\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "人员", "prop": "personName", "width_attr": "width=\"120\""},
            {"label": "考勤日期", "prop": "attendanceDate", "width_attr": "width=\"120\"", "format": "date"},
            {"label": "出勤状态", "prop": "attendanceStatus", "width_attr": "width=\"100\"", "dict": ATTENDANCE_STATUS_DICT},
            {"label": "打卡次数", "prop": "checkCount", "width_attr": "width=\"100\""},
            {"label": "工作时长", "prop": "workHours", "width_attr": "width=\"110\""},
        ],
        "detail_items": [
            {"label": "企业", "prop": "enterpriseName"}, {"label": "人员", "prop": "personName"}, {"label": "考勤日期", "prop": "attendanceDate", "format": "date"},
            {"label": "出勤状态", "prop": "attendanceStatus"}, {"label": "打卡次数", "prop": "checkCount"}, {"label": "工作时长", "prop": "workHours"}, {"label": "创建时间", "prop": "createTime", "format": "datetime"},
        ],
        "summary_cards": [
            {"key": "totalRecord", "label": "考勤记录", "unit": "条", "note": "当前范围"},
            {"key": "attendanceRate", "label": "出勤率", "unit": "%", "note": "统计月份"},
            {"key": "abnormalRate", "label": "异常率", "unit": "%", "note": "需关注", "cardClass": "warning"},
            {"key": "deviceOnlineRate", "label": "设备在线率", "unit": "%", "note": "实时均值"},
        ],
    },
    {
        "path": "attendance/abnormal/index.vue",
        "route_name": "YgbAttendanceAbnormal",
        "eyebrow": "用工考勤",
        "title": "异常考勤统计",
        "desc": "统计并展示异常考勤记录，支持按异常类型、企业、月份筛选。",
        "imports": ["import { listAttendanceRaw, getAttendanceRawSummary } from '@/api/ygb/attendanceRaw'"],
        "list_call": "listAttendanceRaw",
        "summary_call": "getAttendanceRawSummary()",
        "search_fields": [
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "异常类型", "prop": "abnormalType", "type": "select", "width": 150, "options": [{"label": "迟到", "value": "late"}, {"label": "早退", "value": "early"}, {"label": "缺卡", "value": "miss"}, {"label": "旷工", "value": "absent"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "attendanceId", "width_attr": "width=\"80\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "人员", "prop": "personName", "width_attr": "width=\"120\""},
            {"label": "考勤日期", "prop": "attendanceDate", "width_attr": "width=\"120\"", "format": "date"},
            {"label": "异常类型", "prop": "abnormalType", "width_attr": "width=\"110\"", "dict": [{"label": "迟到", "value": "late"}, {"label": "早退", "value": "early"}, {"label": "缺卡", "value": "miss"}, {"label": "旷工", "value": "absent"}]},
            {"label": "异常说明", "prop": "abnormalRemark", "width_attr": "min-width=\"200\""},
            {"label": "处理状态", "prop": "handleStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未处理", "value": "0"}, {"label": "已处理", "value": "1"}]},
        ],
        "detail_items": [
            {"label": "企业", "prop": "enterpriseName"}, {"label": "人员", "prop": "personName"}, {"label": "考勤日期", "prop": "attendanceDate", "format": "date"},
            {"label": "异常类型", "prop": "abnormalType"}, {"label": "异常说明", "prop": "abnormalRemark"}, {"label": "处理状态", "prop": "handleStatus"}, {"label": "创建时间", "prop": "createTime", "format": "datetime"},
        ],
        "summary_cards": [
            {"key": "abnormalCount", "label": "异常次数", "unit": "次", "note": "当前范围", "cardClass": "warning"},
            {"key": "unhandledCount", "label": "未处理", "unit": "次", "note": "需跟进", "cardClass": "danger"},
            {"key": "lateCount", "label": "迟到", "unit": "次", "note": "本月累计"},
            {"key": "absentCount", "label": "旷工", "unit": "次", "note": "本月累计", "cardClass": "danger"},
        ],
    },
    {
        "path": "attendance/deviceOnline/index.vue",
        "route_name": "YgbAttendanceDeviceOnline",
        "eyebrow": "用工考勤",
        "title": "设备在线率报表",
        "desc": "展示考勤相关设备的在线状态、在线率统计与离线预警，支撑考勤数据质量治理。",
        "imports": ["import { listDevice, getDeviceSummary } from '@/api/ygb/device'"],
        "list_call": "listDevice",
        "summary_call": "getDeviceSummary()",
        "search_fields": [
            {"label": "设备编号", "prop": "deviceNo", "type": "input", "width": 180},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "在线状态", "prop": "onlineStatus", "type": "select", "width": 140, "options": DEVICE_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "设备ID", "prop": "deviceId", "width_attr": "width=\"80\""},
            {"label": "设备编号", "prop": "deviceNo", "width_attr": "width=\"160\""},
            {"label": "设备名称", "prop": "deviceName", "width_attr": "min-width=\"180\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "设备类型", "prop": "deviceType", "width_attr": "width=\"120\""},
            {"label": "在线状态", "prop": "onlineStatus", "width_attr": "width=\"100\"", "dict": DEVICE_STATUS_DICT},
            {"label": "最近心跳", "prop": "lastHeartbeatTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "设备编号", "prop": "deviceNo"}, {"label": "设备名称", "prop": "deviceName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "设备类型", "prop": "deviceType"},
            {"label": "在线状态", "prop": "onlineStatus"}, {"label": "最近心跳", "prop": "lastHeartbeatTime", "format": "datetime"}, {"label": "安装位置", "prop": "installLocation", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalDevice", "label": "设备总数", "unit": "台", "note": "考勤相关"},
            {"key": "onlineCount", "label": "在线设备", "unit": "台", "note": "实时在线", "cardClass": "success"},
            {"key": "offlineCount", "label": "离线设备", "unit": "台", "note": "需排查", "cardClass": "danger"},
            {"key": "onlineRate", "label": "在线率", "unit": "%", "note": "实时均值"},
        ],
    },
    {
        "path": "salary/paymentMonitor/index.vue",
        "route_name": "YgbSalaryPaymentMonitor",
        "eyebrow": "工资监管",
        "title": "工资发放监控",
        "desc": "监控工资批次从创建、到账确认到银行代发的全流程状态，确保工资按时足额发放。",
        "imports": ["import { listSalaryBatch, getSalaryBatchSummary } from '@/api/ygb/salaryBatch'"],
        "list_call": "listSalaryBatch",
        "summary_call": "getSalaryBatchSummary()",
        "search_fields": [
            {"label": "批次编号", "prop": "batchNo", "type": "input", "width": 200},
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "批次状态", "prop": "batchStatus", "type": "select", "width": 140, "options": BATCH_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "ID", "prop": "batchId", "width_attr": "width=\"80\""},
            {"label": "批次编号", "prop": "batchNo", "width_attr": "width=\"180\""},
            {"label": "统计月份", "prop": "statMonth", "width_attr": "width=\"100\""},
            {"label": "派遣单位", "prop": "dispatchEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "人数", "prop": "totalPersonCount", "width_attr": "width=\"80\""},
            {"label": "应发合计", "prop": "totalPayableAmount", "width_attr": "width=\"120\"", "format": "money"},
            {"label": "实发合计", "prop": "totalPaidAmount", "width_attr": "width=\"120\"", "format": "money"},
            {"label": "批次状态", "prop": "batchStatus", "width_attr": "width=\"110\"", "dict": BATCH_STATUS_DICT},
        ],
        "detail_items": [
            {"label": "批次编号", "prop": "batchNo"}, {"label": "统计月份", "prop": "statMonth"}, {"label": "派遣单位", "prop": "dispatchEnterpriseName"}, {"label": "人数", "prop": "totalPersonCount"},
            {"label": "应发合计", "prop": "totalPayableAmount", "format": "money"}, {"label": "实发合计", "prop": "totalPaidAmount", "format": "money"}, {"label": "批次状态", "prop": "batchStatus"}, {"label": "发放时间", "prop": "paidTime", "format": "datetime"},
        ],
        "summary_cards": [
            {"key": "totalBatch", "label": "批次总数", "unit": "笔", "note": "当前范围"},
            {"key": "totalPayable", "label": "应发总额", "unit": "元", "note": "累计", "format": "money", "expr": "data.totalPayableAmount"},
            {"key": "totalPaid", "label": "实发总额", "unit": "元", "note": "累计", "format": "money", "expr": "data.totalPaidAmount"},
            {"key": "pendingBatch", "label": "待发放", "unit": "笔", "note": "需推进", "cardClass": "warning"},
        ],
    },
    {
        "path": "salary/accountMonitor/index.vue",
        "route_name": "YgbSalaryAccountMonitor",
        "eyebrow": "工资监管",
        "title": "监管账户监控",
        "desc": "监控工资监管专用账户的到账情况，识别未到账、部分到账等异常状态。",
        "imports": ["import { listSalaryBatch, getSalaryBatchSummary } from '@/api/ygb/salaryBatch'"],
        "list_call": "listSalaryBatch",
        "summary_call": "getSalaryBatchSummary()",
        "search_fields": [
            {"label": "批次编号", "prop": "batchNo", "type": "input", "width": 200},
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "到账状态", "prop": "accountStatus", "type": "select", "width": 140, "options": ACCOUNT_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "ID", "prop": "batchId", "width_attr": "width=\"80\""},
            {"label": "批次编号", "prop": "batchNo", "width_attr": "width=\"180\""},
            {"label": "统计月份", "prop": "statMonth", "width_attr": "width=\"100\""},
            {"label": "派遣单位", "prop": "dispatchEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "监管账户名", "prop": "regulatorAccountName", "width_attr": "min-width=\"160\""},
            {"label": "到账金额", "prop": "accountReceivedAmount", "width_attr": "width=\"120\"", "format": "money"},
            {"label": "到账状态", "prop": "accountStatus", "width_attr": "width=\"100\"", "dict": ACCOUNT_STATUS_DICT},
            {"label": "银行流水号", "prop": "bankSerialNo", "width_attr": "width=\"180\""},
        ],
        "detail_items": [
            {"label": "批次编号", "prop": "batchNo"}, {"label": "统计月份", "prop": "statMonth"}, {"label": "派遣单位", "prop": "dispatchEnterpriseName"}, {"label": "监管账户名", "prop": "regulatorAccountName"},
            {"label": "监管账号", "prop": "regulatorAccountNo"}, {"label": "到账金额", "prop": "accountReceivedAmount", "format": "money"}, {"label": "到账状态", "prop": "accountStatus"}, {"label": "银行流水号", "prop": "bankSerialNo"},
        ],
        "summary_cards": [
            {"key": "totalBatch", "label": "批次总数", "unit": "笔", "note": "当前范围"},
            {"key": "receivedCount", "label": "已到账", "unit": "笔", "note": "合规", "cardClass": "success"},
            {"key": "partialCount", "label": "部分到账", "unit": "笔", "note": "需核对", "cardClass": "warning"},
            {"key": "unreceivedCount", "label": "未到账", "unit": "笔", "note": "需催缴", "cardClass": "danger"},
        ],
    },
    {
        "path": "salary/overdueWarning/index.vue",
        "route_name": "YgbSalaryOverdueWarning",
        "eyebrow": "工资监管",
        "title": "拖欠工资预警",
        "desc": "识别存在拖欠工资风险的批次与企业，支持处置跟踪与闭环管理。",
        "imports": ["import { listSalaryArrears, getSalaryArrears } from '@/api/ygb/salaryArrears'"],
        "list_call": "listSalaryArrears",
        "summary_call": "Promise.resolve({ data: {} })",
        "search_fields": [
            {"label": "批次编号", "prop": "batchNo", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "处置状态", "prop": "handleStatus", "type": "select", "width": 140, "options": [{"label": "未处置", "value": "0"}, {"label": "处置中", "value": "1"}, {"label": "已处置", "value": "2"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "arrearsId", "width_attr": "width=\"80\""},
            {"label": "批次编号", "prop": "batchNo", "width_attr": "width=\"180\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "拖欠金额", "prop": "arrearsAmount", "width_attr": "width=\"120\"", "format": "money"},
            {"label": "拖欠人数", "prop": "arrearsPersonCount", "width_attr": "width=\"100\""},
            {"label": "处置状态", "prop": "handleStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未处置", "value": "0"}, {"label": "处置中", "value": "1"}, {"label": "已处置", "value": "2"}]},
            {"label": "发现时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "批次编号", "prop": "batchNo"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "拖欠金额", "prop": "arrearsAmount", "format": "money"}, {"label": "拖欠人数", "prop": "arrearsPersonCount"},
            {"label": "处置状态", "prop": "handleStatus"}, {"label": "发现时间", "prop": "createTime", "format": "datetime"}, {"label": "处置说明", "prop": "handleRemark", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalArrears", "label": "拖欠笔数", "unit": "笔", "note": "当前范围", "cardClass": "danger"},
            {"key": "totalArrearsAmount", "label": "拖欠总额", "unit": "元", "note": "累计", "format": "money", "expr": "data.totalArrearsAmount"},
            {"key": "unhandledCount", "label": "未处置", "unit": "笔", "note": "需跟进", "cardClass": "warning"},
            {"key": "handledCount", "label": "已处置", "unit": "笔", "note": "闭环", "cardClass": "success"},
        ],
    },
    {
        "path": "salary/bankDistribution/index.vue",
        "route_name": "YgbSalaryBankDistribution",
        "eyebrow": "工资监管",
        "title": "银行代发结果监控",
        "desc": "监控工资银行代发结果与回调状态，跟踪每笔代发交易的最终到账结果。",
        "imports": ["import { listSalaryBatch, getSalaryBatchSummary } from '@/api/ygb/salaryBatch'"],
        "list_call": "listSalaryBatch",
        "summary_call": "getSalaryBatchSummary()",
        "search_fields": [
            {"label": "批次编号", "prop": "batchNo", "type": "input", "width": 200},
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "代发状态", "prop": "distributionStatus", "type": "select", "width": 140, "options": [{"label": "未代发", "value": "0"}, {"label": "代发中", "value": "1"}, {"label": "成功", "value": "2"}, {"label": "失败", "value": "3"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "batchId", "width_attr": "width=\"80\""},
            {"label": "批次编号", "prop": "batchNo", "width_attr": "width=\"180\""},
            {"label": "统计月份", "prop": "statMonth", "width_attr": "width=\"100\""},
            {"label": "派遣单位", "prop": "dispatchEnterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "实发合计", "prop": "totalPaidAmount", "width_attr": "width=\"120\"", "format": "money"},
            {"label": "银行流水号", "prop": "bankSerialNo", "width_attr": "width=\"180\""},
            {"label": "代发状态", "prop": "distributionStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未代发", "value": "0"}, {"label": "代发中", "value": "1"}, {"label": "成功", "value": "2"}, {"label": "失败", "value": "3"}]},
            {"label": "发放时间", "prop": "paidTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "批次编号", "prop": "batchNo"}, {"label": "统计月份", "prop": "statMonth"}, {"label": "派遣单位", "prop": "dispatchEnterpriseName"}, {"label": "实发合计", "prop": "totalPaidAmount", "format": "money"},
            {"label": "银行流水号", "prop": "bankSerialNo"}, {"label": "代发状态", "prop": "distributionStatus"}, {"label": "发放时间", "prop": "paidTime", "format": "datetime"}, {"label": "回调结果", "prop": "callbackResult"},
        ],
        "summary_cards": [
            {"key": "totalBatch", "label": "代发批次", "unit": "笔", "note": "当前范围"},
            {"key": "successCount", "label": "成功", "unit": "笔", "note": "已到账", "cardClass": "success"},
            {"key": "failCount", "label": "失败", "unit": "笔", "note": "需处理", "cardClass": "danger"},
            {"key": "pendingCount", "label": "处理中", "unit": "笔", "note": "等待回调", "cardClass": "warning"},
        ],
    },
    {
        "path": "injury/recognitionAssist/index.vue",
        "route_name": "YgbInjuryRecognitionAssist",
        "eyebrow": "工伤监管",
        "title": "工伤认定辅助",
        "desc": "为工伤认定提供证据归集、相似案例推荐与认定结论辅助参考。",
        "imports": ["import { listRecognitionAssist, getRecognitionAssist } from '@/api/ygb/injuryEvent'"],
        "list_call": "listRecognitionAssist",
        "summary_call": "Promise.resolve({ data: {} })",
        "search_fields": [
            {"label": "事件编号", "prop": "eventNo", "type": "input", "width": 180},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "认定状态", "prop": "recognitionStatus", "type": "select", "width": 140, "options": INJURY_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "ID", "prop": "eventId", "width_attr": "width=\"80\""},
            {"label": "事件编号", "prop": "eventNo", "width_attr": "width=\"160\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "受伤人员", "prop": "injuredPersonName", "width_attr": "width=\"120\""},
            {"label": "受伤时间", "prop": "injuryTime", "width_attr": "width=\"170\"", "format": "datetime"},
            {"label": "认定状态", "prop": "recognitionStatus", "width_attr": "width=\"100\"", "dict": INJURY_STATUS_DICT},
            {"label": "相似案例数", "prop": "similarCaseCount", "width_attr": "width=\"100\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "事件编号", "prop": "eventNo"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "受伤人员", "prop": "injuredPersonName"}, {"label": "受伤时间", "prop": "injuryTime", "format": "datetime"},
            {"label": "受伤地点", "prop": "injuryLocation"}, {"label": "认定状态", "prop": "recognitionStatus"}, {"label": "相似案例数", "prop": "similarCaseCount"}, {"label": "辅助结论", "prop": "assistConclusion"},
            {"label": "证据摘要", "prop": "evidenceSummary", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalEvent", "label": "认定事件", "unit": "件", "note": "当前范围"},
            {"key": "pendingCount", "label": "待认定", "unit": "件", "note": "需补充材料", "cardClass": "warning"},
            {"key": "recognizedCount", "label": "已认定", "unit": "件", "note": "结论明确", "cardClass": "success"},
            {"key": "rejectedCount", "label": "不认定", "unit": "件", "note": "结论明确", "cardClass": "info"},
        ],
    },
    {
        "path": "injury/statisticalAnalysis/index.vue",
        "route_name": "YgbInjuryStatisticalAnalysis",
        "eyebrow": "工伤监管",
        "title": "工伤统计分析",
        "desc": "从行业、区域、企业、时间等维度对工伤事件进行统计分析，发现高发规律。",
        "imports": ["import { listInjuryEvent, getInjuryEventAnalysis, getInjuryEventSummary } from '@/api/ygb/injuryEvent'"],
        "list_call": "listInjuryEvent",
        "summary_call": "getInjuryEventSummary()",
        "search_fields": [
            {"label": "事件编号", "prop": "eventNo", "type": "input", "width": 180},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "统计维度", "prop": "dimension", "type": "select", "width": 140, "options": [{"label": "行业", "value": "industry"}, {"label": "区域", "value": "region"}, {"label": "月份", "value": "month"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "eventId", "width_attr": "width=\"80\""},
            {"label": "事件编号", "prop": "eventNo", "width_attr": "width=\"160\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "受伤人员", "prop": "injuredPersonName", "width_attr": "width=\"120\""},
            {"label": "受伤时间", "prop": "injuryTime", "width_attr": "width=\"170\"", "format": "datetime"},
            {"label": "事故类型", "prop": "accidentType", "width_attr": "width=\"120\""},
            {"label": "伤害程度", "prop": "injurySeverity", "width_attr": "width=\"110\""},
        ],
        "detail_items": [
            {"label": "事件编号", "prop": "eventNo"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "受伤人员", "prop": "injuredPersonName"}, {"label": "受伤时间", "prop": "injuryTime", "format": "datetime"},
            {"label": "事故类型", "prop": "accidentType"}, {"label": "伤害程度", "prop": "injurySeverity"}, {"label": "受伤地点", "prop": "injuryLocation"}, {"label": "处理状态", "prop": "status"},
            {"label": "事件描述", "prop": "eventDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalEvent", "label": "工伤事件", "unit": "件", "note": "累计"},
            {"key": "fatalCount", "label": "死亡事故", "unit": "件", "note": "重点关注", "cardClass": "danger"},
            {"key": "seriousCount", "label": "重伤事故", "unit": "件", "note": "需预防", "cardClass": "warning"},
            {"key": "yoyChange", "label": "同比变化", "unit": "%", "note": "较去年同期"},
        ],
    },
    {
        "path": "aqInsurance/preventionService/index.vue",
        "route_name": "YgbAqInsurancePreventionService",
        "eyebrow": "安责险管理",
        "title": "事故预防服务",
        "desc": "展示安责险事故预防服务项目与服务记录，跟踪服务落地情况。",
        "imports": ["import { listAqInsurance, getAqInsuranceSummary, listPreventionProject, getPreventionProjectSummary } from '@/api/ygb/aqInsurance'\nimport { listPreventionProject as listPreventionProject2, getPreventionProjectSummary as getPreventionProjectSummary2 } from '@/api/ygb/preventionProject'"],
        "list_call": "listPreventionProject2",
        "summary_call": "getPreventionProjectSummary2()",
        "search_fields": [
            {"label": "项目名称", "prop": "projectName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "服务状态", "prop": "serviceStatus", "type": "select", "width": 140, "options": [{"label": "未开始", "value": "0"}, {"label": "进行中", "value": "1"}, {"label": "已完成", "value": "2"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "projectId", "width_attr": "width=\"80\""},
            {"label": "项目名称", "prop": "projectName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "服务类型", "prop": "serviceType", "width_attr": "width=\"120\""},
            {"label": "服务状态", "prop": "serviceStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未开始", "value": "0"}, {"label": "进行中", "value": "1"}, {"label": "已完成", "value": "2"}]},
            {"label": "计划时间", "prop": "planDate", "width_attr": "width=\"120\"", "format": "date"},
            {"label": "完成时间", "prop": "finishDate", "width_attr": "width=\"120\"", "format": "date"},
        ],
        "detail_items": [
            {"label": "项目名称", "prop": "projectName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "服务类型", "prop": "serviceType"}, {"label": "服务状态", "prop": "serviceStatus"},
            {"label": "计划时间", "prop": "planDate", "format": "date"}, {"label": "完成时间", "prop": "finishDate", "format": "date"}, {"label": "服务内容", "prop": "serviceContent", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalProject", "label": "项目总数", "unit": "个", "note": "当前范围"},
            {"key": "ongoingCount", "label": "进行中", "unit": "个", "note": "需跟踪", "cardClass": "warning"},
            {"key": "finishedCount", "label": "已完成", "unit": "个", "note": "服务落地", "cardClass": "success"},
            {"key": "coverageRate", "label": "覆盖率", "unit": "%", "note": "投保企业"},
        ],
    },
    {
        "path": "device/sunlightScreen/index.vue",
        "route_name": "YgbDeviceSunlightScreen",
        "eyebrow": "设备管理",
        "title": "阳光劳务屏管理",
        "desc": "管理阳光劳务屏设备台账，监控设备运行状态与展示内容。",
        "imports": ["import { listDevice, getDeviceSummary } from '@/api/ygb/device'"],
        "list_call": "listDevice",
        "summary_call": "getDeviceSummary()",
        "search_fields": [
            {"label": "设备编号", "prop": "deviceNo", "type": "input", "width": 180},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "在线状态", "prop": "onlineStatus", "type": "select", "width": 140, "options": DEVICE_STATUS_DICT},
        ],
        "table_columns": [
            {"label": "ID", "prop": "deviceId", "width_attr": "width=\"80\""},
            {"label": "设备编号", "prop": "deviceNo", "width_attr": "width=\"160\""},
            {"label": "设备名称", "prop": "deviceName", "width_attr": "min-width=\"180\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "安装位置", "prop": "installLocation", "width_attr": "min-width=\"160\""},
            {"label": "在线状态", "prop": "onlineStatus", "width_attr": "width=\"100\"", "dict": DEVICE_STATUS_DICT},
            {"label": "最近心跳", "prop": "lastHeartbeatTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "设备编号", "prop": "deviceNo"}, {"label": "设备名称", "prop": "deviceName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "安装位置", "prop": "installLocation"},
            {"label": "在线状态", "prop": "onlineStatus"}, {"label": "最近心跳", "prop": "lastHeartbeatTime", "format": "datetime"}, {"label": "展示内容", "prop": "displayContent", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalDevice", "label": "设备总数", "unit": "台", "note": "阳光劳务屏"},
            {"key": "onlineCount", "label": "在线", "unit": "台", "note": "正常运行", "cardClass": "success"},
            {"key": "offlineCount", "label": "离线", "unit": "台", "note": "需运维", "cardClass": "danger"},
            {"key": "onlineRate", "label": "在线率", "unit": "%", "note": "实时均值"},
        ],
    },
    {
        "path": "device/ledger/index.vue",
        "route_name": "YgbDeviceLedger",
        "eyebrow": "设备管理",
        "title": "设备台账",
        "desc": "集中管理各类监管设备的台账信息，支持按设备类型与状态筛选。",
        "imports": ["import { listDevice, getDeviceSummary } from '@/api/ygb/device'"],
        "list_call": "listDevice",
        "summary_call": "getDeviceSummary()",
        "search_fields": [
            {"label": "设备编号", "prop": "deviceNo", "type": "input", "width": 180},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "设备类型", "prop": "deviceType", "type": "select", "width": 150, "options": [{"label": "考勤机", "value": "attendance"}, {"label": "阳光屏", "value": "sunlight"}, {"label": "AI摄像头", "value": "ai"}, {"label": "芯片", "value": "chip"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "deviceId", "width_attr": "width=\"80\""},
            {"label": "设备编号", "prop": "deviceNo", "width_attr": "width=\"160\""},
            {"label": "设备名称", "prop": "deviceName", "width_attr": "min-width=\"180\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "设备类型", "prop": "deviceType", "width_attr": "width=\"120\""},
            {"label": "在线状态", "prop": "onlineStatus", "width_attr": "width=\"100\"", "dict": DEVICE_STATUS_DICT},
            {"label": "安装时间", "prop": "installTime", "width_attr": "width=\"120\"", "format": "date"},
        ],
        "detail_items": [
            {"label": "设备编号", "prop": "deviceNo"}, {"label": "设备名称", "prop": "deviceName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "设备类型", "prop": "deviceType"},
            {"label": "在线状态", "prop": "onlineStatus"}, {"label": "安装时间", "prop": "installTime", "format": "date"}, {"label": "安装位置", "prop": "installLocation", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalDevice", "label": "设备总数", "unit": "台", "note": "当前范围"},
            {"key": "attendanceCount", "label": "考勤设备", "unit": "台", "note": "人脸/指纹"},
            {"key": "onlineCount", "label": "在线", "unit": "台", "note": "实时", "cardClass": "success"},
            {"key": "offlineCount", "label": "离线", "unit": "台", "note": "需运维", "cardClass": "danger"},
        ],
    },
    {
        "path": "device/batchOperation/index.vue",
        "route_name": "YgbDeviceBatchOperation",
        "eyebrow": "设备管理",
        "title": "批量操作",
        "desc": "对设备执行批量锁定、解锁、授权等操作，提升运维效率。",
        "imports": ["import { listDevice, batchLockDevice, batchUnlockDevice, batchAuthorizeDevice } from '@/api/ygb/device'", "import { ElMessageBox } from 'element-plus'"],
        "list_call": "listDevice",
        "summary_call": "Promise.resolve({ data: {} })",
        "extra_script": "async function batchLock() {\n  if (!ids.value.length) return ElMessage.warning('请选择设备')\n  await ElMessageBox.confirm('确认锁定已选设备？', '提示', { type: 'warning' })\n  await batchLockDevice(ids.value)\n  ElMessage.success('批量锁定成功')\n  getList()\n}\nasync function batchUnlock() {\n  if (!ids.value.length) return ElMessage.warning('请选择设备')\n  await ElMessageBox.confirm('确认解锁已选设备？', '提示', { type: 'warning' })\n  await batchUnlockDevice(ids.value)\n  ElMessage.success('批量解锁成功')\n  getList()\n}\nasync function batchAuthorize() {\n  if (!ids.value.length) return ElMessage.warning('请选择设备')\n  await batchAuthorizeDevice({ deviceIds: ids.value })\n  ElMessage.success('批量授权成功')\n  getList()\n}",
        "search_fields": [
            {"label": "设备编号", "prop": "deviceNo", "type": "input", "width": 180},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "设备类型", "prop": "deviceType", "type": "select", "width": 150, "options": [{"label": "考勤机", "value": "attendance"}, {"label": "阳光屏", "value": "sunlight"}, {"label": "AI摄像头", "value": "ai"}, {"label": "芯片", "value": "chip"}]},
        ],
        "table_columns": [
            {"label": "选择", "type": "selection", "width_attr": "width=\"55\""},
            {"label": "ID", "prop": "deviceId", "width_attr": "width=\"80\""},
            {"label": "设备编号", "prop": "deviceNo", "width_attr": "width=\"160\""},
            {"label": "设备名称", "prop": "deviceName", "width_attr": "min-width=\"180\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "设备类型", "prop": "deviceType", "width_attr": "width=\"120\""},
            {"label": "在线状态", "prop": "onlineStatus", "width_attr": "width=\"100\"", "dict": DEVICE_STATUS_DICT},
        ],
        "detail_items": [
            {"label": "设备编号", "prop": "deviceNo"}, {"label": "设备名称", "prop": "deviceName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "设备类型", "prop": "deviceType"},
            {"label": "在线状态", "prop": "onlineStatus"}, {"label": "安装位置", "prop": "installLocation", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalDevice", "label": "设备总数", "unit": "台", "note": "当前范围"},
            {"key": "selectedCount", "label": "已选设备", "unit": "台", "note": "待批量操作", "cardClass": "primary"},
        ],
        "extra_toolbar": "        <el-col :span=\"1.5\">\n          <el-button type=\"warning\" plain icon=\"Lock\" @click=\"batchLock\" v-hasPermi=\"['ygb:device:lock']\">批量锁定</el-button>\n        </el-col>\n        <el-col :span=\"1.5\">\n          <el-button type=\"success\" plain icon=\"Unlock\" @click=\"batchUnlock\" v-hasPermi=\"['ygb:device:unlock']\">批量解锁</el-button>\n        </el-col>\n        <el-col :span=\"1.5\">\n          <el-button type=\"primary\" plain icon=\"CircleCheck\" @click=\"batchAuthorize\" v-hasPermi=\"['ygb:device:authorize']\">批量授权</el-button>\n        </el-col>",
    },
    {
        "path": "expansion/collectionTracking/index.vue",
        "route_name": "YgbExpansionCollectionTracking",
        "eyebrow": "扩面减损",
        "title": "催缴跟踪",
        "desc": "对未参保或欠费企业进行催缴跟踪，记录催缴过程与结果。",
        "imports": ["import { listUninsuredList, getUninsuredListSummary } from '@/api/ygb/uninsuredList'"],
        "list_call": "listUninsuredList",
        "summary_call": "getUninsuredListSummary()",
        "search_fields": [
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "催缴状态", "prop": "collectionStatus", "type": "select", "width": 140, "options": [{"label": "未催缴", "value": "0"}, {"label": "催缴中", "value": "1"}, {"label": "已参保", "value": "2"}, {"label": "催缴失败", "value": "3"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "listId", "width_attr": "width=\"80\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "漏保人员数", "prop": "uninsuredCount", "width_attr": "width=\"110\""},
            {"label": "应缴金额", "prop": "payableAmount", "width_attr": "width=\"120\"", "format": "money"},
            {"label": "催缴状态", "prop": "collectionStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未催缴", "value": "0"}, {"label": "催缴中", "value": "1"}, {"label": "已参保", "value": "2"}, {"label": "催缴失败", "value": "3"}]},
            {"label": "最后催缴时间", "prop": "lastCollectionTime", "width_attr": "width=\"170\"", "format": "datetime"},
            {"label": "责任人", "prop": "handlerName", "width_attr": "width=\"110\""},
        ],
        "detail_items": [
            {"label": "企业", "prop": "enterpriseName"}, {"label": "漏保人员数", "prop": "uninsuredCount"}, {"label": "应缴金额", "prop": "payableAmount", "format": "money"}, {"label": "催缴状态", "prop": "collectionStatus"},
            {"label": "最后催缴时间", "prop": "lastCollectionTime", "format": "datetime"}, {"label": "责任人", "prop": "handlerName"}, {"label": "催缴记录", "prop": "collectionLog", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalList", "label": "催缴任务", "unit": "条", "note": "当前范围"},
            {"key": "uninsuredTotal", "label": "漏保人数", "unit": "人", "note": "累计", "cardClass": "danger"},
            {"key": "collectionPending", "label": "未催缴", "unit": "条", "note": "需启动", "cardClass": "warning"},
            {"key": "insuredCount", "label": "已参保", "unit": "条", "note": "催缴成功", "cardClass": "success"},
        ],
    },
    {
        "path": "expansion/preventionTraining/index.vue",
        "route_name": "YgbExpansionPreventionTraining",
        "eyebrow": "扩面减损",
        "title": "工伤预防培训管理",
        "desc": "管理工伤预防培训项目，跟踪培训计划、实施与覆盖企业情况。",
        "imports": ["import { listPreventionProject, getPreventionProjectSummary } from '@/api/ygb/preventionProject'"],
        "list_call": "listPreventionProject",
        "summary_call": "getPreventionProjectSummary()",
        "search_fields": [
            {"label": "项目名称", "prop": "projectName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "项目状态", "prop": "projectStatus", "type": "select", "width": 140, "options": [{"label": "未开始", "value": "0"}, {"label": "进行中", "value": "1"}, {"label": "已完成", "value": "2"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "projectId", "width_attr": "width=\"80\""},
            {"label": "项目名称", "prop": "projectName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "培训类型", "prop": "trainingType", "width_attr": "width=\"120\""},
            {"label": "项目状态", "prop": "projectStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未开始", "value": "0"}, {"label": "进行中", "value": "1"}, {"label": "已完成", "value": "2"}]},
            {"label": "计划人数", "prop": "planPersonCount", "width_attr": "width=\"100\""},
            {"label": "完成人数", "prop": "finishPersonCount", "width_attr": "width=\"100\""},
        ],
        "detail_items": [
            {"label": "项目名称", "prop": "projectName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "培训类型", "prop": "trainingType"}, {"label": "项目状态", "prop": "projectStatus"},
            {"label": "计划人数", "prop": "planPersonCount"}, {"label": "完成人数", "prop": "finishPersonCount"}, {"label": "培训内容", "prop": "trainingContent", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalProject", "label": "项目总数", "unit": "个", "note": "当前范围"},
            {"key": "planPersonTotal", "label": "计划人数", "unit": "人", "note": "累计"},
            {"key": "finishPersonTotal", "label": "完成人数", "unit": "人", "note": "累计", "cardClass": "success"},
            {"key": "completionRate", "label": "完成率", "unit": "%", "note": "培训覆盖"},
        ],
    },
    {
        "path": "expansion/courseHours/index.vue",
        "route_name": "YgbExpansionCourseHours",
        "eyebrow": "扩面减损",
        "title": "培训课程与学时管理",
        "desc": "管理培训课程资源与学员学时记录，支撑培训效果追踪。",
        "imports": ["import { listNewformTraining, getNewformTrainingSummary } from '@/api/ygb/newformWorker'"],
        "list_call": "listNewformTraining",
        "summary_call": "getNewformTrainingSummary()",
        "search_fields": [
            {"label": "课程名称", "prop": "courseName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "课程状态", "prop": "courseStatus", "type": "select", "width": 140, "options": [{"label": "未开始", "value": "0"}, {"label": "进行中", "value": "1"}, {"label": "已结束", "value": "2"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "trainingId", "width_attr": "width=\"80\""},
            {"label": "课程名称", "prop": "courseName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "培训时长", "prop": "durationHours", "width_attr": "width=\"100\""},
            {"label": "参训人数", "prop": "traineeCount", "width_attr": "width=\"100\""},
            {"label": "课程状态", "prop": "courseStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未开始", "value": "0"}, {"label": "进行中", "value": "1"}, {"label": "已结束", "value": "2"}]},
            {"label": "培训时间", "prop": "trainingTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "课程名称", "prop": "courseName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "培训时长", "prop": "durationHours"}, {"label": "参训人数", "prop": "traineeCount"},
            {"label": "课程状态", "prop": "courseStatus"}, {"label": "培训时间", "prop": "trainingTime", "format": "datetime"}, {"label": "课程内容", "prop": "courseContent", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalCourse", "label": "课程总数", "unit": "门", "note": "当前范围"},
            {"key": "totalHours", "label": "累计学时", "unit": "小时", "note": "培训时长"},
            {"key": "totalTrainee", "label": "参训人数", "unit": "人", "note": "累计"},
            {"key": "finishedCourse", "label": "已结束", "unit": "门", "note": "可评估", "cardClass": "success"},
        ],
    },
    {
        "path": "expansion/trainingEvaluation/index.vue",
        "route_name": "YgbExpansionTrainingEvaluation",
        "eyebrow": "扩面减损",
        "title": "培训效果评估",
        "desc": "对已完成的培训课程进行效果评估，收集反馈并分析培训成效。",
        "imports": ["import { listNewformTraining, getNewformTraining } from '@/api/ygb/newformWorker'"],
        "list_call": "listNewformTraining",
        "summary_call": "Promise.resolve({ data: {} })",
        "search_fields": [
            {"label": "课程名称", "prop": "courseName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "评估状态", "prop": "evaluationStatus", "type": "select", "width": 140, "options": [{"label": "未评估", "value": "0"}, {"label": "已评估", "value": "1"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "trainingId", "width_attr": "width=\"80\""},
            {"label": "课程名称", "prop": "courseName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "参训人数", "prop": "traineeCount", "width_attr": "width=\"100\""},
            {"label": "满意度", "prop": "satisfactionScore", "width_attr": "width=\"100\""},
            {"label": "评估状态", "prop": "evaluationStatus", "width_attr": "width=\"100\"", "dict": [{"label": "未评估", "value": "0"}, {"label": "已评估", "value": "1"}]},
            {"label": "培训时间", "prop": "trainingTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "课程名称", "prop": "courseName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "参训人数", "prop": "traineeCount"}, {"label": "满意度", "prop": "satisfactionScore"},
            {"label": "评估状态", "prop": "evaluationStatus"}, {"label": "培训时间", "prop": "trainingTime", "format": "datetime"}, {"label": "评估意见", "prop": "evaluationComment", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalCourse", "label": "可评估课程", "unit": "门", "note": "当前范围"},
            {"key": "evaluatedCount", "label": "已评估", "unit": "门", "note": "完成评估", "cardClass": "success"},
            {"key": "avgSatisfaction", "label": "平均满意度", "unit": "分", "note": "满分10分"},
            {"key": "unevaluatedCount", "label": "未评估", "unit": "门", "note": "需跟进", "cardClass": "warning"},
        ],
    },
    {
        "path": "credit/overview/index.vue",
        "route_name": "YgbCreditOverview",
        "eyebrow": "信用评价",
        "title": "信用总览",
        "desc": "总览企业信用评分分布、等级变化趋势与关键信用指标。",
        "imports": ["import { listCreditScore, getCreditScoreSummary } from '@/api/ygb/creditScore'"],
        "list_call": "listCreditScore",
        "summary_call": "getCreditScoreSummary()",
        "search_fields": [
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "信用等级", "prop": "creditLevel", "type": "select", "width": 140, "options": [{"label": "AAA", "value": "AAA"}, {"label": "AA", "value": "AA"}, {"label": "A", "value": "A"}, {"label": "B", "value": "B"}, {"label": "C", "value": "C"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "scoreId", "width_attr": "width=\"80\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "信用分", "prop": "score", "width_attr": "width=\"110\""},
            {"label": "信用等级", "prop": "creditLevel", "width_attr": "width=\"100\""},
            {"label": "排名", "prop": "rankNo", "width_attr": "width=\"80\""},
            {"label": "所属区域", "prop": "regionName", "width_attr": "width=\"150\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "企业", "prop": "enterpriseName"}, {"label": "信用分", "prop": "score"}, {"label": "信用等级", "prop": "creditLevel"}, {"label": "排名", "prop": "rankNo"},
            {"label": "所属区域", "prop": "regionName"}, {"label": "更新时间", "prop": "updateTime", "format": "datetime"}, {"label": "评分说明", "prop": "scoreDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalEnterprise", "label": "参评企业", "unit": "家", "note": "当前范围"},
            {"key": "avgScore", "label": "平均信用分", "unit": "分", "note": "平台均值"},
            {"key": "aaaCount", "label": "AAA级", "unit": "家", "note": "优秀", "cardClass": "success"},
            {"key": "cCount", "label": "C级", "unit": "家", "note": "重点关注", "cardClass": "danger"},
        ],
    },
    {
        "path": "credit/enterpriseArchive/index.vue",
        "route_name": "YgbCreditEnterpriseArchive",
        "eyebrow": "信用评价",
        "title": "企业信用档案",
        "desc": "展示单个企业的完整信用档案，包括评分历史、奖惩记录与修复记录。",
        "imports": ["import { listCreditScore, getEnterpriseCreditScore } from '@/api/ygb/creditScore'", "import { getEnterprise } from '@/api/ygb/enterprise'"],
        "list_call": "listCreditScore",
        "summary_call": "Promise.resolve({ data: {} })",
        "search_fields": [
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
        ],
        "table_columns": [
            {"label": "ID", "prop": "scoreId", "width_attr": "width=\"80\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "信用分", "prop": "score", "width_attr": "width=\"110\""},
            {"label": "信用等级", "prop": "creditLevel", "width_attr": "width=\"100\""},
            {"label": "评分周期", "prop": "scorePeriod", "width_attr": "width=\"120\""},
            {"label": "加减分项", "prop": "scoreChange", "width_attr": "width=\"110\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "企业", "prop": "enterpriseName"}, {"label": "统一社会信用代码", "prop": "creditCode"}, {"label": "信用分", "prop": "score"}, {"label": "信用等级", "prop": "creditLevel"},
            {"label": "评分周期", "prop": "scorePeriod"}, {"label": "更新时间", "prop": "updateTime", "format": "datetime"}, {"label": "档案摘要", "prop": "archiveSummary", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalRecord", "label": "档案记录", "unit": "条", "note": "当前企业"},
            {"key": "currentScore", "label": "当前信用分", "unit": "分", "note": "最新"},
            {"key": "rewardCount", "label": "奖励记录", "unit": "条", "note": "加分项", "cardClass": "success"},
            {"key": "punishCount", "label": "惩戒记录", "unit": "条", "note": "减分项", "cardClass": "danger"},
        ],
    },
    {
        "path": "credit/reportExport/index.vue",
        "route_name": "YgbCreditReportExport",
        "eyebrow": "信用评价",
        "title": "信用报告导出",
        "desc": "生成并导出企业信用评价报告，支持批量选择与自定义报告模板。",
        "imports": ["import { listCreditScore, generateCreditScore } from '@/api/ygb/creditScore'"],
        "list_call": "listCreditScore",
        "summary_call": "Promise.resolve({ data: {} })",
        "extra_script": "async function generateReport() {\n  if (!ids.value.length) return ElMessage.warning('请选择企业')\n  await generateCreditScore({ scoreIds: ids.value })\n  ElMessage.success('报告生成任务已提交')\n}",
        "search_fields": [
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "信用等级", "prop": "creditLevel", "type": "select", "width": 140, "options": [{"label": "AAA", "value": "AAA"}, {"label": "AA", "value": "AA"}, {"label": "A", "value": "A"}, {"label": "B", "value": "B"}, {"label": "C", "value": "C"}]},
        ],
        "table_columns": [
            {"label": "选择", "type": "selection", "width_attr": "width=\"55\""},
            {"label": "ID", "prop": "scoreId", "width_attr": "width=\"80\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "信用分", "prop": "score", "width_attr": "width=\"110\""},
            {"label": "信用等级", "prop": "creditLevel", "width_attr": "width=\"100\""},
            {"label": "所属区域", "prop": "regionName", "width_attr": "width=\"150\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "企业", "prop": "enterpriseName"}, {"label": "信用分", "prop": "score"}, {"label": "信用等级", "prop": "creditLevel"}, {"label": "所属区域", "prop": "regionName"},
            {"label": "更新时间", "prop": "updateTime", "format": "datetime"}, {"label": "报告内容", "prop": "reportContent", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalEnterprise", "label": "可选企业", "unit": "家", "note": "当前范围"},
            {"key": "exportedCount", "label": "已导出", "unit": "份", "note": "本月", "cardClass": "success"},
        ],
        "extra_toolbar": "        <el-col :span=\"1.5\">\n          <el-button type=\"primary\" plain icon=\"Document\" @click=\"generateReport\" v-hasPermi=\"['ygb:creditScore:generate']\">生成报告</el-button>\n        </el-col>",
    },
    {
        "path": "statReport/employment/index.vue",
        "route_name": "YgbStatReportEmployment",
        "eyebrow": "统计报表",
        "title": "用工报表",
        "desc": "按区域、行业、企业等维度统计用工情况，生成用工分析报表。",
        "imports": ["import { listStatReport, getStatReportSummary } from '@/api/ygb/statReport'"],
        "list_call": "listStatReport",
        "summary_call": "getStatReportSummary()",
        "search_fields": [
            {"label": "报表名称", "prop": "reportName", "type": "input", "width": 200},
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
        ],
        "table_columns": [
            {"label": "ID", "prop": "reportId", "width_attr": "width=\"80\""},
            {"label": "报表名称", "prop": "reportName", "width_attr": "min-width=\"200\""},
            {"label": "统计月份", "prop": "statMonth", "width_attr": "width=\"100\""},
            {"label": "用工人数", "prop": "employmentCount", "width_attr": "width=\"110\""},
            {"label": "派遣人数", "prop": "dispatchCount", "width_attr": "width=\"110\""},
            {"label": "新增人数", "prop": "newCount", "width_attr": "width=\"100\""},
            {"label": "生成时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "报表名称", "prop": "reportName"}, {"label": "统计月份", "prop": "statMonth"}, {"label": "用工人数", "prop": "employmentCount"}, {"label": "派遣人数", "prop": "dispatchCount"},
            {"label": "新增人数", "prop": "newCount"}, {"label": "生成时间", "prop": "createTime", "format": "datetime"}, {"label": "报表说明", "prop": "reportDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalReport", "label": "报表数", "unit": "份", "note": "当前范围"},
            {"key": "totalEmployment", "label": "用工总数", "unit": "人", "note": "累计"},
            {"key": "totalDispatch", "label": "派遣总数", "unit": "人", "note": "累计"},
            {"key": "monthNew", "label": "本月新增", "unit": "人", "note": "较上月", "cardClass": "primary"},
        ],
    },
    {
        "path": "statReport/attendance/index.vue",
        "route_name": "YgbStatReportAttendance",
        "eyebrow": "统计报表",
        "title": "考勤报表",
        "desc": "汇总考勤上报率、异常率、出勤率等指标，生成考勤统计报表。",
        "imports": ["import { listStatReport, getStatReportSummary } from '@/api/ygb/statReport'"],
        "list_call": "listStatReport",
        "summary_call": "getStatReportSummary()",
        "search_fields": [
            {"label": "报表名称", "prop": "reportName", "type": "input", "width": 200},
            {"label": "统计月份", "prop": "statMonth", "type": "month", "width": 160},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
        ],
        "table_columns": [
            {"label": "ID", "prop": "reportId", "width_attr": "width=\"80\""},
            {"label": "报表名称", "prop": "reportName", "width_attr": "min-width=\"200\""},
            {"label": "统计月份", "prop": "statMonth", "width_attr": "width=\"100\""},
            {"label": "应考勤人次", "prop": "expectedCount", "width_attr": "width=\"120\""},
            {"label": "实际考勤人次", "prop": "actualCount", "width_attr": "width=\"120\""},
            {"label": "异常人次", "prop": "abnormalCount", "width_attr": "width=\"100\""},
            {"label": "生成时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "报表名称", "prop": "reportName"}, {"label": "统计月份", "prop": "statMonth"}, {"label": "应考勤人次", "prop": "expectedCount"}, {"label": "实际考勤人次", "prop": "actualCount"},
            {"label": "异常人次", "prop": "abnormalCount"}, {"label": "生成时间", "prop": "createTime", "format": "datetime"}, {"label": "报表说明", "prop": "reportDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalReport", "label": "报表数", "unit": "份", "note": "当前范围"},
            {"key": "attendanceRate", "label": "考勤率", "unit": "%", "note": "均值"},
            {"key": "abnormalRate", "label": "异常率", "unit": "%", "note": "需关注", "cardClass": "warning"},
            {"key": "totalAttendance", "label": "考勤人次", "unit": "次", "note": "累计"},
        ],
    },
    {
        "path": "statReport/rectification/index.vue",
        "route_name": "YgbStatReportRectification",
        "eyebrow": "统计报表",
        "title": "专项整治报表",
        "desc": "统计专项整治行动的发现问题、整改情况与验收结果。",
        "imports": ["import { listStatReport, getStatReportSummary } from '@/api/ygb/statReport'"],
        "list_call": "listStatReport",
        "summary_call": "getStatReportSummary()",
        "search_fields": [
            {"label": "报表名称", "prop": "reportName", "type": "input", "width": 200},
            {"label": "整治主题", "prop": "theme", "type": "select", "width": 160, "options": [{"label": "假外包", "value": "fake_outsourcing"}, {"label": "三性岗位", "value": "three_nature"}, {"label": "用工比例", "value": "employment_ratio"}]},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
        ],
        "table_columns": [
            {"label": "ID", "prop": "reportId", "width_attr": "width=\"80\""},
            {"label": "报表名称", "prop": "reportName", "width_attr": "min-width=\"200\""},
            {"label": "整治主题", "prop": "theme", "width_attr": "width=\"120\""},
            {"label": "发现问题", "prop": "problemCount", "width_attr": "width=\"100\""},
            {"label": "已整改", "prop": "rectifiedCount", "width_attr": "width=\"100\""},
            {"label": "整改率", "prop": "rectificationRate", "width_attr": "width=\"100\""},
            {"label": "生成时间", "prop": "createTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "报表名称", "prop": "reportName"}, {"label": "整治主题", "prop": "theme"}, {"label": "发现问题", "prop": "problemCount"}, {"label": "已整改", "prop": "rectifiedCount"},
            {"label": "整改率", "prop": "rectificationRate"}, {"label": "生成时间", "prop": "createTime", "format": "datetime"}, {"label": "整治说明", "prop": "reportDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalReport", "label": "报表数", "unit": "份", "note": "当前范围"},
            {"key": "totalProblem", "label": "发现问题", "unit": "个", "note": "累计", "cardClass": "warning"},
            {"key": "totalRectified", "label": "已整改", "unit": "个", "note": "累计", "cardClass": "success"},
            {"key": "avgRectificationRate", "label": "平均整改率", "unit": "%", "note": "主题均值"},
        ],
    },
    {
        "path": "person/newform/index.vue",
        "route_name": "YgbPersonNewform",
        "eyebrow": "人员管理",
        "title": "新业态人员库",
        "desc": "管理网约车、外卖、快递等新业态从业人员档案与监测数据。",
        "imports": ["import { listNewformWorker, getNewformWorkerSummary } from '@/api/ygb/newformWorker'"],
        "list_call": "listNewformWorker",
        "summary_call": "getNewformWorkerSummary()",
        "search_fields": [
            {"label": "姓名", "prop": "personName", "type": "input", "width": 150},
            {"label": "手机号", "prop": "mobile", "type": "input", "width": 160},
            {"label": "所属平台", "prop": "platformId", "type": "select", "width": 160, "options": [{"label": "平台A", "value": "1"}, {"label": "平台B", "value": "2"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "workerId", "width_attr": "width=\"80\""},
            {"label": "姓名", "prop": "personName", "width_attr": "width=\"120\""},
            {"label": "手机号", "prop": "mobile", "width_attr": "width=\"140\""},
            {"label": "所属平台", "prop": "platformName", "width_attr": "min-width=\"160\""},
            {"label": "岗位类型", "prop": "jobType", "width_attr": "width=\"120\""},
            {"label": "参保状态", "prop": "insuranceStatus", "width_attr": "width=\"100\"", "dict": [{"label": "已参保", "value": "1"}, {"label": "未参保", "value": "0"}]},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "姓名", "prop": "personName"}, {"label": "手机号", "prop": "mobile"}, {"label": "所属平台", "prop": "platformName"}, {"label": "岗位类型", "prop": "jobType"},
            {"label": "参保状态", "prop": "insuranceStatus"}, {"label": "更新时间", "prop": "updateTime", "format": "datetime"}, {"label": "工作记录", "prop": "workRecord", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalWorker", "label": "人员总数", "unit": "人", "note": "当前范围"},
            {"key": "insuredCount", "label": "已参保", "unit": "人", "note": "合规", "cardClass": "success"},
            {"key": "uninsuredCount", "label": "未参保", "unit": "人", "note": "需扩面", "cardClass": "danger"},
            {"key": "platformCount", "label": "覆盖平台", "unit": "家", "note": "接入数"},
        ],
    },
    {
        "path": "enterprise/relation/index.vue",
        "route_name": "YgbEnterpriseRelation",
        "eyebrow": "单位管理",
        "title": "派遣/用工关联关系",
        "desc": "维护劳务派遣单位与用工单位之间的派遣关联关系，支撑合规监管。",
        "imports": ["import { listEnterpriseSubmodule, getEnterpriseSubmoduleSummary } from '@/api/ygb/enterprise'"],
        "list_call": "fetchList",
        "summary_call": "fetchSummary()",
        "extra_script": "async function fetchList(query) {\n  return listEnterpriseSubmodule('relation', query)\n}\nasync function fetchSummary() {\n  return getEnterpriseSubmoduleSummary('relation')\n}",
        "search_fields": [
            {"label": "派遣单位", "prop": "dispatchEnterpriseId", "type": "enterprise"},
            {"label": "用工单位", "prop": "employerEnterpriseId", "type": "enterprise"},
            {"label": "关联状态", "prop": "relationStatus", "type": "select", "width": 140, "options": [{"label": "有效", "value": "1"}, {"label": "失效", "value": "0"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "relationId", "width_attr": "width=\"80\""},
            {"label": "派遣单位", "prop": "dispatchEnterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "用工单位", "prop": "employerEnterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "派遣人数", "prop": "dispatchPersonCount", "width_attr": "width=\"100\""},
            {"label": "关联状态", "prop": "relationStatus", "width_attr": "width=\"100\"", "dict": [{"label": "有效", "value": "1"}, {"label": "失效", "value": "0"}]},
            {"label": "生效日期", "prop": "effectiveDate", "width_attr": "width=\"120\"", "format": "date"},
        ],
        "detail_items": [
            {"label": "派遣单位", "prop": "dispatchEnterpriseName"}, {"label": "用工单位", "prop": "employerEnterpriseName"}, {"label": "派遣人数", "prop": "dispatchPersonCount"}, {"label": "关联状态", "prop": "relationStatus"},
            {"label": "生效日期", "prop": "effectiveDate", "format": "date"}, {"label": "备注", "prop": "remark", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalRelation", "label": "关联总数", "unit": "条", "note": "当前范围"},
            {"key": "activeCount", "label": "有效关联", "unit": "条", "note": "正常", "cardClass": "success"},
            {"key": "inactiveCount", "label": "失效关联", "unit": "条", "note": "需清理", "cardClass": "warning"},
            {"key": "totalDispatchPerson", "label": "派遣人数", "unit": "人", "note": "累计"},
        ],
    },
    {
        "path": "enterprise/newform/index.vue",
        "route_name": "YgbEnterpriseNewform",
        "eyebrow": "单位管理",
        "title": "新业态平台企业管理",
        "desc": "管理网约车、外卖、快递等新业态平台企业信息，支撑新业态监管。",
        "imports": ["import { listEnterpriseSubmodule, getEnterpriseSubmoduleSummary } from '@/api/ygb/enterprise'", "import { listNewformPlatform, getNewformPlatformSummary } from '@/api/ygb/newformWorker'"],
        "list_call": "listNewformPlatform",
        "summary_call": "getNewformPlatformSummary()",
        "search_fields": [
            {"label": "平台名称", "prop": "platformName", "type": "input", "width": 200},
            {"label": "企业", "prop": "enterpriseId", "type": "enterprise"},
            {"label": "平台类型", "prop": "platformType", "type": "select", "width": 140, "options": [{"label": "网约车", "value": "taxi"}, {"label": "外卖", "value": "food"}, {"label": "快递", "value": "express"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "platformId", "width_attr": "width=\"80\""},
            {"label": "平台名称", "prop": "platformName", "width_attr": "min-width=\"200\""},
            {"label": "企业", "prop": "enterpriseName", "width_attr": "min-width=\"200\""},
            {"label": "平台类型", "prop": "platformType", "width_attr": "width=\"120\""},
            {"label": "注册人员", "prop": "registerCount", "width_attr": "width=\"100\""},
            {"label": "活跃人员", "prop": "activeCount", "width_attr": "width=\"100\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "平台名称", "prop": "platformName"}, {"label": "企业", "prop": "enterpriseName"}, {"label": "平台类型", "prop": "platformType"}, {"label": "注册人员", "prop": "registerCount"},
            {"label": "活跃人员", "prop": "activeCount"}, {"label": "更新时间", "prop": "updateTime", "format": "datetime"}, {"label": "平台说明", "prop": "platformDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalPlatform", "label": "平台总数", "unit": "家", "note": "当前范围"},
            {"key": "totalRegister", "label": "注册人员", "unit": "人", "note": "累计"},
            {"key": "totalActive", "label": "活跃人员", "unit": "人", "note": "累计", "cardClass": "primary"},
            {"key": "monitoredCount", "label": "已监测", "unit": "家", "note": "接入监管", "cardClass": "success"},
        ],
    },
    {
        "path": "enterprise/selfService/index.vue",
        "route_name": "YgbEnterpriseSelfService",
        "eyebrow": "单位管理",
        "title": "企业自主服务",
        "desc": "为企业提供自助查询、信息维护、业务办理等自主服务入口的监管视图。",
        "imports": ["import { listEnterprise, getEnterpriseSummary } from '@/api/ygb/enterprise'"],
        "list_call": "listEnterprise",
        "summary_call": "getEnterpriseSummary()",
        "search_fields": [
            {"label": "企业名称", "prop": "enterpriseName", "type": "input", "width": 200},
            {"label": "统一社会信用代码", "prop": "creditCode", "type": "input", "width": 200},
            {"label": "企业类型", "prop": "enterpriseType", "type": "select", "width": 140, "options": [{"label": "派遣单位", "value": "dispatch"}, {"label": "用工单位", "value": "employer"}]},
        ],
        "table_columns": [
            {"label": "ID", "prop": "enterpriseId", "width_attr": "width=\"80\""},
            {"label": "企业名称", "prop": "enterpriseName", "width_attr": "min-width=\"220\""},
            {"label": "统一社会信用代码", "prop": "creditCode", "width_attr": "width=\"200\""},
            {"label": "企业类型", "prop": "enterpriseType", "width_attr": "width=\"120\""},
            {"label": "联系人", "prop": "contactName", "width_attr": "width=\"120\""},
            {"label": "联系电话", "prop": "contactPhone", "width_attr": "width=\"140\""},
            {"label": "自助服务开通", "prop": "selfServiceEnabled", "width_attr": "width=\"120\"", "dict": [{"label": "已开通", "value": "1"}, {"label": "未开通", "value": "0"}]},
        ],
        "detail_items": [
            {"label": "企业名称", "prop": "enterpriseName"}, {"label": "统一社会信用代码", "prop": "creditCode"}, {"label": "企业类型", "prop": "enterpriseType"}, {"label": "联系人", "prop": "contactName"},
            {"label": "联系电话", "prop": "contactPhone"}, {"label": "自助服务开通", "prop": "selfServiceEnabled"}, {"label": "开通时间", "prop": "selfServiceOpenTime", "format": "datetime"}, {"label": "服务记录", "prop": "selfServiceLog", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalEnterprise", "label": "企业总数", "unit": "家", "note": "当前范围"},
            {"key": "enabledCount", "label": "已开通", "unit": "家", "note": "自助服务", "cardClass": "success"},
            {"key": "disabledCount", "label": "未开通", "unit": "家", "note": "待推广", "cardClass": "warning"},
            {"key": "monthActive", "label": "月活跃", "unit": "家", "note": "使用自助服务"},
        ],
    },
    {
        "path": "operation/dataOverview/index.vue",
        "route_name": "YgbOperationDataOverview",
        "eyebrow": "运营后台",
        "title": "运营数据总览",
        "desc": "总览平台运营核心数据，包括企业入驻、招聘、简历、设备等关键指标。",
        "imports": ["import { getOperationOverview } from '@/api/ygb/operation'"],
        "list_call": "fetchList",
        "summary_call": "getOperationOverview()",
        "extra_script": "async function fetchList() {\n  return { rows: [], total: 0 }\n}",
        "search_fields": [],
        "table_columns": [
            {"label": "指标名称", "prop": "indicatorName", "width_attr": "min-width=\"200\""},
            {"label": "指标值", "prop": "indicatorValue", "width_attr": "width=\"150\""},
            {"label": "环比", "prop": "momRate", "width_attr": "width=\"120\""},
            {"label": "同比", "prop": "yoyRate", "width_attr": "width=\"120\""},
            {"label": "统计时间", "prop": "statTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "指标名称", "prop": "indicatorName"}, {"label": "指标值", "prop": "indicatorValue"}, {"label": "环比", "prop": "momRate"}, {"label": "同比", "prop": "yoyRate"},
            {"label": "统计时间", "prop": "statTime", "format": "datetime"}, {"label": "指标说明", "prop": "indicatorDesc", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalEnterprise", "label": "入驻企业", "unit": "家", "note": "累计"},
            {"key": "totalJob", "label": "在招岗位", "unit": "个", "note": "累计"},
            {"key": "totalResume", "label": "简历数", "unit": "份", "note": "累计"},
            {"key": "totalDevice", "label": "设备数", "unit": "台", "note": "累计"},
        ],
    },
    {
        "path": "operation/dataAnalysis/index.vue",
        "route_name": "YgbOperationDataAnalysis",
        "eyebrow": "运营后台",
        "title": "数据统计与分析",
        "desc": "对运营数据进行多维度统计分析，支撑运营决策与平台治理。",
        "imports": ["import { listOperationModule, getOperationModuleSummary } from '@/api/ygb/operation'"],
        "list_call": "fetchList",
        "summary_call": "fetchSummary()",
        "extra_script": "async function fetchList(query) {\n  return listOperationModule('recruitStats', query)\n}\nasync function fetchSummary() {\n  return getOperationModuleSummary('recruitStats')\n}",
        "search_fields": [
            {"label": "统计维度", "prop": "dimension", "type": "select", "width": 140, "options": [{"label": "日", "value": "day"}, {"label": "周", "value": "week"}, {"label": "月", "value": "month"}]},
            {"label": "统计日期", "prop": "statDate", "type": "input", "width": 150},
        ],
        "table_columns": [
            {"label": "ID", "prop": "recordId", "width_attr": "width=\"80\""},
            {"label": "统计日期", "prop": "statDate", "width_attr": "width=\"120\""},
            {"label": "新增企业", "prop": "newEnterpriseCount", "width_attr": "width=\"110\""},
            {"label": "新增岗位", "prop": "newJobCount", "width_attr": "width=\"110\""},
            {"label": "新增简历", "prop": "newResumeCount", "width_attr": "width=\"110\""},
            {"label": "活跃用户数", "prop": "activeUserCount", "width_attr": "width=\"110\""},
            {"label": "更新时间", "prop": "updateTime", "width_attr": "width=\"170\"", "format": "datetime"},
        ],
        "detail_items": [
            {"label": "统计日期", "prop": "statDate"}, {"label": "新增企业", "prop": "newEnterpriseCount"}, {"label": "新增岗位", "prop": "newJobCount"}, {"label": "新增简历", "prop": "newResumeCount"},
            {"label": "活跃用户数", "prop": "activeUserCount"}, {"label": "更新时间", "prop": "updateTime", "format": "datetime"}, {"label": "分析结论", "prop": "analysisResult", "span": 2},
        ],
        "summary_cards": [
            {"key": "totalRecord", "label": "统计记录", "unit": "条", "note": "当前范围"},
            {"key": "avgNewEnterprise", "label": "日均新企", "unit": "家", "note": "周期均值"},
            {"key": "avgNewJob", "label": "日均新岗", "unit": "个", "note": "周期均值"},
            {"key": "avgActiveUser", "label": "日均活跃", "unit": "人", "note": "周期均值", "cardClass": "primary"},
        ],
    },
]


def main():
    for cfg in PAGES:
        file_path = UI_DIR / cfg['path']
        file_path.parent.mkdir(parents=True, exist_ok=True)
        content = render_page(cfg)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Generated: {file_path}")


if __name__ == '__main__':
    main()
