package com.thanhthbm.fashionshop.auth.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Autowired
  private UserDetailsService userDetailsService;

  private final String[] publicApis = {
      "/api/auth/**"
  };

  @Autowired
  private JwtTokenHelper jwtTokenHelper;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests((authorized) -> authorized
        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/products", "/api/category").permitAll()
            .requestMatchers("/oauth2/success").permitAll()
        .anyRequest().authenticated())
        .oauth2Login((oauth2login) -> oauth2login.defaultSuccessUrl("/oauth2/success"))
        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .addFilterBefore(new JwtAuthenticationFilter(userDetailsService, jwtTokenHelper), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web -> web.ignoring().requestMatchers(publicApis));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

    return new ProviderManager(daoAuthenticationProvider);
  }

}
