package cn.tomato.ai.domain.protocol.model.valobj.enums;

import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 协议状态枚举
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:30
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProtocolStatusEnum {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用"),

    ;
    private Integer code;
    private String info;

    public static ProtocolStatusEnum get(Integer code) {
        if (code == null) return null;
        for (ProtocolStatusEnum val : values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
    }
}
