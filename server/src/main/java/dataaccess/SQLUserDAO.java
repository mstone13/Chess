package dataaccess;

import model.UserData;
import java.sql.*;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email, json FROM users WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            }
        }  catch (SQLException e) {
            throw new DataAccessException("Error retrieving user", e);
        }

        return null;
    }

    @Override
    public void createUser(UserData user) {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(
                statement,
                user.username(),
                user.password(),
                user.email()
        );
    }

    @Override
    public void clearUsers() {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {

                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];

                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }

                ps.executeUpdate();
                return 0;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
