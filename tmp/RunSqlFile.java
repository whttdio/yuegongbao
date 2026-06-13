import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RunSqlFile {
    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            throw new IllegalArgumentException("Usage: <jdbcUrl> <username> <password> <sqlFile> <encoding>");
        }
        String jdbcUrl = args[0];
        String username = args[1];
        String password = args[2];
        String sqlFile = args[3];
        String encoding = args[4];

        String sql = Files.readString(Path.of(sqlFile), java.nio.charset.Charset.forName(encoding));
        String[] statements = sql.split("(?m);\\s*$");

        int executed = 0;
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                for (String part : statements) {
                    String trimmed = part.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                    statement.execute(trimmed);
                    executed++;
                }
            }
            connection.commit();
        }

        System.out.println("EXECUTED_STATEMENTS=" + executed);
        System.out.println("SQL_FILE=" + Path.of(sqlFile).toAbsolutePath());
    }
}
