package service;
import dataaccess.*;
import model.*;

import java.util.List;

public class GameService {
   private final AuthDAO authDAO;
   private final GameDAO gameDAO;
   private final UserDAO userDAO;

   public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
       this.authDAO = authDAO;
       this.gameDAO = gameDAO;
       this.userDAO = userDAO;
   }

   public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        checkAuthData(authToken);

        List<GameData> result = gameDAO.listGames();
        return new ListGamesResult(result);
   }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new RuntimeException("Error: unauthorized");
        }

        checkAuthData(authToken);

        if (request.gameName == null || request.gameName.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        int gameID = gameDAO.createGame(request.gameName);
        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws AlreadyTakenException, DataAccessException {
       if (request.playerColor == null || request.playerColor.isBlank()
       || authToken == null || authToken.isBlank()) {
           throw new RuntimeException("Bad Request");
       }

       checkAuthData(authToken);

       GameData gameData = gameDAO.getGame(request.gameID);
       if (gameData == null){
            throw new RuntimeException("Bad Request");
       }

       AuthData authData = authDAO.getAuth(authToken);
       String username = authData.username();
       GameData updatedGame;

       if (request.playerColor.equals("WHITE")) {
           if (gameData.whiteUsername() != null) {
               throw new AlreadyTakenException("Error: already taken");
           }
           updatedGame = new GameData(
                   gameData.gameID(),
                   username,
                   gameData.blackUsername(),
                   gameData.gameName(),
                   gameData.game()
           );
       } else if (request.playerColor.equals("BLACK")){
           if (gameData.blackUsername() != null) {
               throw new AlreadyTakenException("Error: already taken");
           }
               updatedGame = new GameData(
                       gameData.gameID(),
                       gameData.whiteUsername(),
                       username,
                       gameData.gameName(),
                       gameData.game()
               );

           } else {
               throw new AlreadyTakenException("Error: already taken");
           }

       gameDAO.updateGame(updatedGame);
    }

    public void leaveGame(String authToken, int gameID) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        checkAuthData(authToken);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null){
            throw new RuntimeException("Bad Request");
        }

        GameData updatedGame;

        if (username.equals(gameData.whiteUsername())) {
            updatedGame = new GameData(
                    gameData.gameID(),
                    null,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
        } else if (username.equals(gameData.blackUsername())) {
            updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game()
            );
        } else {
            throw new RuntimeException("Bad Request"); //this means the player slot is already not taken
        }

        gameDAO.updateGame(updatedGame);

        GameData after = gameDAO.getGame(gameID);
        System.out.println("AFTER UPDATE -> White: " + after.whiteUsername()
                + ", Black: " + after.blackUsername());
    }

    public void checkAuthData(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null || !authData.authToken().equals(authToken)) {
            throw new RuntimeException("Error: unauthorized"); //change later to different exception
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new RuntimeException("Error: game does not exist");
        }

        return game;
    }
}
