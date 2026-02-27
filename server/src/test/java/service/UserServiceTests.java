package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private UserService userService;
    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("barbie", "ken123", "barbara@email.com");
        UserResult result = userService.register(request);

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
    void registerUserTaken() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("orpheus", "eurydice", "wait@email.com");
        userService.register(request);

        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    void loginSuccess() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("batman", "robin", "alfred@email.com");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest("batman", "robin");
        UserResult result = userService.login(loginRequest);

        assertEquals("batman", result.username);
        assertNotNull(result.authToken);
    }

    @Test
    void loginNonExistentUser() {
        LoginRequest loginRequest = new LoginRequest("anthony", "kate");

        assertThrows(RuntimeException.class, () -> {
           userService.login(loginRequest);
        });
    }

    @Test
    void loginWrongPassword() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("galinda", "upland", "oz@hotmail.com");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest("galinda", "tigelaar");
        assertThrows(RuntimeException.class, () -> {
           userService.login(loginRequest);
        });
    }

    @Test
    void logoutSuccess() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("michael", "bluth", "123@gmail.com");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest("michael", "bluth");
        UserResult loginResult = userService.login(loginRequest);

        userService.logout(loginResult.authToken);
        assertNull(authDAO.getAuth(loginResult.authToken));
    }

    @Test
    void logoutFailure() {
        assertThrows(RuntimeException.class, () -> {
            userService.logout("token-non-existent");
        });
    }
}
