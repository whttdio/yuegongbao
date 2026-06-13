const http = require("http");
const redis = require("redis");
const client = redis.createClient({ host: "127.0.0.1", port: 6379 });
function req(path, body, headers) {
  headers = headers || {};
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = { hostname: "localhost", port: 80, path: "/dev-api" + path, method: body ? "POST" : "GET", headers: Object.assign({ "Content-Type": "application/json", "X-Portal-Code": "ygb" }, headers, data ? { "Content-Length": Buffer.byteLength(data) } : {}) };
    const r = http.request(options, res => { let c=""; res.on("data",d=>c+=d); res.on("end",()=>resolve({status:res.statusCode, body:c})); });
    r.on("error", reject);
    if (data) r.write(data);
    r.end();
  });
}
client.on("error", err => console.error("redis error", err.message));
client.get = require("util").promisify(client.get).bind(client);
(async () => {
  await new Promise((res, rej) => client.on("ready", res));
  const cap = JSON.parse((await req("/captchaImage")).body);
  const code = await client.get("captcha_codes:" + cap.uuid);
  console.log("uuid", cap.uuid, "redis code", code);
  const login = JSON.parse((await req("/login", { username: "admin", password: "admin123", code: code, uuid: cap.uuid, portalCode: "ygb" })).body);
  console.log("login", JSON.stringify(login).slice(0,300));
  if (login.code === 200) {
    const info = JSON.parse((await req("/getInfo", null, { Authorization: "Bearer " + login.token })).body);
    console.log("getInfo code", info.code, "roles", info.roles, "allowed", info.allowedPortalCodes, "effective", info.effectivePortalCode);
    const routers = JSON.parse((await req("/getRouters", null, { Authorization: "Bearer " + login.token })).body);
    console.log("getRouters code", routers.code, "count", (routers.data || []).length);
  }
  client.quit();
})();
