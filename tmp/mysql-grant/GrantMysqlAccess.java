import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class GrantMysqlAccess {
    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            throw new IllegalArgumentException("Usage: <url> <adminUser> <adminPassword> <appUser> <appPassword>");
        }

        String url = args[0];
        String adminUser = args[1];
        String adminPassword = args[2];
        String appUser = args[3];
        String appPassword = args[4];

        try (Connection connection = DriverManager.getConnection(url, adminUser, adminPassword);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE USER IF NOT EXISTS '" + appUser + "'@'%' IDENTIFIED BY '" + appPassword + "'");
            statement.execute("GRANT ALL PRIVILEGES ON yuegongbao.* TO '" + appUser + "'@'%'");
            statement.execute("FLUSH PRIVILEGES");
            System.out.println("grant-complete");
        }
    }
}
