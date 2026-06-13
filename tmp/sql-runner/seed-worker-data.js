const mysql = require('mysql2/promise');

const dbConfig = {
  host: process.env.YGB_DB_HOST || '127.0.0.1',
  port: Number(process.env.YGB_DB_PORT || 3306),
  user: process.env.YGB_DB_USER || 'root',
  password: process.env.YGB_DB_PASSWORD || 'root',
  database: process.env.YGB_DB_NAME || 'yuegongbao',
  charset: process.env.YGB_DB_CHARSET || 'utf8mb4'
};

const retryTimes = Number(process.env.YGB_DB_RETRY_TIMES || 1);
const retryDelayMs = Number(process.env.YGB_DB_RETRY_DELAY_MS || 1000);

const seed = {
  userId: 910001,
  userName: '13700010001',
  personId: 10001,
  personName: '\u8d75\u5fd7\u6210',
  mobile: '13700010001',
  enterpriseId: 1001,
  enterpriseName: '\u5e7f\u5dde\u5357\u7ca4\u4eba\u529b\u8d44\u6e90\u6709\u9650\u516c\u53f8'
};

const ids = {
  resume: 910001,
  feedback: [910001, 910002],
  complaint: [910001, 910002],
  consult: [910001, 910002],
  activityJoin: [910001, 910002],
  videoProgress: [910001, 910002],
  setting: 910001,
  ledger: [910001, 910002, 910003, 910004],
  goods: [910001, 910002, 910003],
  exchange: [910001, 910002],
  upload: [910001, 910002, 910003],
  pushRecord: [910001],
  noticeMessage: [910001, 910002, 910003],
  realnameApply: [910001],
  jobApply: [910001, 910002],
  portalContent: [910001, 910002, 910003, 910004, 910005, 910006, 910007, 910008, 910009, 910010, 910011, 910012, 910013, 910014],
  sysNotice: [9001, 9002, 9003, 9004]
};

const certificateText = JSON.stringify([
  {
    certificateName: '\u710a\u5de5\u7279\u79cd\u4f5c\u4e1a\u8bc1',
    certificateNo: 'CERT-WELD-2024-001',
    expireDate: '2027-12-31',
    status: 'valid'
  },
  {
    certificateName: '\u9ad8\u5904\u4f5c\u4e1a\u8bc1',
    certificateNo: 'CERT-HEIGHT-2025-009',
    expireDate: '2026-11-30',
    status: 'expiring'
  }
]);

const resumeRow = [
  ids.resume,
  seed.userId,
  seed.personId,
  seed.personName,
  seed.mobile,
  '\u84dd\u9886\u6280\u5de5',
  '\u710a\u5de5 / \u88c5\u914d\u5de5',
  '\u5e7f\u5dde, \u6df1\u5733',
  '9000-12000',
  '\u710a\u63a5, \u88c5\u914d, \u8bbe\u5907\u70b9\u68c0, \u73ed\u7ec4\u534f\u4f5c',
  certificateText,
  '5\u5e74\u5236\u9020\u4e1a\u4e00\u7ebf\u7ecf\u9a8c\uff0c\u719f\u6089\u710a\u63a5\u3001\u88c5\u914d\u548c\u73b0\u573a\u5b89\u5168\u89c4\u8303\uff0c\u53ef\u63a5\u53d7\u5012\u73ed\u548c\u8de8\u57ce\u6d3e\u9a7b\u3002',
  seed.userName,
  '2026-06-09 16:30:00',
  seed.userName,
  '2026-06-09 16:30:00',
  'worker \u6d4b\u8bd5\u7b80\u5386',
  '0'
];

const feedbackRows = [
  [
    ids.feedback[0],
    seed.userId,
    seed.personId,
    seed.personName,
    seed.mobile,
    '\u5e0c\u671b\u589e\u52a0\u5de5\u8d44\u6761\u5bfc\u51fa\u529f\u80fd',
    '\u79fb\u52a8\u7aef\u67e5\u770b\u5de5\u8d44\u6761\u65f6\u60f3\u76f4\u63a5\u5bfc\u51fa PDF\uff0c\u65b9\u4fbf\u548c\u73ed\u7ec4\u957f\u5bf9\u8d26\u3002',
    seed.mobile,
    '1',
    seed.userName,
    '2026-06-07 09:20:00',
    'admin',
    '2026-06-08 11:05:00',
    '\u5df2\u8bb0\u5f55\u5230\u4ea7\u54c1\u5f85\u529e',
    '0'
  ],
  [
    ids.feedback[1],
    seed.userId,
    seed.personId,
    seed.personName,
    seed.mobile,
    '\u4eba\u8138\u5b9e\u540d\u8ba4\u8bc1\u62cd\u7167\u63d0\u793a\u4e0d\u591f\u6e05\u6670',
    '\u5efa\u8bae\u5728\u4e0a\u4f20\u8eab\u4efd\u8bc1\u81ea\u62cd\u9875\u8865\u5145\u793a\u4f8b\u56fe\u548c\u906e\u6321\u63d0\u9192\u3002',
    seed.mobile,
    '0',
    seed.userName,
    '2026-06-09 10:18:00',
    null,
    null,
    '\u5f85\u5ba2\u670d\u56de\u8bbf',
    '0'
  ]
];

const complaintRows = [
  [
    ids.complaint[0],
    seed.userId,
    seed.personId,
    seed.personName,
    seed.enterpriseId,
    seed.enterpriseName,
    'salary',
    '5\u6708\u52a0\u73ed\u8d39\u6838\u7b97\u6709\u8bef',
    '5\u670828\u65e5\u81f35\u670831\u65e5\u591c\u73ed\u52a0\u73ed\u65f6\u957f\u4e0e\u5de5\u8d44\u6761\u8bb0\u5f55\u4e0d\u4e00\u81f4\uff0c\u7533\u8bf7\u590d\u6838\u3002',
    seed.mobile,
    '0',
    '1',
    JSON.stringify([{ name: 'overtime-proof.jpg', url: 'https://cdn.ygb.local/mock/overtime-proof.jpg' }]),
    '1',
    '\u4f01\u4e1a\u5df2\u53d7\u7406\uff0c\u6b63\u5728\u6838\u5bf9\u8003\u52e4\u4e0e\u5de5\u65f6\u53f0\u8d26\u3002',
    '\u9884\u8ba1 2 \u4e2a\u5de5\u4f5c\u65e5\u5185\u53cd\u9988',
    seed.userName,
    '2026-06-05 18:00:00',
    'union-worker',
    '2026-06-06 09:10:00',
    '\u5f85\u4f01\u4e1a\u590d\u6838',
    '0'
  ],
  [
    ids.complaint[1],
    seed.userId,
    seed.personId,
    seed.personName,
    seed.enterpriseId,
    seed.enterpriseName,
    'accommodation',
    '\u5bbf\u820d\u7a7a\u8c03\u62a5\u4fee\u8fdb\u5ea6\u6162',
    '3\u680b402\u5bbf\u820d\u7a7a\u8c03\u8fde\u7eed\u4e24\u5929\u65e0\u6cd5\u5236\u51b7\uff0c\u591c\u73ed\u4f11\u606f\u53d7\u5f71\u54cd\u3002',
    seed.mobile,
    '1',
    '0',
    null,
    '0',
    null,
    null,
    seed.userName,
    '2026-06-09 08:32:00',
    null,
    null,
    '\u533f\u540d\u6295\u8bc9\u6837\u4f8b',
    '0'
  ]
];

