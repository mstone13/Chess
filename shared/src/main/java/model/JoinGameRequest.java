package model;

public class JoinGameRequest {
    public String playerColor;
    public int gameID;
    public String action;

    public JoinGameRequest() {}

    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
