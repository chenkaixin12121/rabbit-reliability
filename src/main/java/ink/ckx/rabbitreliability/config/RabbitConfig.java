package ink.ckx.rabbitreliability.config;

import ink.ckx.rabbitreliability.service.IMsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author chenkaixin
 * @description
 * @date 2020/09/24 上午 10:38
 */
@Slf4j
@Configuration
public class RabbitConfig {

    public static final Integer MSG_TIMEOUT = 1; // 消息超时时间
    public static final Integer MAX_TRY_COUNT = 3; // 最大重试次数
    public static final String ORDER_MSG_LOG = "order_msg_log";

    public static final String MAIL_QUEUE_NAME = "ckx.mail.queue";
    public static final String MAIL_EXCHANGE_NAME = "ckx.mail.exchange";
    public static final String MAIL_ROUTING_KEY_NAME = "ckx.mail.routing.key";

    public static final String RETRY_QUEUE_NAME = "ckx.retry.queue";
    public static final String RETRY_EXCHANGE_NAME = "ckx.retry.exchange";
    public static final String RETRY_ROUTING_KEY_NAME = "ckx.retry.routing.key";
    public static final Integer RETRY_TTL = 5000; // 在延迟队列的时间
    public static final String RETRY_HEADER = "x-retry-count";

    public static final Integer MSG_DELIVER_SUCCESS = 1; // 发送成功
    public static final Integer MSG_DELIVER_FAIL = 2; // 发送失败
    public static final Integer MSG_CONSUMED_SUCCESS = 3; // 消费成功
    public static final Integer MSG_CONSUMED_FAIL = 4; // 消费失败

    private final CachingConnectionFactory cachingConnectionFactory;

    private final IMsgLogService msgLogService;

    public RabbitConfig(CachingConnectionFactory cachingConnectionFactory, IMsgLogService msgLogService) {
        this.cachingConnectionFactory = cachingConnectionFactory;
        this.msgLogService = msgLogService;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String msgId = Objects.requireNonNull(correlationData).getId();
            if (ack) {
                log.info("消息成功发送到Exchange, msgId: {}", msgId);
                msgLogService.updateStatus(msgId, MSG_DELIVER_SUCCESS);
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

    @Bean
    public DirectExchange mailExchange() {
        return ExchangeBuilder
                .directExchange(MAIL_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange retryExchange() {
        return ExchangeBuilder
                .directExchange(RETRY_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue mailQueue() {
        return new Queue(MAIL_QUEUE_NAME, true);
    }

    /**
     * 经过延迟时间后，将该消息重新投递到对应的 Exchange 中
     */
    @Bean
    public Queue retryQueue() {
        return QueueBuilder
                .durable(RETRY_QUEUE_NAME)
                .withArgument("x-dead-letter-routing-key", MAIL_ROUTING_KEY_NAME)
                .withArgument("x-dead-letter-exchange", MAIL_EXCHANGE_NAME)
                .withArgument("x-message-ttl", RETRY_TTL)
                .build();
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAIL_ROUTING_KEY_NAME);
    }

    @Bean
    public Binding retryBinding() {
        return BindingBuilder.bind(retryQueue()).to(retryExchange()).with(RETRY_ROUTING_KEY_NAME);
    }
}
