package service;

import model.*;
import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private String authToken;

    @BeforeEach
    void setUp() throws AlreadyTakenException {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO, userDAO);

        RegisterRequest registerRequest = new RegisterRequest("jack", "wolfe", "orpheus@gmail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("jack", "wolfe");
        UserResult result = userService.login(loginRequest);
        authToken = result.authToken;
    }

    @Test
    void createGameSuccess() {
        CreateGameRequest request = new CreateGameRequest("MyGameName");
        CreateGameResult result = gameService.createGame(authToken, request);

        assertEquals(1, result.gameID);
    }

    @Test
    void createGameEmptyGameName() {
        CreateGameRequest request = new CreateGameRequest("");

        assertThrows(RuntimeException.class, () -> {
            gameService.createGame(authToken, request);
        });
    }

    @Test
    void createGameBadAuthToken() {
        CreateGameRequest request = new CreateGameRequest("AnotherGame");
        authToken = "WrongAuthToken";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.createGame(authToken, request);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
    }

}
