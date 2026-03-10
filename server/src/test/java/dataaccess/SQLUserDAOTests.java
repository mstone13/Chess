package dataaccess;

import dataaccess.SQLUserDAO;
import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            var createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(50) NOT NULL PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL
            )
        """;
            try (var ps = conn.prepareStatement(createUsersTable)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to create tables", e);
        }

        userDAO = new SQLUserDAO();
        userDAO.clearUsers();
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        userDAO.clearUsers();
    }

    @Test
    void clearUsersSuccess() throws DataAccessException {
        UserData user = new UserData("Andrew", "Hozier", "hoz@email.com");
        userDAO.createUser(user);

        userDAO.clearUsers();
        assertNull(userDAO.getUser("Andrew"), "User should be gone after clear");
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("Example", "Password", "email@gmail.com");
        userDAO.createUser(user);

        //second User
        UserData secondUser = new UserData("Bad", "Example", "notgood@gmail.com");
        userDAO.createUser(secondUser);

        UserData retrievedUser = userDAO.getUser(user.username());
        assertNotNull(retrievedUser);
        assertEquals("Example", retrievedUser.username());
        assertEquals("Password", retrievedUser.password());
        assertEquals("email@gmail.com", retrievedUser.email());
    }

    @Test
    void getUserFailure() throws DataAccessException {
        assertNull(userDAO.getUser("FakeUsername"), "User does not exist");
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        assertNull(userDAO.getUser("NewUser"), "No user should appear yet");

        UserData user = new UserData("NewUser", "NewPass", "new@email.com");
        userDAO.createUser(user);

        UserData newUser = userDAO.getUser("NewUser");
        assertEquals("NewUser", newUser.username());
        assertEquals("NewPass", newUser.password());
        assertEquals("new@email.com", newUser.email());
    }

    @Test
    void createUserFailure() throws DataAccessException {
        UserData firstUser = new UserData("First", "Pass", "first@gmail.com");
        userDAO.createUser(firstUser);

        UserData secondUser = new UserData("First", "secondPass", "second@gmail.com");
        assertThrows(DataAccessException.class, () -> {
           userDAO.createUser(secondUser);
        });
    }
}
