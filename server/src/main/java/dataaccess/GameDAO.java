package dataaccess;

import model.GameData;

public interface GameDAO {

    GameData getGame(int gameID);
    GameData createGame(String gameName);
    void clearGames();
}
