package com.allcom.security;

import java.io.Serializable;

/**
 * Created by ljy on 17/7/7.
 * ok
 */
public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;
    private final int userId;

    public JwtAuthenticationResponse(String token,int userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return this.token;
    }

    public int getUserId() {
        return userId;
    }



}
