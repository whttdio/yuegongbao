import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class EnsureGlobalAdmin {
    private static final long ROLE_ID = 990001L;
    private static final long USER_ID = 990001L;
    private static final String USERNAME = "globaladmin";
    private static final String PASSWORD_HASH = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2";

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: <url> <user> <password>");
        }

        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2])) {
            connection.setAutoCommit(false);
            try {
                Set<String> roleColumns = columns(connection, "sys_role");
                Set<String> roleMenuColumns = columns(connection, "sys_role_menu");

                long deptId = queryLong(connection, "select coalesce((select dept_id from sys_user where user_name='admin' limit 1), (select min(dept_id) from sys_dept), 100)");

                upsertRole(connection, roleColumns);
                upsertUser(connection, deptId);
                upsertUserRole(connection);
                upsertRoleMenus(connection, roleMenuColumns);

                connection.commit();
                System.out.println("global-admin-ready user=globaladmin password=admin123 role=global_admin roleId=" + ROLE_ID + " userId=" + USER_ID);
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    private static Set<String> columns(Connection connection, String tableName) throws Exception {
        Set<String> result = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "select column_name from information_schema.columns where table_schema = database() and table_name = ?")) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString(1).toLowerCase());
                }
            }
        }
        return result;
    }

    private static long queryLong(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static void upsertRole(Connection connection, Set<String> columns) throws Exception {
        if (columns.contains("allowed_portal_scope") && columns.contains("default_portal_code")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "insert into sys_role (role_id, role_name, role_key, role_sort, allowed_portal_scope, default_portal_code, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) " +
                    "values (?, '全局管理员', 'global_admin', 1, 'both', 'ygb', '1', 1, 1, '0', '0', 'admin', sysdate(), '', null, '全局功能账号角色，默认密码：admin123') " +
                    "on duplicate key update role_name=values(role_name), role_key=values(role_key), role_sort=values(role_sort), allowed_portal_scope=values(allowed_portal_scope), default_portal_code=values(default_portal_code), data_scope=values(data_scope), status='0', del_flag='0', update_by='admin', update_time=sysdate(), remark=values(remark)")) {
                ps.setLong(1, ROLE_ID);
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = connection.prepareStatement(
                    "insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) " +
                    "values (?, '全局管理员', 'global_admin', 1, '1', 1, 1, '0', '0', 'admin', sysdate(), '', null, '全局功能账号角色，默认密码：admin123') " +
                    "on duplicate key update role_name=values(role_name), role_key=values(role_key), role_sort=values(role_sort), data_scope=values(data_scope), status='0', del_flag='0', update_by='admin', update_time=sysdate(), remark=values(remark)")) {
                ps.setLong(1, ROLE_ID);
                ps.executeUpdate();
            }
        }
    }

    private static void upsertUser(Connection connection, long deptId) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement(
                "insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark) " +
                "values (?, ?, ?, '全局管理员', '00', 'globaladmin@yuegongbao.local', '13999000001', '1', '', ?, '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '全局管理员账号；默认密码：admin123') " +
                "on duplicate key update dept_id=values(dept_id), nick_name=values(nick_name), user_type='00', email=values(email), phonenumber=values(phonenumber), password=values(password), status='0', del_flag='0', update_by='admin', update_time=sysdate(), remark=values(remark)")) {
            ps.setLong(1, USER_ID);
            ps.setLong(2, deptId);
            ps.setString(3, USERNAME);
            ps.setString(4, PASSWORD_HASH);
            ps.executeUpdate();
        }
    }

    private static void upsertUserRole(Connection connection) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement(
                "insert ignore into sys_user_role (user_id, role_id) values (?, ?)")) {
            ps.setLong(1, USER_ID);
            ps.setLong(2, ROLE_ID);
            ps.executeUpdate();
        }
    }

    private static void upsertRoleMenus(Connection connection, Set<String> columns) throws Exception {
        if (columns.contains("portal_scope")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "insert ignore into sys_role_menu (role_id, menu_id, portal_scope) select ?, menu_id, 'both' from sys_menu where status = '0'")) {
                ps.setLong(1, ROLE_ID);
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = connection.prepareStatement(
                    "insert ignore into sys_role_menu (role_id, menu_id) select ?, menu_id from sys_menu where status = '0'")) {
                ps.setLong(1, ROLE_ID);
                ps.executeUpdate();
            }
        }
    }
}
