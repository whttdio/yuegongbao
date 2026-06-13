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
  await conn.query(fs.readFileSync('../../sql/ygb_phase19_portal_news_content.sql', 'utf8'))
  const [rows] = await conn.query(
    "select content_id, title, char_length(content) as content_len from ygb_portal_content where section_code='news' and portal_code='ygb'"
  )
  console.log(rows)
  await conn.end()
})().catch((e) => {
  console.error(e.message)
  process.exit(1)
})
