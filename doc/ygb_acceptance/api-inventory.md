# YGB API Inventory

## Added In This Round

- `GET /ygb/operation/overview`
- `GET /ygb/operation/{submodule}/list|summary|{recordId}`
- `POST /ygb/operation/{submodule}/export`
- `POST|PUT|DELETE /ygb/operation/{submodule}`
- `GET /ygb/operation/jobReview/list|{jobId}`
- `POST /ygb/operation/jobReview/export`
- `GET /ygb/operation/resume/list|{resumeId}`
- `POST /ygb/operation/resume/export`
- `GET /ygb/platform/runtime/summary`
- `GET /ygb/platform/{submodule}/list|summary|{recordId}`
- `POST /ygb/platform/{submodule}/export`
- `POST|PUT|DELETE /ygb/platform/{submodule}`

## Extended

- `/ygb/report/*` now supports `AQ_INSURANCE / NEWFORM / OCCUPATION / UNION_SUPERVISION / CUSTOM`
- portal public home payload now includes:
  - `warmMap`
  - `trainingCourses`
  - `lawLibrary`
  - `mutualHelp`
  - `recruitMarket`
