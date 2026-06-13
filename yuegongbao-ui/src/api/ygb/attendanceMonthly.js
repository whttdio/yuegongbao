import request from '@/utils/request'

// 查询考勤归集列表
export function listAttendanceMonthly(query) {
  return request({
    url: '/ygb/attendance/monthly/list',
    method: 'get',
    params: query
  })
}

// 查询考勤归集汇总
export function getAttendanceMonthlySummary(query) {
  return request({
    url: '/ygb/attendance/monthly/summary',
    method: 'get',
    params: query
  })
}

// 执行考勤归集
export function aggregateAttendanceMonthly(data) {
  return request({
    url: '/ygb/attendance/monthly/aggregate',
    method: 'post',
    data: data
  })
}
