package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest("barbie", "ken123", "barbara@email.com");
        RegisterResult result = userService.register(request);

        assertEquals("barbie", result.username);
        assertNotNull(result.authToken);
    }

    @ParameterizedTest
    @CsvSource({" ,password1,email@email.com",
                "'',password2,test@email.com",
                "username1, ,fake@gmail.com",
                "username2,'',123@gmail.com",
                "fakeuser,fakepassword, ",
                "user!,pass!,''"})
    void registerMissingEntries(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    void registerUserTaken() {
        RegisterRequest request = new RegisterRequest("orpheus", "eurydice", "wait@email.com");
        userService.register(request);

        assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
    }

}
