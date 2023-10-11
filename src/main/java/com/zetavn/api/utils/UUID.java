package com.zetavn.api.utils;

public class UUID {

    public static String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
