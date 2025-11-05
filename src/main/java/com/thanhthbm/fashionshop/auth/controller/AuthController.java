package com.thanhthbm.fashionshop.auth.controller;

import com.thanhthbm.fashionshop.auth.config.JwtTokenHelper;
import com.thanhthbm.fashionshop.auth.dto.LoginRequest;
import com.thanhthbm.fashionshop.auth.dto.RegistrationRequest;
import com.thanhthbm.fashionshop.auth.dto.RegistrationResponse;
import com.thanhthbm.fashionshop.auth.dto.UserToken;
import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import com.thanhthbm.fashionshop.auth.service.RegistrationService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
  public ResponseEntity<UserToken> login(@RequestBody LoginRequest loginRequest ) {
    try{
      Authentication authentication= UsernamePasswordAuthenticationToken.unauthenticated(
          loginRequest.getUsername(),
          loginRequest.getPassword()
      );
      Authentication authenticationResponse = this.authenticationManager.authenticate(authentication);

      if (authenticationResponse.isAuthenticated()) {
        User user = (User) authenticationResponse.getPrincipal();
        if (!user.isEnabled()) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //tao jwt token
        String token = jwtTokenHelper.generateToken(user.getEmail());


        UserToken userToken = UserToken.builder()
            .token(token)
            .build();
        return ResponseEntity.ok(userToken);
      }

    } catch (BadCredentialsException e){
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  @PostMapping("/register")
  public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
    RegistrationResponse registrationResponse = registrationService.createUser(registrationRequest);

    return new ResponseEntity<>(registrationResponse, registrationResponse.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> map){
    String username = map.get("username");
    String code = map.get("code");

    User user = (User) customUserDetailService.loadUserByUsername(username);

    if (null != user && user.getVerificationCode().equals(code)) {
      registrationService.verifyUser(username);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

}
