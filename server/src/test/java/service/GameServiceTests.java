package service;

import model.*;
import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private MemoryGameDAO gameDAO;
    private String authToken;

    @BeforeEach
    void setUp() throws AlreadyTakenException, DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);

        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO, userDAO);

        RegisterRequest registerRequest = new RegisterRequest("jack", "wolfe", "orpheus@gmail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("jack", "wolfe");
        UserResult result = userService.login(loginRequest);
        authToken = result.authToken;
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("MyGameName");
        CreateGameResult result = gameService.createGame(authToken, request);

        assertEquals(1, result.gameID);
    }

    @Test
    void createGameEmptyGameName() throws DataAccessException{
        CreateGameRequest request = new CreateGameRequest("");

        assertThrows(RuntimeException.class, () -> {
            gameService.createGame(authToken, request);
        });
    }

    @Test
    void createGameBadAuthToken() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("AnotherGame");
        authToken = "WrongAuthToken";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.createGame(authToken, request);
        });

        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("TestGameName");
        CreateGameRequest secondRequest = new CreateGameRequest("SecondGameName");

        gameService.createGame(authToken, request);
        gameService.createGame(authToken, secondRequest);

        ListGamesResult response = gameService.listGames(authToken);

        assertNotNull(response);
        assertEquals(2, response.games().size());

        List<GameData> games = response.games();

        assertEquals("TestGameName", games.get(0).gameName());
        assertEquals("SecondGameName", games.get(1).gameName());
    }

    @Test
    void listGamesFailure() {
        authToken = "FailureAuthToken";

        assertThrows(RuntimeException.class, () -> {
           gameService.listGames(authToken);
        });
    }

    @Test
    void joinGameSuccess() throws AlreadyTakenException, DataAccessException {
        CreateGameRequest request = new CreateGameRequest("JoinGameTest");
        gameService.createGame(authToken, request);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        gameService.joinGame(authToken, joinGameRequest);

        GameData game = gameDAO.getGame(1);
        assertEquals("jack", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameFailure() throws AlreadyTakenException, DataAccessException {
        CreateGameRequest request = new CreateGameRequest("PopularGame");
        gameService.createGame(authToken, request);

        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", 1);
        gameService.joinGame(authToken, joinGameRequest);

        JoinGameRequest anotherJoinRequest = new JoinGameRequest("BLACK", 1);
        assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(authToken, anotherJoinRequest);
        });
    }
}
