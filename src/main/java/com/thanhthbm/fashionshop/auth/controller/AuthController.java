package com.thanhthbm.fashionshop.auth.controller;

import com.thanhthbm.fashionshop.auth.config.JwtTokenHelper;
import com.thanhthbm.fashionshop.auth.dto.LoginRequest;
import com.thanhthbm.fashionshop.auth.dto.RegistrationRequest;
import com.thanhthbm.fashionshop.auth.dto.RegistrationResponse;
import com.thanhthbm.fashionshop.auth.dto.UserToken;
import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import com.thanhthbm.fashionshop.auth.service.RegistrationService;
import com.thanhthbm.fashionshop.dto.ApiResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private RegistrationService registrationService;

  @Autowired
  private CustomUserDetailService  customUserDetailService;
  @Autowired
  private JwtTokenHelper jwtTokenHelper;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<UserToken>> login(@RequestBody LoginRequest loginRequest ) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          UsernamePasswordAuthenticationToken.unauthenticated(
              loginRequest.getUsername(),
              loginRequest.getPassword()
          )
      );

      User user = (User) authentication.getPrincipal();
      String token = jwtTokenHelper.generateToken(user.getEmail());

      UserToken userToken = UserToken.builder()
          .token(token)
          .build();

      return ResponseEntity.ok(ApiResponse.success(userToken, "Login successful"));

    } catch (BadCredentialsException e) {
      return new ResponseEntity<>(
          ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"),
          HttpStatus.UNAUTHORIZED
      );
    } catch (DisabledException e) {
      return new ResponseEntity<>(
          ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "User is not verified. Please check your email."),
          HttpStatus.BAD_REQUEST
      );
    } catch (Exception e) {
      return new ResponseEntity<>(
          ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegistrationResponse>> register(@RequestBody RegistrationRequest registrationRequest) {
    RegistrationResponse registrationResponse = registrationService.createUser(registrationRequest);

    if (registrationResponse.getCode() == 200) {
      return ResponseEntity.ok(ApiResponse.success(registrationResponse));
    }
    return new ResponseEntity<>(ApiResponse.fail(registrationResponse.getCode(), registrationResponse.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<?>> verifyCode(@RequestBody Map<String, String> map){
    String username = map.get("username");
    String code = map.get("code");

    User user = (User) customUserDetailService.loadUserByUsername(username);

    if (null != user && user.getVerificationCode().equals(code)) {
      registrationService.verifyUser(username);
      return ResponseEntity.ok(ApiResponse.success(null));
    }
    return new ResponseEntity(ApiResponse.fail(400, "Invalid code"), HttpStatus.BAD_REQUEST);
  }

}
