package model;

public class RegisterOrLoginResult {
    public String username;
    public String authToken;

    public RegisterOrLoginResult() {}

    public RegisterOrLoginResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

}
