package com.thanhthbm.fashionshop.auth.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.helper.VerificationCodeGenerator;
import com.thanhthbm.fashionshop.auth.service.CustomUserDetailService;
import com.thanhthbm.fashionshop.auth.service.EmailService;
import com.thanhthbm.fashionshop.auth.service.RegistrationService;
import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OTPController {
  private final RedisTemplate<String, String> redisTemplate;
  private final RegistrationService registrationService;
  private final EmailService emailService;
  private final CustomUserDetailService customUserDetailService;

  @PostMapping
  public ResponseEntity<ApiResponse<?>> sendOtp(@RequestBody Map<String, String> body) {
    String email = body.get("username");
    String key = "otp_limit:" + email;

    Long count = redisTemplate.opsForValue().increment(key);

    if (count == 1){
      redisTemplate.expire(key, 2, TimeUnit.MINUTES);
    }

    if (count > 3){
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ApiResponse.fail(429, "Please try again after 2 minutes"));
    }

    String otp = VerificationCodeGenerator.generateCode();
    User user = (User) customUserDetailService.loadUserByUsername(email);
    emailService.sendMail(user, otp);
    redisTemplate.opsForValue().set("otp:" + email, otp, Duration.ofMinutes(5));
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("OTP sent successfully"));
  }

  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody Map<String, String> payload){
    String email = payload.get("username");
    String otp = payload.get("code");
    String otpStored = redisTemplate.opsForValue().get("otp:" + email);
    if (otpStored == null || !otpStored.equals(otp)) {
      return ResponseEntity.status(400).body(ApiResponse.fail(400, "OTP invalid"));
    }

    redisTemplate.delete("otp:" + email);

    registrationService.verifyUser(email);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
  }
}

