const http = require("http");
function req(path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = { hostname: "localhost", port: 8080, path, method: body ? "POST" : "GET", headers: { "Content-Type": "application/json", "X-Portal-Code": "ygb", ...(data ? { "Content-Length": Buffer.byteLength(data) } : {}) } };
    const r = http.request(options, res => { let c=""; res.on("data",d=>c+=d); res.on("end",()=>resolve({status:res.statusCode, body:c})); });
    r.on("error", reject);
    if (data) r.write(data);
    r.end();
  });
}
(async () => {
  const cap = JSON.parse((await req("/captchaImage")).body);
  console.log("captcha uuid:", cap.uuid);
  const login = await req("/login", { username: "admin", password: "admin123", code: "3", uuid: cap.uuid, portalCode: "ygb" });
  console.log("login status:", login.status, login.body.slice(0, 300));
})();
