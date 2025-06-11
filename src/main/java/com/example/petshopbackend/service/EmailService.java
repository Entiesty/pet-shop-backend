package com.example.petshopbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("boogiepop1221@qq.com"); // 必须和配置文件中的username一致
        message.setTo(toEmail);
        message.setSubject("萌宠之家 - 登录验证码");
        message.setText("您好！\n\n您的登录验证码是： " + code + "\n\n该验证码5分钟内有效，请勿泄露给他人。");
        mailSender.send(message);
    }
}
