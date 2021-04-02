package ink.ckx.rabbitreliability.task;

import ink.ckx.rabbitreliability.entity.MsgLog;
import ink.ckx.rabbitreliability.service.IMsgLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static ink.ckx.rabbitreliability.config.RabbitConfig.MAX_TRY_COUNT;
import static ink.ckx.rabbitreliability.enums.MsgStatusEnum.MSG_SEND_FAIL;


/**
 * @author chenkaixin
 * @description 重新投递发送失败的消息
 * @date 2020/09/24 下午 2:50
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class ResendMsgTask {

    private final IMsgLogService msgLogService;

    private final RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/10 * * * * ?")
    public void resend() {

        log.info("定时任务 -------> 重新投递消息 -------> 开始");

        List<MsgLog> msgLogList = msgLogService.selectTimeoutMsg();
        msgLogList.forEach(
                msgLog -> {
                    String msgId = msgLog.getMsgId();
                    if (msgLog.getTryCount() >= MAX_TRY_COUNT) {
                        // 设置该条消息发送失败
                        msgLogService.updateStatus(msgId, MSG_SEND_FAIL.type);
                        log.info("超过最大重试次数，消息投递失败，msgId: {}", msgId);
                    } else {
                        msgLogService.updateTryCount(msgId, msgLog.getNextTryTime());
                        rabbitTemplate.convertAndSend(msgLog.getExchange(), msgLog.getRoutingKey(), msgLog.getMsg(), new CorrelationData(msgId));
                    }
                }
        );

        log.info("任务结束 <------- 重新投递消息 <------- 结束");
    }
}
