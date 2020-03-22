package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
class Mail {
    @Autowired
    private JavaMailSender emailSender;

    private Logger logger = LoggerFactory.getLogger(Mail.class);

    public void sendEMail(String to, String subject, String text) {
        if (StringUtils.isEmpty(to) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(text)) {
            logger.error("[sendEMail] error empty params, to: {} subject: {} text: {}", to, subject, text);
            return;
        }
        if (emailSender == null) {
            logger.error("[sendEMail] emailSender is null");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom((String) AppProperties.getValue("MAIL_FROM"));
        message.setSubject(subject);
        message.setText(text);

        this.emailSender.send(message);
        logger.info("[sendEMail] SimpleMessage to {} sent", to);

    }

    public boolean sendEMail(String to, String subject, String text, String fileName, File sendfile) {
        MimeMessage mimeMailMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage, true);
            helper.setTo(to);
            helper.setFrom(AppProperties.getStringValue("MAIL_FROM"));
            helper.setSubject(subject);
            helper.setText(text);
            FileSystemResource attachFile = new FileSystemResource(sendfile);
            helper.addAttachment(fileName , attachFile);
            emailSender.send(mimeMailMessage);
            logger.info("[sendEMail attach] MimeMessage to {} sent", to);

        } catch (MessagingException e) {
            logger.error("[sendEMail attach] Create MimeMessage error:", e);
            return false;
        }
        return true;
    }
}
