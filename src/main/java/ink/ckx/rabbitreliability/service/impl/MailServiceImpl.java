package ink.ckx.rabbitreliability.service.impl;

import ink.ckx.rabbitreliability.entity.Mail;
import ink.ckx.rabbitreliability.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author chenkaixin
 * @description
 * @date 2020/09/14 下午 3:54
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送简单邮件
     */
    @Override
    public void send(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(mail.getTo()); // 目标邮箱
        message.setSubject(mail.getTitle()); // 邮件标题
        message.setText(mail.getContent());  // 邮件正文

//        int i = 1 / 0;
        mailSender.send(message);
    }

    /**
     * 发送附件邮件
     */
    @Override
    public void sendAttachment(Mail mail, File file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getTitle());
        helper.setText(mail.getContent());
        FileSystemResource resource = new FileSystemResource(file);
        String fileName = file.getName();
        helper.addAttachment(fileName, resource);

        mailSender.send(message);
    }
}
