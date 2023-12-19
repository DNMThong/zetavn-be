package com.zetavn.api.config;

import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig {
    private static final Long MAX_AGE = 3600L;
    private static final int CORS_FILTER_ORDER = -102;

    @Value("${zetavn.domain}")
    private String domain;
    @Autowired
    UserRepository repository;

    @Value("${zetavn.admin-domain}")
    private String adminDomain;

    @Bean
    public FilterRegistrationBean corsFilter() {
        List<String> allowedOrigins = Arrays.asList(domain, adminDomain);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(allowedOrigins);
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));
        config.setMaxAge(MAX_AGE);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));

        bean.setOrder(CORS_FILTER_ORDER);
        return bean;
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> {
//            System.out.println("username\t" + username);
//            UserEntity e = repository.findUserEntityByEmail(username);
//            if (e == null) throw new NotFoundException("Not found username");
//            else return e;
//        };
//    }
}
