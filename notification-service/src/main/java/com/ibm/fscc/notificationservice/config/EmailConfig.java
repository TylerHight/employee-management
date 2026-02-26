package com.ibm.fscc.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Properties;

@Configuration
public class EmailConfig {

    private final Environment env;

    public EmailConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port", "587")));
        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable",
                env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        props.put("mail.debug", env.getProperty("spring.mail.properties.mail.debug", "false"));
        props.put("mail.smtp.connectiontimeout",
                env.getProperty("spring.mail.properties.mail.smtp.connectiontimeout", "5000"));
        props.put("mail.smtp.timeout", env.getProperty("spring.mail.properties.mail.smtp.timeout", "5000"));
        props.put("mail.smtp.writetimeout", env.getProperty("spring.mail.properties.mail.smtp.writetimeout", "5000"));

        return mailSender;
    }

    @Bean
    @Primary
    public ITemplateResolver thymeleafTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
