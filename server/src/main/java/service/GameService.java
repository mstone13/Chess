package service;
import dataaccess.*;
import model.*;

public class GameService {
   private final AuthDAO authDAO;
   private final GameDAO gameDAO;

   public GameService(AuthDAO authDAO, GameDAO gameDAO) {
       this.authDAO = authDAO;
       this.gameDAO = gameDAO;
   }

//   public ListGamesResult listGames(String authToken) {
//
//   }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) {
        if (authToken == null || request.gameName == null || request.gameName.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null || !authData.authToken().equals(authToken)) {
            throw new RuntimeException("Error: unauthorized"); //change later to different exception
        }

        GameData game = gameDAO.createGame(request.gameName);
        int gameID = game.gameID();
        return new CreateGameResult(gameID);
    }

}
