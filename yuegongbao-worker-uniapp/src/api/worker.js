import request, { clearCurrentWorkerAuthState, getBaseUrl, getToken } from '../utils/request'

export function getWorkerHome() {
  return request({ url: '/app/worker/home' })
}

export function getWorkerProfile() {
  return request({ url: '/app/worker/profile' })
}

export function getWorkerWorkbench() {
  return request({ url: '/app/worker/workbench' })
}

export function getTrainingProgress() {
  return request({ url: '/app/worker/training/progress' })
}

export function getTrainingQuestions() {
  return request({ url: '/app/worker/training/questions' })
}

export function answerTrainingQuestion(data) {
  return request({
    url: '/app/worker/training/answer',
    method: 'POST',
    data
  })
}

export function getTrainingHistory() {
  return request({
    url: '/app/worker/training/history'
  })
}

export function getTrainingCourses() {
  return request({
    url: '/app/worker/training/courses'
  })
}

export function getTrainingCourseDetail(courseKey) {
  return request({
    url: '/app/worker/training/course-detail',
    data: { courseKey }
  })
}

export function saveTrainingStudyProgress(courseKey, studiedSeconds) {
  return request({
    url: `/app/worker/training/study-progress?courseKey=${encodeURIComponent(courseKey)}&studiedSeconds=${studiedSeconds}`,
    method: 'POST'
  })
}

export function checkIn(data) {
  return request({
    url: '/app/worker/attendance/check-in',
    method: 'POST',
    data
  })
}

export function checkOut(data) {
  return request({
    url: '/app/worker/attendance/check-out',
    method: 'POST',
    data
  })
}

export function getAttendanceMonthly(month) {
  return request({
    url: '/app/worker/attendance/monthly',
    data: { month }
  })
}

export function getAttendanceDay(date) {
  return request({
    url: '/app/worker/attendance/day',
    data: { date }
  })
}

export function getSalaryList(year) {
  return request({
    url: '/app/worker/salary/list',
    data: { year }
  })
}

export function getSalaryDetail(month) {
  return request({
    url: '/app/worker/salary/detail',
    data: { month }
  })
}

export function getSocialSecurityList(year) {
  return request({
    url: '/app/worker/social-security/list',
    data: { year }
  })
}

export function getSocialSecurityDetail(month) {
  return request({
    url: '/app/worker/social-security/detail',
    data: { month }
  })
}

export function getTaxList(year) {
  return request({
    url: '/app/worker/tax/list',
    data: { year }
  })
}

export function getTaxDetail(month) {
  return request({
    url: '/app/worker/tax/detail',
    data: { month }
  })
}

export function createComplaint(data) {
  return request({
    url: '/app/worker/complaint/create',
    method: 'POST',
    data
  })
}

export function getComplaintList(status = '') {
  return request({
    url: '/app/worker/complaint/list',
    data: { status }
  })
}

export function getComplaintDetail(complaintId) {
  return request({
    url: '/app/worker/complaint/detail',
    data: { complaintId }
  })
}

export function createLegalConsult(data) {
  return request({
    url: '/app/worker/legal-consult/create',
    method: 'POST',
    data
  })
}

export function getLegalConsultList(status = '') {
  return request({
    url: '/app/worker/legal-consult/list',
    data: { status }
  })
}

export function getLegalConsultDetail(consultId) {
  return request({
    url: '/app/worker/legal-consult/detail',
    data: { consultId }
  })
}

export function getLegalHotline() {
  return request({
    url: '/app/worker/legal-consult/hotline'
  })
}

export function getLegalArticleList() {
  return request({
    url: '/app/worker/legal-article/list'
  })
}

export function getLegalArticleDetail(articleKey) {
  return request({
    url: '/app/worker/legal-article/detail',
    data: { articleKey }
  })
}

export function getLegalFaqList(keyword = '') {
  return request({
    url: '/app/worker/legal-faq/list',
    data: { keyword }
  })
}

export function getLegalFaqDetail(faqKey) {
  return request({
    url: '/app/worker/legal-faq/detail',
    data: { faqKey }
  })
}

export function getNoticeList(pageNum = 1, pageSize = 10) {
  return request({
    url: '/app/worker/notice/list',
    data: { pageNum, pageSize }
  })
}

export function getNoticeDetail(noticeId) {
  return request({
    url: '/app/worker/notice/detail',
    data: { noticeId }
  })
}

export function markNoticeRead(noticeId) {
  return request({
    url: `/app/worker/notice/read?noticeId=${noticeId}`,
    method: 'POST'
  })
}

export function getJobList(params = {}) {
  return request({
    url: '/app/worker/job/list',
    data: {
      keyword: params.keyword || '',
      jobType: params.jobType || '',
      salaryMin: params.salaryMin,
      salaryMax: params.salaryMax,
      latitude: params.latitude,
      longitude: params.longitude,
      radiusKm: params.radiusKm
    }
  })
}

export function getNearbyJobMapConfig(latitude, longitude) {
  return request({
    url: '/app/worker/job/map-config',
    data: { latitude, longitude }
  })
}

