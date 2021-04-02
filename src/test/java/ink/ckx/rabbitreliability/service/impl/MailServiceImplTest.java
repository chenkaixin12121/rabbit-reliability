package ink.ckx.rabbitreliability.service.impl;

import ink.ckx.rabbitreliability.entity.Mail;
import ink.ckx.rabbitreliability.mq.producer.MailProducer;
import ink.ckx.rabbitreliability.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
class MailServiceImplTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private MailProducer mailProducer;

    @Test
    void send() {
        Mail mail = Mail.builder().to("chenkaixin12121@163.com").title("测试标题").content("测试内容").build();
        mailService.send(mail);
    }

    @Test
    void sendAttachment() throws Exception {
        Mail mail = Mail.builder().to("chenkaixin12121@163.com").title("测试附件标题").content("测试附件内容").build();
        // https://blog.ckx.ink/upload/2020/06/1-bcdcd1cef2224d47a699c04c069565e0.png
        File file = new File("C:\\Users\\chenkaixin\\Downloads\\1-bcdcd1cef2224d47a699c04c069565e0.png");
        mailService.sendAttachment(mail, file);
    }

    @Test
    void testMq() {
        Mail mail = Mail.builder()
                .to("chenkaixin12121@163.com")
                .title("测试mq消息-标题-" + ThreadLocalRandom.current().nextInt(5))
                .content("测试mq消息-内容")
                .build();
        mailProducer.send(mail);
    }
}