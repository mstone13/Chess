package service;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.*;
import model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameService {
   private final AuthDAO authDAO;
   private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
       this.authDAO = authDAO;
       this.gameDAO = gameDAO;
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
                   gameData.game(),
                   false
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
                       gameData.game(),
                       false
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
                    gameData.game(),
                    false
            );
        } else if (username.equals(gameData.blackUsername())) {
            updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game(),
                    false
            );
        } else {
            throw new RuntimeException("Bad Request");
        }

        gameDAO.updateGame(updatedGame);
    }

    public void resign(String authToken, int gameID) throws DataAccessException {
       if (authToken == null || authToken.isBlank()) {
           throw new RuntimeException("Bad Request");
       }

       checkAuthData(authToken);
       GameData gameData = gameDAO.getGame(gameID);
       if (gameData == null) {
           throw new RuntimeException("Bad Request");
       }

       GameData updatedGame;

       updatedGame = new GameData(
               gameData.gameID(),
               gameData.whiteUsername(),
               gameData.blackUsername(),
               gameData.gameName(),
               gameData.game(),
               true
       );

       gameData.game().finishGame();
       gameDAO.updateGame(updatedGame);
    }

    public void makeMove(String authToken, int gameID, ChessMove move)
            throws DataAccessException, InvalidMoveException {

        checkAuthData(authToken);

        String username = authDAO.getAuth(authToken).username();

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new RuntimeException("Game does not exist");
        }

        ChessGame game = getChessGame(gameData, username);

        game.makeMove(move);

        if (game.isInStalemate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInCheckmate(ChessGame.TeamColor.WHITE)) {

            game.finishGame();
        }

        gameDAO.updateGame(new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game,
                false
        ));
    }

    @NotNull
    private static ChessGame getChessGame(GameData gameData, String username)
            throws InvalidMoveException {

        ChessGame game = gameData.game();

        if (!game.canMove()) {
            throw new InvalidMoveException("The game is over.");
        }

        ChessGame.TeamColor playerColor;

        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new InvalidMoveException("You are not a player in this game.");
        }

        if (game.getTeamTurn() != playerColor) {
            throw new InvalidMoveException("Sorry, it's not your turn.");
        }

        return game;
    }

    public void checkAuthData(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null || !authData.authToken().equals(authToken)) {
            throw new RuntimeException("Error: unauthorized"); //change later to different exception
        }
    }

}
