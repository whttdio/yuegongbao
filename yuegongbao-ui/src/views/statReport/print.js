import { formatMetricRate, statReportRegionNameMap, statusLabel } from '@/views/statReport/useStatReportPage'

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')
}

function buildWindow(content) {
  const printWindow = window.open('', '_blank', 'width=1200,height=900')
  if (!printWindow) {
    return null
  }
  printWindow.document.write(content)
  printWindow.document.close()
  printWindow.focus()
  return printWindow
}

function buildStyles() {
  return `
    <style>
      * { box-sizing: border-box; }
      body { margin: 0; padding: 24px; font-family: "Microsoft YaHei", sans-serif; color: #1f2937; background: #fff; }
      .toolbar { display: flex; gap: 12px; margin-bottom: 18px; }
      .toolbar button { border: 1px solid #cbd5e1; background: #fff; color: #0f172a; border-radius: 8px; padding: 8px 14px; cursor: pointer; }
      .sheet { max-width: 1200px; margin: 0 auto; }
      .header { display: flex; justify-content: space-between; gap: 24px; align-items: flex-start; margin-bottom: 16px; }
      h1 { margin: 0; font-size: 28px; }
      .sub { margin-top: 8px; color: #475569; font-size: 14px; line-height: 1.7; }
      .meta { color: #64748b; font-size: 13px; text-align: right; line-height: 1.8; }
      .chips { display: flex; flex-wrap: wrap; gap: 8px; margin: 14px 0 20px; }
      .chip { padding: 6px 10px; border-radius: 999px; background: #f1f5f9; color: #334155; font-size: 12px; }
      .metrics { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-bottom: 18px; }
      .metric { border: 1px solid #e2e8f0; border-radius: 12px; padding: 14px 16px; background: #f8fafc; }
      .metric-label { color: #64748b; font-size: 12px; }
      .metric-value { margin-top: 8px; font-size: 24px; font-weight: 700; color: #0f172a; }
      .section-title { margin: 22px 0 10px; font-size: 18px; color: #0f172a; }
      table { width: 100%; border-collapse: collapse; }
      th, td { border: 1px solid #dbe5f0; padding: 10px 12px; text-align: left; vertical-align: top; font-size: 13px; }
      th { background: #f8fafc; color: #334155; }
      .summary { padding: 14px 16px; border: 1px solid #dbe5f0; border-radius: 12px; background: #f8fafc; line-height: 1.8; }
      @media print {
        body { padding: 0; }
        .toolbar { display: none; }
        .sheet { max-width: none; }
      }
    </style>
  `
}

function buildToolbar() {
  return `
    <div class="toolbar">
      <button onclick="window.print()">打印</button>
      <button onclick="window.close()">关闭</button>
    </div>
  `
}

export function printStatReportList({ config, querySummary = [], metrics = [], columns = [], rows = [], printTime }) {
  const header = `
    <div class="header">
      <div>
        <h1>${escapeHtml(config.printTitle)}</h1>
        <div class="sub">${escapeHtml(config.description)}</div>
      </div>
      <div class="meta">
        <div>打印时间：${escapeHtml(printTime)}</div>
        <div>门户：${escapeHtml(config.portalCode.toUpperCase())}</div>
      </div>
    </div>
  `

  const chips = `
    <div class="chips">
      ${querySummary.map(item => `<span class="chip">${escapeHtml(item.label)}：${escapeHtml(item.value)}</span>`).join('')}
    </div>
  `

  const metricBlocks = `
    <div class="metrics">
      ${metrics.map(item => `
        <div class="metric">
          <div class="metric-label">${escapeHtml(item.label)}</div>
          <div class="metric-value">${escapeHtml(item.value)}${item.unit ? `<span style="font-size:12px;margin-left:4px;color:#64748b;">${escapeHtml(item.unit)}</span>` : ''}</div>
        </div>
      `).join('')}
    </div>
  `

  const table = `
    <table>
      <thead>
        <tr>${columns.map(item => `<th>${escapeHtml(item.label)}</th>`).join('')}</tr>
      </thead>
      <tbody>
        ${rows.map(row => `<tr>${columns.map(column => `<td>${escapeHtml(row[column.key])}</td>`).join('')}</tr>`).join('')}
      </tbody>
    </table>
  `

  const doc = `
    <html>
      <head>
        <meta charset="utf-8" />
        <title>${escapeHtml(config.printTitle)}</title>
        ${buildStyles()}
      </head>
      <body>
        ${buildToolbar()}
        <div class="sheet">
          ${header}
          ${chips}
          ${metricBlocks}
          <div class="section-title">月报列表</div>
          ${table}
        </div>
      </body>
    </html>
  `

  buildWindow(doc)
}

export function printStatReportDetail({ config, report, items = [], printTime }) {
  const metrics = [
    { label: '统计月份', value: report.statMonth || '-', unit: '' },
    { label: '区域', value: statReportRegionNameMap[report.regionCode] || report.regionCode || '-', unit: '' },
    { label: '状态', value: statusLabel(report.reportStatus), unit: '' },
    { label: '核心比率', value: formatMetricRate(report.metricRate, report.reportCode), unit: '' }
  ]

  const detailTable = `
    <table>
      <thead>
        <tr>
          <th>分类</th>
          <th>名称</th>
          <th>维度</th>
          <th>数量</th>
          <th>数值</th>
          <th>比率</th>
        </tr>
      </thead>
      <tbody>
        ${items.map(item => `
          <tr>
            <td>${escapeHtml(item.itemCategory)}</td>
            <td>${escapeHtml(item.itemName)}</td>
            <td>${escapeHtml(item.itemDimension)}</td>
            <td>${escapeHtml(item.metricCount)}</td>
            <td>${escapeHtml(item.metricValue)}</td>
            <td>${escapeHtml(formatMetricRate(item.metricRate, report.reportCode))}</td>
          </tr>
        `).join('')}
      </tbody>
    </table>
  `

  const doc = `
    <html>
      <head>
        <meta charset="utf-8" />
        <title>${escapeHtml(config.printTitle)}</title>
        ${buildStyles()}
      </head>
      <body>
        ${buildToolbar()}
        <div class="sheet">
          <div class="header">
            <div>
              <h1>${escapeHtml(report.reportName || config.printTitle)}</h1>
              <div class="sub">${escapeHtml(config.description)}</div>
            </div>
            <div class="meta">
              <div>打印时间：${escapeHtml(printTime)}</div>
              <div>报表编号：${escapeHtml(report.reportId || '-')}</div>
            </div>
          </div>
          <div class="metrics">
            ${metrics.map(item => `
              <div class="metric">
                <div class="metric-label">${escapeHtml(item.label)}</div>
                <div class="metric-value" style="font-size:18px;">${escapeHtml(item.value)}</div>
              </div>
            `).join('')}
          </div>
          <div class="summary">
            <strong>摘要：</strong>${escapeHtml(report.reportSummary || '-')}
          </div>
          <div class="section-title">明细项</div>
          ${detailTable}
        </div>
      </body>
    </html>
  `

  buildWindow(doc)
}
