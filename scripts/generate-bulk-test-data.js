/**
 * 批量生成粤工保业务仿真测试数据 SQL
 * 用法: node scripts/generate-bulk-test-data.js > sql/ygb_phase49_bulk_simulation_seed.sql
 */
const fs = require('fs')
const path = require('path')

const OUT = path.join(__dirname, '..', 'sql', 'ygb_phase49_bulk_simulation_seed.sql')
const OUT_CLEANUP = path.join(__dirname, '..', 'sql', 'ygb_phase49_bulk_simulation_seed_cleanup.sql')
const N = 80
const N_SMALL = 50
const N_MED = 60

const surnames = '赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜'.split('')
const given = '伟芳娜敏静丽强磊洋勇军杰涛明超秀英华慧建平国华金龙玉兰桂英志强建国建军国庆建华文静雪梅'.split('')
const regions = ['440100', '440106', '440111', '440113', '440300', '440305', '440606', '440700', '441900', '442000']
const entSuffix = ['人力资源有限公司', '机电工程有限公司', '智造服务有限公司', '劳务派遣有限公司', '建筑工程有限公司', '物流科技有限公司', '安保服务有限公司']
const streets = ['体育西路', '天河路', '南山大道', '顺德大道', '番禺大道', '黄埔东路', '福田中心区', '海珠新港路']
const recordTypes = [
  'SOCIAL_ENROLLMENT', 'SOCIAL_SUPPLEMENT', 'TAX_INVOICE', 'TAX_FUND_FLOW', 'UNION_ORG', 'UNION_SUPERVISION',
  'PREVENTION_PUBLICITY', 'PREVENTION_TRAINING', 'DEVICE_INSTALL_ORDER', 'DEVICE_REPAIR_ORDER', 'INJURY_PERSON_MONITOR',
  'OPERATION_JOB_CATEGORY', 'CREDIT_RULE', 'CREDIT_REPAIR', 'EXPANSION_SUBSIDY'
]
const workflowStatuses = ['pending', 'processing', 'closed', 'rejected']
const portalSections = ['warm_map', 'training_course', 'law_library', 'mutual_help', 'recruit_market', 'news', 'policy']

function esc(v) {
  if (v === null || v === undefined) return 'NULL'
  return `'${String(v).replace(/\\/g, '\\\\').replace(/'/g, "''")}'`
}

function pick(arr, i) { return arr[i % arr.length] }
function rand(i, min, max) { return min + ((i * 7919 + 104729) % (max - min + 1)) }
function money(i, base = 3000) { return (base + (i % 37) * 137.53 + (i % 11) * 0.17).toFixed(2) }
function name(i) { return pick(surnames, i) + pick(given, i + 3) + (i > 60 ? pick(given, i) : '') }
function mobile(i) { return `13${String(700000000 + i * 10007).slice(0, 9)}` }
function idCard(i) {
  const region = pick(regions, i).slice(0, 4) + '1990'
  const month = String(rand(i, 1, 12)).padStart(2, '0')
  const day = String(rand(i + 1, 1, 28)).padStart(2, '0')
  const seq = String(1000 + (i % 8999)).padStart(4, '0')
  return `${region}${month}${day}${seq}${i % 10}`
}
function dt(i, offsetDays = 0) {
  const now = new Date('2026-06-16T12:00:00')
  const days = -365 + (i % 366) + offsetDays
  const d = new Date(now.getTime() + days * 86400000 + (i % 86400) * 1000)
  return d.toISOString().slice(0, 19).replace('T', ' ')
}
function d(i, offsetDays = 0) { return dt(i, offsetDays).slice(0, 10) }
function month(i) {
  const m = 1 + (i % 12)
  const y = i % 24 < 12 ? 2025 : 2026
  return `${y}-${String(m).padStart(2, '0')}`
}
/** 生成不超过 maxLen 字符的边界测试文本（按字符计，适配 utf8mb4 varchar） */
function maxLenStr(maxLen, unit = 'X') {
  return unit.repeat(Math.ceil(maxLen / unit.length)).slice(0, maxLen)
}

const lines = []
lines.push('set names utf8mb4;')
lines.push('-- ygb_phase49_bulk_simulation_seed.sql  批量仿真测试数据（仅 INSERT）')
lines.push('-- 依赖: yuegongbao_20260417 + phase1~48 表结构已存在')
lines.push('-- 重复导入: 先 source sql/ygb_phase49_bulk_simulation_seed_cleanup.sql')
lines.push('')

// ---- t_enterprise ----
lines.push('-- t_enterprise')
const enterprises = []
for (let i = 0; i < N; i++) {
  const id = 1101 + i
  const region = pick(regions, i)
  const ename = `仿真${pick(['广州', '深圳', '佛山', '东莞', '中山'], i)}${pick(entSuffix, i)}`
  const code = `9144${String(10000000000000 + id).slice(-13)}`
  const etype = String((i % 4) + 1)
  const sync = String(i % 3)
  const status = i === N - 1 ? '1' : String(i % 2)
  const addr = i === N - 2 ? '' : `广东省${region}市${pick(streets, i)}${100 + i}号`
  const remark = i === N - 3 ? maxLenStr(500, '边界') : `仿真企业种子${i + 1}`
  enterprises.push({ id, ename, code, region, etype, status, addr, remark })
  lines.push(`insert into t_enterprise (enterprise_id,enterprise_name,enterprise_code,region_code,enterprise_type,legal_person,contact_person,contact_phone,address,established_date,sync_status,status,del_flag,create_by,create_time,remark) values (${id},${esc(ename)},${esc(code)},${esc(region)},${esc(etype)},${esc(name(i))},${esc(name(i + 5))},${esc(mobile(i))},${esc(addr)},${esc(d(i))},${esc(sync)},${esc(status)},'0','sim_seed',${esc(dt(i))},${esc(remark)});`)
}
lines.push('')

// ---- t_person ----
lines.push('-- t_person')
const persons = []
for (let i = 0; i < N; i++) {
  const id = 11001 + i
  const ent = enterprises[i]
  const pname = name(i + 10)
  const card = idCard(i)
  const wtype = String((i % 3) + 1)
  const cert = String(i % 4)
  const ins = String(i % 3)
  const emp = i === N - 1 ? '2' : String(i % 3)
  const job = i === N - 2 ? '' : pick(['焊工', '电工', '装配工', '司机', '保洁', '保安', '叉车工'], i)
  persons.push({ id, ent, pname, card, region: ent.region })
  lines.push(`insert into t_person (person_id,enterprise_id,region_code,person_name,id_card,mobile,worker_type,job_type,cert_status,insurance_status,employment_status,entry_date,leave_date,del_flag,create_by,create_time,remark) values (${id},${ent.id},${esc(ent.region)},${esc(pname)},${esc(card)},${esc(mobile(i + 100))},${esc(wtype)},${esc(job)},${esc(cert)},${esc(ins)},${esc(emp)},${esc(d(i, -30))},${emp === '2' ? esc(d(i)) : 'NULL'},'0','sim_seed',${esc(dt(i))},${i === N - 4 ? 'NULL' : esc(`人员仿真${i + 1}`)});`)
}
lines.push('')

