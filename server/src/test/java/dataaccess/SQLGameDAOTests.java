package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            var createGamesTable = """
             CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(50),
                blackUsername VARCHAR(50),
                gameName VARCHAR(100),
                game TEXT
             )
             """;
            try (var ps = conn.prepareStatement(createGamesTable)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to create tables", e);
        }

        gameDAO = new SQLGameDAO();
        gameDAO.clearGames();
    }

    @AfterEach
    void cleanUP() throws DataAccessException {
        gameDAO.clearGames();
    }

    @Test
    void clearGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Name!");

        List<GameData> gamesBefore = gameDAO.listGames();
        assertFalse(gamesBefore.isEmpty());

        gameDAO.clearGames();
        List<GameData> gamesAfter = gameDAO.listGames();
        assertEquals(0, gamesAfter.size());
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        gameDAO.createGame("FlipSeven");
        GameData game = gameDAO.getGame(1);

        assertNotNull(game);
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals("FlipSeven", game.gameName());
        assertNotNull(game.game());
    }

    @Test
    void getGameFailure() throws DataAccessException {
        assertNull(gameDAO.getGame(15));
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        assertNull(gameDAO.getGame(1));

        List<GameData> gamesBefore = gameDAO.listGames();
        assertTrue(gamesBefore.isEmpty());

        gameDAO.createGame("TNTL");
        GameData game = gameDAO.getGame(1);

        List<GameData> gamesAfter = gameDAO.listGames();
        assertEquals(1, gamesAfter.size());

        assertEquals("TNTL", game.gameName());
    }

    @Test
    void createGameFailure() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Mafia");
        gameDAO.createGame("UltimateChess");
        gameDAO.createGame("5DChess");

        List<GameData> gamesList = gameDAO.listGames();
        assertEquals(3, gamesList.size());
    }

    @Test
    void listGamesFailure() throws DataAccessException {
        gameDAO.createGame("NewGame");
        GameData game = gameDAO.getGame(1);
        assertNotNull(game);

        gameDAO.clearGames();
        List<GameData> list = gameDAO.listGames();
        assertTrue(list.isEmpty());
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        gameDAO.createGame("ChessTime");
        GameData newGame = gameDAO.getGame(1);
        assertNull(newGame.whiteUsername());
        assertNull(newGame.blackUsername());

        GameData updatedGame = new GameData(
                1,
                "whitePlayer",
                "blackPlayer",
                newGame.gameName(),
                newGame.game()
        );
        gameDAO.updateGame(updatedGame);
        GameData retrievedGame = gameDAO.getGame(1);
        assertEquals("whitePlayer", retrievedGame.whiteUsername());
        assertEquals("blackPlayer", retrievedGame.blackUsername());
    }

    @Test
    void updateGameFailure() throws DataAccessException {
        gameDAO.clearGames();

        int id = gameDAO.createGame("NewGameYay");
        GameData game = gameDAO.getGame(id);

        GameData updatedGame = new GameData(
                77,
                "white",
                "black",
                game.gameName(),
                game.game()
        );

        gameDAO.updateGame(updatedGame);
        GameData retrievedGame = gameDAO.getGame(77);
        assertNull(retrievedGame);
    }

}
