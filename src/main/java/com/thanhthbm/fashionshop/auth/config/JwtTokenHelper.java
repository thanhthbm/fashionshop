package com.thanhthbm.fashionshop.auth.config;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenHelper {

  @Value("${jwt.auth.app}")
  private String appName;

  @Value("${jwt.auth.secret_key}")
  private String secretKey;

  @Value("${jwt.auth.expires_in}")
  private int expiresIn; // seconds

  @Value("${refresh.auth.secret_key}")
  private String refreshSecretKey;

  @Getter
  @Value("${refresh.auth.expires_in}")
  private int refreshExpiresIn; // seconds

  @Autowired
  private CustomUserDetailService  customUserDetailService;

  public String generateToken(String username) {
    return Jwts.builder()
        .setIssuer(appName)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(generateExpirationDate())
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private Date generateExpirationDate() {
    return new Date(System.currentTimeMillis() + expiresIn * 1000L);
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }


  public String generateRefreshToken(String username) {
    return Jwts.builder()
        .setIssuer(appName)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(generateRefreshExpirationDate())
        .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private Date generateRefreshExpirationDate() {
    return new Date(System.currentTimeMillis() + refreshExpiresIn * 1000L);
  }

  private SecretKey getRefreshSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(refreshSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public boolean validateRefreshToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getRefreshSigningKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsernameFromRefreshToken(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(getRefreshSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
      return claims.getSubject();
    } catch (Exception ex) {
      return null;
    }
  }

  public boolean checkValidRefreshToken(String token) {
    if (token == null || token.isBlank()) return false;
    if (!validateRefreshToken(token)) return false;

    String username = getUsernameFromRefreshToken(token);
    if (username == null) return false;

    User user;
    try {
      user = (User) this.customUserDetailService.loadUserByUsername(username);
    } catch (Exception ex) {
      return false;
    }
    if (user == null) return false;

    String stored = user.getRefreshToken();
    if (stored == null) return false;
    return token.equals(stored);
  }


  public String getToken(HttpServletRequest request) {
    String authHeader = getAuthHeaderFromRequest(request);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

  private String getAuthHeaderFromRequest(HttpServletRequest request) {
    return request.getHeader("Authorization");
  }

  public String getUsernameFromToken(String authToken) {
    try {
      final Claims claims = this.getAllClaimsFromToken(authToken);
      return claims != null ? claims.getSubject() : null;
    } catch (Exception ex) {
      return null;
    }
  }

  private Claims getAllClaimsFromToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (Exception ex) {
      return null;
    }
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    Date expireDate = getExpirationDate(token);
    if (expireDate == null) return true;
    return expireDate.before(new Date());
  }

  private Date getExpirationDate(String token) {
    try {
      final Claims claims = this.getAllClaimsFromToken(token);
      return claims != null ? claims.getExpiration() : null;
    } catch (Exception ex) {
      return null;
    }
  }

}
