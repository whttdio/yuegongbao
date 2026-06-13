const fs = require('fs')
const path = require('path')

const rootDir = path.resolve(__dirname, '..')

const checks = [
  {
    name: 'attendance location permission downgrade',
    files: ['pages/attendance/checkin.vue', 'src/pages/attendance/checkin.vue'],
    patterns: ['uni.getLocation', 'classifyLocationError', 'LOCATION_PERMISSION_DENIED', 'recordLocationSnapshot(false']
  },
  {
    name: 'attendance offline queue and retry',
    files: ['pages/attendance/checkin.vue', 'src/pages/attendance/checkin.vue'],
    patterns: ['worker_attendance_offline_queue', 'enqueueOfflineRecord', 'flushOfflineQueue', 'uni.onNetworkStatusChange']
  },
  {
    name: 'attendance face camera upload',
    files: ['pages/attendance/checkin.vue', 'src/pages/attendance/checkin.vue'],
    patterns: ["sourceType: ['camera']", 'uploadWorkerImage', 'faceImageUrl']
  },
  {
    name: 'real-name camera and album upload',
    files: ['pages/profile/real-name.vue', 'src/pages/profile/real-name.vue'],
    patterns: ['uni.chooseImage', 'uploadWorkerImage', "['camera']", "['camera', 'album']"]
  },
  {
    name: 'complaint attachment upload',
    files: ['pages/complaint/index.vue', 'src/pages/complaint/index.vue'],
    patterns: ['uni.chooseImage', 'uploadWorkerImage', 'createWorkerUploadRecord', 'createComplaint']
  },
  {
    name: 'upload 401 scoped auth cleanup',
    files: ['api/worker.js', 'src/api/worker.js'],
    patterns: ['uni.uploadFile', 'payload.code === 401', 'clearCurrentWorkerAuthState', "uni.reLaunch({ url: '/pages/login/index' })"]
  },
  {
    name: 'push listener registration and click routing',
    files: ['utils/push.js', 'src/utils/push.js'],
    patterns: ['uni.onPushMessage', 'syncWorkerPushRegistration', 'savePendingWorkerJumpTarget', 'consumePendingWorkerJumpTarget']
  },
  {
    name: 'push app manifest enabled',
    files: ['manifest.json', 'src/manifest.json'],
    patterns: ['"push"', '"unipush"', '"offline"']
  },
  {
    name: 'settings push diagnostics and notification setting',
    files: ['pages/profile/settings.vue', 'src/pages/profile/settings.vue'],
    patterns: ['openSystemNotificationSetting', 'sendWorkerPushTest', 'syncWorkerPushRegistration', 'getRecentWorkerPushEvents']
  },
  {
    name: 'acceptance export covers device artifacts',
    files: ['utils/acceptance-overview.js', 'src/utils/acceptance-overview.js', 'utils/acceptance-results.js', 'src/utils/acceptance-results.js'],
    patterns: ['worker_attendance_offline_queue', 'worker_push_event_history', 'push_real_device', 'attendance_offline_sync']
  }
]

function read(file) {
  return fs.readFileSync(path.join(rootDir, file), 'utf8')
}

function main() {
  const failures = []
  console.log('Device capability summary:')
  for (const check of checks) {
    const missingFiles = check.files.filter((file) => !fs.existsSync(path.join(rootDir, file)))
    if (missingFiles.length) {
      failures.push(`${check.name}: missing files ${missingFiles.join(', ')}`)
      console.log(`- FAIL ${check.name}: missing files`)
      continue
    }
    const contents = check.files.map(read).join('\n')
    const missingPatterns = check.patterns.filter((pattern) => !contents.includes(pattern))
    if (missingPatterns.length) {
      failures.push(`${check.name}: missing patterns ${missingPatterns.join(', ')}`)
      console.log(`- FAIL ${check.name}: missing ${missingPatterns.join(', ')}`)
      continue
    }
    console.log(`- PASS ${check.name}`)
  }

  if (failures.length) {
    console.error('Device capability verification failed:')
    for (const failure of failures) {
      console.error(`- ${failure}`)
    }
    process.exitCode = 1
    return
  }

  console.log('Device capability verification passed.')
  console.log('Note: real device permission grant, camera capture, GPS coordinates, and push delivery still require manual device evidence.')
}

main()