const consultRows = [
  [
    ids.consult[0],
    seed.userId,
    seed.personId,
    seed.personName,
    seed.enterpriseId,
    seed.enterpriseName,
    'contract',
    '\u52b3\u52a1\u5408\u540c\u5230\u671f\u7eed\u7b7e\u662f\u5426\u5fc5\u987b\u63d0\u524d\u901a\u77e5',
    '\u6211\u7684\u52b3\u52a1\u5408\u540c 6 \u6708\u5e95\u5230\u671f\uff0c\u5982\u679c\u4f01\u4e1a\u4e0d\u7eed\u7b7e\uff0c\u5e73\u53f0\u901a\u5e38\u63d0\u524d\u591a\u4e45\u901a\u77e5\uff0c\u662f\u5426\u9700\u8981\u4e66\u9762\u8bf4\u660e\uff1f',
    seed.mobile,
    null,
    '1',
    '\u5efa\u8bae\u5728\u5408\u540c\u5230\u671f\u524d 30 \u5929\u5173\u6ce8\u5e73\u53f0\u901a\u77e5\uff0c\u5e76\u4fdd\u7559\u5c97\u4f4d\u8c03\u6574\u6216\u4e0d\u7eed\u7b7e\u7684\u4e66\u9762\u8bb0\u5f55\u3002',
    '2026-06-08 15:30',
    seed.userName,
    '2026-06-07 14:12:00',
    'lawyer-on-duty',
    '2026-06-08 15:30:00',
    '\u5df2\u7b54\u590d',
    '0'
  ],
  [
    ids.consult[1],
    seed.userId,
    seed.personId,
    seed.personName,
    seed.enterpriseId,
    seed.enterpriseName,
    'injury',
    '\u8f7b\u5fae\u5de5\u4f24\u5c31\u533b\u6750\u6599\u600e\u4e48\u7559\u5b58',
    '\u5982\u679c\u53d1\u751f\u8f7b\u5fae\u70eb\u4f24\uff0c\u95e8\u8bca\u5c31\u533b\u65f6\u9700\u8981\u4fdd\u7559\u54ea\u4e9b\u6750\u6599\uff0c\u540e\u7eed\u7533\u8bf7\u5de5\u4f24\u8ba4\u5b9a\u624d\u6bd4\u8f83\u5b8c\u6574\uff1f',
    seed.mobile,
    null,
    '0',
    null,
    null,
    seed.userName,
    '2026-06-09 12:06:00',
    null,
    null,
    '\u5f85\u503c\u73ed\u5f8b\u5e08\u7b54\u590d',
    '0'
  ]
];

const activityJoinRows = [
  [
    ids.activityJoin[0],
    'monthly-discount',
    seed.userId,
    seed.personId,
    seed.personName,
    seed.mobile,
    '1',
    seed.userName,
    '2026-06-03 10:00:00',
    'system',
    '2026-06-03 10:00:00',
    '{"activityKey":"monthly-discount","rewardTitle":"6\\u6708\\u57f9\\u8bad\\u6ee1\\u52e4\\u7acb\\u51cf\\u91d1","status":"joined","joinedAt":"2026-06-03 10:00:00"}',
    '0'
  ],
  [
    ids.activityJoin[1],
    'summer-care',
    seed.userId,
    seed.personId,
    seed.personName,
    seed.mobile,
    '0',
    seed.userName,
    '2026-06-09 09:25:00',
    null,
    null,
    '{"activityKey":"summer-care","rewardTitle":"\\u590f\\u5b63\\u5173\\u6000\\u793c\\u5305","status":"pending","joinedAt":"2026-06-09 09:25:00"}',
    '0'
  ]
];

const videoProgressRows = [
  [ids.videoProgress[0], 'safety-helmet', seed.userId, seed.personId, 96, 180, '0', '2026-06-08 20:10:00', '2026-06-08 20:25:00', '0'],
  [ids.videoProgress[1], 'heatstroke-first-aid', seed.userId, seed.personId, 240, 240, '1', '2026-06-06 19:30:00', '2026-06-06 19:34:00', '0']
];

const settingRow = [
  ids.setting,
  seed.userId,
  seed.personId,
  '1',
  'test-client-910001',
  'authorized',
  'h5',
  seed.userName,
  '2026-06-09 16:20:00',
  seed.userName,
  '2026-06-09 16:20:00',
  'worker \u6d4b\u8bd5\u63a8\u9001\u914d\u7f6e',
  '0'
];

const goodsRows = [
  [ids.goods[0], 'phone-coupon-10', '10\u5143\u8bdd\u8d39\u5238', '\u5145\u503c\u5230\u8d26\u524d\u8bf7\u6838\u5bf9\u624b\u673a\u53f7', 100.0, 'coupon', '1', 200, 1, 'system', '2026-06-01 09:00:00', 'system', '2026-06-01 09:00:00', 'worker \u79ef\u5206\u5546\u54c1', '0'],
  [ids.goods[1], 'summer-kit', '\u6e05\u51c9\u9632\u6691\u793c\u5305', '\u542b\u6bdb\u5dfe\u3001\u6e05\u51c9\u8d34\u548c\u4fbf\u643a\u6c34\u676f', 180.0, 'physical', '1', 80, 2, 'system', '2026-06-01 09:05:00', 'system', '2026-06-01 09:05:00', 'worker \u79ef\u5206\u5546\u54c1', '0'],
  [ids.goods[2], 'meal-coupon-20', '20\u5143\u9910\u8865\u5238', '\u9650\u5408\u4f5c\u98df\u5802\u6838\u9500\u4f7f\u7528', 150.0, 'coupon', '1', 120, 3, 'system', '2026-06-01 09:10:00', 'system', '2026-06-01 09:10:00', 'worker \u79ef\u5206\u5546\u54c1', '0']
];

