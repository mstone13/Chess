package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        if (request.username == null || request.username.isBlank()
                || request.password == null || request.password.isBlank()
                || request.email == null || request.email.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        UserData existingUser = userDAO.getUser(request.username);
        if (existingUser != null) {
            throw new RuntimeException("Error: username already taken");
        }

        UserData newUser = new UserData (
                request.username,
                request.password,
                request.email
        );

        userDAO.createUser(newUser);
        String token = UUID.randomUUID().toString();

        AuthData newAuth = new AuthData (
            token, request.username
        );
        authDAO.createAuth(newAuth);

        return new RegisterResult(request.username, token);
    }

}

