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

   public ListGamesResponse listGames(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        checkAuthData(authToken);

        List<GameData> result = gameDAO.listGames();
        return new ListGamesResponse(result);
   }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) {
        if (authToken == null || request.gameName == null || request.gameName.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        checkAuthData(authToken);

        GameData game = gameDAO.createGame(request.gameName);
        int gameID = game.gameID();
        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws AlreadyTakenException {
       if (request.playerColor == null || request.playerColor.isBlank()
       || authToken == null || authToken.isBlank()) {
           throw new RuntimeException("Bad Request");
       }

       checkAuthData(authToken);

       GameData gameData = gameDAO.getGame(request.gameID);
       if (gameData == null){ //check if the game already is taken!
            throw new RuntimeException("Error: Unauthorized");
       }

       AuthData authData = authDAO.getAuth(authToken);
       String username = authData.username();
       GameData updatedGame;

       if (request.playerColor.equals("WHITE")){
           if (gameData.whiteUsername() != null) {
               throw new AlreadyTakenException("Error: already taken");
           }

           updatedGame = new GameData(
                   gameData.gameID(),
                   username,
                   gameData.blackUsername(),
                   gameData.gameName()
           );
       } else if (request.playerColor.equals("BLACK")){
           if (gameData.blackUsername() != null) {
               throw new AlreadyTakenException("Error: already taken");
           }
           updatedGame = new GameData(
                   gameData.gameID(),
                   gameData.whiteUsername(),
                   username,
                   gameData.gameName()
           );
       } else {
           throw new RuntimeException("Bad Request");
       }

       gameDAO.updateGame(updatedGame);
    }

    public void checkAuthData(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null || !authData.authToken().equals(authToken)) {
            throw new RuntimeException("Error: unauthorized"); //change later to different exception
        }
    }

}
