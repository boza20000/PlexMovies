package com.example.movies.config;

import com.example.movies.constants.StringBundle.SecurityConstants;

public enum SecurityRole {
    ROLE_USER(Constants.USER),
    ROLE_ADMIN(Constants.ADMIN);

    SecurityRole(String roleName) {
    }

    public static class Constants {
        public static final String USER = SecurityConstants.ROLE_USER;
        public static final String ADMIN = SecurityConstants.ROLE_ADMIN;
    }

}
