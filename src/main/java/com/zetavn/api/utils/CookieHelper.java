package com.zetavn.api.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieHelper {

    public static Cookie getCookie(Cookie[] cookies, String cookieName) {
        Cookie cookie = null;
        for (Cookie c: cookies) {
            if (c.getName().equals(cookieName)) {
                cookie = c;
            }
        }
        return cookie;
    }

    public static Cookie addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
        return cookie;
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie c = getCookie(request.getCookies(), name);
        c.setPath("/");
        c.setValue("");
        c.setMaxAge(0);
        response.addCookie(c);
    }

}
