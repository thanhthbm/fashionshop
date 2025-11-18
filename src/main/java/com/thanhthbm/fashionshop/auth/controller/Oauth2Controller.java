package com.thanhthbm.fashionshop.auth.controller;

import com.thanhthbm.fashionshop.auth.config.JwtTokenHelper;
import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.auth.service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

  @Autowired
  private OAuth2Service oAuth2Service;

  @Autowired
  private JwtTokenHelper jwtTokenHelper;

  @GetMapping("/success")
  public void callbackOAuth2(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
    String username = oAuth2User.getAttribute("email");
    User user = oAuth2Service.getUser(username);

    if (null == user) {
      user = oAuth2Service.createUser(oAuth2User, "google");
    }

    String token = jwtTokenHelper.generateToken(user.getUsername());

    response.sendRedirect("http://localhost:3000/oauth2/callback?token=" + token);

  }
}
