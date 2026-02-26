package com.ibm.fscc.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    private String messageId;
    private String to;
    private String subject;
    private boolean sent;
    private LocalDateTime sentAt;
    private String errorMessage;
}
