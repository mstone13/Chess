package model;

public class JoinGameRequest {
    public String playerColor;
    public Integer gameID;

    public JoinGameRequest() {}

    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
