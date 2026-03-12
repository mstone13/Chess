package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            var createAuthsTable =
            """
           CREATE TABLE IF NOT EXISTS auths (
              auth VARCHAR(225) NOT NULL PRIMARY KEY,
              username VARCHAR(50) NOT NULL
            )
           """;
            try (var ps = conn.prepareStatement(createAuthsTable)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to create tables", e);
        }

        authDAO = new SQLAuthDAO();
        authDAO.clearAuths();
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        authDAO.clearAuths();
    }

    @Test
    void clearAuthsSuccess() throws DataAccessException {
        AuthData authData = new AuthData("FakeToken", "Username");
        authDAO.createAuth(authData);

        authDAO.clearAuths();
        assertNull(authDAO.getAuth("FakeToken"));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        AuthData authData = new AuthData("Token", "User");
        authDAO.createAuth(authData);

        authDAO.deleteAuth(authData);
        assertNull(authDAO.getAuth("Token"));
    }

    @Test
    void deleteAuthFailure() {
        AuthData authData = new AuthData("Fake", "Example");
        assertThrows(DataAccessException.class, () -> {
           authDAO.deleteAuth(authData);
        });
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        AuthData authData = new AuthData("NewAuth", "NewUser");
        authDAO.createAuth(authData);

        AuthData retrievedAuth = authDAO.getAuth(authData.authToken());
        assertNotNull(retrievedAuth);
        assertEquals("NewAuth", retrievedAuth.authToken());
        assertEquals("NewUser", retrievedAuth.username());
    }

    @Test
    void getAuthFailure() throws DataAccessException {
        assertNull(authDAO.getAuth("FakeAuth"));
    }

    @Test
    void getAuthFailureWrongAuth() throws DataAccessException {
        AuthData authData = new AuthData("AuthReal", "RealUser");
        authDAO.createAuth(authData);

        assertNull(authDAO.getAuth("AuthFake"));
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        assertNull(authDAO.getAuth("NewAuth"));

        AuthData authData = new AuthData("NewAuth", "NewUser");
        authDAO.createAuth(authData);

        AuthData newAuth = authDAO.getAuth("NewAuth");
        assertEquals("NewAuth", newAuth.authToken());
        assertEquals("NewUser", newAuth.username());
    }

    @Test
    void createAuthFailure() throws DataAccessException {
        AuthData authData = new AuthData("First", "FirstUser");
        authDAO.createAuth(authData);

        AuthData secondAuthData = new AuthData("First", "SecondUser");
        assertThrows(DataAccessException.class, () -> {
           authDAO.createAuth(secondAuthData);
        });
    }
}
