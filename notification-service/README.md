# Notification Service

A Spring Boot service that provides email notification capabilities for the application. This service can be used to send simple text emails, HTML emails, and template-based emails.

## Table of Contents

- [Overview](#overview)
- [Setup and Configuration](#setup-and-configuration)
- [API Endpoints](#api-endpoints)
- [Email Templates](#email-templates)
- [Security](#security)
- [Testing](#testing)

## Overview

The Notification Service is designed to handle various types of email notifications:

- Simple text emails
- HTML-formatted emails
- Template-based emails using Thymeleaf
- Emails with attachments
- Emails with CC and BCC recipients

The service is built using Spring Boot and integrates with Spring Cloud for service discovery.

## Setup and Configuration

### Prerequisites

- Java 17 or higher
- Maven
- SMTP server access (Gmail, Amazon SES, etc.)

### Configuration

1. **Email Configuration**

   Configure your email settings in `application.properties`:

   ```properties
   # Email Configuration
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=${EMAIL_USERNAME:your-email@gmail.com}
   spring.mail.password=${EMAIL_PASSWORD:your-app-password}
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

   For security reasons, it's recommended to use environment variables for sensitive information:

   ```bash
   export EMAIL_USERNAME=your-email@gmail.com
   export EMAIL_PASSWORD=your-app-password
   export EMAIL_DEFAULT_SENDER=your-email@gmail.com
   ```

2. **Application Configuration**

   Configure the application port and other settings:

   ```properties
   spring.application.name=notification-service
   server.port=8085
   ```

3. **Service Discovery**

   If you're using Eureka for service discovery:

   ```properties
   eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
   eureka.instance.preferIpAddress=true
   ```

### Building and Running

1. Build the service:

   ```bash
   cd notification-service
   mvn clean package
   ```

2. Run the service:

   ```bash
   java -jar target/notification-service-0.0.1-SNAPSHOT.jar
   ```

   Or using Maven:

   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

The service exposes the following REST endpoints:

### Send a Complete Email

```
POST /api/v1/emails/send
```

Request body:

```json
{
  "to": "recipient@example.com",
  "cc": ["cc1@example.com", "cc2@example.com"],
  "bcc": ["bcc@example.com"],
  "subject": "Email Subject",
  "body": "Email content here",
  "isHtml": true,
  "templateName": "welcome-email",
  "templateVariables": {
    "name": "John Doe",
    "loginUrl": "https://example.com/login"
  },
  "attachments": [
    {
      "filename": "document.pdf",
      "content": "base64-encoded-content",
      "contentType": "application/pdf"
    }
  ]
}
```

Note: You can either provide `body` directly or use `templateName` with `templateVariables`.

### Send a Simple Text Email

```
POST /api/v1/emails/send/simple
```

Parameters:
- `to`: Recipient email address
- `subject`: Email subject
- `text`: Email content

Example:
```
POST /api/v1/emails/send/simple?to=recipient@example.com&subject=Hello&text=This%20is%20a%20test%20email
```

### Send an HTML Email

```
POST /api/v1/emails/send/html
```

Parameters:
- `to`: Recipient email address
- `subject`: Email subject
- `htmlContent`: HTML content for the email

### Send a Template Email

```
POST /api/v1/emails/send/template
```

Parameters:
- `to`: Recipient email address
- `subject`: Email subject
- `templateName`: Name of the template to use

Request body (template variables):
```json
{
  "name": "John Doe",
  "loginUrl": "https://example.com/login"
}
```

### Send a Welcome Email

```
POST /api/v1/emails/send/welcome
```

Parameters:
- `to`: Recipient email address
- `name`: Recipient's name
- `loginUrl`: (Optional) URL for the login button

## Email Templates

The service uses Thymeleaf for email templates. Templates are stored in the `src/main/resources/templates` directory.

### Creating a New Template

1. Create a new HTML file in the templates directory, e.g., `src/main/resources/templates/my-template.html`
2. Use Thymeleaf syntax for dynamic content:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>My Template</title>
</head>
<body>
    <h1>Hello, <span th:text="${name}">User</span>!</h1>
    <p>This is a sample template.</p>
</body>
</html>
```

3. Use the template in your API calls by specifying the template name (without the .html extension):

```
POST /api/v1/emails/send/template?to=recipient@example.com&subject=Hello&templateName=my-template
```

With request body:
```json
{
  "name": "John Doe"
}
```

## Security

The API endpoints are secured using Spring Security. By default, the endpoints require authentication.

To customize the security configuration, modify the `SecurityConfig.java` file.

## Testing

The service includes unit tests for both the service layer and the controller layer. To run the tests:

```bash
mvn test
```

### Manual Testing

You can test the API endpoints using tools like Postman or curl:

```bash
curl -X POST \
  http://localhost:8085/api/v1/emails/send/simple \
  -H 'Content-Type: application/json' \
  -d '{
    "to": "recipient@example.com",
    "subject": "Test Email",
    "text": "This is a test email from the notification service."
}'
```

## Integration with Other Services

Other services can call the Notification Service API directly or through a service discovery mechanism like Eureka.

Example of calling the service from another Spring Boot application:

```java
@Service
public class NotificationClient {

    private final RestTemplate restTemplate;
    
    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void sendEmail(String to, String subject, String content) {
        String url = "http://notification-service/api/v1/emails/send/simple";
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("to", to)
                .queryParam("subject", subject)
                .queryParam("text", content);
                
        restTemplate.postForEntity(builder.toUriString(), null, Void.class);
    }
}
```

## Troubleshooting

### Common Issues

1. **Email Not Sending**
   - Check SMTP server settings
   - Verify credentials
   - Check firewall settings
   - Enable debug logging: `logging.level.org.springframework.mail=DEBUG`

2. **Template Not Found**
   - Ensure the template exists in the correct location
   - Check the template name (without .html extension)
   - Verify Thymeleaf configuration

3. **Authentication Issues**
   - For Gmail, you may need to enable "Less secure app access" or use an App Password