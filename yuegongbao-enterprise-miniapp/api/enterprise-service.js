import request, { clearCurrentWorkerAuthState, getBaseUrl, getToken } from '../utils/request'

const PREFIX = '/app/enterprise/service'

function get(url, data) {
  return request({ url: `${PREFIX}${url}`, data })
}

function post(url, data) {
  return request({ url: `${PREFIX}${url}`, method: 'POST', data })
}

export function getWorkerHome() {
  return get('/home')
}

export function getWorkerProfile() {
  return get('/profile')
}

export function getWorkerWorkbench() {
  return get('/workbench')
}

export function getTrainingProgress() {
  return get('/training/progress')
}

export function getTrainingQuestions() {
  return get('/training/questions')
}

export function answerTrainingQuestion(data) {
  return post('/training/answer', data)
}

export function getTrainingHistory() {
  return get('/training/history')
}

export function getTrainingCourses() {
  return get('/training/courses')
}

export function getTrainingCourseDetail(courseKey) {
  return get('/training/course-detail', { courseKey })
}

export function getTrainingHistoryDetail(month) {
  return get('/training/history-detail', { month })
}

export function saveTrainingStudyProgress(courseKey, studiedSeconds) {
  return post(`/training/study-progress?courseKey=${encodeURIComponent(courseKey)}&studiedSeconds=${studiedSeconds}`)
}

export function checkIn(data) {
  return post('/attendance/check-in', data)
}

export function checkOut(data) {
  return post('/attendance/check-out', data)
}

export function getAttendanceMonthly(month) {
  return get('/attendance/monthly', { month })
}

export function getAttendanceDay(date) {
  return get('/attendance/day', { date })
}

export function getSalaryList(year) {
  return get('/salary/list', { year })
}

export function getSalaryDetail(month) {
  return get('/salary/detail', { month })
}

export function getSocialSecurityList(year) {
  return get('/social-security/list', { year })
}

export function getSocialSecurityDetail(month) {
  return get('/social-security/detail', { month })
}

export function getTaxList(year) {
  return get('/tax/list', { year })
}

export function getTaxDetail(month) {
  return get('/tax/detail', { month })
}

export function createComplaint(data) {
  return post('/complaint/create', data)
}

export function getComplaintList(status = '') {
  return get('/complaint/list', { status })
}

export function getComplaintDetail(complaintId) {
  return get('/complaint/detail', { complaintId })
}

export function createLegalConsult(data) {
  return post('/legal-consult/create', data)
}

export function getLegalConsultList(status = '') {
  return get('/legal-consult/list', { status })
}

export function getLegalConsultDetail(consultId) {
  return get('/legal-consult/detail', { consultId })
}

export function getLegalHotline() {
  return get('/legal-consult/hotline')
}

export function getLegalArticleList() {
  return get('/legal-article/list')
}

export function getLegalArticleDetail(articleKey) {
  return get('/legal-article/detail', { articleKey })
}

export function getLegalFaqList(keyword = '') {
  return get('/legal-faq/list', { keyword })
}

export function getLegalFaqDetail(faqKey) {
  return get('/legal-faq/detail', { faqKey })
}

export function getNoticeList(pageNum = 1, pageSize = 10) {
  return get('/notice/list', { pageNum, pageSize })
}

export function getNoticeDetail(noticeId) {
  return get('/notice/detail', { noticeId })
}

export function markNoticeRead(noticeId) {
  return post(`/notice/read?noticeId=${noticeId}`)
}

export function getJobList(params = {}) {
  return get('/job/list', {
    keyword: params.keyword || '',
    jobType: params.jobType || '',
    salaryMin: params.salaryMin,
    salaryMax: params.salaryMax,
    latitude: params.latitude,
    longitude: params.longitude,
    radiusKm: params.radiusKm
  })
}

export function getNearbyJobMapConfig(latitude, longitude) {
  return get('/job/map-config', { latitude, longitude })
}

export function getNearbyJobList(params = {}) {
  return get('/job/nearby', {
    keyword: params.keyword || '',
    jobType: params.jobType || '',
    salaryMin: params.salaryMin,
    salaryMax: params.salaryMax,
    latitude: params.latitude,
    longitude: params.longitude,
    radiusKm: params.radiusKm
  })
}

export function getJobDetail(jobId) {
  return get('/job/detail', { jobId })
}

export function applyJob(jobId) {
  return post(`/job/apply?jobId=${jobId}`)
}

export function getJobApplyList() {
  return get('/job/apply/list')
}

export function getResumeDetail() {
  return get('/resume/detail')
}

export function getLaborContractList() {
  return get('/labor-contract/list')
}

export function getLaborContractDetail(contractId) {
  return get('/labor-contract/detail', { contractId })
}

export function saveResume(data) {
  return post('/resume/save', data)
}

export function getHelpList() {
  return get('/help/list')
}

export function getHelpDetail(articleKey) {
  return get('/help/detail', { articleKey })
}

export function getWorkerRealnameDetail() {
  return get('/real-name/detail')
}

export function submitWorkerRealname(data) {
  return post('/real-name/submit', data)
}

export function createFeedback(data) {
  return post('/feedback/create', data)
}

export function getWorkerSettings() {
  return get('/settings/detail')
}

export function saveWorkerSettings(data) {
  return post('/settings/save', data)
}

export function registerWorkerPush(data) {
  return post('/settings/push-register', data)
}

export function sendWorkerPushTest(data = {}) {
  return post('/settings/push-test', data)
}

export function getWorkerPointsAccount() {
  return get('/points/account')
}

export function exchangeWorkerPointGoods(data) {
  return post('/points/exchange', data)
}

export function getWorkerInsuranceSecurity() {
  return get('/insurance/security')
}

export function createWorkerUploadRecord(data) {
  return post('/upload-record/create', data)
}

export function getWorkerUploadRecordList(categoryCode = '') {
  return get('/upload-record/list', { categoryCode })
}

export function getActivityDetail() {
  return get('/activity/detail')
}

export function joinActivity(activityKey) {
  return post(`/activity/join?activityKey=${encodeURIComponent(activityKey)}`)
}

export function getActivityJoinList() {
  return get('/activity/join-list')
}

export function getVideoList() {
  return get('/video/list')
}

export function getVideoDetail(videoKey) {
  return get('/video/detail', { videoKey })
}

export function saveVideoProgress(videoKey, watchedSeconds, totalSeconds) {
  return post(`/video/progress?videoKey=${encodeURIComponent(videoKey)}&watchedSeconds=${watchedSeconds}&totalSeconds=${totalSeconds || 0}`)
}

export function getAiTrainingDetail() {
  return get('/ai-training/detail')
}

export function getUnionServiceHome() {
  return get('/union-service/home')
}

export function getUnionCases() {
  return get('/union-service/cases')
}

export function getUnionNotices() {
  return get('/union-service/notices')
}

export function getUnionCaseDetail(caseKey) {
  return get('/union-service/case-detail', { caseKey })
}

export function getUnionNoticeDetail(noticeKey) {
  return get('/union-service/notice-detail', { noticeKey })
}

export function getUnionContracts() {
  return get('/union-service/contracts')
}

export function getUnionContractDetail(contractKey) {
  return get('/union-service/contract-detail', { contractKey })
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
