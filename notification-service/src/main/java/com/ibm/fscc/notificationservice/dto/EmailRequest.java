package com.ibm.fscc.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotBlank(message = "To address is required")
    @Email(message = "Invalid email format")
    private String to;

    private List<@Email(message = "Invalid CC email format") String> cc;

    private List<@Email(message = "Invalid BCC email format") String> bcc;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Email body is required")
    private String body;

    private boolean isHtml;

    private String templateName;

    private Map<String, Object> templateVariables;

    private List<EmailAttachment> attachments;
}
