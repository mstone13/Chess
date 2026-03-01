package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    GameData getGame(int gameID);
    GameData createGame(String gameName);
    List<GameData> listGames();
    void clearGames();
}
