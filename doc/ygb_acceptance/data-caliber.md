# YGB Data Caliber

## Typed Report

- `AQ_INSURANCE`: source `t_aq_insurance`, grouped by enterprise.
- `NEWFORM`: source `t_newform_worker`, grouped by platform.
- `OCCUPATION`: source `t_occupation_monitor`, grouped by industry.
- `UNION_SUPERVISION`: source worker complaint/legal/feedback tables, grouped by source.
- `CUSTOM`: source credit score and warning closure aggregation.

## Operation

- `enterpriseReview`: managed as extension records under `OP_ENTERPRISE_REVIEW`.
- `jobReview`: reads actual `WorkerJobPost` admin list.
- `resume`: reads actual `ygb_worker_resume`.
- `message`: managed as extension records under `OP_MESSAGE`.

## Platform

- `runtime`: server metrics + system login/oper log counts.
- `document/exchange/securityAudit/backup`: managed as extension records.
