const http = require("http");
function req(prefix, path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = { hostname: "localhost", port: 80, path: prefix + path, method: body ? "POST" : "GET", headers: Object.assign({ "Content-Type": "application/json", "X-Portal-Code": "ygb" }, data ? { "Content-Length": Buffer.byteLength(data) } : {}) };
    const r = http.request(options, res => { let c=""; res.on("data",d=>c+=d); res.on("end",()=>resolve({status:res.statusCode, ct:res.headers["content-type"], body:c.slice(0,200)})); });
    r.on("error", reject);
    if (data) r.write(data);
    r.end();
  });
}
(async () => {
  console.log("dev login", await req("/dev-api", "/login", {username:"admin",password:"admin123",code:"",uuid:"",portalCode:"ygb"}));
  console.log("prod login", await req("/prod-api", "/login", {username:"admin",password:"admin123",code:"",uuid:"",portalCode:"ygb"}));
})();
