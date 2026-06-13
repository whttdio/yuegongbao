const fs = require("fs");
const mysql = require("mysql2/promise");

async function main() {
  const sql = fs.readFileSync("d:/javaproject/yuegongbao/YueGongBao-Vue/sql/ygb_phase12_ygb_official_portal.sql", "utf8");
  const statements = sql.split(/;\s*\n/).map(s => s.trim()).filter(s => s && !s.startsWith("--"));
  const conn = await mysql.createConnection({
    host: "127.0.0.1",
    port: 3306,
    user: "root",
    password: "root",
    database: "yuegongbao",
    multipleStatements: true
  });
  try {
    for (const stmt of statements) {
      const [result] = await conn.query(stmt);
      console.log("OK:", stmt.split("\n")[0].slice(0, 80));
      if (result && result.affectedRows !== undefined) {
        console.log("  affectedRows:", result.affectedRows);
      }
    }
    const [rows] = await conn.query("SELECT menu_id, menu_name, path, component, portal_scope FROM sys_menu WHERE menu_id = 5");
    console.log("VERIFY menu:", JSON.stringify(rows));
    const [roleRows] = await conn.query("SELECT role_id, menu_id FROM sys_role_menu WHERE menu_id = 5 ORDER BY role_id");
    console.log("VERIFY role_menu:", JSON.stringify(roleRows));
  } finally {
    await conn.end();
  }
}

main().catch(err => { console.error("FAILED:", err.message); process.exit(1); });
