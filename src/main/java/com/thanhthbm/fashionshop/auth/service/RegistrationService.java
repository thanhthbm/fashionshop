package com.thanhthbm.fashionshop.auth.service;

import com.thanhthbm.fashionshop.auth.dto.RegistrationRequest;
import com.thanhthbm.fashionshop.auth.dto.RegistrationResponse;
import com.thanhthbm.fashionshop.auth.entity.Authority;
import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.helper.VerificationCodeGenerator;
import com.thanhthbm.fashionshop.auth.repository.UserDetailRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

@Service
public class RegistrationService {
  @Autowired
  private UserDetailRepository userDetailRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthorityService authorityService;

  @Autowired
  private EmailService emailService;

  public RegistrationResponse createUser(RegistrationRequest registrationRequest) {
    User existing = userDetailRepository.findByEmail(registrationRequest.getEmail());

    if (null != existing) {
      return RegistrationResponse.builder()
          .code(400)
          .message("Email already exists")
          .build();
    }

    try {

      User user = new User();
      user.setFirstName(registrationRequest.getFirstName());
      user.setLastName(registrationRequest.getLastName());
      user.setEmail(registrationRequest.getEmail());
      user.setEnabled(false);
      user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
      user.setProvider("manual");

      String code = VerificationCodeGenerator.generateCode();
      user.setVerificationCode(code);


      user.setAuthorities(authorityService.getUserAuthority());
      userDetailRepository.save(user);

      //send mail
      emailService.sendMail(user);

      return RegistrationResponse.builder()
          .code(200)
          .message("User created")
          .build();


    } catch (Exception e) {
      System.out.printf("Error: %s%n", e.getMessage());
      throw new ServerErrorException(e.getMessage(), e.getCause());
    }
  }

  public void verifyUser(String username) {
    User user = userDetailRepository.findByEmail(username);
    user.setEnabled(true);
    userDetailRepository.save(user);
  }
}
