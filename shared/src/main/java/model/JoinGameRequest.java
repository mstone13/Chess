package model;

public class JoinGameRequest {
    public String playerColor;
    public int gameID;
    public String action;

    public JoinGameRequest() {}

    public JoinGameRequest(String playerColor, int gameID, String action) {
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.action = action;
    }
}
