package com.teamrocket.stream.cache.respository;

public interface CacheRepository {

    void putAccessToken(String token, String userId);
    
    String getUserIdByAccessToken(String token);

    void putActivationCode(String email, String activationCode);
    
    String queryEmailActivationCode(String email, String activationCode);

    void deleteAny(String key);
}

