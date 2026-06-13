# UAT Real Backend Checklist

This checklist operationalizes the UAT real-backend manual device test.

## Private Config

Create a private env file outside tracked source or as ignored `.env.uat`.

```env
VITE_WORKER_API_BASE_URL=https://uat-api.your-domain.example
UAT_WORKER_ACCOUNT=13700010001
UAT_WORKER_LOGIN_METHOD=password
UAT_WORKER_PASSWORD=replace-with-private-password
```

For SMS login:

```env
VITE_WORKER_API_BASE_URL=https://uat-api.your-domain.example
UAT_WORKER_ACCOUNT=13700010001
UAT_WORKER_LOGIN_METHOD=sms
UAT_WORKER_SMS_CODE=replace-with-fixed-code
# Or use UAT_WORKER_SMS_CHANNEL=real when the real SMS channel is available.
```

## Commands

```powershell
npm run verify:uat-readiness -- --config C:\private\yuegongbao-worker-uat.env
npm run verify:acceptance
npm run build:app:uat
```

Use `--skip-network` only when the UAT network is intentionally unreachable from this machine and the real-device check will verify connectivity.

## Manual Device Flow

1. Install or run `dist\build\app` on a real device through HBuilderX.
2. Open Settings and confirm `build mode=uat`.
3. Confirm the effective API address is the UAT baseUrl from the private config or enter it in the Settings API environment section.
4. Tap the API connectivity check and verify `/captchaImage` succeeds.
5. Log in with `13700010001` using the confirmed password or SMS flow.
6. Open profile, home, workbench, attendance, upload records, notices, activities, videos, and union service pages.
7. For each module, confirm the page does not crash and its snapshot contains real UAT data.
8. Execute controlled writes: check-in/check-out, image upload, complaint/legal attachment carry-over, activity join, video progress save, and notice read.
9. Copy Settings exports after each group: real-device overview, API checklist, and full acceptance export.

## Pass Criteria

- UAT readiness script passes with a non-placeholder baseUrl.
- `npm run verify:acceptance` passes.
- `npm run build:app:uat` passes.
- Real-device login succeeds and UAT token remains scoped to the UAT environment.
- Read and write flows return expected UAT structures and are captured in Settings exports.

## Current Defaults

- Candidate worker account: `13700010001`
- Candidate worker person: `赵志成`
- Expected seeded data: profile, complaints, notices, activities, videos, upload records, push settings.
