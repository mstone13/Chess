package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public AuthData getAuth(String token) {
        return auths.get(token);
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }

    @Override
    public void deleteAuth(AuthData authData) {
        auths.remove(authData.authToken());
    }
}
