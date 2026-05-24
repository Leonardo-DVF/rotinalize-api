package com.rotinalize.api.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    // Injetamos o email do remetente (o seu do Mailtrap) para usar no campo 'From'
    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(remetente);
            mailMessage.setTo(destinatario);
            mailMessage.setSubject(assunto);
            mailMessage.setText(mensagem);

            mailSender.send(mailMessage);
            log.info("Email enviado com sucesso para: {}", destinatario);
        } catch (Exception e) {
            log.error("Falha ao enviar email para: {}", destinatario, e);
        }
    }
}