const ledgerRows = [
  [ids.ledger[0], seed.userId, seed.personId, seed.personName, 'IN', '\u5b8c\u6210\u9632\u6691\u57f9\u8bad', '\u5b8c\u6210\u89c6\u9891\u300a\u590f\u5b63\u9ad8\u6e29\u9632\u62a4\u4e0e\u6025\u6551\u300b\u83b7\u5f97\u79ef\u5206', 30.0, 30.0, 'system', '2026-06-06 19:35:00', null, null, '\u57f9\u8bad\u5956\u52b1', '0'],
  [ids.ledger[1], seed.userId, seed.personId, seed.personName, 'IN', '\u6708\u5ea6\u7b7e\u5230\u6ee1\u52e4', '6\u6708\u7b2c\u4e00\u5468\u7b7e\u5230\u6ee1\u52e4\u5956\u52b1', 80.0, 110.0, 'system', '2026-06-07 08:30:00', null, null, '\u7b7e\u5230\u5956\u52b1', '0'],
  [ids.ledger[2], seed.userId, seed.personId, seed.personName, 'OUT', '\u5151\u636210\u5143\u8bdd\u8d39\u5238', '\u79ef\u5206\u5546\u57ce\u5151\u6362\u6210\u529f', -100.0, 10.0, seed.userName, '2026-06-08 12:10:00', null, null, '\u79ef\u5206\u5151\u6362', '0'],
  [ids.ledger[3], seed.userId, seed.personId, seed.personName, 'IN', '\u63d0\u4ea4\u5c97\u4f4d\u5efa\u8bae', '\u53cd\u9988\u88ab\u91c7\u7eb3\uff0c\u8865\u53d1 40 \u79ef\u5206', 40.0, 50.0, 'admin', '2026-06-08 18:20:00', null, null, '\u8fd0\u8425\u8865\u5206', '0']
];

const exchangeRows = [
  [ids.exchange[0], seed.userId, seed.personId, seed.personName, 'phone-coupon-10', '10\u5143\u8bdd\u8d39\u5238', 'coupon', 100.0, '1', '\u77ed\u4fe1\u5238\u7801\u5df2\u4e0b\u53d1\u5230 13700010001', seed.userName, '2026-06-08 12:10:00', null, null, '\u5df2\u5151\u6362', '0'],
  [ids.exchange[1], seed.userId, seed.personId, seed.personName, 'summer-kit', '\u6e05\u51c9\u9632\u6691\u793c\u5305', 'physical', 180.0, '0', '\u5f85\u4ed3\u5e93\u914d\u8d27', seed.userName, '2026-06-09 13:20:00', null, null, '\u5f85\u53d1\u8d27\u6837\u4f8b', '0']
];

const uploadRows = [
  [ids.upload[0], seed.userId, seed.personId, seed.personName, 'idcard_front', '\u8eab\u4efd\u8bc1\u4eba\u50cf\u9762', 'https://cdn.ygb.local/mock/idcard-front-10001.jpg', 'idcard-front-10001.jpg', '\u8eab\u4efd\u8bc1\u6b63\u9762.jpg', 245120, 'image/jpeg', 'camera', seed.userName, '2026-06-09 09:40:00', null, null, '\u5b9e\u540d\u8ba4\u8bc1\u7d20\u6750', '0'],
  [ids.upload[1], seed.userId, seed.personId, seed.personName, 'idcard_back', '\u8eab\u4efd\u8bc1\u56fd\u5fbd\u9762', 'https://cdn.ygb.local/mock/idcard-back-10001.jpg', 'idcard-back-10001.jpg', '\u8eab\u4efd\u8bc1\u53cd\u9762.jpg', 231552, 'image/jpeg', 'camera', seed.userName, '2026-06-09 09:41:00', null, null, '\u5b9e\u540d\u8ba4\u8bc1\u7d20\u6750', '0'],
  [ids.upload[2], seed.userId, seed.personId, seed.personName, 'selfie', '\u624b\u6301\u8bc1\u4ef6\u81ea\u62cd', 'https://cdn.ygb.local/mock/selfie-10001.jpg', 'selfie-10001.jpg', '\u5b9e\u540d\u8ba4\u8bc1\u81ea\u62cd.jpg', 318220, 'image/jpeg', 'camera', seed.userName, '2026-06-09 09:42:00', null, null, '\u5b9e\u540d\u8ba4\u8bc1\u7d20\u6750', '0']
];

const pushRecordRow = [
  ids.pushRecord[0],
  seed.userId,
  seed.personId,
  seed.personName,
  'trace-910001-20260609',
  'test-client-****0001',
  'h5',
  'authorized',
  '1',
  'mock-gateway',
  'https://push.ygb.local/mock/send',
  '/pages/message/index',
  '{"tab":"notice"}',
  '\u67e5\u770b\u6d88\u606f',
  '\u63a8\u9001\u8054\u8c03',
  '{"title":"\\u6d4b\\u8bd5\\u63a8\\u9001","body":"\\u60a8\\u6709\\u4e00\\u6761\\u65b0\\u7684\\u7ad9\\u5185\\u6d88\\u606f"}',
  '{"code":200,"msg":"mock success"}',
  'SUCCESS',
  '\u672c\u5730\u8054\u8c03\u63a8\u9001\u6210\u529f',
  'system',
  '2026-06-09 16:10:00',
  null,
  null,
  'worker \u63a8\u9001\u8054\u8c03\u6837\u4f8b',
  '0'
];

const noticeMessageRows = [
  [
    ids.noticeMessage[0],
    seed.userId,
    seed.personId,
    seed.personName,
    'BUSINESS',
    '\u7b80\u5386\u5df2\u5b8c\u5584\uff0c\u53ef\u7ee7\u7eed\u6295\u9012\u5c97\u4f4d',
    '\u5f53\u524d\u7b80\u5386\u5b8c\u6574\u5ea6\u6ee1\u8db3\u6295\u9012\u8981\u6c42\uff0c\u53ef\u524d\u5f80\u5c97\u4f4d\u5217\u8868\u7ee7\u7eed\u6295\u9012\u3002',
    '\u60a8\u5df2\u8865\u9f50\u671f\u671b\u5c97\u4f4d\u3001\u6280\u80fd\u6807\u7b7e\u548c\u8bc1\u4e66\u4fe1\u606f\uff0c\u7cfb\u7edf\u5df2\u5f00\u653e\u5c97\u4f4d\u6295\u9012\u80fd\u529b\u3002',
    'resume-complete',
    String(ids.resume),
    '/pages/profile/resume',
    '{"from":"notice","scene":"resume"}',
    '\u67e5\u770b\u7b80\u5386',
    '\u7b80\u5386\u4e2d\u5fc3',
    '1',
    '2026-06-09 16:32:00',
    'system',
    '2026-06-09 16:31:00',
    'system',
    '2026-06-09 16:32:00',
    '\u5df2\u8bfb\u6837\u4f8b',
    '0'
  ],
  [
    ids.noticeMessage[1],
    seed.userId,
    seed.personId,
    seed.personName,
    'TRAINING',
    '\u9632\u6691\u8bfe\u7a0b\u5b66\u4e60\u8fdb\u5ea6\u5df2\u540c\u6b65',
    '\u300a\u590f\u5b63\u9ad8\u6e29\u9632\u62a4\u4e0e\u6025\u6551\u300b\u5df2\u5b8c\u6210\uff0c\u53ef\u524d\u5f80\u79ef\u5206\u9875\u67e5\u770b\u5956\u52b1\u3002',
    '\u60a8\u5df2\u5b8c\u6210\u9632\u6691\u8bfe\u7a0b\u5b66\u4e60\uff0c\u79ef\u5206\u5df2\u5230\u8d26\u3002\u9ad8\u6e29\u4f5c\u4e1a\u524d\u8bf7\u518d\u6b21\u786e\u8ba4\u8865\u6c34\u3001\u901a\u98ce\u548c\u8f6e\u4f11\u5b89\u6392\u3002',
    'course-progress',
    'heatstroke-course',
    '/pages/training/course-detail',
    '{"courseKey":"heatstroke-course"}',
    '\u7ee7\u7eed\u5b66\u4e60',
    '\u5b89\u5168\u57f9\u8bad',
    '0',
    null,
    'system',
    '2026-06-09 11:00:00',
    null,
    null,
    '\u672a\u8bfb\u6837\u4f8b',
    '0'
  ],
  [
    ids.noticeMessage[2],
    seed.userId,
    seed.personId,
    seed.personName,
    'ACTIVITY',
    '\u590f\u5b63\u5173\u6000\u793c\u5305\u62a5\u540d\u6210\u529f',
    '\u60a8\u5df2\u63d0\u4ea4\u6d3b\u52a8\u62a5\u540d\uff0c\u5ba1\u6838\u901a\u8fc7\u540e\u5c06\u53d1\u653e\u793c\u5305\u3002',
    '\u7cfb\u7edf\u5df2\u6536\u5230\u60a8\u7684\u6d3b\u52a8\u62a5\u540d\u4fe1\u606f\uff0c\u8bf7\u4fdd\u6301\u8054\u7cfb\u65b9\u5f0f\u7545\u901a\uff0c\u793c\u5305\u53d1\u653e\u7ed3\u679c\u5c06\u901a\u8fc7\u7ad9\u5185\u6d88\u606f\u901a\u77e5\u3002',
    'activity-join',
    'summer-care',
    '/pages/activity/detail',
    '{"activityKey":"summer-care"}',
    '\u67e5\u770b\u6d3b\u52a8',
    '\u798f\u5229\u6d3b\u52a8',
    '0',
    null,
    'system',
    '2026-06-09 09:26:00',
    null,
    null,
    '\u6d3b\u52a8\u6d88\u606f\u6837\u4f8b',
    '0'
  ]
];

