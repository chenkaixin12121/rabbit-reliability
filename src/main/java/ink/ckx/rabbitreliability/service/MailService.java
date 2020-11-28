package ink.ckx.rabbitreliability.service;


import ink.ckx.rabbitreliability.entity.Mail;

import javax.mail.MessagingException;
import java.io.File;

/**
 * @author chenkaixin
 * @description
 * @date 2020/09/14 下午 3:54
 */
public interface MailService {

    void send(Mail mail);

    void sendAttachment(Mail mail, File file) throws MessagingException;
}
