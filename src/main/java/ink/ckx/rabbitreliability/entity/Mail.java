package ink.ckx.rabbitreliability.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenkaixin
 * @description
 * @date 2020/09/14 下午 3:03
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    private String to;

    private String title;

    private String content;
}