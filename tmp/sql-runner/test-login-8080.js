const http = require("http");
const redis = require("redis");
const client = redis.createClient({ host: "127.0.0.1", port: 6379 });
function req(path, body, headers) {
  headers = headers || {};
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = { hostname: "localhost", port: 8080, path: path, method: body ? "POST" : "GET", headers: Object.assign({ "Content-Type": "application/json", "X-Portal-Code": "ygb" }, headers, data ? { "Content-Length": Buffer.byteLength(data) } : {}) };
    const r = http.request(options, res => { let c=""; res.on("data",d=>c+=d); res.on("end",()=>resolve({status:res.statusCode, body:c})); });
    r.on("error", reject);
    if (data) r.write(data);
    r.end();
  });
}
client.get = require("util").promisify(client.get).bind(client);
(async () => {
  await new Promise((res) => client.on("ready", res));
  const cap = JSON.parse((await req("/captchaImage")).body);
  const key = "captcha_codes:" + cap.uuid;
  const raw = await client.get(key);
  console.log("raw bytes", raw);
  const attempts = [raw, raw && raw.replace(/^"|"$/g, ""), raw && JSON.parse(raw)];
  for (const code of attempts) {
    if (code == null) continue;
    const login = JSON.parse((await req("/login", { username: "admin", password: "admin123", code: String(code), uuid: cap.uuid, portalCode: "ygb" })).body);
    console.log("try", code, login.msg, login.code);
    if (login.code === 200) {
      const info = JSON.parse((await req("/getInfo", null, { Authorization: "Bearer " + login.token })).body);
      console.log("getInfo", info.code, info.msg, "roles", info.roles);
      break;
    }
    const cap2 = JSON.parse((await req("/captchaImage")).body);
    cap.uuid = cap2.uuid;
  }
  client.quit();
})();
