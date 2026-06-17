package cn.tomato.ai.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分页响应包装类
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePage<T> implements Serializable {

    private static final long serialVersionUID = 7000723935764546322L;

    private String code;
    private String info;
    private T data;
    /** 总记录数 */
    private Long total;

}