export function getNearbyJobList(params = {}) {
  return request({
    url: '/app/worker/job/nearby',
    data: {
      keyword: params.keyword || '',
      jobType: params.jobType || '',
      salaryMin: params.salaryMin,
      salaryMax: params.salaryMax,
      latitude: params.latitude,
      longitude: params.longitude,
      radiusKm: params.radiusKm
    }
  })
}

export function getJobDetail(jobId) {
  return request({
    url: '/app/worker/job/detail',
    data: { jobId }
  })
}

export function applyJob(jobId) {
  return request({
    url: `/app/worker/job/apply?jobId=${jobId}`,
    method: 'POST'
  })
}

export function getJobApplyList() {
  return request({
    url: '/app/worker/job/apply/list'
  })
}

export function getResumeDetail() {
  return request({
    url: '/app/worker/resume/detail'
  })
}

export function getLaborContractList() {
  return request({
    url: '/app/worker/labor-contract/list'
  })
}

export function getLaborContractDetail(contractId) {
  return request({
    url: '/app/worker/labor-contract/detail',
    data: { contractId }
  })
}

export function saveResume(data) {
  return request({
    url: '/app/worker/resume/save',
    method: 'POST',
    data
  })
}

export function getHelpList() {
  return request({
    url: '/app/worker/help/list'
  })
}

export function getHelpDetail(articleKey) {
  return request({
    url: '/app/worker/help/detail',
    data: { articleKey }
  })
}

export function getWorkerRealnameDetail() {
  return request({
    url: '/app/worker/real-name/detail'
  })
}

export function submitWorkerRealname(data) {
  return request({
    url: '/app/worker/real-name/submit',
    method: 'POST',
    data
  })
}

export function createFeedback(data) {
  return request({
    url: '/app/worker/feedback/create',
    method: 'POST',
    data
  })
}

export function getWorkerSettings() {
  return request({
    url: '/app/worker/settings/detail'
  })
}

export function saveWorkerSettings(data) {
  return request({
    url: '/app/worker/settings/save',
    method: 'POST',
    data
  })
}

export function registerWorkerPush(data) {
  return request({
    url: '/app/worker/settings/push-register',
    method: 'POST',
    data
  })
}

export function sendWorkerPushTest(data = {}) {
  return request({
    url: '/app/worker/settings/push-test',
    method: 'POST',
    data
  })
}

export function getWorkerPointsAccount() {
  return request({
    url: '/app/worker/points/account'
  })
}

export function exchangeWorkerPointGoods(data) {
  return request({
    url: '/app/worker/points/exchange',
    method: 'POST',
    data
  })
}

export function getWorkerInsuranceSecurity() {
  return request({
    url: '/app/worker/insurance/security'
  })
}

export function createWorkerUploadRecord(data) {
  return request({
    url: '/app/worker/upload-record/create',
    method: 'POST',
    data
  })
}

export function getWorkerUploadRecordList(categoryCode = '') {
  return request({
    url: '/app/worker/upload-record/list',
    data: { categoryCode }
  })
}

export function getActivityDetail() {
  return request({
    url: '/app/worker/activity/detail'
  })
}

export function joinActivity(activityKey) {
  return request({
    url: `/app/worker/activity/join?activityKey=${encodeURIComponent(activityKey)}`,
    method: 'POST'
  })
}

export function getActivityJoinList() {
  return request({
    url: '/app/worker/activity/join-list'
  })
}

export function getVideoList() {
  return request({
    url: '/app/worker/video/list'
  })
}

export function getVideoDetail(videoKey) {
  return request({
    url: '/app/worker/video/detail',
    data: { videoKey }
  })
}

export function saveVideoProgress(videoKey, watchedSeconds, totalSeconds) {
  return request({
    url: `/app/worker/video/progress?videoKey=${encodeURIComponent(videoKey)}&watchedSeconds=${watchedSeconds}&totalSeconds=${totalSeconds || 0}`,
    method: 'POST'
  })
}

export function getAiTrainingDetail() {
  return request({
    url: '/app/worker/ai-training/detail'
  })
}

export function getUnionServiceHome() {
  return request({
    url: '/app/worker/union-service/home'
  })
}

export function getUnionCases() {
  return request({
    url: '/app/worker/union-service/cases'
  })
}

export function getUnionNotices() {
  return request({
    url: '/app/worker/union-service/notices'
  })
}

export function getUnionCaseDetail(caseKey) {
  return request({
    url: '/app/worker/union-service/case-detail',
    data: { caseKey }
  })
}

export function getUnionNoticeDetail(noticeKey) {
  return request({
    url: '/app/worker/union-service/notice-detail',
    data: { noticeKey }
  })
}

export function getUnionContracts() {
  return request({
    url: '/app/worker/union-service/contracts'
  })
}

export function getUnionContractDetail(contractKey) {
  return request({
    url: '/app/worker/union-service/contract-detail',
    data: { contractKey }
  })
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
