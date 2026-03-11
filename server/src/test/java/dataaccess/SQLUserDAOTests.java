package dataaccess;

import model.UserData;
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
        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("Andrew");
        });
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("Example", "Password", "email@gmail.com");
        userDAO.createUser(user);

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
        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("FakeUsername");
        });
    }

    @Test
    void getUserFailureWrongUser() throws DataAccessException {
        UserData user = new UserData("RealUser", "RealPass", "real@email.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("FakeUser");
        });

        // throw error if user does not exist ?>???/
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("NewUser");
        });

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
