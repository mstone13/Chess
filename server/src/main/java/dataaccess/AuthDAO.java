package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData getAuth(String token);
    void createAuth(AuthData authData);
    void clearAuths();
}
