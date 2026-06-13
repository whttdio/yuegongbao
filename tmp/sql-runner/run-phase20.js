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
  await conn.query(fs.readFileSync('../../sql/ygb_phase20_portal_content_enrich.sql', 'utf8'))
  const [rows] = await conn.query(
    "select portal_code, section_code, count(*) as cnt, sum(case when content is not null and content <> '' then 1 else 0 end) as with_content from ygb_portal_content where del_flag='0' group by portal_code, section_code order by portal_code, section_code"
  )
  console.table(rows)
  await conn.end()
})().catch((e) => {
  console.error(e.message)
  process.exit(1)
})
