package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public UserResult register(RegisterRequest request) throws AlreadyTakenException, DataAccessException {
        if (request.username == null || request.username.isBlank()
                || request.password == null || request.password.isBlank()
                || request.email == null || request.email.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        UserData existingUser = userDAO.getUser(request.username);
        if (existingUser != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        String hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt());

        UserData newUser = new UserData (
                request.username,
                hashedPassword,
                request.email
        );

        userDAO.createUser(newUser);
        return createAuth(request.username);
    }

    public UserResult login(LoginRequest request) throws DataAccessException {
        if (request.username == null || request.username.isBlank()
                || request.password == null || request.password.isBlank()) {
            throw new RuntimeException("Bad Request");
        }

        UserData existingUser = userDAO.getUser(request.username);
        if (existingUser == null) {
            throw new RuntimeException("Error: unauthorized");
        }

        String storedHash = existingUser.password();
        boolean passwordMatch = BCrypt.checkpw(request.password, storedHash);

        if(!passwordMatch) {
            throw new RuntimeException("Error: unauthorized");
        }

        return createAuth(existingUser.username());
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData existingAuthData = authDAO.getAuth(authToken);

        if (!existingAuthData.authToken().equals(authToken)) {
            throw new RuntimeException("Error: unauthorized");
        }

        authDAO.deleteAuth(existingAuthData);
    }

    public UserResult createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();

        AuthData newAuth = new AuthData (token, username);
        authDAO.createAuth(newAuth);

        return new UserResult(username, token);
    }

}

