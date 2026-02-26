package model;

public class RegisterResult {
    public String username;
    public String authToken;

    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

}
