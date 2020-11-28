package ink.ckx.rabbitreliability.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息投递日志
 * </p>
 *
 * @author chenkaixin
 * @since 2020-11-20
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class MsgLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一标识
     */
    private String msgId;

    /**
     * 消息体, json格式化
     */
    private String msg;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 状态: 状态: 0-发送中 1-发送成功 2-发送失败 3-消费成功 4-消费失败
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer tryCount;

    /**
     * 下一次重试时间
     */
    private LocalDateTime nextTryTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}