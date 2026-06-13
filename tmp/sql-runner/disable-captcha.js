const http = require('http')
const mysql = require('mysql2/promise')
const redis = require('redis')

function req(path, body, headers = {}) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null
    const options = {
      hostname: 'localhost',
      port: 80,
      path: '/dev-api' + path,
      method: body ? 'POST' : 'GET',
      headers: Object.assign(
        { 'Content-Type': 'application/json', 'X-Portal-Code': 'ygb' },
        headers,
        data ? { 'Content-Length': Buffer.byteLength(data) } : {}
      )
    }
    const r = http.request(options, res => {
      let c = ''
      res.on('data', d => { c += d })
      res.on('end', () => resolve({ status: res.statusCode, body: c }))
    })
    r.on('error', reject)
    if (data) r.write(data)
    r.end()
  })
}

function redisSet(key, value) {
  const client = redis.createClient({ host: '127.0.0.1', port: 6379 })
  const setAsync = require('util').promisify(client.set).bind(client)
  return setAsync(key, value).finally(() => client.quit())
}

async function main() {
  const conn = await mysql.createConnection({
    host: '127.0.0.1',
    port: 3306,
    user: 'root',
    password: 'root',
    database: 'yuegongbao'
  })
  try {
    await conn.query("UPDATE sys_config SET config_value = 'false' WHERE config_key = 'sys.account.captchaEnabled'")
    await redisSet('sys_config:sys.account.captchaEnabled', '"false"')

    const login = JSON.parse((await req('/login', {
      username: 'admin',
      password: 'admin123',
      code: '',
      uuid: '',
      portalCode: 'ygb'
    })).body)
    console.log('login:', login.code, login.msg || 'ok', login.token ? 'has token' : 'no token')

    if (login.code === 200) {
      const info = JSON.parse((await req('/getInfo', null, { Authorization: 'Bearer ' + login.token })).body)
      console.log('getInfo:', info.code, info.msg || 'ok', 'roles:', info.roles)
      const routers = JSON.parse((await req('/getRouters', null, { Authorization: 'Bearer ' + login.token })).body)
      console.log('getRouters:', routers.code, 'menus:', (routers.data || []).length)
    }

    await conn.query("UPDATE sys_config SET config_value = 'true' WHERE config_key = 'sys.account.captchaEnabled'")
    await redisSet('sys_config:sys.account.captchaEnabled', '"true"')
  } finally {
    await conn.end()
  }
}

main().catch(err => {
  console.error('FAILED:', err.message)
  process.exit(1)
})
