const fs = require('fs')
const mysql = require('mysql2/promise')

;(async () => {
  const conn = await mysql.createConnection({
    host: '127.0.0.1',
    port: 3306,
    user: 'root',
    password: 'root',
    database: 'yuegongbao',
    multipleStatements: true
  })
  for (const file of [
    '../../sql/ygb_phase17_portal_azb_cms.sql',
    '../../sql/ygb_phase18_worker_job_admin.sql'
  ]) {
    await conn.query(fs.readFileSync(file, 'utf8'))
    console.log('ok', file)
  }
  const [azb] = await conn.query("select count(*) as c from ygb_portal_content where portal_code='azb'")
  const [menu] = await conn.query('select menu_id, menu_name from sys_menu where menu_id=4200')
  console.log('azb content', azb[0].c, 'menu', menu)
  await conn.end()
})().catch((e) => {
  console.error(e.message)
  process.exit(1)
})
