package com.ibm.fscc.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fscc.notificationservice.dto.EmailRequest;
import com.ibm.fscc.notificationservice.dto.EmailResponse;
import com.ibm.fscc.notificationservice.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private EmailService emailService;

        private EmailRequest emailRequest;
        private EmailResponse successResponse;
        private EmailResponse failureResponse;

        private final String recipient = "test@example.com";
        private final String subject = "Test Subject";
        private final String content = "Test Content";

        @BeforeEach
        void setUp() {
                emailRequest = EmailRequest.builder()
                                .to(recipient)
                                .subject(subject)
                                .body(content)
                                .isHtml(false)
                                .build();

                successResponse = EmailResponse.builder()
                                .messageId(UUID.randomUUID().toString())
                                .to(recipient)
                                .subject(subject)
                                .sent(true)
                                .sentAt(LocalDateTime.now())
                                .build();

                failureResponse = EmailResponse.builder()
                                .messageId(UUID.randomUUID().toString())
                                .to(recipient)
                                .subject(subject)
                                .sent(false)
                                .errorMessage("Failed to send email")
                                .build();
        }

        @Test
        @WithMockUser
        void sendEmail_Success() throws Exception {
                when(emailService.sendEmail(any(EmailRequest.class))).thenReturn(successResponse);

                mockMvc.perform(post("/api/v1/emails/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sent").value(true))
                                .andExpect(jsonPath("$.to").value(recipient))
                                .andExpect(jsonPath("$.subject").value(subject))
                                .andExpect(jsonPath("$.messageId").isNotEmpty())
                                .andExpect(jsonPath("$.sentAt").isNotEmpty());
        }

        @Test
        @WithMockUser
        void sendEmail_Failure() throws Exception {
                when(emailService.sendEmail(any(EmailRequest.class))).thenReturn(failureResponse);

                mockMvc.perform(post("/api/v1/emails/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailRequest)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.sent").value(false))
                                .andExpect(jsonPath("$.to").value(recipient))
                                .andExpect(jsonPath("$.subject").value(subject))
                                .andExpect(jsonPath("$.messageId").isNotEmpty())
                                .andExpect(jsonPath("$.errorMessage").value("Failed to send email"));
        }

        @Test
        @WithMockUser
        void sendSimpleEmail_Success() throws Exception {
                when(emailService.sendSimpleEmail(anyString(), anyString(), anyString())).thenReturn(successResponse);

                mockMvc.perform(post("/api/v1/emails/send/simple")
                                .param("to", recipient)
                                .param("subject", subject)
                                .param("text", content))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sent").value(true))
                                .andExpect(jsonPath("$.to").value(recipient))
                                .andExpect(jsonPath("$.subject").value(subject));
        }

        @Test
        @WithMockUser
        void sendTemplateEmail_Success() throws Exception {
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("name", "Test User");

                when(emailService.sendTemplateEmail(
                                eq(recipient),
                                eq(subject),
                                eq("test-template"),
                                any(Map.class))).thenReturn(successResponse);

                mockMvc.perform(post("/api/v1/emails/send/template")
                                .param("to", recipient)
                                .param("subject", subject)
                                .param("templateName", "test-template")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(templateModel)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sent").value(true));
        }

        @Test
        @WithMockUser
        void sendWelcomeEmail_Success() throws Exception {
                when(emailService.sendTemplateEmail(
                                eq(recipient),
                                eq("Welcome to Our Service"),
                                eq("welcome-email"),
                                any(Map.class))).thenReturn(successResponse);

                mockMvc.perform(post("/api/v1/emails/send/welcome")
                                .param("to", recipient)
                                .param("name", "Test User")
                                .param("loginUrl", "https://example.com/login"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sent").value(true));
        }
}
