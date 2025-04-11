package org.zerock.arcteryx.domain.auth.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmailAuthService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private String code;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    // 임시코드 생성
    public String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }


    // email 만들기
    public MimeMessage creatMailMassage(String toEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            code = generateCode(); // 변수 생성

            // 템플릿에 데이터 바인딩
            Context context = new Context();
            context.setVariable("code", code);
            String html = templateEngine.process("email-template", context);

            helper.setTo(toEmail);
            helper.setText(html, true);
            helper.setSubject("[Acteryx] 이메일 인증 요청");

            return message;
        }
        catch(MessagingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // email 보내기
    public String sendMail(String email) {
        MimeMessage message = creatMailMassage(email);
        javaMailSender.send(message);
        return code;
    }

}
