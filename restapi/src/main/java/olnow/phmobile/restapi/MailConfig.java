package olnow.phmobile.restapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

@Configuration
class MailConfig {
    @SuppressWarnings("ConstantConditions")
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties props = mailSender.getJavaMailProperties();
        mailSender.setHost((String) AppProperties.getValue("SMTP_HOST"));
        mailSender.setPort((Integer) AppProperties.getValue("SMTP_PORT"));
        if ((boolean) AppProperties.getValue("SMTP_AUTH")) {
            mailSender.setUsername((String) AppProperties.getValue("MAIL_USER_NAME"));
            mailSender.setPassword((String) AppProperties.getValue("MAIL_PASSWORD"));
            props.put("mail.smtp.auth", (Boolean) Objects.requireNonNull(AppProperties.getValue("SMTP_AUTH")));
        }
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.enable", AppProperties.getStringValue("SMTP_SSL"));
        props.put("mail.smtp.starttls.enable", AppProperties.getStringValue("SMTP_START_TLS"));
        props.put("mail.debug", (Boolean) Objects.requireNonNull(AppProperties.getValue("SMTP_DEBUG")));
        return mailSender;
    }
}
