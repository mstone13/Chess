package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import facade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade(
                new communicator.ClientCommunicator("http://localhost:" + port)
        );
    }

    @BeforeEach
    public void clear() {
        facade.clearApplication();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPositive() {
        UserResult result = facade.register("player1", "password", "p1@email.com");
        assertTrue(result.authToken.length() > 10);
        assertEquals("player1", result.username);
    }

    @Test
    void registerNegative(){
        facade.register("newUser", "newPass", "email.com");

        assertThrows(Exception.class, () ->
                facade.register("newUser", "diffPass", "email2.com")
        );
    }

    @Test
    void loginPositive() {
        facade.register("user1", "pass1", "email@gmail.com");
        UserResult result = facade.login("user1", "pass1");

        assertNotNull(result);
        assertEquals("user1", result.username);
    }

    @Test
    void loginNegative() {
        assertThrows(Exception.class, () ->
            facade.login("fakeUser", "fakePass")
        );
    }

    @Test
    void loginNegativeEmptyParams() {
        facade.register("username", "password", "email.com");

        assertThrows(Exception.class, () ->
           facade.login("", "password")
        );

        assertThrows(RuntimeException.class, () ->
           facade.login("username", "")
        );
    }

    @Test
    void logoutPositive() {
        facade.register("username", "password", "g@gmail.com");
        UserResult loginResult = facade.login("username", "password");
        assertNotNull(loginResult.authToken);

        assertDoesNotThrow(() ->
            facade.logout(loginResult.authToken)
        );
    }

    @Test
    void logoutNegative() {
        assertThrows(RuntimeException.class, () ->
            facade.logout("fake authToken")
        );
    }

    @Test
    void listGamesPositive() {
        facade.register("List Games", "Attempt1234", "list@gmail.com");
        UserResult loginResult = facade.login("List Games", "Attempt1234");
        String authToken = loginResult.authToken;

        ListGamesResult resultNoGames = facade.listGames(authToken);
        assertTrue(resultNoGames.games().isEmpty());

        facade.createGame(authToken, "First Game");
        ListGamesResult finalResult = facade.listGames(authToken);
        assertFalse(finalResult.games().isEmpty());
        assertEquals(1, finalResult.games().size());
    }

    @Test
    void listGamesNegative() {
        facade.register("List Games", "Attempt1234", "list@gmail.com");
        facade.login("List Games", "Attempt1234");

        assertThrows(RuntimeException.class, () ->
            facade.listGames("fake authToken")
        );
    }

    @Test
    void createGamePositive() {
        facade.register("Ryland", "Grace", "eridani@gmail.com");
        UserResult result = facade.login("Ryland", "Grace");
        String authToken = result.authToken;

        assertDoesNotThrow(() ->
            facade.createGame(authToken, "Rocky Love Games!")
        );
    }

    @Test
    void createGameNegative() {
        facade.register("Ryland", "Grace", "eridani@gmail.com");
        facade.login("Ryland", "Grace");

        assertThrows(RuntimeException.class, () ->
            facade.createGame("another fake!", "This is a fake game.")
        );

        assertThrows(RuntimeException.class, () ->
           facade.createGame("", "Not fake, but no auth")
        );
    }

    @Test
    void joinGamePositive() {
        facade.register("Eva", "Stratt", "boss@gmail.com");
        UserResult result = facade.login("Eva", "Stratt");
        String authToken = result.authToken;

        facade.createGame(authToken, "Hail Mary Chess");
        ListGamesResult listGamesResult = facade.listGames(authToken);
        GameData game = listGamesResult.games().getFirst();
        assertEquals("Hail Mary Chess", game.gameName());


        assertDoesNotThrow(() ->
            facade.joinGame(authToken, 1, "BLACK")
        );

        assertEquals("Eva", game.blackUsername());
    }

    @Test
    void joinGameNegative() {
        facade.register("Eva", "Stratt", "boss@gmail.com");
        UserResult result = facade.login("Eva", "Stratt");
        String authToken = result.authToken;

        facade.createGame(authToken, "Great Game!");

        assertThrows(RuntimeException.class, () ->
           facade.joinGame(authToken, 150, "WHITE")
        );

        assertThrows(RuntimeException.class, () ->
           facade.joinGame(authToken, 1, "RED")
        );
    }

    @Test
    void clearApplication() {
        facade.register("Grace", "Rocky", "mary@gmail.com");
        facade.login("Grace", "Rocky");

        facade.clearApplication();

        assertThrows(RuntimeException.class, () ->
           facade.login("Grace", "Rocky")
        );
     }

}
