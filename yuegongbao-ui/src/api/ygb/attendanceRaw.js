import request from '@/utils/request'

// 查询考勤上报列表
export function listAttendanceRaw(query) {
  return request({
    url: '/ygb/attendance/raw/list',
    method: 'get',
    params: query
  })
}

// 查询考勤上报汇总
export function getAttendanceRawSummary(query) {
  return request({
    url: '/ygb/attendance/raw/summary',
    method: 'get',
    params: query
  })
}

export function getAttendanceRawOverview(query) {
  return request({
    url: '/ygb/attendance/raw/overview',
    method: 'get',
    params: query
  })
}

// 查询考勤上报详细
export function getAttendanceRaw(attendanceId) {
  return request({
    url: '/ygb/attendance/raw/' + attendanceId,
    method: 'get'
  })
}

// 新增考勤上报
export function addAttendanceRaw(data) {
  return request({
    url: '/ygb/attendance/raw',
    method: 'post',
    data: data
  })
}

// 修改考勤上报
export function updateAttendanceRaw(data) {
  return request({
    url: '/ygb/attendance/raw',
    method: 'put',
    data: data
  })
}

// 删除考勤上报
export function delAttendanceRaw(attendanceId) {
  return request({
    url: '/ygb/attendance/raw/' + attendanceId,
    method: 'delete'
  })
}
