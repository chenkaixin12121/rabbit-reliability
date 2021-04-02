package ink.ckx.rabbitreliability.config;

import ink.ckx.rabbitreliability.service.IMsgLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

import static ink.ckx.rabbitreliability.enums.MsgStatusEnum.MSG_SEND_SUCCESS;

/**
 * @author chenkaixin
 * @description
 * @date 2020/09/24 上午 10:38
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
public class RabbitConfig {

    private final CachingConnectionFactory cachingConnectionFactory;

    private final IMsgLogService msgLogService;

    public static final Integer MSG_TIMEOUT = 1; // 发送失败后的重试时间（分）
    public static final Integer MAX_TRY_COUNT = 3; // 最大重试次数
    public static final String RETRY_HEADER = "x-retry-count"; // 发送重试次数的Header
    public static final Integer MAIL_DELAY = 60 * 1000; // 在延迟队列的时间
    public static final String ORDER_MSG_LOG = "order_msg_log";

    public static final String MAIL_QUEUE_NAME = "ckx.mail.queue";
    public static final String MAIL_EXCHANGE_NAME = "ckx.mail.exchange";
    public static final String MAIL_ROUTING_KEY_NAME = "ckx.mail.routing.key";

    @Bean
    public RabbitTemplate rabbitTemplate() {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        /**
         * correlationData 唯一标识
         * ack 消息是否到达
         * cause 失败的异常消息
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String msgId = Objects.requireNonNull(correlationData).getId();
            if (ack) {
                log.info("消息成功发送到Exchange, msgId: {}", msgId);
                msgLogService.updateStatus(msgId, MSG_SEND_SUCCESS.type);
            } else {
                log.info("消息发送到Exchange失败, correlationData : {}, cause: {}", correlationData, cause);
            }
        });

        rabbitTemplate.setReturnsCallback(
                returnedMessage ->
                        log.info("消息从Exchange路由到Queue失败: exchange: {}, routingKey: {}, replyCode: {}, replyText: {}, message: {}",
                                returnedMessage.getExchange(), returnedMessage.getRoutingKey(), returnedMessage.getReplyCode(),
                                returnedMessage.getReplyText(), returnedMessage.getMessage()));

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}