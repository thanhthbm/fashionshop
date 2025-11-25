package com.thanhthbm.fashionshop.auth.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String sender;

  @Async
  public void sendMail(User user, String otp) {
    String subject = "Verify your email";
    String senderName = "FashionShop";
    // Nội dung HTML
    String mailContent = "<p>Hello " + user.getUsername() + ",</p>";
    mailContent += "<p>Your verification code is <b>" + otp + "</b></p>";
    mailContent += "<p>Please enter this code to verify your email.</p>";
    mailContent += "<br>";
    mailContent += "<p>" + senderName + "</p>";

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(sender);
      helper.setTo(user.getEmail());
      helper.setSubject(subject);
      helper.setText(mailContent, true);

      mailSender.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException("Error sending email", e);
    }
  }
}
