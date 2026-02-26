package com.ibm.fscc.notificationservice.controller;

import com.ibm.fscc.notificationservice.dto.EmailRequest;
import com.ibm.fscc.notificationservice.dto.EmailResponse;
import com.ibm.fscc.notificationservice.services.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

        private final EmailService emailService;

        @PostMapping("/send")
        public ResponseEntity<EmailResponse> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
                log.info("Received request to send email to: {}", emailRequest.getTo());
                EmailResponse response = emailService.sendEmail(emailRequest);
                return ResponseEntity.status(response.isSent() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        @PostMapping("/send/simple")
        public ResponseEntity<EmailResponse> sendSimpleEmail(
                        @RequestParam String to,
                        @RequestParam String subject,
                        @RequestParam String text) {
                log.info("Received request to send simple email to: {}", to);
                EmailResponse response = emailService.sendSimpleEmail(to, subject, text);
                return ResponseEntity.status(response.isSent() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        @PostMapping("/send/html")
        public ResponseEntity<EmailResponse> sendHtmlEmail(
                        @RequestParam String to,
                        @RequestParam String subject,
                        @RequestParam String htmlContent) {
                log.info("Received request to send HTML email to: {}", to);
                EmailResponse response = emailService.sendHtmlEmail(to, subject, htmlContent);
                return ResponseEntity.status(response.isSent() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        @PostMapping("/send/template")
        public ResponseEntity<EmailResponse> sendTemplateEmail(
                        @RequestParam String to,
                        @RequestParam String subject,
                        @RequestParam String templateName,
                        @RequestBody(required = false) Map<String, Object> templateModel) {
                log.info("Received request to send template email to: {}", to);
                if (templateModel == null) {
                        templateModel = new HashMap<>();
                }
                EmailResponse response = emailService.sendTemplateEmail(to, subject, templateName, templateModel);
                return ResponseEntity.status(response.isSent() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        @PostMapping("/send/welcome")
        public ResponseEntity<EmailResponse> sendWelcomeEmail(
                        @RequestParam String to,
                        @RequestParam String name,
                        @RequestParam(required = false) String loginUrl) {
                log.info("Received request to send welcome email to: {}", to);

                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("name", name);
                templateModel.put("loginUrl", loginUrl != null ? loginUrl : "https://example.com/login");

                EmailResponse response = emailService.sendTemplateEmail(
                                to,
                                "Welcome to Our Service",
                                "welcome-email",
                                templateModel);

                return ResponseEntity.status(response.isSent() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }
}
