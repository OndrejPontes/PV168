package security.db;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtils {

    private static String[] readSqlStatements(InputStream is) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read ", ex);
        }
    }

    public static void executeSqlScript(DataSource ds, InputStream is) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            for (String sqlStatement : readSqlStatements(is)) {
                if (!sqlStatement.trim().isEmpty()) {
                    try (PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        }
    }
}
