package dataaccess;

import model.UserData;
import java.sql.*;
import static dataaccess.DatabaseManager.executeUpdate;

public class SQLUserDAO implements UserDAO {

    private static final String TABLE_NAME = "users";

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM " + TABLE_NAME + " WHERE username=?";
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
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO " + TABLE_NAME + " (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE TABLE " + TABLE_NAME;
        executeUpdate(statement);
    }
}
