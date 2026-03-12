package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import chess.ChessGame;

import static java.sql.Types.NULL;


public class SQLGameDAO implements GameDAO {

    private final String TABLE_NAME = "games";

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM " + TABLE_NAME + " WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                        var serializer = new Gson();
                        String gameJson = rs.getString("game");
                        ChessGame game = serializer.fromJson(gameJson, ChessGame.class);

                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        );
                    }
                }
            }
        }  catch (SQLException e) {
            throw new DataAccessException("Error retrieving game", e);
        }

        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {

        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Game Name cannot be null or empty");
        }

        var serializer = new Gson();
        ChessGame game = new ChessGame();
        String json = serializer.toJson(game);

        String statement = "INSERT INTO " + TABLE_NAME + " (whiteUsername, blackUsername, gameName, game)" +
                " VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, gameName);
            ps.setString(4, json);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new DataAccessException("Failed to retrieve generated gameID");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating game", e);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM " + TABLE_NAME;

            try (PreparedStatement ps = conn.prepareStatement(statement);
                ResultSet rs = ps.executeQuery()) {

                var serializer = new Gson();

                while (rs.next()) {
                    String gameJson = rs.getString("game");
                    ChessGame game = serializer.fromJson(gameJson, ChessGame.class);

                    GameData gameData = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            game
                    );

                    games.add(gameData);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games", e);
        }
        return games;
    }

    @Override
    public void clearGames() throws DataAccessException {
        String statement = "TRUNCATE TABLE " + TABLE_NAME;
        executeUpdate(statement);
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        var serializer = new Gson();
        String json = serializer.toJson(updatedGame.game());

        String statement = "UPDATE " + TABLE_NAME + " SET whiteUsername=?, blackUsername=?, gameName=?," +
                " game=? WHERE gameID=?";
        int rows = executeUpdate(
                statement,
                updatedGame.whiteUsername(),
                updatedGame.blackUsername(),
                updatedGame.gameName(),
                json,
                updatedGame.gameID());

//        if (rows == 0) {
//            throw new DataAccessException("Game does not exist");
//        }
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case null -> ps.setNull(i + 1, NULL);
                    default -> {
                    }
                }
            }

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to execute update: " + statement, e);
        }
    }
}
