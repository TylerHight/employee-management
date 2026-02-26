package com.ibm.fscc.notificationservice.services;

import com.ibm.fscc.notificationservice.dto.EmailRequest;
import com.ibm.fscc.notificationservice.dto.EmailResponse;
import com.ibm.fscc.notificationservice.services.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String defaultSender = "test@example.com";
    private final String recipient = "recipient@example.com";
    private final String subject = "Test Subject";
    private final String content = "Test Content";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "defaultSender", defaultSender);
        ReflectionTestUtils.setField(emailService, "enableHtml", true);

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendSimpleEmail_Success() {
        // Arrange
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // Act
        EmailResponse response = emailService.sendSimpleEmail(recipient, subject, content);

        // Assert
        assertTrue(response.isSent());
        assertEquals(recipient, response.getTo());
        assertEquals(subject, response.getSubject());
        assertNotNull(response.getMessageId());
        assertNotNull(response.getSentAt());
        assertNull(response.getErrorMessage());

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleEmail_Failure() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(emailSender).send(any(SimpleMailMessage.class));

        // Act
        EmailResponse response = emailService.sendSimpleEmail(recipient, subject, content);

        // Assert
        assertFalse(response.isSent());
        assertEquals(recipient, response.getTo());
        assertEquals(subject, response.getSubject());
        assertNotNull(response.getMessageId());
        assertNull(response.getSentAt());
        assertNotNull(response.getErrorMessage());

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendHtmlEmail_Success() throws Exception {
        // Arrange
        doNothing().when(emailSender).send(any(MimeMessage.class));

        // Act
        EmailResponse response = emailService.sendHtmlEmail(recipient, subject, "<p>" + content + "</p>");

        // Assert
        assertTrue(response.isSent());
        assertEquals(recipient, response.getTo());
        assertEquals(subject, response.getSubject());
        assertNotNull(response.getMessageId());
        assertNotNull(response.getSentAt());
        assertNull(response.getErrorMessage());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendTemplateEmail_Success() {
        // Arrange
        String templateName = "test-template";
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", "Test User");

        when(templateEngine.process(eq(templateName), any())).thenReturn("<p>Hello Test User</p>");
        doNothing().when(emailSender).send(any(MimeMessage.class));

        // Act
        EmailResponse response = emailService.sendTemplateEmail(recipient, subject, templateName, templateModel);

        // Assert
        assertTrue(response.isSent());
        assertEquals(recipient, response.getTo());
        assertEquals(subject, response.getSubject());
        assertNotNull(response.getMessageId());
        assertNotNull(response.getSentAt());
        assertNull(response.getErrorMessage());

        verify(templateEngine, times(1)).process(eq(templateName), any());
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_CompleteRequest_Success() throws Exception {
        // Arrange
        EmailRequest request = EmailRequest.builder()
                .to(recipient)
                .subject(subject)
                .body(content)
                .isHtml(true)
                .build();

        doNothing().when(emailSender).send(any(MimeMessage.class));

        // Act
        EmailResponse response = emailService.sendEmail(request);

        // Assert
        assertTrue(response.isSent());
        assertEquals(recipient, response.getTo());
        assertEquals(subject, response.getSubject());
        assertNotNull(response.getMessageId());
        assertNotNull(response.getSentAt());
        assertNull(response.getErrorMessage());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }
}
