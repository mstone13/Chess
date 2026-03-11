package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    GameData getGame(int gameID) throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void clearGames() throws DataAccessException;
    void updateGame(GameData updatedGame) throws DataAccessException;
}
