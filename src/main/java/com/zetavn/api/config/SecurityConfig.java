package com.zetavn.api.config;

import com.zetavn.api.enums.RoleEnum;
import com.zetavn.api.jwt.JwtAuthenticationEntryPoint;
import com.zetavn.api.jwt.JwtAuthorizationFilter;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.RefreshTokenService;
import com.zetavn.api.utils.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration configuration;

    private final JwtHelper jwtHelper;

    private final RefreshTokenService refreshTokenService;

    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .sameOrigin()
                        )
                )
                .authorizeHttpRequests(r -> r.requestMatchers("/api/v0/auth/**","/ws/**","/api/v0/admins/**").permitAll()
                        .requestMatchers("/api/v0/auth/logout").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(get(jwtHelper, refreshTokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }
    private JwtAuthorizationFilter get(JwtHelper jwtHelper, RefreshTokenService refreshTokenService) {
        return new JwtAuthorizationFilter(jwtHelper, refreshTokenService,userRepository);
    }
}
