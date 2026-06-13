# Dual Portal Module Breakdown (YGB / AZB)

## Goal

This document fixes the implementation boundary for `one backend + two long-lived frontends`:

1. Which modules stay backend-shared.
2. Which modules must split into `ygb` and `azb` views.
3. Which roles should default into which portal.
4. Which backend capability must stay common and which must become portal-aware.

## Fixed Positioning

### `ygb`

Primary portal for:

- enterprise admin
- enterprise operator
- HRSS supervisor
- finance-heavy business users

Primary working style:

- filing
- reconciliation
- correction
- monthly processing
- business closure

### `azb`

Primary portal for:

- emergency supervisor
- insurer
- bank
- site safety lead
- equipment manager

Primary working style:

- risk discovery
- on-site governance
- warning linkage
- safety insurance coverage review
- disposal and closure tracking

## Module Split Rules

### A. Backend-shared only

These stay as one backend data object and one business state model:

- enterprise master data
- person master data
- warning event / warning rule core data
- device core data / trace logs / AI raw events
- aq insurance policy core data
- prevention fund / prevention project core data
- injury event core data
- height work report core data
- AI report raw result / scoring result
- role / menu / permission / portal scope data

### B. YGB-primary modules

These should not become AZB main workbench pages:

- contract
- attendance raw
- attendance monthly
- salary batch
- salary detail
- social payment
- social base compare
- tax compare
- employment ratio
- fake outsourcing
- newform worker
- occupation monitor

AZB may quote results from these modules, but should not expose their original processing workbench.

### C. Shared backend + dual frontend views

These modules must keep one backend object but allow two portal-specific pages:

- enterprise
- person
- warning
- warningRule
- device
- aqInsurance
- preventionFund
- preventionProject
- injuryEvent
- heightWorkReport
- uninsuredList
- creditScore
- aiReport
- aiReportConfig
- statReport
- cockpit / dashboard aggregates

Portal split principle:

- `ygb` page emphasizes processing, reconciliation, and pending work.
- `azb` page emphasizes risk level, disposal time, and governance action.

### D. AZB-primary governance views

These are allowed to reuse backend data but should remain AZB-first in frontend expression:

- emergency cockpit
- safety insurance governance dashboard
- device governance workbench
- on-site risk warning workbench
- height work governance view
- AI risk governance dashboard

## Role to Portal Mapping

| Role Key | Default Portal | Portal Use Rule |
| --- | --- | --- |
| `ygb_enterprise_admin` | `ygb` | Main enterprise workbench |
| `ygb_enterprise_operator` | `ygb` | Reduced enterprise processing workbench |
| `ygb_hrss_supervisor` | `ygb` | HRSS supervision portal |
| `ygb_emergency_supervisor` | `azb` | Emergency governance portal |
| `ygb_insurer` | `azb` | Read-only insurance coordination |
| `ygb_bank` | `azb` | Read-only bank view |

## Portal-aware Backend Requirements

Backend stays unified, but these outputs must become portal-aware:

- router result
- menu tree result
- role-menu authorization save/read
- home summary
- cockpit summary
- module summary
- focus queue
- detail hint
- report explanation

Implementation basis already present in repo:

- `sys_menu.portal_scope`
- `sys_role.allowed_portal_scope`
- `sys_role.default_portal_code`
- `sys_role_menu.portal_scope`
- request header `X-Portal-Code`

## Current Repo Landing

Already present:

- portal config and runtime switching
- dual login entry
- `src/views/ygb`
- `src/views/azb`
- portal route filtering
- role read-only helper

Direct implementation priority:

1. finish read-only boundaries inside shared `ygb` pages
2. continue moving portal summaries to backend aggregate interfaces
3. finish homepage and cockpit portal differentiation
4. bind 530.1 business data to 6.1 scoring explanation by portal

## Acceptance

The split is considered correct only when all four are true:

1. users enter the correct default portal by role
2. wrong-portal menus and buttons do not appear
3. the same object shows different summary emphasis in `ygb` and `azb`
4. backend data is still single-source and not duplicated by portal