const realnameApplyRow = [
  ids.realnameApply[0],
  seed.userId,
  seed.personId,
  seed.personName,
  seed.mobile,
  '440101199401011234',
  '0',
  null,
  'https://cdn.ygb.local/mock/idcard-front-10001.jpg',
  'https://cdn.ygb.local/mock/idcard-back-10001.jpg',
  'https://cdn.ygb.local/mock/selfie-10001.jpg',
  'worker-uniapp',
  seed.userName,
  '2026-06-09 09:45:00',
  seed.userName,
  '2026-06-09 09:45:00',
  '\u5f85\u5ba1\u6838\u5b9e\u540d\u8ba4\u8bc1\u7533\u8bf7',
  '0'
];

const jobApplyRows = [
  [ids.jobApply[0], 1, seed.userId, seed.personId, seed.personName, seed.mobile, '1', '2026-06-08 16:20:00', seed.userName, '2026-06-08 16:20:00', 'hr-1001', '2026-06-08 18:00:00', '\u5df2\u8fdb\u5165\u9762\u8bd5\u6c9f\u901a', '0'],
  [ids.jobApply[1], 2, seed.userId, seed.personId, seed.personName, seed.mobile, '0', '2026-06-09 14:05:00', seed.userName, '2026-06-09 14:05:00', null, null, '\u5f85\u67e5\u770b', '0']
];