// ---- t_labor_contract ----
lines.push('-- t_labor_contract')
const contracts = []
for (let i = 0; i < N; i++) {
  const id = 21001 + i
  const p = persons[i]
  const ent = p.ent
  const employer = enterprises[(i + 7) % N]
  const cno = `SIM-CT-${20250000 + i}`
  const ctype = String((i % 3) + 1)
  const cstatus = String(i % 5)
  contracts.push({ id, cno, dispatch: ent, employer, person: p })
  lines.push(`insert into t_labor_contract (contract_id,contract_no,dispatch_enterprise_id,dispatch_enterprise_name,employer_enterprise_id,employer_enterprise_name,person_id,person_name,id_card,region_code,contract_type,contract_status,sign_date,start_date,end_date,monthly_wage,filing_no,filing_time,ocr_status,clause_check_status,blockchain_hash,contract_file_url,del_flag,create_by,create_time,remark) values (${id},${esc(cno)},${ent.id},${esc(ent.ename)},${employer.id},${esc(employer.ename)},${p.id},${esc(p.pname)},${esc(p.card)},${esc(p.region)},${esc(ctype)},${esc(cstatus)},${esc(d(i, -60))},${esc(d(i, -30))},${esc(d(i, 300))},${money(i, 4500)},${esc(`FIL-${i}`)},${esc(dt(i))},${esc(String(i % 3))},${esc(String(i % 3))},${esc(`hash-${i}`)},${esc(`https://stub/contracts/${i}.pdf`)},'0','sim_seed',${esc(dt(i))},${esc(`合同仿真${i + 1}`)});`)
}
lines.push('')

// ---- t_attendance_raw / monthly ----
lines.push('-- t_attendance_raw')
const monthlies = []
for (let i = 0; i < N; i++) {
  const c = contracts[i]
  const ano = `SIM-ATT-${300000 + i}`
  const ast = String((i % 5) + 1)
  lines.push(`insert into t_attendance_raw (attendance_id,attendance_no,contract_id,contract_no,dispatch_enterprise_id,dispatch_enterprise_name,employer_enterprise_id,employer_enterprise_name,person_id,person_name,id_card,region_code,attendance_date,clock_in_time,clock_out_time,shift_name,attendance_hours,overtime_hours,attendance_status,source_type,collect_status,att_check,device_code,anomaly_remark,del_flag,create_by,create_time,remark) values (${31001 + i},${esc(ano)},${c.id},${esc(c.cno)},${c.dispatch.id},${esc(c.dispatch.ename)},${c.employer.id},${esc(c.employer.ename)},${c.person.id},${esc(c.person.pname)},${esc(c.person.card)},${esc(c.person.region)},${esc(d(i))},${esc(dt(i, 0).slice(0, 10) + ' 08:' + String(i % 60).padStart(2, '0') + ':00')},${esc(dt(i, 0).slice(0, 10) + ' 18:' + String(i % 60).padStart(2, '0') + ':00')},${esc(pick(['白班', '夜班', '中班'], i))},${(8 + (i % 4)).toFixed(2)},${(i % 3).toFixed(2)},${esc(ast)},${esc(String((i % 2) + 1))},${esc(String(i % 3))},${esc(String(i % 2))},${esc(`DEV-${860000 + (i % 60)}`)},${i === N - 1 ? esc(maxLenStr(255, '异')) : esc(i % 7 === 0 ? '迟到异常' : '')},'0','sim_seed',${esc(dt(i))},${esc('考勤仿真')});`)
  const mid = 32001 + i
  monthlies.push({ id: mid, contract: c, month: month(i) })
  lines.push(`insert into t_attendance_monthly (monthly_id,stat_month,contract_id,contract_no,dispatch_enterprise_id,dispatch_enterprise_name,employer_enterprise_id,employer_enterprise_name,person_id,person_name,id_card,region_code,attendance_days,absence_days,late_days,early_leave_days,overtime_hours,total_hours,att_check,summary_status,last_attendance_date,del_flag,create_by,create_time,remark) values (${mid},${esc(month(i))},${c.id},${esc(c.cno)},${c.dispatch.id},${esc(c.dispatch.ename)},${c.employer.id},${esc(c.employer.ename)},${c.person.id},${esc(c.person.pname)},${esc(c.person.card)},${esc(c.person.region)},${20 + (i % 8)},${i % 4},${i % 3},${i % 2},${(i % 12).toFixed(2)},${(168 + i % 24).toFixed(2)},${esc(String(i % 2))},${esc(String((i % 4) + 1))},${esc(d(i))},'0','sim_seed',${esc(dt(i))},${esc('月考勤仿真')});`)
}
lines.push('')

// ---- t_salary_batch / detail ----
lines.push('-- t_salary_batch')
const batches = []
for (let i = 0; i < N_MED; i++) {
  const ent = enterprises[i]
  const bid = 51001 + i
  const bno = `SIM-SAL-B-${500000 + i}`
  const bst = String((i % 5) + 1)
  batches.push({ id: bid, bno, ent, month: month(i) })
  lines.push(`insert into t_salary_batch (batch_id,batch_no,stat_month,dispatch_enterprise_id,dispatch_enterprise_name,region_code,total_person_count,total_payable_amount,total_paid_amount,account_received_amount,batch_status,account_status,regulator_account_name,regulator_account_no,bank_serial_no,submit_time,paid_time,del_flag,create_by,create_time,remark) values (${bid},${esc(bno)},${esc(month(i))},${ent.id},${esc(ent.ename)},${esc(ent.region)},${5 + (i % 20)},${money(i, 50000)},${money(i, 48000)},${money(i, 47000)},${esc(bst)},${esc(String(i % 3))},${esc('监管账户')},${esc('622202' + String(1000000000000 + i))},${esc('BNK' + i)},${esc(dt(i))},${esc(dt(i, 2))},'0','sim_seed',${esc(dt(i))},${esc('工资批次仿真')});`)
}
lines.push('-- t_salary_detail')
for (let i = 0; i < N; i++) {
  const c = contracts[i]
  const m = monthlies[i]
  const b = batches[i % N_MED]
  const pst = String(i % 4)
  lines.push(`insert into t_salary_detail (detail_id,batch_id,batch_no,stat_month,monthly_id,contract_id,contract_no,dispatch_enterprise_id,dispatch_enterprise_name,employer_enterprise_id,employer_enterprise_name,person_id,person_name,id_card,region_code,attendance_days,total_hours,att_check,payable_amount,deduction_amount,net_amount,bank_account_name,bank_account_no,pay_status,fail_reason,del_flag,create_by,create_time,remark) values (${61001 + i},${b.id},${esc(b.bno)},${esc(m.month)},${m.id},${c.id},${esc(c.cno)},${c.dispatch.id},${esc(c.dispatch.ename)},${c.employer.id},${esc(c.employer.ename)},${c.person.id},${esc(c.person.pname)},${esc(c.person.card)},${esc(c.person.region)},${22},${(176).toFixed(2)},'1',${money(i, 6000)},${(i % 200).toFixed(2)},${money(i, 5800)},${esc(c.person.pname)},${esc('622848' + String(1000000000000 + i))},${esc(pst)},${pst === '3' ? esc('银行卡异常') : "''"},'0','sim_seed',${esc(dt(i))},${esc('工资明细仿真')});`)
}
lines.push('')

// ---- regulation tables ----
lines.push('-- t_social_payment')
for (let i = 0; i < N; i++) {
  const p = persons[i]
  lines.push(`insert into t_social_payment (payment_id,stat_month,enterprise_id,enterprise_name,person_id,person_name,id_card,region_code,base_amount,paid_amount,payment_status,source_serial_no,source_status,source_message,callback_time,del_flag,create_by,create_time,remark) values (${71001 + i},${esc(month(i))},${p.ent.id},${esc(p.ent.ename)},${p.id},${esc(p.pname)},${esc(p.card)},${esc(p.region)},${money(i, 5000)},${money(i, 1200)},${esc(String((i % 3) + 1))},${esc('SOC-' + i)},${esc('SUCCESS')},${esc('回写成功')},${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('社保缴费仿真')});`)
}
lines.push('-- t_social_base_compare')
for (let i = 0; i < N; i++) {
  const p = persons[i]
  lines.push(`insert into t_social_base_compare (compare_id,stat_month,enterprise_id,enterprise_name,person_id,person_name,id_card,region_code,salary_amount,social_base_amount,diff_ratio,compare_result,warning_status,del_flag,create_by,create_time,remark) values (${72001 + i},${esc(month(i))},${p.ent.id},${esc(p.ent.ename)},${p.id},${esc(p.pname)},${esc(p.card)},${esc(p.region)},${money(i, 6000)},${money(i, 5500)},${(i % 15).toFixed(2)},${esc(String((i % 3) + 1))},${esc(String(i % 2))},'0','sim_seed',${esc(dt(i))},${esc('社保基数比对')});`)
}
lines.push('-- t_tax_compare')
for (let i = 0; i < N; i++) {
  const p = persons[i]
  lines.push(`insert into t_tax_compare (compare_id,stat_month,enterprise_id,enterprise_name,person_id,person_name,id_card,region_code,salary_amount,declared_amount,diff_ratio,compare_result,warning_status,source_serial_no,source_status,source_message,callback_time,del_flag,create_by,create_time,remark) values (${73001 + i},${esc(month(i))},${p.ent.id},${esc(p.ent.ename)},${p.id},${esc(p.pname)},${esc(p.card)},${esc(p.region)},${money(i, 6000)},${money(i, 5900)},${(i % 8).toFixed(2)},${esc(String((i % 3) + 1))},${esc(String(i % 2))},${esc('TAX-' + i)},'SUCCESS','',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('个税比对')});`)
}
lines.push('-- t_uninsured_list')
for (let i = 0; i < N; i++) {
  const p = persons[i]
  lines.push(`insert into t_uninsured_list (list_id,batch_no,stat_month,list_type,enterprise_id,enterprise_name,person_id,person_name,id_card,region_code,salary_amount,detected_reason,disposal_status,warning_status,del_flag,create_by,create_time,remark) values (${74001 + i},${esc('UNINS-B-' + i)},${esc(month(i))},${esc(String((i % 2) + 1))},${p.ent.id},${esc(p.ent.ename)},${p.id},${esc(p.pname)},${esc(p.card)},${esc(p.region)},${money(i, 5000)},${esc(pick(['工资发放未参保', '新入职漏保', '退保未停薪'], i))},${esc(String(i % 4))},${esc(String(i % 2))},'0','sim_seed',${esc(dt(i))},${esc('未参保清单')});`)
}
lines.push('-- t_employment_proportion')
for (let i = 0; i < N_MED; i++) {
  const ent = enterprises[i]
  lines.push(`insert into t_employment_proportion (record_id,stat_month,employer_enterprise_id,employer_enterprise_name,region_code,dispatch_count,formal_count,ratio_value,warning_level,warning_status,del_flag,create_by,create_time,remark) values (${75001 + i},${esc(month(i))},${ent.id},${esc(ent.ename)},${esc(ent.region)},${20 + i % 30},${5 + i % 10},${(25 + i % 40).toFixed(2)},${esc(String(i % 4))},${esc(String(i % 2))},'0','sim_seed',${esc(dt(i))},${esc('用工比例')});`)
}
lines.push('-- t_fake_outsourcing_record')
for (let i = 0; i < N_MED; i++) {
  const ent = enterprises[i]
  lines.push(`insert into t_fake_outsourcing_record (record_id,stat_month,enterprise_id,enterprise_name,region_code,attendance_score,schedule_score,reward_score,training_score,total_score,suspected_flag,warning_status,evidence_summary,del_flag,create_by,create_time,remark) values (${76001 + i},${esc(month(i))},${ent.id},${esc(ent.ename)},${esc(ent.region)},${50 + i % 50},${40 + i % 60},${30 + i % 70},${60 + i % 40},${200 + i % 100},${esc(String(i % 2))},${esc(String(i % 2))},${esc('考勤与排班不一致')},'0','sim_seed',${esc(dt(i))},${esc('假外包')});`)
}
lines.push('')

// ---- device chain ----
lines.push('-- t_device')
const devices = []
for (let i = 0; i < N; i++) {
  const id = 861001 + i
  const ent = enterprises[i]
  const code = `SIM-DEV-${860000 + i}`
  devices.push({ id, code, ent })
  lines.push(`insert into t_device (device_id,device_code,device_name,device_type,enterprise_id,enterprise_name,region_code,chip_id,sim_card_no,device_status,auth_status,last_heartbeat,install_location,firmware_version,del_flag,create_by,create_time,remark) values (${id},${esc(code)},${esc('考勤终端' + i)},${esc(String((i % 3) + 1))},${ent.id},${esc(ent.ename)},${esc(ent.region)},${esc('CHIP-' + i)},${esc('89860' + String(10000000000 + i))},${esc(String(i % 4))},${esc(String(i % 2))},${esc(dt(i))},${esc(ent.addr || streets[i % streets.length])},${esc('v1.' + (i % 9) + '.0')},'0','sim_seed',${esc(dt(i))},${esc('设备仿真')});`)
}
lines.push('-- t_device_command_log')
for (let i = 0; i < N; i++) {
  const dev = devices[i]
  lines.push(`insert into t_device_command_log (log_id,device_id,device_code,command_type,command_payload,command_result,result_message,source_serial_no,source_status,source_message,callback_time,raw_payload,operator_name,create_by,create_time) values (${871001 + i},${dev.id},${esc(dev.code)},${esc(String((i % 3) + 1))},${esc('{"cmd":"reboot"}')},${esc(String(i % 3))},${esc('执行完成')},${esc('CMD-' + i)},'OK','',${esc(dt(i))},'{}',${esc('运维员' + (i % 5))},'sim_seed',${esc(dt(i))});`)
}
lines.push('-- t_device_event')
for (let i = 0; i < N; i++) {
  const dev = devices[i]
  lines.push(`insert into t_device_event (event_id,device_id,device_code,enterprise_id,enterprise_name,region_code,event_type,event_code,event_content,evidence_url,event_status,source_serial_no,source_status,source_message,callback_time,raw_payload,event_time,create_by,create_time) values (${881001 + i},${dev.id},${esc(dev.code)},${dev.ent.id},${esc(dev.ent.ename)},${esc(dev.ent.region)},${esc(String((i % 4) + 1))},${esc('EVT-' + i)},${esc(pick(['离线告警', '围栏越界', '拆卸告警', '心跳恢复'], i))},${esc('https://stub/ev/' + i + '.jpg')},${esc(String(i % 3))},${esc('EVT-S-' + i)},'OK','',${esc(dt(i))},'{}',${esc(dt(i))},'sim_seed',${esc(dt(i))});`)
}
lines.push('-- t_injury_event')
for (let i = 0; i < N_MED; i++) {
  const p = persons[i]
  lines.push(`insert into t_injury_event (event_id,person_id,person_name,enterprise_id,enterprise_name,region_code,event_date,report_time,injury_location,injury_part,diagnosis_url,injury_status,approval_deadline,remaining_days,approval_result,warning_status,del_flag,create_by,create_time,remark) values (${891001 + i},${p.id},${esc(p.pname)},${p.ent.id},${esc(p.ent.ename)},${esc(p.region)},${esc(d(i, -10))},${esc(dt(i))},${esc('作业现场')},${esc(pick(['手部', '腿部', '腰部'], i))},${esc('https://stub/diag/' + i + '.pdf')},${esc(String(i % 5))},${esc(d(i, 20))},${10 - (i % 11)},${esc(i % 3 === 0 ? '认定中' : '已备案')},${esc(String(i % 2))},'0','sim_seed',${esc(dt(i))},${esc('工伤事件')});`)
}
lines.push('-- t_prevention_project')
for (let i = 0; i < N_MED; i++) {
  const ent = enterprises[i]
  lines.push(`insert into t_prevention_project (project_id,project_name,project_type,enterprise_id,enterprise_name,region_code,budget_amount,actual_amount,start_date,end_date,project_status,evaluation_score,evaluation_report,del_flag,create_by,create_time,remark) values (${801001 + i},${esc('预防项目-' + i)},${esc(String((i % 3) + 1))},${ent.id},${esc(ent.ename)},${esc(ent.region)},${money(i, 100000)},${money(i, 80000)},${esc(d(i, -90))},${esc(d(i, 90))},${esc(String(i % 4))},${60 + i % 40},${esc('https://stub/report/' + i + '.pdf')},'0','sim_seed',${esc(dt(i))},${esc('预防项目')});`)
}
lines.push('')

// ---- warning ----
lines.push('-- t_warning_rule')
const rules = []
for (let i = 0; i < N_SMALL; i++) {
  const rid = 811001 + i
  rules.push(rid)
  lines.push(`insert into t_warning_rule (rule_id,rule_name,warn_level,source_module,condition_text,push_targets,timeout_minutes,upgrade_level,rule_status,create_by,create_time,remark) values (${rid},${esc('规则-' + pick(['社保', '工资', '设备', '工伤', '考勤'], i) + i)},${esc(String((i % 3) + 1))},${esc(pick(['social', 'salary', 'device', 'injury', 'attendance'], i))},${esc('阈值>' + (10 + i))},${esc('监管员,企业管理员')},${120 + i * 5},${esc(String((i % 3) + 1))},${esc(String(i % 2))},'sim_seed',${esc(dt(i))},${esc('预警规则')});`)
}
lines.push('-- t_warning')
const warnings = []
for (let i = 0; i < N; i++) {
  const wid = 821001 + i
  const ent = enterprises[i]
  warnings.push(wid)
  lines.push(`insert into t_warning (warn_id,warn_level,warn_type,source_module,target_object_id,target_type,enterprise_id,enterprise_name,region_code,content,evidence_url,warn_status,assign_to,assign_name,create_by,create_time,update_by,update_time,resolve_time,remark) values (${wid},${esc(String((i % 3) + 1))},${esc(pick(['社保异常', '工资逾期', '设备离线', '工伤超期'], i))},${esc(pick(['social', 'salary', 'device', 'injury'], i))},${ent.id},'1',${ent.id},${esc(ent.ename)},${esc(ent.region)},${esc('预警内容仿真' + i)},${esc('https://stub/warn/' + i)},${esc(String(i % 5))},${900110 + (i % 9)},${esc('处理人' + i)},'sim_seed',${esc(dt(i))},'sim_seed',${esc(dt(i, 1))},${i % 3 === 0 ? esc(dt(i, 3)) : 'NULL'},${esc('预警工单')});`)
}
lines.push('-- t_warning_handle_log')
for (let i = 0; i < N; i++) {
  lines.push(`insert into t_warning_handle_log (log_id,warn_id,action_type,opinion,attachment_urls,before_status,after_status,handler_name,handle_time,create_by,create_time) values (${831001 + i},${warnings[i]},${esc(pick(['签收', '派发', '办结', '误报'], i))},${esc('处理意见' + i)},${esc('https://stub/attach/' + i)},${esc(String(i % 5))},${esc(String((i + 1) % 5))},${esc('经办' + i)},${esc(dt(i, 1))},'sim_seed',${esc(dt(i, 1))});`)
}
lines.push('')

// ---- aq insurance / fund ----
lines.push('-- t_aq_insurance')
const policies = []
for (let i = 0; i < N_MED; i++) {
  const ent = enterprises[i]
  const pid = 961001 + i
  const m = month(i)
  policies.push({ id: pid, ent, month: m })
  lines.push(`insert into t_aq_insurance (policy_id,stat_month,enterprise_id,enterprise_name,region_code,insurer_name,policy_no,premium,start_date,end_date,policy_status,insured_person_count,prevention_fund_ratio,prevention_fund_amount,used_fund_amount,remaining_fund_amount,source_serial_no,source_status,source_message,callback_time,del_flag,create_by,create_time,remark) values (${pid},${esc(m)},${ent.id},${esc(ent.ename)},${esc(ent.region)},${esc(pick(['人保财险', '平安产险', '太保产险'], i))},${esc('POL-SIM-' + (20260000 + i))},${money(i, 20000)},${esc(d(i, -180))},${esc(d(i, 180))},${esc(String(i % 4))},${50 + i % 100},${(15).toFixed(2)},${money(i, 3000)},${money(i, 800)},${money(i, 2200)},${esc('AQ-' + i)},'SUCCESS','',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('安责险')});`)
}
lines.push('-- t_prevention_fund')
for (let i = 0; i < N_MED; i++) {
  const pol = policies[i]
  lines.push(`insert into t_prevention_fund (fund_id,policy_id,stat_month,enterprise_id,enterprise_name,region_code,accrued_amount,used_amount,remaining_amount,fund_status,usage_purpose,evidence_url,last_settle_time,source_mode,del_flag,create_by,create_time,remark) values (${971001 + i},${pol.id},${esc(pol.month)},${pol.ent.id},${esc(pol.ent.ename)},${esc(pol.ent.region)},${money(i, 3000)},${money(i, 500)},${money(i, 2500)},${esc(String((i % 4)))},${esc('培训支出')},${esc('https://stub/fund/' + i)},${esc(dt(i))},${esc(i % 5 === 0 ? 'manual' : 'stub')},'0','sim_seed',${esc(dt(i))},${esc('预防资金')});`)
}
lines.push('')

// ---- credit / newform / occupation ----
lines.push('-- t_credit_score')
for (let i = 0; i < N_MED; i++) {
  const ent = enterprises[i]
  const m = month(i + 3)
  lines.push(`insert into t_credit_score (score_id,stat_month,enterprise_id,enterprise_name,region_code,enterprise_type,contract_score,attendance_score,salary_score,social_tax_score,safety_score,governance_score,total_score,credit_level,color_code,rank_no,factor_json,summary_text,warning_status,source_mode,evaluate_time,del_flag,create_by,create_time,remark) values (${981001 + i},${esc(m)},${ent.id},${esc(ent.ename)},${esc(ent.region)},${esc(ent.etype)},${(80 + i % 10).toFixed(2)},${(75 + i % 15).toFixed(2)},${(70 + i % 20).toFixed(2)},${(65 + i % 25).toFixed(2)},${(85 + i % 10).toFixed(2)},${(60 + i % 30).toFixed(2)},${(60 + i % 40).toFixed(2)},${esc(pick(['A', 'B', 'C', 'D'], i))},${esc(pick(['GREEN', 'YELLOW', 'RED'], i))},${i + 1},${esc('{}')},${esc('信用评估摘要')},${esc(String(i % 2))},'internal',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('信用评分')});`)
}
lines.push('-- t_newform_worker')
for (let i = 0; i < N_MED; i++) {
  const p = persons[i]
  lines.push(`insert into t_newform_worker (worker_record_id,stat_month,enterprise_id,enterprise_name,person_id,person_name,id_card,region_code,platform_name,employment_type,insurance_status,injury_insurance_status,monthly_income,warning_status,source_serial_no,source_status,source_message,callback_time,del_flag,create_by,create_time,remark) values (${991001 + i},${esc(month(i))},${p.ent.id},${esc(p.ent.ename)},${p.id},${esc(p.pname)},${esc(p.card)},${esc(p.region)},${esc(pick(['美团', '饿了么', '滴滴', '家政平台'], i))},${esc(pick(['外卖骑手', '网约车司机', '家政员'], i))},${esc(String(i % 2))},${esc(String(i % 2))},${money(i, 5000)},${esc(String(i % 2))},${esc('NF-' + i)},'SUCCESS','',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('新业态人员')});`)
}
lines.push('-- t_occupation_monitor')
for (let i = 0; i < N_MED; i++) {
  lines.push(`insert into t_occupation_monitor (monitor_id,stat_month,region_code,industry_type,enterprise_count,worker_count,case_count,high_risk_enterprise_count,incidence_rate,warning_level,warning_status,source_channel,source_serial_no,source_status,source_message,callback_time,del_flag,create_by,create_time,remark) values (${992001 + i},${esc(month(i))},${esc(pick(regions, i))},${esc(pick(['制造', '建筑', '化工'], i))},${10 + i % 20},${500 + i * 3},${i % 15},${5 + i % 8},${(i % 12).toFixed(2)},${esc(String(i % 4))},${esc(String(i % 2))},'stub',${esc('OCC-' + i)},'SUCCESS','',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('职业病监测')});`)
}
lines.push('')

// ---- cockpit / stat / ai ----
lines.push('-- t_cockpit_snapshot')
for (let i = 0; i < N_SMALL; i++) {
  lines.push(`insert into t_cockpit_snapshot (snapshot_id,stat_date,region_code,dispatch_company_count,employer_count,dispatched_worker_count,high_risk_enterprise_count,insurance_rate,aq_insurance_rate,today_warning_count,expand_completion_rate,new_injury_rate,online_device_count,pending_warning_count,overdue_injury_count,source_mode,create_by,create_time,remark) values (${900501 + i},${esc(d(0, -i))},${esc(regions[i % regions.length])},${10 + i},${20 + i},${500 + i * 3},${5 + i % 8},${(85 + i % 10).toFixed(2)},${(70 + i % 20).toFixed(2)},${i % 15},${(60 + i % 35).toFixed(2)},${(i % 5).toFixed(2)},${100 + i},${i % 20},${i % 7},'stub','sim_seed',${esc(dt(i))},${esc('驾驶舱快照')});`)
}
lines.push('-- t_cockpit_map_feature')
for (let i = 0; i < N_MED; i++) {
  lines.push(`insert into t_cockpit_map_feature (feature_id,stat_date,region_code,feature_type,feature_name,geometry_type,geometry_json,feature_status,source_mode,properties_json,sort_no,create_by,create_time,remark) values (${905501 + i},${esc(d(i))},${esc(pick(regions, i))},${esc(pick(['ENTERPRISE', 'DEVICE', 'STATION'], i))},${esc('地图要素' + i)},'Point',${esc('{"lng":113.3,"lat":23.1}')},${esc(String(i % 3))},'stub',${esc('{"risk":"medium"}')},${i},'sim_seed',${esc(dt(i))},${esc('地图要素')});`)
}
lines.push('-- t_stat_report')
const reports = []
const reportCodes = ['SALARY_STATS', 'SOCIAL_STATS', 'INJURY_STATS', 'WARNING_STATS', 'DEVICE_STATS']
for (let i = 0; i < N_SMALL; i++) {
  const rid = 910501 + i
  const code = reportCodes[i % reportCodes.length]
  const reg = regions[i % regions.length]
  const m = month(i + 1)
  reports.push({ id: rid, code })
  lines.push(`insert into t_stat_report (report_id,report_code,report_name,stat_month,region_code,report_status,metric_count,metric_amount,metric_rate,report_summary,attachment_url,source_mode,generated_time,del_flag,create_by,create_time,remark) values (${rid},${esc(code)},${esc('月报-' + code + '-' + reg)},${esc(m)},${esc(reg)},${esc(String(i % 3))},${100 + i},${money(i, 100000)},${(80 + i % 15).toFixed(2)},${esc('报表摘要' + i)},${esc('https://stub/report/' + i + '.xlsx')},'stub',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('统计报表')});`)
}
lines.push('-- t_stat_report_item')
for (let i = 0; i < N; i++) {
  const rep = reports[i % N_SMALL]
  lines.push(`insert into t_stat_report_item (item_id,report_id,item_category,item_name,item_dimension,metric_count,metric_value,metric_rate,sort_no) values (${911001 + i},${rep.id},${esc('分类' + (i % 5))},${esc('指标' + i)},${esc('DIM-' + (i % 8))},${10 + i % 50},${money(i, 5000)},${(i % 100).toFixed(2)},${i});`)
}
lines.push('-- t_ai_report_config')
for (let i = 0; i < 20; i++) {
  lines.push(`insert into t_ai_report_config (config_id,region_code,version,config_status,dimension_weights,target_values,effective_date,source_mode,create_by,create_time,remark) values (${920501 + i},${esc(pick(regions, i))},${esc('v1.' + i)},${esc(String(i % 2))},${esc('{"A":0.2}')},${esc('{"A":90}')},${esc(d(i, -30))},'stub','sim_seed',${esc(dt(i))},${esc('AI配置')});`)
}
lines.push('-- t_ai_report')
const aiReports = []
const aiTypes = ['DAILY', 'WEEKLY', 'MONTHLY']
for (let i = 0; i < N_SMALL; i++) {
  const aid = 921501 + i
  const reg = regions[i % regions.length]
  const pstart = d(i * 7, -30)
  const pend = d(i * 7, -1)
  aiReports.push(aid)
  lines.push(`insert into t_ai_report (report_id,report_type,region_code,period_start,period_end,enterprise_type,selected_dimensions,total_score,risk_level,ranking_no,config_version,report_summary,report_pdf_url,source_mode,generated_time,del_flag,create_by,create_time,remark) values (${aid},${esc(aiTypes[i % 3])},${esc(reg)},${esc(pstart)},${esc(pend)},'ALL','A,B,C,D,E',${(50 + i % 50).toFixed(2)},${esc(pick(['LOW', 'MEDIUM', 'HIGH'], i))},${i + 1},${esc('v1.' + (i % 5))},${esc('AI摘要' + i)},${esc('https://stub/ai/' + i + '.pdf')},'stub',${esc(dt(i))},'0','sim_seed',${esc(dt(i))},${esc('AI报告')});`)
}
lines.push('-- t_ai_report_item')
for (let i = 0; i < N; i++) {
  lines.push(`insert into t_ai_report_item (item_id,report_id,dimension_code,dimension_name,metric_label,metric_value,target_value,dimension_weight,dimension_score,risk_level,suggestion_text,detail_json,sort_no) values (${922001 + i},${aiReports[i % N_SMALL]},${esc(String.fromCharCode(65 + (i % 5)))},${esc('维度' + (i % 5))},${esc('指标' + i)},${(40 + i % 60).toFixed(2)},${90},${(0.1 + (i % 5) * 0.05).toFixed(2)},${(70 + i % 25).toFixed(2)},${esc(pick(['LOW', 'MEDIUM', 'HIGH'], i))},${esc('建议' + i)},${esc('{}')},${i});`)
}
lines.push('-- t_ai_report_task')
for (let i = 0; i < N_SMALL; i++) {
  const ar = aiReports[i % N_SMALL]
  lines.push(`insert into t_ai_report_task (task_id,report_id,report_type,task_type,task_name,region_code,risk_level,report_period,report_summary,suggestion_text,receive_user,receive_dept,handle_status,due_date,feedback_text,source_mode,del_flag,create_by,create_time,remark) values (${923001 + i},${ar},${esc(aiTypes[i % 3])},${esc(pick(['review', 'inspect', 'followup'], i))},${esc('监测任务' + i)},${esc(pick(regions, i))},${esc(pick(['LOW', 'MEDIUM', 'HIGH'], i))},${esc(month(i))},${esc('摘要')},${esc('建议')},${esc('user' + i)},${esc('监管部')},${esc(pick(workflowStatuses, i))},${esc(dt(i, 7))},${esc(i % 3 === 0 ? '已反馈' : '')},'manual','0','sim_seed',${esc(dt(i))},${esc('AI任务')});`)
}
lines.push('-- t_ai_report_subscription')
for (let i = 0; i < N_SMALL; i++) {
  lines.push(`insert into t_ai_report_subscription (subscription_id,subscription_name,report_type,region_code,cycle_type,receive_type,receiver,version_scope,status,last_send_time,source_mode,del_flag,create_by,create_time,remark) values (${924001 + i},${esc('订阅-' + i)},${esc(pick(['DAILY', 'WEEKLY', 'MONTHLY'], i))},${esc(pick(regions, i))},${esc(pick(['DAILY', 'WEEKLY', 'MONTHLY'], i))},${esc(pick(['INTERNAL', 'EMAIL', 'WECOM'], i))},${esc(`receiver${i}@test.local`)},'current',${esc(String(i % 2))},${esc(dt(i))},'manual','0','sim_seed',${esc(dt(i))},${esc('订阅')});`)
}
lines.push('-- t_cockpit_config')
for (let i = 0; i < 30; i++) {
  lines.push(`insert into t_cockpit_config (config_id,config_code,config_name,status,region_code,default_region_code,summary_card_config,focus_queue_config,rotate_seconds,refresh_seconds,map_center_lng,map_center_lat,map_zoom,default_focus_key,source_mode,del_flag,create_by,create_time,remark) values (${925001 + i},${esc('cockpit-sim-' + i)},${esc('驾驶舱配置' + i)},${esc(String(i % 2))},${esc(pick(regions, i))},${esc(pick(regions, i))},${esc('[]')},${esc('[]')},${10 + i},${30},${113.3 + i * 0.01},${23.1 + i * 0.01},${11},${esc('focus' + (i % 5))},'manual','0','sim_seed',${esc(dt(i))},${esc('驾驶舱配置')});`)
}
lines.push('-- t_contract_template')
for (let i = 0; i < 30; i++) {
  lines.push(`insert into t_contract_template (template_id,template_code,template_name,template_version,template_type,applicable_scope,review_status,status,region_code,template_file_url,content_text,del_flag,create_by,create_time,remark) values (${926001 + i},${esc('TPL-SIM-' + i)},${esc('合同模板' + i)},${esc('v' + (1 + i % 5) + '.0')},${esc(String((i % 3) + 1))},${esc('劳务派遣')},${esc(pick(['approved', 'pending', 'rejected'], i))},${esc(String(i % 2))},${esc(pick(regions, i))},${esc('https://stub/tpl/' + i + '.docx')},${esc('模板正文' + i)},'0','sim_seed',${esc(dt(i))},${esc('模板')});`)
}
lines.push('-- t_salary_arrears_handle')
for (let i = 0; i < N_SMALL; i++) {
  const b = batches[i]
  lines.push(`insert into t_salary_arrears_handle (arrears_id,batch_id,warning_id,handle_status,follow_user,follow_time,next_follow_time,handle_result,create_by,create_time,remark) values (${927001 + i},${b.id},${warnings[i % N]},${esc(pick(workflowStatuses, i))},${esc('跟进人' + i)},${esc(dt(i))},${esc(dt(i, 7))},${esc(i % 2 ? '已清偿' : '跟踪中')},'sim_seed',${esc(dt(i))},${esc('欠薪处置')});`)
}
lines.push('-- t_height_work_report')
const heightReports = []
for (let i = 0; i < N_SMALL; i++) {
  const hid = 102501 + i
  const ent = enterprises[i]
  heightReports.push(hid)
  lines.push(`insert into t_height_work_report (report_id,report_no,enterprise_id,enterprise_name,reporter_type,applicant_name,applicant_phone,work_location,longitude,latitude,start_time,end_time,work_height_m,worker_count,guardian_name,guardian_phone,safety_measures_json,cert_valid_status,cert_valid_count,cert_invalid_count,report_status,source_mode,source_platform,source_serial_no,voucher_token,actual_end_time,end_photo_url,del_flag,create_by,create_time,remark) values (${hid},${esc('HWR-SIM-' + i)},${ent.id},${esc(ent.ename)},${esc(String((i % 3) + 1))},${esc(name(i))},${esc(mobile(i))},${esc(ent.addr || '作业面A区')},${113.3 + i * 0.001},${23.1 + i * 0.001},${esc(dt(i, 1))},${esc(dt(i, 2))},${5 + i % 20},${2 + i % 5},${esc(name(i + 1))},${esc(mobile(i + 1))},${esc('["安全带","监护"]')},${esc(String(i % 3))},${2 + i % 3},${i % 4},${esc(String(i % 2))},'PC','YGB',${esc('SRC-' + i)},${esc('VCH-' + i)},${i % 2 ? esc(dt(i, 2)) : 'NULL'},${esc('https://stub/end/' + i + '.jpg')},'0','sim_seed',${esc(dt(i))},${esc('高处作业')});`)
}
lines.push('-- t_height_work_report_worker')
for (let i = 0; i < N; i++) {
  const p = persons[i]
  lines.push(`insert into t_height_work_report_worker (row_id,report_id,worker_name,id_card,cert_no,cert_photo_url,cert_valid_status,cert_valid_message,sort_order) values (${103001 + i},${heightReports[i % N_SMALL]},${esc(p.pname)},${esc(p.card)},${esc('CERT-H-' + i)},${esc('https://stub/cert/' + i + '.jpg')},${esc(String(i % 2))},${esc(i % 2 ? '有效' : '即将过期')},${i});`)
}
lines.push('')

// ---- ygb_module_record / portal ----
lines.push('-- ygb_module_record')
for (let i = 0; i < N; i++) {
  const ent = enterprises[i]
  const p = persons[i]
  const rt = pick(recordTypes, i)
  lines.push(`insert into ygb_module_record (record_id,record_type,record_name,category_code,stat_month,portal_code,workflow_status,status,region_code,enterprise_id,enterprise_name,person_id,person_name,related_code,sort_order,source_label,payload_json,remark,create_by,create_time,del_flag) values (${930001 + i},${esc(rt)},${esc('台账记录-' + i)},${esc('cat' + (i % 8))},${esc(month(i))},${esc(i % 5 === 0 ? 'azb' : 'ygb')},${esc(pick(workflowStatuses, i))},${esc(String(i % 2))},${esc(ent.region)},${ent.id},${esc(ent.ename)},${i % 4 === 0 ? 'NULL' : p.id},${esc(i % 4 === 0 ? '' : p.pname)},${esc('REL-' + i)},${i},'sim_seed',${esc('{"idx":' + i + '}')},${i === N - 1 ? esc('边界空载荷测试') : esc('模块台账')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_portal_content')
for (let i = 0; i < N_MED; i++) {
  const sec = pick(portalSections, i)
  lines.push(`insert into ygb_portal_content (content_id,portal_code,section_code,category_code,title,summary,content,cover_url,link_url,source_name,publish_time,sort_order,status,extra_json,create_by,create_time,del_flag) values (${940001 + i},${esc(i % 4 === 0 ? 'azb' : 'ygb')},${esc(sec)},${esc('cat-' + i)},${esc('门户内容标题' + i)},${esc('摘要' + i)},${esc('<p>正文仿真' + i + '</p>')},${esc(i === N_MED - 1 ? '' : 'https://stub/cover/' + i + '.jpg')},${esc(i === N_MED - 2 ? '' : '/pages/detail?id=' + i)},'sim_seed',${esc(dt(i))},${i},${esc(String(i % 2))},${esc('{"tag":"sim"}')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('')

// ---- worker tables ----
lines.push('-- ygb_worker_job_post')
const jobs = []
for (let i = 0; i < N_SMALL; i++) {
  const jid = 950001 + i
  const ent = enterprises[i]
  jobs.push(jid)
  lines.push(`insert into ygb_worker_job_post (job_id,enterprise_id,enterprise_name,title,job_type,work_address,longitude,latitude,salary_min,salary_max,salary_text,recruit_count,contact_name,contact_mobile,description,requirement_text,status,publish_time,create_by,create_time,del_flag) values (${jid},${ent.id},${esc(ent.ename)},${esc(pick(['焊工', '装配工', '司机', '保洁'], i) + '招聘')},${esc(pick(['full', 'part', 'temp'], i))},${esc(ent.addr || '招聘地址' + i)},${113.25 + i * 0.01},${23.12 + i * 0.01},${5000 + i * 100},${7000 + i * 100},${esc((5000 + i * 100) + '-' + (7000 + i * 100))},${3 + i % 10},${esc(name(i))},${esc(mobile(i))},${esc('岗位描述' + i)},${esc('岗位要求' + i)},${esc(String(i % 4))},${esc(dt(i, -5))},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_job_apply')
for (let i = 0; i < N_MED; i++) {
  const uid = 910101 + (i % N_SMALL)
  const p = persons[i % N]
  lines.push(`insert into ygb_worker_job_apply (apply_id,job_id,user_id,person_id,person_name,mobile,status,apply_time,create_by,create_time,del_flag) values (${951001 + i},${jobs[i % N_SMALL]},${uid},${p.id},${esc(p.pname)},${esc(mobile(i))},${esc(pick(['0', '1', '2', '3'], i))},${esc(dt(i))},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_complaint')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  const uid = 910101 + i
  lines.push(`insert into ygb_worker_complaint (complaint_id,user_id,person_id,person_name,enterprise_id,enterprise_name,complaint_type,title,content,contact_mobile,anonymous_flag,sync_union_flag,attachments,status,reply_content,handle_time_text,create_by,create_time,del_flag) values (${952001 + i},${uid},${p.id},${esc(p.pname)},${p.ent.id},${esc(p.ent.ename)},${esc(pick(['salary', 'safety', 'contract'], i))},${esc('投诉标题' + i)},${esc('投诉内容仿真' + i)},${esc(mobile(i))},${esc(String(i % 2))},${esc(String(i % 2))},${esc('[]')},${esc(String(i % 4))},${esc(i % 2 ? '已受理' : '')},${esc(i % 2 ? dt(i, 2) : '')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_legal_consult')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  lines.push(`insert into ygb_worker_legal_consult (consult_id,user_id,person_id,person_name,enterprise_id,enterprise_name,consult_type,title,content,contact_mobile,attachments,status,reply_content,reply_time_text,create_by,create_time,del_flag) values (${953001 + i},${910101 + i},${p.id},${esc(p.pname)},${p.ent.id},${esc(p.ent.ename)},${esc(pick(['injury', 'salary', 'contract'], i))},${esc('咨询' + i)},${esc('法律咨询内容' + i)},${esc(mobile(i))},'[]',${esc(String(i % 3))},${esc(i % 2 ? '律师回复' : '')},${esc(i % 2 ? dt(i, 1) : '')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_resume')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  lines.push(`insert into ygb_worker_resume (resume_id,user_id,person_id,person_name,mobile,job_type,expected_job,expected_city,expected_salary,skill_tags,certificate_text,intro,create_by,create_time,del_flag) values (${954001 + i},${910101 + i},${p.id},${esc(p.pname)},${esc(mobile(i))},${esc(pick(['焊工', '司机'], i))},${esc('期望岗位' + i)},${esc('广州')},${esc('6000-9000')},${esc('焊接,装配')},${esc('特种作业证')},${esc('个人简介' + i)},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_feedback')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  lines.push(`insert into ygb_worker_feedback (feedback_id,user_id,person_id,person_name,mobile,title,content,contact_mobile,status,create_by,create_time,del_flag) values (${955001 + i},${910101 + i},${p.id},${esc(p.pname)},${esc(mobile(i))},${esc('反馈' + i)},${esc('意见反馈' + i)},${esc(mobile(i))},${esc(String(i % 3))},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_activity_join')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  lines.push(`insert into ygb_worker_activity_join (join_id,activity_key,user_id,person_id,person_name,mobile,status,create_by,create_time,del_flag) values (${956001 + i},${esc('act-' + i)},${910101 + i},${p.id},${esc(p.pname)},${esc(mobile(i))},${esc(String(i % 3))},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_video_progress')
for (let i = 0; i < N_SMALL; i++) {
  lines.push(`insert into ygb_worker_video_progress (progress_id,video_key,user_id,person_id,watched_seconds,total_seconds,completed_flag,create_time,update_time,del_flag) values (${957001 + i},${esc('video-' + i)},${910101 + i},${persons[i].id},${30 + i * 10},${120},${esc(String(i % 2))},${esc(dt(i))},${esc(dt(i, 1))},'0');`)
}
lines.push('-- ygb_worker_setting')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  lines.push(`insert into ygb_worker_setting (setting_id,user_id,person_id,notify_enabled,push_client_id,notification_permission,push_platform,create_by,create_time,del_flag) values (${958001 + i},${910101 + i},${p.id},${esc(String(i % 2))},${esc('client-' + i)},${esc(pick(['granted', 'denied', 'unknown'], i))},${esc(pick(['h5', 'app', 'mp-weixin'], i))},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_point_goods')
for (let i = 0; i < 30; i++) {
  lines.push(`insert into ygb_worker_point_goods (goods_id,goods_key,goods_name,goods_desc,required_score,goods_type,status,stock_count,sort_num,create_by,create_time,del_flag) values (${959001 + i},${esc('goods-' + i)},${esc('积分商品' + i)},${esc('兑换说明' + i)},${100 + i * 10},${esc(pick(['coupon', 'gift', 'service'], i))},${esc(String(i % 2))},${50 + i},${i},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_point_ledger')
for (let i = 0; i < N; i++) {
  const p = persons[i % N_SMALL]
  lines.push(`insert into ygb_worker_point_ledger (ledger_id,user_id,person_id,person_name,change_type,title,summary,score_delta,balance_after,create_by,create_time,del_flag) values (${960001 + i},${910101 + (i % N_SMALL)},${p.id},${esc(p.pname)},${esc(pick(['earn', 'spend', 'adjust'], i))},${esc('积分变动' + i)},${esc('说明' + i)},${i % 3 === 0 ? -(10 + i % 50) : 10 + i % 50},${200 + i},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_point_exchange')
for (let i = 0; i < N_SMALL; i++) {
  const p = persons[i]
  lines.push(`insert into ygb_worker_point_exchange (exchange_id,user_id,person_id,person_name,goods_key,goods_name,goods_type,score_cost,exchange_status,delivery_remark,create_by,create_time,del_flag) values (${961501 + i},${910101 + i},${p.id},${esc(p.pname)},${esc('goods-' + (i % 30))},${esc('积分商品' + (i % 30))},'coupon',${100 + i * 5},${esc(pick(['0', '1', '2'], i))},${esc('发放备注')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_upload_record')
for (let i = 0; i < N_MED; i++) {
  const p = persons[i % N_SMALL]
  lines.push(`insert into ygb_worker_upload_record (upload_id,user_id,person_id,person_name,category_code,category_name,file_url,file_name,original_filename,file_size,content_type,source_module,create_by,create_time,del_flag) values (${962001 + i},${910101 + (i % N_SMALL)},${p.id},${esc(p.pname)},${esc(pick(['idcard', 'contract', 'injury', 'complaint'], i))},${esc('材料上传')},${esc('https://stub/upload/' + i + '.jpg')},${esc('file' + i + '.jpg')},${esc('原始' + i + '.jpg')},${102400 + i},'image/jpeg',${esc('worker')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_notice_message')
for (let i = 0; i < N; i++) {
  const p = persons[i % N_SMALL]
  lines.push(`insert into ygb_worker_notice_message (message_id,user_id,person_id,person_name,message_type,title,summary,content,biz_type,biz_id,jump_path,jump_query_text,action_label,source_label,read_flag,read_time,create_by,create_time,del_flag) values (${963001 + i},${910101 + (i % N_SMALL)},${p.id},${esc(p.pname)},${esc(pick(['notice', 'warning', 'activity'], i))},${esc('通知' + i)},${esc('摘要' + i)},${esc('通知正文' + i)},${esc('biz')},${i},${esc('/pages/notice/detail')},${esc('{"id":' + i + '}')},${esc('查看')},${esc('系统')},${esc(String(i % 2))},${i % 2 ? esc(dt(i, 1)) : 'NULL'},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_push_test_record')
for (let i = 0; i < 40; i++) {
  const p = persons[i % N_SMALL]
  lines.push(`insert into ygb_worker_push_test_record (record_id,user_id,person_id,person_name,trace_id,push_client_id_masked,push_platform,notification_permission,notify_enabled,gateway_provider,gateway_url,target_path,target_query_text,action_label,source_label,request_body,response_body,test_status,status_message,create_by,create_time,del_flag) values (${964001 + i},${910101 + (i % N_SMALL)},${p.id},${esc(p.pname)},${esc('trace-' + i)},${esc('cli***' + i)},'h5','granted','1','stub','http://127.0.0.1/push',${esc('/pages/home/index')},'{}',${esc('打开')},'测试',${esc('{}')},${esc('{}')},${esc(pick(['success', 'fail', 'blocked'], i))},${esc('联调记录')},'sim_seed',${esc(dt(i))},'0');`)
}
lines.push('-- ygb_worker_realname_apply')
for (let i = 0; i < 40; i++) {
  const p = persons[i % N_SMALL]
  lines.push(`insert into ygb_worker_realname_apply (apply_id,user_id,person_id,person_name,mobile,id_card,apply_status,reject_reason,id_card_front_url,id_card_back_url,selfie_url,source_module,create_by,create_time,del_flag) values (${965001 + i},${910101 + (i % N_SMALL)},${p.id},${esc(p.pname)},${esc(mobile(i))},${esc(p.card)},${esc(pick(['0', '1', '2'], i))},${esc(i % 3 === 2 ? '照片不清晰' : '')},${esc('https://stub/id/f' + i + '.jpg')},${esc('https://stub/id/b' + i + '.jpg')},${esc('https://stub/selfie/' + i + '.jpg')},'worker','sim_seed',${esc(dt(i))},'0');`)
}
lines.push('')

// ---- sys logs / notice (supplement) ----
lines.push('-- sys_notice')
for (let i = 0; i < 30; i++) {
  lines.push(`insert into sys_notice (notice_id,notice_title,notice_type,notice_content,status,create_by,create_time,remark) values (${970001 + i},${esc('系统通知' + i)},${esc(String((i % 2) + 1))},${esc('通知正文' + i)},'0','sim_seed',${esc(dt(i))},${esc('{"jumpPath":"/pages/notice/detail"}')});`)
}
lines.push('-- sys_oper_log')
for (let i = 0; i < N_SMALL; i++) {
  lines.push(`insert into sys_oper_log (oper_id,title,business_type,method,request_method,operator_type,oper_name,dept_name,oper_url,oper_ip,oper_location,oper_param,json_result,status,error_msg,oper_time,cost_time) values (${971501 + i},${esc('业务操作' + i)},${i % 5},${esc('com.ygb.Test.method')},'POST',1,${esc(pick(['admin', 'ygb_hrss', 'azb_emergency'], i))},${esc('监管中心')},'/ygb/test','127.0.0.1','内网IP','{}','{}',${i % 7 === 0 ? 1 : 0},${i % 7 === 0 ? esc('模拟异常') : "''"},${esc(dt(i))},${100 + i});`)
}
lines.push('-- sys_logininfor')
for (let i = 0; i < N_SMALL; i++) {
  lines.push(`insert into sys_logininfor (info_id,user_name,ipaddr,login_location,browser,os,status,msg,login_time) values (${972001 + i},${esc(pick(['admin', 'ygb_hrss', 'azb_emergency', '13700010001'], i))},'127.0.0.1','内网IP','Chrome','Windows 10',${esc(String(i % 3 === 0 ? 1 : 0))},${esc(i % 3 === 0 ? '密码错误' : '登录成功')},${esc(dt(i))});`)
}

const sql = lines.join('\n')
fs.writeFileSync(OUT, sql, 'utf8')
console.log('Wrote', OUT, 'lines:', lines.length, 'bytes:', Buffer.byteLength(sql))

// ---- cleanup SQL（子表优先，按 create_by=sim_seed 或固定 ID 段）----
const cleanup = []
cleanup.push('set names utf8mb4;')
cleanup.push('-- ygb_phase49_bulk_simulation_seed_cleanup.sql')
cleanup.push('-- 重复导入 phase49 前执行；仅删除 sim_seed 批次数据')
cleanup.push('')
const del = (table, where) => cleanup.push(`delete from ${table} where ${where};`)
del('sys_logininfor', `info_id between 972001 and ${972001 + N_SMALL - 1}`)
del('sys_oper_log', `oper_id between 971501 and ${971501 + N_SMALL - 1}`)
del('sys_notice', `create_by = 'sim_seed'`)
del('ygb_worker_realname_apply', `create_by = 'sim_seed'`)
del('ygb_worker_push_test_record', `create_by = 'sim_seed'`)
del('ygb_worker_notice_message', `create_by = 'sim_seed'`)
del('ygb_worker_upload_record', `create_by = 'sim_seed'`)
del('ygb_worker_point_exchange', `create_by = 'sim_seed'`)
del('ygb_worker_point_ledger', `create_by = 'sim_seed'`)
del('ygb_worker_point_goods', `create_by = 'sim_seed'`)
del('ygb_worker_setting', `create_by = 'sim_seed'`)
del('ygb_worker_video_progress', `progress_id between 957001 and ${957001 + N_SMALL - 1}`)
del('ygb_worker_activity_join', `create_by = 'sim_seed'`)
del('ygb_worker_feedback', `create_by = 'sim_seed'`)
del('ygb_worker_resume', `create_by = 'sim_seed'`)
del('ygb_worker_legal_consult', `create_by = 'sim_seed'`)
del('ygb_worker_complaint', `create_by = 'sim_seed'`)
del('ygb_worker_job_apply', `create_by = 'sim_seed'`)
del('ygb_worker_job_post', `create_by = 'sim_seed'`)
del('ygb_portal_content', `create_by = 'sim_seed'`)
del('ygb_module_record', `create_by = 'sim_seed'`)
del('t_height_work_report_worker', `row_id between 103001 and ${103001 + N - 1}`)
del('t_height_work_report', `create_by = 'sim_seed'`)
del('t_salary_arrears_handle', `create_by = 'sim_seed'`)
del('t_contract_template', `create_by = 'sim_seed' and template_code like 'TPL-SIM-%'`)
del('t_cockpit_config', `create_by = 'sim_seed' and config_code like 'cockpit-sim-%'`)
del('t_ai_report_subscription', `create_by = 'sim_seed'`)
del('t_ai_report_task', `create_by = 'sim_seed'`)
del('t_ai_report_item', `item_id between 922001 and ${922001 + N - 1}`)
del('t_ai_report', `create_by = 'sim_seed'`)
del('t_ai_report_config', `create_by = 'sim_seed'`)
del('t_stat_report_item', `item_id between 911001 and ${911001 + N - 1}`)
del('t_stat_report', `create_by = 'sim_seed'`)
del('t_cockpit_map_feature', `create_by = 'sim_seed'`)
del('t_cockpit_snapshot', `create_by = 'sim_seed'`)
del('t_occupation_monitor', `create_by = 'sim_seed'`)
del('t_newform_worker', `create_by = 'sim_seed'`)
del('t_credit_score', `create_by = 'sim_seed'`)
del('t_prevention_fund', `create_by = 'sim_seed'`)
del('t_aq_insurance', `create_by = 'sim_seed'`)
del('t_warning_handle_log', `create_by = 'sim_seed'`)
del('t_warning', `create_by = 'sim_seed'`)
del('t_warning_rule', `create_by = 'sim_seed'`)
del('t_prevention_project', `create_by = 'sim_seed'`)
del('t_injury_event', `create_by = 'sim_seed'`)
del('t_device_event', `create_by = 'sim_seed'`)
del('t_device_command_log', `create_by = 'sim_seed'`)
del('t_device', `create_by = 'sim_seed'`)
del('t_fake_outsourcing_record', `create_by = 'sim_seed'`)
del('t_employment_proportion', `create_by = 'sim_seed'`)
del('t_uninsured_list', `create_by = 'sim_seed'`)
del('t_tax_compare', `create_by = 'sim_seed'`)
del('t_social_base_compare', `create_by = 'sim_seed'`)
del('t_social_payment', `create_by = 'sim_seed'`)
del('t_salary_detail', `create_by = 'sim_seed'`)
del('t_salary_batch', `create_by = 'sim_seed'`)
del('t_attendance_monthly', `create_by = 'sim_seed'`)
del('t_attendance_raw', `create_by = 'sim_seed'`)
del('t_labor_contract', `create_by = 'sim_seed'`)
del('t_person', `create_by = 'sim_seed'`)
del('t_enterprise', `create_by = 'sim_seed'`)

const cleanupSql = cleanup.join('\n')
fs.writeFileSync(OUT_CLEANUP, cleanupSql, 'utf8')
console.log('Wrote', OUT_CLEANUP, 'lines:', cleanup.length)
