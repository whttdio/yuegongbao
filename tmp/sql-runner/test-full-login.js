const http = require("http");
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
(async () => {
  const cap = JSON.parse((await req("/captchaImage")).body);
  console.log("uuid", cap.uuid);
  for (const code of ["3","0","1","2","4","5","6","7","8","9","10","11","12"]) {
    const login = JSON.parse((await req("/login", { username: "admin", password: "admin123", code: code, uuid: cap.uuid, portalCode: "ygb" })).body);
    if (login.code === 200) {
      console.log("LOGIN OK code", code, "token", (login.token || "").slice(0,20));
      const info = await req("/getInfo", null, { Authorization: "Bearer " + login.token });
      console.log("getInfo", info.status, info.body.slice(0,500));
      const routers = await req("/getRouters", null, { Authorization: "Bearer " + login.token });
      console.log("getRouters", routers.status, routers.body.slice(0,500));
      return;
    }
    if (login.msg !== "\u9a8c\u8bc1\u7801\u9519\u8bef" && login.msg !== "验证码错误") {
      console.log("failed code", code, login);
      return;
    }
  }
  console.log("no success with same uuid");
})();
