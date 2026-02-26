package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    AuthData getAuth(String token);
    void createAuth(AuthData authData);
    void clearAuths();
}
