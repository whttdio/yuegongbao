# YGB Menu Matrix

## Scope

- Pack A: injury event analysis/monitor, new report codes, future device and person sub-ledgers.
- Pack B: enterprise extensions, operation overview, enterprise review, job review, resume, message, portal content sections.
- Pack C: platform document, exchange, runtime summary, security audit, backup, typed-report full set.

## Current Matrix

| Level 1 | Level 2 | Route / Prefix | Status |
| --- | --- | --- | --- |
| `operation` | `overview` | `/ygb/operation/overview` | implemented |
| `operation` | `enterpriseReview` | `/ygb/operation/enterpriseReview/*` | implemented |
| `operation` | `jobReview` | `/ygb/operation/jobReview/*` | implemented |
| `operation` | `resume` | `/ygb/operation/resume/*` | implemented |
| `operation` | `message` | `/ygb/operation/message/*` | implemented |
| `platform` | `runtime` | `/ygb/platform/runtime/summary` | implemented |
| `platform` | `document` | `/ygb/platform/document/*` | implemented |
| `platform` | `exchange` | `/ygb/platform/exchange/*` | implemented |
| `platform` | `securityAudit` | `/ygb/platform/securityAudit/*` | implemented |
| `platform` | `backup` | `/ygb/platform/backup/*` | implemented |
| `report` | `typed-report` | `/ygb/report/*` | extended |
| `portalContent` | `sectionCode` | `warm_map / training_course / law_library / mutual_help / recruit_market` | backend ready |