const portalContentRows = [
  [ids.portalContent[0], 'ygb', 'worker_activity', 'monthly-discount', '6\u6708\u57f9\u8bad\u6ee1\u52e4\u7acb\u51cf\u91d1', '\u5b8c\u6210\u5b89\u5168\u57f9\u8bad\u5e76\u4fdd\u6301\u826f\u597d\u51fa\u52e4\uff0c\u53ef\u53c2\u4e0e\u6708\u5ea6\u7acb\u51cf\u91d1\u6d3b\u52a8\u3002', '\u9762\u5411\u5728\u5c97\u84dd\u9886\u5de5\u53cb\u7684\u6708\u5ea6\u798f\u5229\u6d3b\u52a8\uff0c\u5b8c\u6210\u6307\u5b9a\u8bfe\u7a0b\u5e76\u4fdd\u6301\u8003\u52e4\u5408\u89c4\u5373\u53ef\u53c2\u4e0e\u3002', 'https://cdn.ygb.local/mock/activity-monthly.jpg', 'https://activity.ygb.local/monthly-discount', '\u7ca4\u5de5\u5b9d\u6d3b\u52a8\u4e2d\u5fc3', '2026-06-01 08:00:00', 1, '0', '{"activityKey":"monthly-discount","ruleList":["\\u5b8c\\u6210\\u672c\\u6708\\u6307\\u5b9a\\u5b89\\u5168\\u57f9\\u8bad","\\u672c\\u6708\\u65e0\\u4e25\\u91cd\\u8fdd\\u7eaa\\u548c\\u7f3a\\u52e4\\u8bb0\\u5f55","\\u5956\\u52b1\\u7ed3\\u679c\\u4ee5\\u7ad9\\u5185\\u901a\\u77e5\\u4e3a\\u51c6"],"rewardTitle":"\\u6700\\u9ad8188\\u5143\\u7acb\\u51cf\\u91d1","eligibilityText":"\\u57f9\\u8bad\\u5b8c\\u6210\\u5e76\\u901a\\u8fc7\\u6821\\u9a8c\\u540e\\u81ea\\u52a8\\u53c2\\u4e0e","deliveryText":"\\u5956\\u52b1\\u5c06\\u5728\\u6708\\u672b\\u7edf\\u4e00\\u53d1\\u653e","externalUrl":"https://activity.ygb.local/monthly-discount"}', 'system', '2026-06-01 08:00:00', 'system', '2026-06-01 08:00:00', 'worker \u6d3b\u52a8\u5185\u5bb9', '0'],
  [ids.portalContent[1], 'ygb', 'worker_activity', 'summer-care', '\u590f\u5b63\u5173\u6000\u793c\u5305', '\u9ad8\u6e29\u5b63\u8282\u4e3a\u4e00\u7ebf\u5de5\u53cb\u53d1\u653e\u6e05\u51c9\u793c\u5305\u3002', '\u793c\u5305\u5305\u542b\u6bdb\u5dfe\u3001\u6e05\u51c9\u8d34\u3001\u9632\u6691\u624b\u518c\uff0c\u5ba1\u6838\u901a\u8fc7\u540e\u7edf\u4e00\u53d1\u653e\u3002', 'https://cdn.ygb.local/mock/activity-summer.jpg', 'https://activity.ygb.local/summer-care', '\u7ca4\u5de5\u5b9d\u6d3b\u52a8\u4e2d\u5fc3', '2026-06-05 09:00:00', 2, '0', '{"activityKey":"summer-care","ruleList":["\\u5b8c\\u6210\\u5b9e\\u540d\\u8ba4\\u8bc1","\\u5f53\\u524d\\u5728\\u5c97\\u72b6\\u6001\\u6b63\\u5e38","\\u6bcf\\u4eba\\u9650\\u98861\\u4efd"],"rewardTitle":"\\u590f\\u5b63\\u5173\\u6000\\u793c\\u5305","eligibilityText":"\\u5ba1\\u6838\\u901a\\u8fc7\\u540e\\u53d1\\u653e","deliveryText":"\\u793c\\u5305\\u5c06\\u6309\\u4f01\\u4e1a\\u6279\\u6b21\\u914d\\u9001"}', 'system', '2026-06-05 09:00:00', 'system', '2026-06-05 09:00:00', 'worker \u6d3b\u52a8\u5185\u5bb9', '0'],
  [ids.portalContent[2], 'ygb', 'worker_video', 'safety-helmet', '\u5b89\u5168\u5e3d\u89c4\u8303\u4f69\u6234', '\u8fdb\u573a\u524d 3 \u5206\u949f\u638c\u63e1\u5b89\u5168\u5e3d\u4f69\u6234\u4e0e\u68c0\u67e5\u8981\u70b9\u3002', '\u8bb2\u89e3\u5e3d\u886c\u8c03\u8282\u3001\u4e0b\u988c\u5e26\u56fa\u5b9a\u548c\u7834\u635f\u8bc6\u522b\u3002', 'https://cdn.ygb.local/mock/video-helmet-cover.jpg', 'https://cdn.ygb.local/mock/video-helmet.mp4', '\u7ca4\u5de5\u5b9d\u5b89\u5168\u8bfe\u5802', '2026-06-02 08:30:00', 1, '0', '{"videoKey":"safety-helmet","durationSeconds":180,"videoUrl":"https://cdn.ygb.local/mock/video-helmet.mp4","posterUrl":"https://cdn.ygb.local/mock/video-helmet-cover.jpg","sourceText":"\\u73ed\\u524d\\u5b89\\u5168\\u5fae\\u8bfe","keyPoints":["\\u8fdb\\u5165\\u5de5\\u4f4d\\u524d\\u68c0\\u67e5\\u5e3d\\u58f3\\u548c\\u5e3d\\u886c","\\u9ad8\\u7a7a\\u548c\\u6d41\\u52a8\\u5c97\\u4f4d\\u5fc5\\u987b\\u56fa\\u5b9a\\u4e0b\\u988c\\u5e26","\\u53d1\\u73b0\\u7834\\u635f\\u6216\\u8fc7\\u671f\\u7acb\\u5373\\u66f4\\u6362"],"fallbackTips":["\\u65e0\\u7f51\\u65f6\\u53ef\\u5148\\u9605\\u8bfb\\u5173\\u952e\\u8981\\u70b9","\\u73ed\\u7ec4\\u957f\\u53ef\\u7ec4\\u7ec7\\u7ebf\\u4e0b\\u590d\\u8bad"]}', 'system', '2026-06-02 08:30:00', 'system', '2026-06-02 08:30:00', 'worker \u89c6\u9891\u5185\u5bb9', '0'],
  [ids.portalContent[3], 'ygb', 'worker_video', 'heatstroke-first-aid', '\u590f\u5b63\u9ad8\u6e29\u9632\u62a4\u4e0e\u6025\u6551', '\u5b66\u4e60\u9ad8\u6e29\u4f5c\u4e1a\u8865\u6c34\u3001\u8f6e\u4f11\u4e0e\u4e2d\u6691\u6025\u6551\u6b65\u9aa4\u3002', '\u9002\u7528\u4e8e\u5236\u9020\u3001\u914d\u9001\u548c\u6237\u5916\u573a\u666f\u7684\u4e00\u7ebf\u9632\u6691\u57f9\u8bad\u3002', 'https://cdn.ygb.local/mock/video-heatstroke-cover.jpg', 'https://cdn.ygb.local/mock/video-heatstroke.mp4', '\u7ca4\u5de5\u5b9d\u5b89\u5168\u8bfe\u5802', '2026-06-03 09:00:00', 2, '0', '{"videoKey":"heatstroke-first-aid","durationSeconds":240,"videoUrl":"https://cdn.ygb.local/mock/video-heatstroke.mp4","posterUrl":"https://cdn.ygb.local/mock/video-heatstroke-cover.jpg","sourceText":"\\u9ad8\\u6e29\\u5b63\\u4e13\\u9879\\u57f9\\u8bad","keyPoints":["\\u73ed\\u524d\\u8865\\u6c34\\u4e0e\\u9632\\u6652\\u68c0\\u67e5","\\u51fa\\u73b0\\u5934\\u6655\\u6076\\u5fc3\\u65f6\\u7acb\\u5373\\u8f6c\\u79fb\\u9634\\u51c9\\u533a","\\u5fc5\\u8981\\u65f6\\u8054\\u7cfb\\u73b0\\u573a\\u6025\\u6551\\u5458\\u548c120"],"fallbackTips":["\\u53ef\\u5148\\u9605\\u8bfb\\u6025\\u6551\\u6d41\\u7a0b\\u56fe","\\u5b8c\\u6210\\u5b66\\u4e60\\u540e\\u4f1a\\u540c\\u6b65\\u79ef\\u5206"]}', 'system', '2026-06-03 09:00:00', 'system', '2026-06-03 09:00:00', 'worker \u89c6\u9891\u5185\u5bb9', '0'],
  [ids.portalContent[4], 'ygb', 'worker_ai_training', 'injury-ai', '\u5de5\u4f24 AI \u5b66\u4e60\u52a9\u624b', '\u6839\u636e\u5c97\u4f4d\u98ce\u9669\u548c\u57f9\u8bad\u8bb0\u5f55\uff0c\u7ed9\u51fa\u4e2a\u6027\u5316\u5b66\u4e60\u5efa\u8bae\u3002', '\u7ed3\u5408\u5de5\u4f24\u9884\u9632\u3001\u8bc1\u636e\u7559\u5b58\u548c\u7533\u8bc9\u6d41\u7a0b\uff0c\u5e2e\u52a9\u5de5\u53cb\u5feb\u901f\u5b9a\u4f4d\u5b66\u4e60\u91cd\u70b9\u3002', 'https://cdn.ygb.local/mock/ai-training-cover.jpg', '', '\u7ca4\u5de5\u5b9d AI \u5b66\u4e60', '2026-06-04 09:00:00', 1, '0', '{"highlights":["\\u7ed3\\u5408\\u5c97\\u4f4d\\u98ce\\u9669\\u751f\\u6210\\u5efa\\u8bae","\\u8865\\u9f50\\u5de5\\u4f24\\u8bc1\\u636e\\u7559\\u5b58\\u6e05\\u5355","\\u8054\\u52a8\\u6cd5\\u5f8b\\u63f4\\u52a9\\u4e0e\\u57f9\\u8bad\\u8bfe\\u7a0b"],"entryPath":"/pages/ai-training/detail"}', 'system', '2026-06-04 09:00:00', 'system', '2026-06-04 09:00:00', 'worker AI \u57f9\u8bad', '0'],
  [ids.portalContent[5], 'ygb', 'worker_training_quiz', 'quiz-safety-1', '\u9ad8\u6e29\u4f5c\u4e1a\u524d\u6700\u9700\u8981\u4f18\u5148\u68c0\u67e5\u4ec0\u4e48\uff1f', '\u5355\u9009\u9898', '\u8bf7\u9009\u62e9\u6700\u7b26\u5408\u73b0\u573a\u89c4\u5b9a\u7684\u4e00\u9879\u3002', '', '', '\u7ca4\u5de5\u5b9d\u9898\u5e93', '2026-06-04 10:00:00', 1, '0', '{"questionId":"quiz-safety-1","options":["\\u5148\\u67e5\\u770b\\u624b\\u673a\\u6d88\\u606f","\\u5148\\u786e\\u8ba4\\u996e\\u6c34\\u3001\\u9632\\u6652\\u548c\\u901a\\u98ce\\u6761\\u4ef6","\\u5148\\u53bb\\u9886\\u9910\\u8865"],"answerIndex":1}', 'system', '2026-06-04 10:00:00', 'system', '2026-06-04 10:00:00', 'worker \u57f9\u8bad\u9898\u5e93', '0'],
  [ids.portalContent[6], 'ygb', 'worker_training_quiz', 'quiz-rights-1', '\u5de5\u8d44\u6761\u53d1\u73b0\u5f02\u5e38\uff0c\u7b2c\u4e00\u6b65\u5efa\u8bae\u505a\u4ec0\u4e48\uff1f', '\u5355\u9009\u9898', '\u8bf7\u9009\u62e9\u6700\u7a33\u59a5\u7684\u4e00\u9879\u3002', '', '', '\u7ca4\u5de5\u5b9d\u9898\u5e93', '2026-06-04 10:05:00', 2, '0', '{"questionId":"quiz-rights-1","options":["\\u76f4\\u63a5\\u79bb\\u804c","\\u5148\\u4fdd\\u5b58\\u5de5\\u8d44\\u6761\\u548c\\u8003\\u52e4\\u8bc1\\u636e\\uff0c\\u518d\\u53d1\\u8d77\\u54a8\\u8be2\\u6216\\u6295\\u8bc9","\\u5ffd\\u7565\\u4e0d\\u7ba1"],"answerIndex":1}', 'system', '2026-06-04 10:05:00', 'system', '2026-06-04 10:05:00', 'worker \u57f9\u8bad\u9898\u5e93', '0'],
  [ids.portalContent[7], 'ygb', 'worker_training_course', 'heatstroke-course', '\u9ad8\u6e29\u4f5c\u4e1a\u9632\u62a4\u8bfe\u7a0b', '\u8986\u76d6\u8865\u6c34\u3001\u8f6e\u4f11\u3001\u9632\u6652\u548c\u4e2d\u6691\u6025\u6551\u7684\u57fa\u7840\u8bfe\u7a0b\u3002', '\u9002\u5408\u5236\u9020\u4e1a\u3001\u914d\u9001\u548c\u6237\u5916\u5c97\u4f4d\u7684\u4e00\u7ebf\u5de5\u53cb\u3002', 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80', '', '\u7ca4\u5de5\u5b9d\u57f9\u8bad\u4e2d\u5fc3', '2026-06-04 11:00:00', 1, '0',     '{"courseKey":"heatstroke-course","durationSeconds":600,"videoUrl":"https://www.w3schools.com/html/mov_bbb.mp4","posterUrl":"https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80","outlineList":["\\u73ed\\u524d\\u9ad8\\u6e29\\u98ce\\u9669\\u68c0\\u67e5","\\u8865\\u6c34\\u4e0e\\u8f6e\\u4f11\\u5b89\\u6392","\\u4e2d\\u6691\\u6025\\u6551\\u4e0e\\u4e0a\\u62a5\\u6d41\\u7a0b"]}', 'system', '2026-06-04 11:00:00', 'system', '2026-06-04 11:00:00', 'worker \u57f9\u8bad\u8bfe\u7a0b', '0'],
  [ids.portalContent[8], 'ygb', 'worker_training_course', 'rights-course', '\u5de5\u8d44\u4e0e\u5408\u540c\u6743\u76ca\u8bc6\u522b', '\u5e2e\u52a9\u5de5\u53cb\u5feb\u901f\u8bc6\u522b\u5de5\u8d44\u3001\u8003\u52e4\u548c\u5408\u540c\u4e2d\u7684\u5e38\u89c1\u98ce\u9669\u3002', '\u9002\u7528\u4e8e\u5165\u804c\u3001\u7eed\u7b7e\u548c\u5f02\u8bae\u7533\u8bc9\u573a\u666f\u3002', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=1200&q=80', '', '\u7ca4\u5de5\u5b9d\u57f9\u8bad\u4e2d\u5fc3', '2026-06-04 11:10:00', 2, '0', '{"courseKey":"rights-course","durationSeconds":720,"videoUrl":"https://www.w3schools.com/html/movie.mp4","posterUrl":"https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=1200&q=80","outlineList":["\\u5de5\\u8d44\\u6761\\u91cd\\u70b9\\u5b57\\u6bb5","\\u5408\\u540c\\u5230\\u671f\\u4e0e\\u7eed\\u7b7e\\u63d0\\u9192","\\u7ef4\\u6743\\u8bc1\\u636e\\u7559\\u5b58\\u6e05\\u5355"]}', 'system', '2026-06-04 11:10:00', 'system', '2026-06-04 11:10:00', 'worker \u57f9\u8bad\u8bfe\u7a0b', '0'],
  [ids.portalContent[9], 'ygb', 'union_contract', 'contract-renewal', '\u5408\u540c\u7eed\u7b7e\u63d0\u9192', '\u7eed\u7b7e\u524d\u5148\u6838\u5bf9\u5c97\u4f4d\u3001\u5de5\u65f6\u548c\u5de5\u8d44\u7ec4\u6210\u3002', '\u7eed\u7b7e\u65f6\u91cd\u70b9\u770b\u5c97\u4f4d\u540d\u79f0\u3001\u8bd5\u7528\u671f\u3001\u5de5\u65f6\u5236\u5ea6\u3001\u5de5\u8d44\u6784\u6210\u548c\u8fdd\u7ea6\u6761\u6b3e\u3002', '', '', '\u7ca4\u5de5\u5b9d\u5de5\u4f1a\u670d\u52a1', '2026-06-05 11:30:00', 1, '0', '{"typeLabel":"\\u5408\\u540c\\u670d\\u52a1","articleKey":"cms-910010"}', 'system', '2026-06-05 11:30:00', 'system', '2026-06-05 11:30:00', '\u5de5\u4f1a\u5408\u540c\u5185\u5bb9', '0'],
  [ids.portalContent[10], 'ygb', 'union_contract', 'attendance-proof', '\u8003\u52e4\u5f02\u5e38\u8bc1\u636e\u5982\u4f55\u7559\u5b58', '\u5de5\u8d44\u4e89\u8bae\u524d\uff0c\u5148\u56fa\u5b9a\u8003\u52e4\u548c\u6c9f\u901a\u8bc1\u636e\u3002', '\u5efa\u8bae\u4fdd\u5b58\u6392\u73ed\u622a\u56fe\u3001\u6253\u5361\u8bb0\u5f55\u3001\u73ed\u7ec4\u901a\u77e5\u548c\u5de5\u8d44\u6761\uff0c\u5fc5\u8981\u65f6\u540c\u6b65\u4e91\u7aef\u3002', '', '', '\u7ca4\u5de5\u5b9d\u5de5\u4f1a\u670d\u52a1', '2026-06-05 11:40:00', 2, '0', '{"typeLabel":"\\u8bc1\\u636e\\u7559\\u5b58","articleKey":"cms-910011"}', 'system', '2026-06-05 11:40:00', 'system', '2026-06-05 11:40:00', '\u5de5\u4f1a\u5408\u540c\u5185\u5bb9', '0'],
  [ids.portalContent[11], 'ygb', 'legal_faq', 'faq-salary', '\u5de5\u8d44\u6761\u548c\u5b9e\u53d1\u5de5\u8d44\u4e0d\u4e00\u81f4\u600e\u4e48\u529e\uff1f', '\u5148\u6838\u5bf9\u5de5\u65f6\u3001\u8865\u8d34\u548c\u6263\u6b3e\u9879\u3002', '\u82e5\u53d1\u73b0\u5f02\u5e38\uff0c\u5148\u4fdd\u5b58\u5de5\u8d44\u6761\u3001\u8003\u52e4\u548c\u6c9f\u901a\u8bb0\u5f55\uff0c\u518d\u901a\u8fc7\u6cd5\u5f8b\u54a8\u8be2\u6216\u6295\u8bc9\u5165\u53e3\u5904\u7406\u3002', '', '', '\u7ca4\u5de5\u5b9d\u6cd5\u5f8b\u670d\u52a1', '2026-06-05 12:00:00', 1, '0', '{"typeLabel":"\\u5de5\\u8d44\\u6743\\u76ca"}', 'system', '2026-06-05 12:00:00', 'system', '2026-06-05 12:00:00', '\u6cd5\u5f8b FAQ', '0'],
  [ids.portalContent[12], 'ygb', 'legal_faq', 'faq-injury', '\u8f7b\u5fae\u5de5\u4f24\u5c31\u533b\u540e\u8981\u4fdd\u7559\u54ea\u4e9b\u6750\u6599\uff1f', '\u95e8\u8bca\u75c5\u5386\u3001\u7968\u636e\u548c\u73b0\u573a\u8bb0\u5f55\u90fd\u8981\u4fdd\u7559\u3002', '\u5efa\u8bae\u4fdd\u7559\u75c5\u5386\u3001\u8d39\u7528\u7968\u636e\u3001\u73b0\u573a\u7167\u7247\u3001\u73ed\u7ec4\u8bc1\u660e\u548c\u4e0e\u4f01\u4e1a\u6c9f\u901a\u8bb0\u5f55\uff0c\u65b9\u4fbf\u540e\u7eed\u7533\u62a5\u3002', '', '', '\u7ca4\u5de5\u5b9d\u6cd5\u5f8b\u670d\u52a1', '2026-06-05 12:10:00', 2, '0', '{"typeLabel":"\\u5de5\\u4f24\\u5904\\u7406"}', 'system', '2026-06-05 12:10:00', 'system', '2026-06-05 12:10:00', '\u6cd5\u5f8b FAQ', '0'],
  [ids.portalContent[13], 'ygb', 'legal_faq', 'faq-contract', '\u5408\u540c\u5230\u671f\u672a\u7eed\u7b7e\u8fd8\u80fd\u7ee7\u7eed\u4e0a\u5c97\u5417\uff1f', '\u8981\u5148\u786e\u8ba4\u4f01\u4e1a\u901a\u77e5\u548c\u5f53\u524d\u6392\u73ed\u5b89\u6392\u3002', '\u5982\u679c\u5408\u540c\u5230\u671f\u4f46\u4ecd\u6301\u7eed\u5b89\u6392\u4e0a\u5c97\uff0c\u5efa\u8bae\u5c3d\u5feb\u901a\u8fc7\u5e73\u53f0\u786e\u8ba4\u7eed\u7b7e\u72b6\u6001\u5e76\u4fdd\u7559\u901a\u77e5\u8bb0\u5f55\u3002', '', '', '\u7ca4\u5de5\u5b9d\u6cd5\u5f8b\u670d\u52a1', '2026-06-05 12:20:00', 3, '0', '{"typeLabel":"\\u5408\\u540c\\u6743\\u76ca"}', 'system', '2026-06-05 12:20:00', 'system', '2026-06-05 12:20:00', '\u6cd5\u5f8b FAQ', '0']
];

const sysNoticeRows = [
  [9001, '6\u6708\u5de5\u8d44\u6761\u5df2\u5f00\u653e\u67e5\u8be2', '1', '\u60a8\u53ef\u5728\u5de5\u4eba\u7aef\u201c\u85aa\u8d44\u201d\u9875\u9762\u67e5\u770b 2026 \u5e74 5 \u6708\u5de5\u8d44\u6761\u660e\u7ec6\uff0c\u5982\u53d1\u73b0\u5f02\u5e38\u8bf7\u53ca\u65f6\u63d0\u4ea4\u53cd\u9988\u3002', '0', 'system', '2026-06-08 09:00:00', 'system', '2026-06-08 09:00:00', '\u5de5\u8d44\u6761\u901a\u77e5'],
  [9002, '\u5b9e\u540d\u8ba4\u8bc1\u8d44\u6599\u5f85\u5ba1\u6838', '1', '\u60a8\u63d0\u4ea4\u7684\u5b9e\u540d\u8ba4\u8bc1\u8d44\u6599\u5df2\u8fdb\u5165\u5ba1\u6838\u961f\u5217\uff0c\u9884\u8ba1 1 \u4e2a\u5de5\u4f5c\u65e5\u5185\u5b8c\u6210\uff0c\u8bf7\u4fdd\u6301\u7167\u7247\u6e05\u6670\u53ef\u8fa8\u3002', '0', 'system', '2026-06-09 09:50:00', 'system', '2026-06-09 09:50:00', '\u5b9e\u540d\u8ba4\u8bc1\u901a\u77e5'],
  [9003, '\u9632\u6691\u57f9\u8bad\u672c\u5468\u4e0a\u7ebf', '2', '\u672c\u5468\u65b0\u589e\u300a\u590f\u5b63\u9ad8\u6e29\u9632\u62a4\u4e0e\u6025\u6551\u300b\u8bfe\u7a0b\uff0c\u5b8c\u6210\u540e\u53ef\u83b7\u5f97\u79ef\u5206\u5956\u52b1\u3002', '0', 'system', '2026-06-06 10:20:00', 'system', '2026-06-06 10:20:00', '\u57f9\u8bad\u901a\u77e5'],
  [9004, '\u5de5\u4f1a\u670d\u52a1\u70ed\u7ebf\u63d0\u9192', '2', '\u5982\u9047\u5de5\u8d44\u3001\u5408\u540c\u6216\u5de5\u4f24\u95ee\u9898\uff0c\u53ef\u4f18\u5148\u5728\u5de5\u4eba\u7aef\u53d1\u8d77\u6cd5\u5f8b\u54a8\u8be2\uff0c\u5fc5\u8981\u65f6\u8054\u7cfb\u5de5\u4f1a\u670d\u52a1\u70ed\u7ebf\u3002', '0', 'system', '2026-06-05 15:00:00', 'system', '2026-06-05 15:00:00', '\u5de5\u4f1a\u670d\u52a1\u63d0\u9192']
];

async function executeMany(conn, sql, rows) {
  for (const row of rows) {
    await conn.execute(sql, row);
  }
}

async function main() {
  const conn = await connectWithRetry();

  try {
    await conn.execute('SET NAMES utf8mb4');
    await conn.beginTransaction();

    await conn.execute(`DELETE FROM ygb_worker_job_apply WHERE apply_id IN (${ids.jobApply.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_realname_apply WHERE apply_id IN (${ids.realnameApply.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_notice_message WHERE message_id IN (${ids.noticeMessage.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_push_test_record WHERE record_id IN (${ids.pushRecord.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_upload_record WHERE upload_id IN (${ids.upload.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_point_exchange WHERE exchange_id IN (${ids.exchange.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_point_ledger WHERE ledger_id IN (${ids.ledger.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_point_goods WHERE goods_id IN (${ids.goods.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_setting WHERE setting_id = ? OR user_id = ?`, [ids.setting, seed.userId]);
    await conn.execute(`DELETE FROM ygb_worker_video_progress WHERE progress_id IN (${ids.videoProgress.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_activity_join WHERE join_id IN (${ids.activityJoin.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_legal_consult WHERE consult_id IN (${ids.consult.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_complaint WHERE complaint_id IN (${ids.complaint.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_feedback WHERE feedback_id IN (${ids.feedback.join(',')})`);
    await conn.execute(`DELETE FROM ygb_worker_resume WHERE resume_id = ? OR user_id = ?`, [ids.resume, seed.userId]);
    await conn.execute(`DELETE FROM ygb_portal_content WHERE content_id IN (${ids.portalContent.join(',')})`);
    await conn.execute(`DELETE FROM sys_notice WHERE notice_id IN (${ids.sysNotice.join(',')})`);

    await conn.execute(`
      INSERT INTO ygb_worker_resume (
        resume_id, user_id, person_id, person_name, mobile, job_type, expected_job, expected_city, expected_salary,
        skill_tags, certificate_text, intro, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, resumeRow);

    await executeMany(conn, `
      INSERT INTO ygb_worker_feedback (
        feedback_id, user_id, person_id, person_name, mobile, title, content, contact_mobile, status,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, feedbackRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_complaint (
        complaint_id, user_id, person_id, person_name, enterprise_id, enterprise_name, complaint_type, title,
        content, contact_mobile, anonymous_flag, sync_union_flag, attachments, status, reply_content, handle_time_text,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, complaintRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_legal_consult (
        consult_id, user_id, person_id, person_name, enterprise_id, enterprise_name, consult_type, title,
        content, contact_mobile, attachments, status, reply_content, reply_time_text,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, consultRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_activity_join (
        join_id, activity_key, user_id, person_id, person_name, mobile, status, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, activityJoinRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_video_progress (
        progress_id, video_key, user_id, person_id, watched_seconds, total_seconds, completed_flag, create_time, update_time, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, videoProgressRows);

    await conn.execute(`
      INSERT INTO ygb_worker_setting (
        setting_id, user_id, person_id, notify_enabled, push_client_id, notification_permission, push_platform,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, settingRow);

    await executeMany(conn, `
      INSERT INTO ygb_worker_point_goods (
        goods_id, goods_key, goods_name, goods_desc, required_score, goods_type, status, stock_count, sort_num,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, goodsRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_point_ledger (
        ledger_id, user_id, person_id, person_name, change_type, title, summary, score_delta, balance_after,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, ledgerRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_point_exchange (
        exchange_id, user_id, person_id, person_name, goods_key, goods_name, goods_type, score_cost, exchange_status,
        delivery_remark, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, exchangeRows);

    await executeMany(conn, `
      INSERT INTO ygb_worker_upload_record (
        upload_id, user_id, person_id, person_name, category_code, category_name, file_url, file_name, original_filename,
        file_size, content_type, source_module, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, uploadRows);

    await conn.execute(`
      INSERT INTO ygb_worker_push_test_record (
        record_id, user_id, person_id, person_name, trace_id, push_client_id_masked, push_platform, notification_permission,
        notify_enabled, gateway_provider, gateway_url, target_path, target_query_text, action_label, source_label,
        request_body, response_body, test_status, status_message, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, pushRecordRow);

    await executeMany(conn, `
      INSERT INTO ygb_worker_notice_message (
        message_id, user_id, person_id, person_name, message_type, title, summary, content, biz_type, biz_id,
        jump_path, jump_query_text, action_label, source_label, read_flag, read_time,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, noticeMessageRows);

    await conn.execute(`
      INSERT INTO ygb_worker_realname_apply (
        apply_id, user_id, person_id, person_name, mobile, id_card, apply_status, reject_reason,
        id_card_front_url, id_card_back_url, selfie_url, source_module,
        create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, realnameApplyRow);

    await executeMany(conn, `
      INSERT INTO ygb_worker_job_apply (
        apply_id, job_id, user_id, person_id, person_name, mobile, status, apply_time, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, jobApplyRows);

    await executeMany(conn, `
      INSERT INTO ygb_portal_content (
        content_id, portal_code, section_code, category_code, title, summary, content, cover_url, link_url, source_name,
        publish_time, sort_order, status, extra_json, create_by, create_time, update_by, update_time, remark, del_flag
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, portalContentRows);

    await executeMany(conn, `
      INSERT INTO sys_notice (
        notice_id, notice_title, notice_type, notice_content, status, create_by, create_time, update_by, update_time, remark
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `, sysNoticeRows);

    await conn.commit();

    const [verifyRows] = await conn.query(`
      SELECT
        r.person_name,
        HEX(r.person_name) AS person_name_hex,
        r.expected_job,
        HEX(r.expected_job) AS expected_job_hex,
        m.title AS notice_title,
        HEX(m.title) AS notice_title_hex
      FROM ygb_worker_resume r
      LEFT JOIN ygb_worker_notice_message m ON m.message_id = 910001
      WHERE r.resume_id = 910001
      LIMIT 1
    `);
    console.log(JSON.stringify(verifyRows, null, 2));
  } catch (error) {
    try {
      await conn.rollback();
    } catch (rollbackError) {
      // ignore rollback error
    }
    throw error;
  } finally {
    await conn.end();
  }
}

async function connectWithRetry() {
  let lastError;
  for (let attempt = 1; attempt <= retryTimes; attempt += 1) {
    try {
      return await mysql.createConnection(dbConfig);
    } catch (error) {
      lastError = error;
      console.error(`mysql connect attempt ${attempt}/${retryTimes} failed: ${error.message}`);
      if (attempt < retryTimes) {
        await new Promise((resolve) => setTimeout(resolve, retryDelayMs));
      }
    }
  }
  throw lastError;
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
