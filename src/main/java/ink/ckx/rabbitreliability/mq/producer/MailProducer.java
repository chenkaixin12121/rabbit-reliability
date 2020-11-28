package ink.ckx.rabbitreliability.mq.producer;

import ink.ckx.rabbitreliability.entity.Mail;
import ink.ckx.rabbitreliability.entity.MsgLog;
import ink.ckx.rabbitreliability.mapper.MsgLogMapper;
import ink.ckx.rabbitreliability.util.JsonUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static ink.ckx.rabbitreliability.config.RabbitConfig.*;


/**
 * @author chenkaixin
 * @description
 * @date 2020/09/26 下午 12:51
 */
@Component
public class MailProducer {

    private final MsgLogMapper msgLogMapper;

    private final RabbitTemplate rabbitTemplate;

    public MailProducer(MsgLogMapper msgLogMapper, RabbitTemplate rabbitTemplate) {
        this.msgLogMapper = msgLogMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Mail mail) {

        String msgId = UUID.randomUUID().toString().replace("-", "");
        String msg = JsonUtil.toJson(mail);
        MsgLog msgLog = MsgLog.builder()
                .msgId(msgId)
                .msg(msg)
                .exchange(MAIL_EXCHANGE_NAME)
                .routingKey(MAIL_ROUTING_KEY_NAME)
                .nextTryTime(LocalDateTime.now().plusMinutes(MSG_TIMEOUT))
                .createTime(LocalDateTime.now())
                .build();
        msgLogMapper.insert(msgLog); // 消息入库

        Message message = MessageBuilder.withBody(Objects.requireNonNull(msg).getBytes()).build();
        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT); // 消息持久化
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON); // JSON
        messageProperties.setHeader(RETRY_HEADER, 0);
        rabbitTemplate.convertAndSend(MAIL_EXCHANGE_NAME, MAIL_ROUTING_KEY_NAME, message, new CorrelationData(msgId)); // 发送消息
    }
}
