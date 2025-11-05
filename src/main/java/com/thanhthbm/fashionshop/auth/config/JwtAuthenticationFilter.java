package com.thanhthbm.fashionshop.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final UserDetailsService userDetailsService;
  private final JwtTokenHelper jwtTokenHelper;

  public JwtAuthenticationFilter(UserDetailsService userDetailsService,  JwtTokenHelper jwtTokenHelper) {
    this.userDetailsService = userDetailsService;
    this.jwtTokenHelper = jwtTokenHelper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authHeader= request.getHeader("Authorization");

    if (null == authHeader || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String authToken = jwtTokenHelper.getToken(request);
      if (null != authToken){
        String username = jwtTokenHelper.getUsernameFromToken(authToken);
        if (null != username){
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          if(jwtTokenHelper.validateToken(authToken,userDetails)){
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
          }

        }
      }
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
