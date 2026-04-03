package model;

public class LeaveGameRequest {
    public String playerColor;
    public int gameID;

    public LeaveGameRequest() {}

    public LeaveGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
