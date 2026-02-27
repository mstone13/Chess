package model;

public class LogoutRequest {
    public String authToken;

    public LogoutRequest() {}

    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }
}
