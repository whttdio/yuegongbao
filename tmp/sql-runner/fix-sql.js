const mysql = require("mysql2/promise");

async function main() {
  const conn = await mysql.createConnection({
    host: "127.0.0.1", port: 3306, user: "root", password: "root", database: "yuegongbao"
  });
  try {
    const [cols] = await conn.query("SHOW COLUMNS FROM sys_menu LIKE 'portal_scope'");
    console.log("portal_scope column:", JSON.stringify(cols));
    const [menus] = await conn.query("SELECT menu_id, menu_name, path FROM sys_menu WHERE menu_id IN (4,5)");
    console.log("existing menus:", JSON.stringify(menus));
    const sql = `INSERT INTO sys_menu (
      menu_id, menu_name, parent_id, order_num, path, component, route_name,
      is_frame, is_cache, menu_type, visible, status, perms, icon,
      create_by, create_time, remark, portal_scope
    )
    SELECT
      5, '粤工保官网', 0, 5, 'ygbOfficialSite', 'portal/ygb-official/index', 'YgbOfficialSite',
      0, 0, 'C', '0', '0', '', 'guide',
      'admin', NOW(), '粤工保官网入口（新窗口打开）', 'ygb'
    FROM dual
    WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 5)`;
    const [result] = await conn.query(sql);
    console.log("insert result:", JSON.stringify(result));
    const [verify] = await conn.query("SELECT menu_id, menu_name, path, component, portal_scope FROM sys_menu WHERE menu_id = 5");
    console.log("verify menu:", JSON.stringify(verify));
    const [role1] = await conn.query("INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 5 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 5)");
    console.log("role1 insert:", JSON.stringify(role1));
    const [roles] = await conn.query("SELECT role_id, menu_id FROM sys_role_menu WHERE menu_id = 5 ORDER BY role_id");
    console.log("role_menu:", JSON.stringify(roles));
  } finally {
    await conn.end();
  }
}
main().catch(err => { console.error("FAILED:", err.message); process.exit(1); });
