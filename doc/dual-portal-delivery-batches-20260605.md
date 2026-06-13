# Dual Portal Delivery Batches (Executable)

## Batch P0: Portal Boundary and Role Placement

### Target

Close the boundary of `who enters which portal` and `which module belongs to which portal`.

### Work Items

- verify `sys_role.default_portal_code`
- verify `sys_role.allowed_portal_scope`
- verify `sys_menu.portal_scope`
- verify `sys_role_menu.portal_scope`
- ensure `107/108/109` default into `azb`
- ensure `104/105/106` default into `ygb`
- ensure read-only roles only keep safe actions inside shared pages

### Frontend Scope

- login redirect
- route filtering
- left menu filtering
- shared-page read-only guards

### Acceptance

1. unauthorized portal cannot be entered by direct URL.
2. read-only roles cannot see or trigger mutation actions.
3. wrong-portal menu trees do not render.

## Batch P1: Shared Module Workbench Upgrade

### Target

Upgrade shared high-frequency pages from base CRUD pages into long-term workbenches.

### Priority Modules

1. warning
2. device
3. aqInsurance
4. heightWorkReport
5. preventionFund
6. preventionProject
7. injuryEvent
8. enterprise
9. person
10. aiReport

### Page Standard

Every high-frequency page should provide:

- summary cards
- focus queue
- current selected object summary
- recommended actions
- detail hints
- role-safe action set

### Acceptance

1. users can identify pending focus within the first screen.
2. selected row and detail drawer linkage is stable.
3. pages no longer rely on plain table CRUD as the main interaction model.

## Batch P2: Homepage and Cockpit Split

### Target

Turn `ygb` and `azb` into two different working portals after login.

### `ygb` Homepage Focus

- contract / attendance / salary
- social / tax / compliance correction
- enterprise monthly processing
- finance closure

### `azb` Homepage Focus

- warnings
- devices
- aq insurance coverage risk
- height work
- AI risk governance

### Acceptance

1. `ygb` first screen reads like a processing portal.
2. `azb` first screen reads like a governance portal.
3. role-based shortcuts differ clearly across portals.

## Batch P3: Scoring Explanation Integration

### Target

Attach `530.1` business facts to `6.1` scoring explanation without building a second backend.

### Backend Work

- scoring dimension config
- scoring point config
- weight config
- target threshold config
- monthly score aggregation
- enterprise score snapshot
- region score snapshot
- portal explanation aggregation

### Frontend Work

`ygb` explains:

- labor compliance
- insurance expansion
- monthly correction closure
- HRSS governance value

`azb` explains:

- safety insurance compliance
- device safety
- risk disposal
- hidden danger closure
- emergency governance value

### Acceptance

1. the same enterprise can show different explanation order in `ygb` and `azb`.
2. every scoring point can trace back to a source object or aggregate metric.
3. weight change does not require frontend page rewrites.

## Batch P4: Full Regression

### Required Verification Matrix

Test by `role + portal + module + action`.

Mandatory roles:

- `104`
- `105`
- `106`
- `107`
- `108`
- `109`

Mandatory modules:

- enterprise
- person
- warning
- device
- aqInsurance
- heightWorkReport
- aiReport
- statReport

### Build Verification

- `vite build --mode ygb`
- `vite build --mode azb`

### Runtime Verification

1. menu visibility matches portal scope.
2. button visibility matches role action set.
3. shared data stays connected across portals.
4. portal wording and recommended actions differ clearly.
