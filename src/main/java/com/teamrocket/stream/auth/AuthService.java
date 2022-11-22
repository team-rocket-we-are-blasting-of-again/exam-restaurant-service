package com.teamrocket.stream.auth;

import com.teamrocket.stream.persistent.model.AccessToken;

public interface AuthService {
    void putAccessToken(String accessToken, Long userId);
    Long loginWithAccessToken(String code);
    AccessToken getAccesToken(Long userId);
    void deleteByUserId(Long userId);
}

