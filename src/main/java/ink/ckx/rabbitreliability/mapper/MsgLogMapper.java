package ink.ckx.rabbitreliability.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.ckx.rabbitreliability.entity.MsgLog;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 消息投递日志 Mapper 接口
 * </p>
 *
 * @author chenkaixin
 * @since 2020-09-24
 */
public interface MsgLogMapper extends BaseMapper<MsgLog> {

    @Select("UPDATE msg_log SET status = #{status}, update_time = now() WHERE msg_id = #{msgId}")
    void updateStatus(MsgLog msgLog);

    List<MsgLog> selectTimeoutMsg();

    @Update("UPDATE msg_log SET try_count = try_count + 1, next_try_time = #{nextTryTime}, update_time = now() WHERE msg_id = #{msgId}")
    void updateTryCount(MsgLog msgLog);
}