//package com.zetavn.api.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.http.HttpStatus.UNAUTHORIZED;
//import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
//
//@Component
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    /**
//     * This method is called when a user tries to access a protected resource without proper authentication.
//     *
//     * @param request       The HTTP request made by the user.
//     * @param response      The HTTP response to be sent back to the user.
//     * @param authException The authentication exception that occurred.
//     * @throws IOException      If an I/O error occurs while handling the response.
//     * @throws ServletException If a servlet-related error occurs while handling the response.
//     */
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//                         AuthenticationException authException) throws IOException, ServletException {
//
//        // Set the HTTP response status to 401 (Unauthorized)
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        // Write a custom message to the response indicating that access is denied
//        response.setHeader("ERROR", authException.getMessage());
//        response.setStatus(UNAUTHORIZED.value());
//        Map<String, String> error = new HashMap<>();
//        error.put("error", authException.getLocalizedMessage());
//        error.put("message", authException.getMessage());
//        response.setContentType(APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(), error);
//    }
//}
