package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public GameData getGame(int gameID) { return games.get(gameID); }

    @Override
    public GameData createGame(String gameName) {
        int gameID = nextGameID++;
        String whiteUsername = null;
        String blackUsername = null;

        var serializer = new Gson();
        var game = new ChessGame();
        var json = serializer.toJson(game);
//        game = serializer.fromJson(json, ChessGame.class);

        GameData newGame = new GameData(
            gameID,
            whiteUsername,
            blackUsername,
            gameName
//            game
        );

        games.put(gameID, newGame);
        return newGame;
    }

    @Override
    public List<GameData> listGames() {
        return List.copyOf(games.values());
    }

    @Override
    public void clearGames() { games.clear(); }

    @Override
    public void updateGame(GameData updatedGame) {
        if (!games.containsKey(updatedGame.gameID())) {
            throw new RuntimeException("Game not found");
        }
        games.remove(updatedGame.gameID());
        games.put(updatedGame.gameID(), updatedGame);
    }
}
