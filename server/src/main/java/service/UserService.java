package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterOrLoginResult register(RegisterRequest request) throws AlreadyTakenException {
        if (request.username == null || request.username.isBlank()
                || request.password == null || request.password.isBlank()
                || request.email == null || request.email.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        UserData existingUser = userDAO.getUser(request.username);
        if (existingUser != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        UserData newUser = new UserData (
                request.username,
                request.password,
                request.email
        );

        userDAO.createUser(newUser);
        return createAuth(request.username);
    }

    public RegisterOrLoginResult login(LoginRequest request) {
        if (request.username == null || request.username.isBlank()
                || request.password == null || request.password.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        UserData existingUser = userDAO.getUser(request.username);
        if (existingUser == null) {
            throw new RuntimeException("Error: unauthorized");
        }

        if(!existingUser.password().equals(request.password)) {
            throw new RuntimeException("Error: unauthorized");
        }

        return createAuth(existingUser.username());
    }

    public RegisterOrLoginResult createAuth(String username) {
        String token = UUID.randomUUID().toString();

        AuthData newAuth = new AuthData (token, username);
        authDAO.createAuth(newAuth);

        return new RegisterOrLoginResult(username, token);
    }

}

