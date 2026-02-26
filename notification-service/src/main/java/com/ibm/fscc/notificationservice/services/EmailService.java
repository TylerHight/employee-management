package com.ibm.fscc.notificationservice.services;

import com.ibm.fscc.notificationservice.dto.EmailRequest;
import com.ibm.fscc.notificationservice.dto.EmailResponse;

import java.util.Map;

public interface EmailService {

    /**
     * Send a simple email with the given details
     * 
     * @param to      recipient email address
     * @param subject email subject
     * @param text    email body text
     * @return EmailResponse with the result of the operation
     */
    EmailResponse sendSimpleEmail(String to, String subject, String text);

    /**
     * Send an email with HTML content
     * 
     * @param to          recipient email address
     * @param subject     email subject
     * @param htmlContent HTML content for the email body
     * @return EmailResponse with the result of the operation
     */
    EmailResponse sendHtmlEmail(String to, String subject, String htmlContent);

    /**
     * Send an email using a template
     * 
     * @param to            recipient email address
     * @param subject       email subject
     * @param templateName  name of the template to use
     * @param templateModel model containing variables for the template
     * @return EmailResponse with the result of the operation
     */
    EmailResponse sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> templateModel);

    /**
     * Process and send an email based on the complete EmailRequest
     * 
     * @param emailRequest the complete email request with all details
     * @return EmailResponse with the result of the operation
     */
    EmailResponse sendEmail(EmailRequest emailRequest);
}
