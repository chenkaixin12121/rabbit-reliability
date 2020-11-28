package ink.ckx.rabbitreliability.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.ckx.rabbitreliability.entity.MsgLog;
import ink.ckx.rabbitreliability.mapper.MsgLogMapper;
import ink.ckx.rabbitreliability.service.IMsgLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static ink.ckx.rabbitreliability.config.RabbitConfig.MSG_TIMEOUT;


/**
 * <p>
 * 消息投递日志 服务实现类
 * </p>
 *
 * @author chenkaixin
 * @since 2020-09-24
 */
@Service
public class MsgLogServiceImpl extends ServiceImpl<MsgLogMapper, MsgLog> implements IMsgLogService {

    private final MsgLogMapper msgLogMapper;

    public MsgLogServiceImpl(MsgLogMapper msgLogMapper) {
        this.msgLogMapper = msgLogMapper;
    }

    @Override
    public void updateStatus(String msgId, Integer status) {
        MsgLog msgLog = MsgLog.builder().
                msgId(msgId).
                status(status).
                updateTime(LocalDateTime.now()).build();
        msgLogMapper.updateStatus(msgLog);
    }

    @Override
    public List<MsgLog> selectTimeoutMsg() {
        return msgLogMapper.selectTimeoutMsg();
    }

    @Override
    public void updateTryCount(String msgId, LocalDateTime tryTime) {
        LocalDateTime nextTryTime = tryTime.plusMinutes(MSG_TIMEOUT);
        MsgLog msgLog = MsgLog.builder().msgId(msgId).nextTryTime(nextTryTime).build();

        msgLogMapper.updateTryCount(msgLog);
    }
}
