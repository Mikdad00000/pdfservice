package com.pacgem.pdfservice.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/actuator/health").permitAll()
//                        .requestMatchers("/api/v1/pdf/**").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/actuator/health").permitAll()
//                        .requestMatchers("/api/v1/pdf/**").permitAll()// Allow health checks without auth
//                        .anyRequest().authenticated()                     // All other requests require JWT
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.disable());
//
//        return http.build();
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Permit everything
                );
//                .oauth2ResourceServer(oauth2 -> oauth2.disable()); // Disable JWT processing

        return http.build();
    }
}
