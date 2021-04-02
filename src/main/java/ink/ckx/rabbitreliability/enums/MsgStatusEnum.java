package ink.ckx.rabbitreliability.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author chenkaixin
 * @date 2021/4/2
 */
@AllArgsConstructor
@Getter
public enum MsgStatusEnum {

    MSG_SEND(0, "发送中"),
    MSG_SEND_SUCCESS(1, "发送成功"),
    MSG_SEND_FAIL(2, "发送失败"),
    MSG_CONSUME_SUCCESS(3, "消费成功"),
    MSG_CONSUME_FAIL(4, "消费失败"),
    ;

    public final Integer type;
    public final String value;
}