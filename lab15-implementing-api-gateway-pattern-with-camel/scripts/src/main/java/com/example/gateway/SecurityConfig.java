// src/main/java/com/example/gateway/SecurityConfig.java
package com.example.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .authorizeHttpRequests(authz -> authz
        .requestMatchers("/api/v1/users/**").hasRole("USER")
        .requestMatchers("/api/v1/products/**").hasRole("USER")
        .requestMatchers("/api/v1/orders/**").hasRole("ADMIN")
        .requestMatchers("/api/v1/dashboard/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .httpBasic();

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.builder()
      .username("user")
      .password(passwordEncoder().encode("password"))
      .roles("USER")
      .build();

    UserDetails admin = User.builder()
      .username("admin")
      .password(passwordEncoder().encode("admin"))
      .roles("USER", "ADMIN")
      .build();

    return new InMemoryUserDetailsManager(user, admin);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
