package com.ibm.fscc.notificationservice.services.impl;

import com.ibm.fscc.notificationservice.dto.EmailAttachment;
import com.ibm.fscc.notificationservice.dto.EmailRequest;
import com.ibm.fscc.notificationservice.dto.EmailResponse;
import com.ibm.fscc.notificationservice.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${notification.email.default-sender}")
    private String defaultSender;

    @Value("${notification.email.enable-html:true}")
    private boolean enableHtml;

    @Override
    public EmailResponse sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(defaultSender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);

            return createSuccessResponse(to, subject);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage(), e);
            return createErrorResponse(to, subject, e.getMessage());
        }
    }

    @Override
    public EmailResponse sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(defaultSender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            emailSender.send(message);

            return createSuccessResponse(to, subject);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
            return createErrorResponse(to, subject, e.getMessage());
        }
    }

    @Override
    public EmailResponse sendTemplateEmail(String to, String subject, String templateName,
            Map<String, Object> templateModel) {
        try {
            Context context = new Context();
            context.setVariables(templateModel);

            String htmlContent = templateEngine.process(templateName, context);
            return sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send template email to {}: {}", to, e.getMessage(), e);
            return createErrorResponse(to, subject, e.getMessage());
        }
    }

    @Override
    public EmailResponse sendEmail(EmailRequest emailRequest) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(defaultSender);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());

            // Add CC recipients if provided
            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc().toArray(new String[0]));
            }

            // Add BCC recipients if provided
            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                helper.setBcc(emailRequest.getBcc().toArray(new String[0]));
            }

            // Process content based on whether it's a template or direct content
            if (emailRequest.getTemplateName() != null && !emailRequest.getTemplateName().isEmpty()) {
                Context context = new Context();
                if (emailRequest.getTemplateVariables() != null) {
                    context.setVariables(emailRequest.getTemplateVariables());
                }
                String htmlContent = templateEngine.process(emailRequest.getTemplateName(), context);
                helper.setText(htmlContent, true);
            } else {
                helper.setText(emailRequest.getBody(), emailRequest.isHtml() || enableHtml);
            }

            // Add attachments if provided
            if (emailRequest.getAttachments() != null && !emailRequest.getAttachments().isEmpty()) {
                for (EmailAttachment attachment : emailRequest.getAttachments()) {
                    helper.addAttachment(
                            attachment.getFilename(),
                            new ByteArrayResource(attachment.getContent()),
                            attachment.getContentType());
                }
            }

            emailSender.send(message);

            return createSuccessResponse(emailRequest.getTo(), emailRequest.getSubject());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", emailRequest.getTo(), e.getMessage(), e);
            return createErrorResponse(emailRequest.getTo(), emailRequest.getSubject(), e.getMessage());
        }
    }

    private EmailResponse createSuccessResponse(String to, String subject) {
        return EmailResponse.builder()
                .messageId(UUID.randomUUID().toString())
                .to(to)
                .subject(subject)
                .sent(true)
                .sentAt(LocalDateTime.now())
                .build();
    }

    private EmailResponse createErrorResponse(String to, String subject, String errorMessage) {
        return EmailResponse.builder()
                .messageId(UUID.randomUUID().toString())
                .to(to)
                .subject(subject)
                .sent(false)
                .errorMessage(errorMessage)
                .build();
    }
}
