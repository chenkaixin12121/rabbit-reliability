package ink.ckx.rabbitreliability.mq.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import ink.ckx.rabbitreliability.entity.Mail;
import ink.ckx.rabbitreliability.service.IMsgLogService;
import ink.ckx.rabbitreliability.service.MailService;
import ink.ckx.rabbitreliability.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static ink.ckx.rabbitreliability.config.RabbitConfig.*;
import static ink.ckx.rabbitreliability.enums.MsgStatusEnum.MSG_CONSUME_FAIL;
import static ink.ckx.rabbitreliability.enums.MsgStatusEnum.MSG_CONSUME_SUCCESS;


/**
 * @author chenkaixin
 * @description
 * @date 2020/09/24 下午 1:58
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class MailConsumer {

    private final StringRedisTemplate redisTemplate;

    private final MailService mailService;

    private final IMsgLogService msgLogService;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MAIL_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(name = MAIL_EXCHANGE_NAME,
                    type = "x-delayed-message",
                    arguments = @Argument(name = "x-delayed-type", value = "direct")
                    , ignoreDeclarationExceptions = "true"),
            key = {MAIL_ROUTING_KEY_NAME}
    ))
    public void handler(Message message, Channel channel) throws IOException {
        Mail mail = JsonUtil.fromJson(new String(message.getBody()), new TypeReference<Mail>() {
        });
        MessageProperties messageProperties = message.getMessageProperties();
        Map<String, Object> headers = messageProperties.getHeaders();
        long tag = messageProperties.getDeliveryTag();
        String msgId = (String) headers.get("spring_returned_message_correlation");
        Integer retryCount = (Integer) headers.get(RETRY_HEADER);

        if (redisTemplate.opsForSet().isMember(ORDER_MSG_LOG, msgId)) {
            // redis中包含该key,说明此消息已经被消费过
            log.info("消息已经被消费, msgId: {}", msgId);
            // 确认消息已消费
            channel.basicAck(tag, false);
            return;
        }

        try {
//            mailService.send(mail); // 发送邮件
            int i = 1 / 0;
            msgLogService.updateStatus(msgId, MSG_CONSUME_SUCCESS.type); // 更新为消费成功
            redisTemplate.opsForSet().add(ORDER_MSG_LOG, msgId); // 避免重复消费
            channel.basicAck(tag, false); // 手动ack
            log.info("发送邮件成功，msgId: {}", msgId);
        } catch (Exception e) {
            channel.basicNack(tag, false, false);
            if (retryCount < MAX_TRY_COUNT) {
                // 发送到延迟队列
                retryCount += 1;
                messageProperties.setHeader(RETRY_HEADER, retryCount); // 重试次数
                messageProperties.setDelay(retryCount * MAIL_DELAY); // 延时时间 1m 2m 3m
                log.info("发送邮件失败，进入第{}次重试，msgId: {}", retryCount, msgId);
                rabbitTemplate.convertAndSend(MAIL_EXCHANGE_NAME, MAIL_ROUTING_KEY_NAME, message, new CorrelationData(msgId));
            } else {
                log.info("发送邮件失败，达到最大重试次数，msgId: {}", msgId);
                msgLogService.updateStatus(msgId, MSG_CONSUME_FAIL.type);
            }
        }
    }
}