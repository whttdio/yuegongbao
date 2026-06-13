import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryGlobalAdmin {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement statement = connection.createStatement()) {
            print(statement, "select u.user_id, u.user_name, u.nick_name, u.status, u.del_flag, r.role_id, r.role_name, r.role_key, r.data_scope from sys_user u join sys_user_role ur on u.user_id=ur.user_id join sys_role r on ur.role_id=r.role_id where u.user_name='globaladmin'");
            print(statement, "select count(*) as menu_count from sys_role_menu where role_id=990001");
        }
    }

    private static void print(Statement statement, String sql) throws Exception {
        try (ResultSet rs = statement.executeQuery(sql)) {
            int columns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder line = new StringBuilder();
                for (int i = 1; i <= columns; i++) {
                    if (i > 1) {
                        line.append(" | ");
                    }
                    line.append(rs.getMetaData().getColumnLabel(i)).append("=").append(rs.getString(i));
                }
                System.out.println(line);
            }
        }
    }
}
