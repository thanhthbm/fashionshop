package com.thanhthbm.fashionshop.auth.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String sender;

  @Async
  public String sendMail(User user) {
    String subject = "Verify your email";
    String senderName = "FashionShop";
    String mailContent = "Hello" + user.getUsername() + ",\n";
    mailContent += "<p>Your verification code is <b>" + user.getVerificationCode() + "</b></p>" + ",\n";
    mailContent += "<p>Please enter this code to verify your email. </p>";
    mailContent += "\n<br></br>";
    mailContent += senderName;

    try {

      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(sender);
      message.setTo(user.getEmail());
      message.setText(mailContent);
      message.setSubject(subject);

      mailSender.send(message);

    } catch (Exception e) {
      return e.getMessage();
    }

    return "Message sent";
  }
}
