# YGB Role Matrix

| Role Domain | Key Modules | Notes |
| --- | --- | --- |
| `ygb-regulator` | report, warning, operation, platform | main supervision view |
| `ygb-enterprise` | ygb main chain, report query/export | no third portal introduced |
| `ygb-operation` | operation overview/review/message | stays under same permission system |
| `ygb-platform-admin` | platform governance objects | bridges to system/monitor |
| `azb-regulator` | typed report + existing azb modules | unchanged portal scope base |

## Permission Families

- `ygb:operation:*`
- `ygb:operationEnterpriseReview:*`
- `ygb:operationJobReview:*`
- `ygb:operationResume:*`
- `ygb:operationMessage:*`
- `ygb:platformRuntime:query`
- `ygb:platformDocument:*`
- `ygb:platformExchange:*`
- `ygb:platformSecurityAudit:*`
- `ygb:platformBackup:*`
