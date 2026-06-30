import request, { clearCurrentWorkerAuthState, getBaseUrl, getToken } from '../utils/request'

function createQuery(params = {}) {
  const query = Object.keys(params)
    .filter((key) => params[key] !== undefined && params[key] !== null && params[key] !== '')
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(String(params[key]))}`)
    .join('&')
  return query ? `?${query}` : ''
}

function get(url, params = {}) {
  return request({
    url: `${url}${createQuery(params)}`
  })
}

function post(url, data) {
  return request({
    url,
    method: 'POST',
    data
  })
}

function postQuery(url, params = {}) {
  return request({
    url: `${url}${createQuery(params)}`,
    method: 'POST'
  })
}

export function getScreenDashboard() {
  return get('/app/screen/dashboard')
}

export function runFaceAuthMock(payload = {}) {
  return post('/app/screen/face-auth/mock', payload)
}

export function getScreenDeviceDashboard() {
  return get('/app/screen/device/dashboard')
}

export function saveDeviceConfigDraft(payload) {
  return post('/app/screen/device/save-config', payload)
}

export function restartDevicePlaceholder() {
  return post('/app/screen/device/restart')
}

export function shutdownDevicePlaceholder() {
  return post('/app/screen/device/shutdown')
}

export function exportDeviceLogPlaceholder() {
  return post('/app/screen/device/export-log')
}

export function getWorkerHome() {
  return get('/app/screen/home')
}

export function getWorkerProfile() {
  return get('/app/screen/profile')
}

export function getWorkerWorkbench() {
  return get('/app/screen/workbench')
}

export function getTrainingProgress() {
  return get('/app/screen/training/progress')
}

export function getTrainingQuestions() {
  return get('/app/screen/training/questions')
}

export function answerTrainingQuestion(data) {
  return post('/app/screen/training/answer', data)
}

export function getTrainingHistory() {
  return get('/app/screen/training/history')
}

export function getTrainingCourses() {
  return get('/app/screen/training/courses')
}

export function getTrainingCourseDetail(courseKey) {
  return get('/app/screen/training/course-detail', { courseKey })
}

export function getTrainingHistoryDetail(month) {
  return get('/app/screen/training/history-detail', { month })
}

export function saveTrainingStudyProgress(courseKey, studiedSeconds) {
  return postQuery('/app/screen/training/study-progress', { courseKey, studiedSeconds })
}

export function checkIn(data) {
  return post('/app/screen/attendance/check-in', data)
}

export function checkOut(data) {
  return post('/app/screen/attendance/check-out', data)
}

export function getAttendanceMonthly(month) {
  return get('/app/screen/attendance/monthly', { month })
}

export function getAttendanceDay(date) {
  return get('/app/screen/attendance/day', { date })
}

export function getSalaryList(year) {
  return get('/app/screen/salary/list', { year })
}

export function getSalaryDetail(month) {
  return get('/app/screen/salary/detail', { month })
}

export function getSocialSecurityList(year) {
  return get('/app/screen/social-security/list', { year })
}

export function getSocialSecurityDetail(month) {
  return get('/app/screen/social-security/detail', { month })
}

export function getTaxList(year) {
  return get('/app/screen/tax/list', { year })
}

export function getTaxDetail(month) {
  return get('/app/screen/tax/detail', { month })
}

export function createComplaint(data) {
  return post('/app/screen/complaint/create', data)
}

export function getComplaintList(status = '') {
  return get('/app/screen/complaint/list', { status })
}

export function getComplaintDetail(complaintId) {
  return get('/app/screen/complaint/detail', { complaintId })
}

export function createLegalConsult(data) {
  return post('/app/screen/legal-consult/create', data)
}

export function getLegalConsultList(status = '') {
  return get('/app/screen/legal-consult/list', { status })
}

export function getLegalConsultDetail(consultId) {
  return get('/app/screen/legal-consult/detail', { consultId })
}

export function getLegalHotline() {
  return get('/app/screen/legal-consult/hotline')
}

export function getLegalArticleList() {
  return get('/app/screen/legal-article/list')
}

export function getLegalArticleDetail(articleKey) {
  return get('/app/screen/legal-article/detail', { articleKey })
}

export function getLegalFaqList(keyword = '') {
  return get('/app/screen/legal-faq/list', { keyword })
}

export function getLegalFaqDetail(faqKey) {
  return get('/app/screen/legal-faq/detail', { faqKey })
}

export function getNoticeList(pageNum = 1, pageSize = 10) {
  return get('/app/screen/notice/list', { pageNum, pageSize })
}

export function getNoticeDetail(noticeId) {
  return get('/app/screen/notice/detail', { noticeId })
}

export function markNoticeRead(noticeId) {
  return postQuery('/app/screen/notice/read', { noticeId })
}

export function getJobList(params = {}) {
  return get('/app/screen/job/list', params)
}

export function getNearbyJobMapConfig(latitude, longitude) {
  return get('/app/screen/job/map-config', { latitude, longitude })
}

export function getNearbyJobList(params = {}) {
  return get('/app/screen/job/nearby', params)
}

export function getJobDetail(jobId) {
  return get('/app/screen/job/detail', { jobId })
}

export function applyJob(jobId) {
  return postQuery('/app/screen/job/apply', { jobId })
}

export function getJobApplyList() {
  return get('/app/screen/job/apply/list')
}

export function getResumeDetail() {
  return get('/app/screen/resume/detail')
}

export function getLaborContractList() {
  return get('/app/screen/labor-contract/list')
}

export function getLaborContractDetail(contractId) {
  return get('/app/screen/labor-contract/detail', { contractId })
}

export function saveResume(data) {
  return post('/app/screen/resume/save', data)
}

export function getHelpList() {
  return get('/app/screen/help/list')
}

export function getHelpDetail(articleKey) {
  return get('/app/screen/help/detail', { articleKey })
}

export function getWorkerRealnameDetail() {
  return get('/app/screen/real-name/detail')
}

export function submitWorkerRealname(data) {
  return post('/app/screen/real-name/submit', data)
}

export function createFeedback(data) {
  return post('/app/screen/feedback/create', data)
}

export function getWorkerSettings() {
  return get('/app/screen/settings/detail')
}

export function saveWorkerSettings(data) {
  return post('/app/screen/settings/save', data)
}

export function registerWorkerPush(data) {
  return post('/app/screen/settings/push-register', data)
}

export function sendWorkerPushTest(data = {}) {
  return post('/app/screen/settings/push-test', data)
}

export function getWorkerPointsAccount() {
  return get('/app/screen/points/account')
}

export function exchangeWorkerPointGoods(data) {
  return post('/app/screen/points/exchange', data)
}

export function getWorkerInsuranceSecurity() {
  return get('/app/screen/insurance/security')
}

export function createWorkerUploadRecord(data) {
  return post('/app/screen/upload-record/create', data)
}

export function getWorkerUploadRecordList(categoryCode = '') {
  return get('/app/screen/upload-record/list', { categoryCode })
}

export function getActivityDetail() {
  return get('/app/screen/activity/detail')
}

export function joinActivity(activityKey) {
  return postQuery('/app/screen/activity/join', { activityKey })
}

export function getActivityJoinList() {
  return get('/app/screen/activity/join-list')
}

export function getVideoList() {
  return get('/app/screen/video/list')
}

export function getVideoDetail(videoKey) {
  return get('/app/screen/video/detail', { videoKey })
}

export function saveVideoProgress(videoKey, watchedSeconds, totalSeconds) {
  return postQuery('/app/screen/video/progress', { videoKey, watchedSeconds, totalSeconds: totalSeconds || 0 })
}

export function getAiTrainingDetail() {
  return get('/app/screen/ai-training/detail')
}

export function getUnionServiceHome() {
  return get('/app/screen/union-service/home')
}

export function getUnionCases() {
  return get('/app/screen/union-service/cases')
}

export function getUnionNotices() {
  return get('/app/screen/union-service/notices')
}

export function getUnionCaseDetail(caseKey) {
  return get('/app/screen/union-service/case-detail', { caseKey })
}

export function getUnionNoticeDetail(noticeKey) {
  return get('/app/screen/union-service/notice-detail', { noticeKey })
}

export function getUnionContracts() {
  return get('/app/screen/union-service/contracts')
}

export function getUnionContractDetail(contractKey) {
  return get('/app/screen/union-service/contract-detail', { contractKey })
}

export function uploadWorkerImage(filePath, name = 'file') {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${getBaseUrl()}/common/upload`,
      filePath,
      name,
      header: {
        Authorization: getToken() ? `Bearer ${getToken()}` : ''
      },
      success: (res) => {
        try {
          const payload = JSON.parse(res.data || '{}')
          if (payload.code === 200) {
            resolve({
              ...payload,
              _httpStatusCode: Number(res.statusCode || 0),
              _rawResponseLength: String(res.data || '').length
            })
            return
          }
          const uploadError = new Error(payload.msg || '上传失败')
          if (payload.code === 401) {
            clearCurrentWorkerAuthState()
            uni.reLaunch({ url: '/pages/login/index' })
            return
          }
          uploadError.httpStatusCode = Number(res.statusCode || 0)
          uploadError.responseCode = payload.code
          uploadError.responseBody = res.data || ''
          reject(uploadError)
        } catch (error) {
          error.httpStatusCode = Number(res.statusCode || 0)
          reject(error)
        }
      },
      fail: (error) => {
        const uploadError = new Error(error?.errMsg || '上传失败')
        uploadError.errMsg = error?.errMsg || ''
        reject(uploadError)
      }
    })
  })
}
