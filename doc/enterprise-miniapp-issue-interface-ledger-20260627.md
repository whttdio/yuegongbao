# Enterprise Miniapp Issue & Interface Ledger - 2026-06-27

Scope: yuegongbao-enterprise-miniapp, AppEnterpriseController, AppEnterpriseServiceController, CommonController upload endpoint.

## Validation Summary

| Check | Result | Notes |
| --- | --- | --- |
| H5 build | PASS | npm.cmd run build:h5; Sass legacy/import warnings remain. |
| UniApp acceptance | PASS | 62/62 pages, 62/62 routes, snapshot markers covered. |
| Backend compile | PASS | mvn.cmd -pl yuegongbao-admin -am -DskipTests compile with JAVA_HOME=D:\JAVA\JDK21\jdk-21. |
| Local readiness | PASS | /captchaImage HTTP 200 against http://127.0.0.1:8080. |
| CC execution | REJECTED | claude.cmd timed out without completion summary; no accepted CC output. |

## Issue Ledger

| ID | Priority | Finding | Evidence | Resolution / Next gate | Owner |
| --- | --- | --- | --- | --- | --- |
| YGB-ENT-P0-001 | P0 | Runtime log reported NoResourceFoundException for enterprise home/workbench dashboard. | backend-stdout.log lines around /app/enterprise/home/dashboard and /app/enterprise/workbench/dashboard. | Source Controller exists and backend compile passes; restart/redeploy backend before next API smoke. | Backend deploy / human verify |
| YGB-ENT-P0-002 | P0 | Home/workbench quick entry only accepted item.path and dropped jumpPath/query/payload style targets. | pages/home/index.vue, pages/workbench/index.vue. | Fixed via normalizeWorkerJumpTarget/openWorkerJumpTarget with fallback. | Codex accepted |
| YGB-ENT-P1-001 | P1 | 60 page files lacked invisible acceptance/linkage snapshot markers; acceptance script failed. | node scripts/verify-acceptance.js. | Added mirrored page comments; acceptance passes. | Codex accepted |
| YGB-ENT-P1-002 | P1 | pages and src/pages must remain mirrored for build/runtime consistency. | verify-acceptance mirror checks. | Verified after edits. | Codex accepted |
| YGB-ENT-P1-003 | P1 | Production/test/uat example env files still contain example.com placeholders. | .env.production.example, .env.test.example, .env.uat.example. | Keep as blocker for release config replacement; no code change without target domains. | Release owner |
| YGB-ENT-P2-001 | P2 | Training media utility uses public external demo media URLs. | utils/training-media.js and src/utils/training-media.js. | Mark as content/security review before production. | Product/security review |
| YGB-ENT-P2-002 | P2 | Profile page still has local reserved-entry toast. | pages/profile/index.vue and src/pages/profile/index.vue. | Confirm whether reserved entry should remain or map to final route. | Product/API owner |

## Interface Coverage

Frontend endpoints: 96; backend mapped endpoints: 96; unmatched endpoints: 0; high-risk manual review endpoints: 50.

