package client;

import dataaccess.DataAccessException;
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

        facade.clearApplication();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPositive() throws Exception {
        UserResult result = facade.register("player1", "password", "p1@email.com");
        assertTrue(result.authToken.length() > 10);
        assertEquals("player1", result.username);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register("newUser", "newPass", "email.com");

        assertThrows(Exception.class, () -> {
            facade.register("newUser", "diffPass", "email2.com");
        });
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("user1", "pass1", "email@gmail.com");
        UserResult result = facade.login("user1", "pass1");

        assertNotNull(result);
        assertEquals("user1", result.username);
    }

    @Test
    void loginNegative() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.login("fakeUser", "fakePass");
        });
    }

    @Test
    void loginNegativeEmptyParams() throws Exception {
        facade.register("username", "password", "email.com");

        assertThrows(Exception.class, () -> {
           facade.login("", "password");
        });

        assertThrows(RuntimeException.class, () -> {
           facade.login("username", "");
        });
    }

    @Test
    void clearApplication() throws Exception {
        facade.register("Grace", "Rocky", "mary@gmail.com");
        facade.login("Grace", "Rocky");

        facade.clearApplication();

        assertThrows(RuntimeException.class, () -> {
           facade.login("Grace", "Rocky");
        });
    }

}
