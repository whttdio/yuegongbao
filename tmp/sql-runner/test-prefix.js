const http = require("http");
function req(path, body, prefix) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = { hostname: "localhost", port: 80, path: prefix + path, method: body ? "POST" : "GET", headers: { "Content-Type": "application/json", "X-Portal-Code": "ygb", ...(data ? { "Content-Length": Buffer.byteLength(data) } : {}) } };
    const r = http.request(options, res => { let c=""; res.on("data",d=>c+=d); res.on("end",()=>resolve({status:res.statusCode, ct:res.headers["content-type"], body:c})); });
    r.on("error", reject);
    if (data) r.write(data);
    r.end();
  });
}
(async () => {
  for (const prefix of ["/dev-api", "/prod-api"]) {
    const cap = await req("/captchaImage", null, prefix);
    console.log("prefix", prefix, "status", cap.status, "ct", cap.ct, "starts", cap.body.slice(0,40));
  }
})();