| ID | Method | Path | Frontend Source | Mapping Status | Risk Gate |
| --- | --- | --- | --- | --- | --- |
| API-001 | GET | /app/enterprise/home/dashboard | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-002 | GET | /app/enterprise/workbench/dashboard | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-003 | GET | /app/enterprise/people/ledger | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-004 | POST | /app/enterprise/people/action | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-005 | POST | /app/enterprise/people/export | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-006 | GET | /app/enterprise/device/ledger | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-007 | POST | /app/enterprise/device/action | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-008 | POST | /app/enterprise/device/export | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-009 | GET | /app/enterprise/salary/dashboard | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-010 | POST | /app/enterprise/salary/confirm | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-011 | POST | /app/enterprise/salary/import-draft | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-012 | GET | /app/enterprise/operation-approval/dashboard | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-013 | POST | /app/enterprise/operation-approval/submit | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-014 | POST | /app/enterprise/operation-approval/export | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-015 | GET | /app/enterprise/insurance/dashboard | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-016 | POST | /app/enterprise/insurance/action | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-017 | GET | /app/enterprise/training/dashboard | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-018 | POST | /app/enterprise/training/save-draft | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-019 | POST | /app/enterprise/training/action | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-020 | GET | /app/enterprise/job-publish/draft | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-021 | POST | /app/enterprise/job-publish/save-draft | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-022 | POST | /app/enterprise/job-publish/submit | api/enterprise-core.js | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-023 | GET | /app/enterprise/service/home | api/enterprise-service.js#getWorkerHome | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-024 | GET | /app/enterprise/service/profile | api/enterprise-service.js#getWorkerProfile | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-025 | GET | /app/enterprise/service/workbench | api/enterprise-service.js#getWorkerWorkbench | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-026 | GET | /app/enterprise/service/training/progress | api/enterprise-service.js#getTrainingProgress | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-027 | GET | /app/enterprise/service/training/questions | api/enterprise-service.js#getTrainingQuestions | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-028 | POST | /app/enterprise/service/training/answer | api/enterprise-service.js#answerTrainingQuestion | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-029 | GET | /app/enterprise/service/training/history | api/enterprise-service.js#getTrainingHistory | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-030 | GET | /app/enterprise/service/training/courses | api/enterprise-service.js#getTrainingCourses | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-031 | GET | /app/enterprise/service/training/course-detail | api/enterprise-service.js#getTrainingCourseDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-032 | GET | /app/enterprise/service/training/history-detail | api/enterprise-service.js#getTrainingHistoryDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-033 | POST | /app/enterprise/service/training/study-progress | api/enterprise-service.js#saveTrainingStudyProgress | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-034 | POST | /app/enterprise/service/attendance/check-in | api/enterprise-service.js#checkIn | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-035 | POST | /app/enterprise/service/attendance/check-out | api/enterprise-service.js#checkOut | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-036 | GET | /app/enterprise/service/attendance/monthly | api/enterprise-service.js#getAttendanceMonthly | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-037 | GET | /app/enterprise/service/attendance/day | api/enterprise-service.js#getAttendanceDay | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-038 | GET | /app/enterprise/service/salary/list | api/enterprise-service.js#getSalaryList | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-039 | GET | /app/enterprise/service/salary/detail | api/enterprise-service.js#getSalaryDetail | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-040 | GET | /app/enterprise/service/social-security/list | api/enterprise-service.js#getSocialSecurityList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-041 | GET | /app/enterprise/service/social-security/detail | api/enterprise-service.js#getSocialSecurityDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-042 | GET | /app/enterprise/service/tax/list | api/enterprise-service.js#getTaxList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-043 | GET | /app/enterprise/service/tax/detail | api/enterprise-service.js#getTaxDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-044 | POST | /app/enterprise/service/complaint/create | api/enterprise-service.js#createComplaint | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-045 | GET | /app/enterprise/service/complaint/list | api/enterprise-service.js#getComplaintList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-046 | GET | /app/enterprise/service/complaint/detail | api/enterprise-service.js#getComplaintDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-047 | POST | /app/enterprise/service/legal-consult/create | api/enterprise-service.js#createLegalConsult | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-048 | GET | /app/enterprise/service/legal-consult/list | api/enterprise-service.js#getLegalConsultList | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-049 | GET | /app/enterprise/service/legal-consult/detail | api/enterprise-service.js#getLegalConsultDetail | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-050 | GET | /app/enterprise/service/legal-consult/hotline | api/enterprise-service.js#getLegalHotline | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-051 | GET | /app/enterprise/service/legal-article/list | api/enterprise-service.js#getLegalArticleList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-052 | GET | /app/enterprise/service/legal-article/detail | api/enterprise-service.js#getLegalArticleDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-053 | GET | /app/enterprise/service/legal-faq/list | api/enterprise-service.js#getLegalFaqList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-054 | GET | /app/enterprise/service/legal-faq/detail | api/enterprise-service.js#getLegalFaqDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-055 | GET | /app/enterprise/service/notice/list | api/enterprise-service.js#getNoticeList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-056 | GET | /app/enterprise/service/notice/detail | api/enterprise-service.js#getNoticeDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-057 | POST | /app/enterprise/service/notice/read | api/enterprise-service.js#markNoticeRead | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-058 | GET | /app/enterprise/service/job/list | api/enterprise-service.js#getJobList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-059 | GET | /app/enterprise/service/job/map-config | api/enterprise-service.js#getNearbyJobMapConfig | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-060 | GET | /app/enterprise/service/job/nearby | api/enterprise-service.js#getNearbyJobList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-061 | GET | /app/enterprise/service/job/detail | api/enterprise-service.js#getJobDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-062 | POST | /app/enterprise/service/job/apply | api/enterprise-service.js#applyJob | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-063 | GET | /app/enterprise/service/job/apply/list | api/enterprise-service.js#getJobApplyList | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-064 | GET | /app/enterprise/service/resume/detail | api/enterprise-service.js#getResumeDetail | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-065 | GET | /app/enterprise/service/labor-contract/list | api/enterprise-service.js#getLaborContractList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-066 | GET | /app/enterprise/service/labor-contract/detail | api/enterprise-service.js#getLaborContractDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-067 | POST | /app/enterprise/service/resume/save | api/enterprise-service.js#saveResume | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-068 | GET | /app/enterprise/service/help/list | api/enterprise-service.js#getHelpList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-069 | GET | /app/enterprise/service/help/detail | api/enterprise-service.js#getHelpDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-070 | GET | /app/enterprise/service/real-name/detail | api/enterprise-service.js#getWorkerRealnameDetail | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-071 | POST | /app/enterprise/service/real-name/submit | api/enterprise-service.js#submitWorkerRealname | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-072 | POST | /app/enterprise/service/feedback/create | api/enterprise-service.js#createFeedback | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-073 | GET | /app/enterprise/service/settings/detail | api/enterprise-service.js#getWorkerSettings | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-074 | POST | /app/enterprise/service/settings/save | api/enterprise-service.js#saveWorkerSettings | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-075 | POST | /app/enterprise/service/settings/push-register | api/enterprise-service.js#registerWorkerPush | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-076 | POST | /app/enterprise/service/settings/push-test | api/enterprise-service.js#sendWorkerPushTest | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-077 | GET | /app/enterprise/service/points/account | api/enterprise-service.js#getWorkerPointsAccount | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-078 | POST | /app/enterprise/service/points/exchange | api/enterprise-service.js#exchangeWorkerPointGoods | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-079 | GET | /app/enterprise/service/insurance/security | api/enterprise-service.js#getWorkerInsuranceSecurity | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-080 | POST | /app/enterprise/service/upload-record/create | api/enterprise-service.js#createWorkerUploadRecord | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-081 | GET | /app/enterprise/service/upload-record/list | api/enterprise-service.js#getWorkerUploadRecordList | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-082 | GET | /app/enterprise/service/activity/detail | api/enterprise-service.js#getActivityDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-083 | POST | /app/enterprise/service/activity/join | api/enterprise-service.js#joinActivity | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-084 | GET | /app/enterprise/service/activity/join-list | api/enterprise-service.js#getActivityJoinList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-085 | GET | /app/enterprise/service/video/list | api/enterprise-service.js#getVideoList | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-086 | GET | /app/enterprise/service/video/detail | api/enterprise-service.js#getVideoDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-087 | POST | /app/enterprise/service/video/progress | api/enterprise-service.js#saveVideoProgress | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |
| API-088 | GET | /app/enterprise/service/ai-training/detail | api/enterprise-service.js#getAiTrainingDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-089 | GET | /app/enterprise/service/union-service/home | api/enterprise-service.js#getUnionServiceHome | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-090 | GET | /app/enterprise/service/union-service/cases | api/enterprise-service.js#getUnionCases | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-091 | GET | /app/enterprise/service/union-service/notices | api/enterprise-service.js#getUnionNotices | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-092 | GET | /app/enterprise/service/union-service/case-detail | api/enterprise-service.js#getUnionCaseDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-093 | GET | /app/enterprise/service/union-service/notice-detail | api/enterprise-service.js#getUnionNoticeDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-094 | GET | /app/enterprise/service/union-service/contracts | api/enterprise-service.js#getUnionContracts | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-095 | GET | /app/enterprise/service/union-service/contract-detail | api/enterprise-service.js#getUnionContractDetail | MAPPED_PENDING_REAL_DEVICE | NORMAL |
| API-096 | POST | /common/upload | api/enterprise-service.js#uploadWorkerImage | MAPPED_PENDING_REAL_DEVICE | HIGH_REVIEW |

## High-Risk Manual Review Modules

- Authentication/session and runtime base URL switching: login, scoped token storage, settings baseUrl check.
- Upload and camera evidence: /common/upload plus upload-record creation.
- Attendance check-in/check-out, face/photo evidence and location data.
- Real-name, resume, salary, tax, social-security and labor-contract data.
- Enterprise write workflows: people/device action, salary confirm/import, operation approval/export, insurance action, training action, job publish submit.
- Push registration/test and points exchange.

## Operator Notes

- Current backend source compiles, but the running process must be restarted/redeployed after compilation; previous logs came from an instance that did not resolve /app/enterprise routes.
- Local device testing must replace 127.0.0.1 with the PC LAN IP for real miniapp devices.
- SMS login cannot be fully automated from the current local script because the code is random and Redis-backed.
