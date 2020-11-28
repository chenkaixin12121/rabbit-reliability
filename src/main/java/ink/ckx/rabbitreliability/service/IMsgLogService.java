package ink.ckx.rabbitreliability.service;


import com.baomidou.mybatisplus.extension.service.IService;
import ink.ckx.rabbitreliability.entity.MsgLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 消息投递日志 服务类
 * </p>
 *
 * @author chenkaixin
 * @since 2020-09-24
 */
public interface IMsgLogService extends IService<MsgLog> {

    void updateStatus(String msgId, Integer status);

    List<MsgLog> selectTimeoutMsg();

    void updateTryCount(String msgId, LocalDateTime tryTime);
}
