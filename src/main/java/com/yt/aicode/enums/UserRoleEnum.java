package com.yt.aicode.enums;

import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wys17
 */

@Getter
public enum UserRoleEnum {
    //枚举常量和字段
    USER("用户","user"),
    ADMIN("管理员","admin");

    // 创建一个静态的 Map 用于快速查找
    private static final Map<String, UserRoleEnum> VALUE_MAP =
            Stream.of(values()).collect(Collectors.toMap(UserRoleEnum::getValue, Function.identity()));

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举实例 (Map 缓存版本)
     */
    public static UserRoleEnum getEnumByValue(String value) {
        return VALUE_MAP.get(value);
    }
}