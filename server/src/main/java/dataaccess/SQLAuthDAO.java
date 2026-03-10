package dataaccess;

import model.AuthData;

import java.sql.*;

import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    private final String TABLE_NAME = "auths";

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM " + TABLE_NAME + " WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
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
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO " + TABLE_NAME + " (authToken, username) VALUES (?, ?, ?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public void clearAuths() throws DataAccessException {
        String statement = "TRUNCATE TABLE " + TABLE_NAME;
        executeUpdate(statement);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        String statement = "DELETE FROM " + TABLE_NAME + " WHERE authToken=?";
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param == null) ps.setNull(i + 1, NULL);
            }

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute update: " + statement, e);
        }
    }
}
