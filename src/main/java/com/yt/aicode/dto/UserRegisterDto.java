package com.yt.aicode.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户(User)实体类
 *
 * @author makejava
 * @since 2025-09-22 11:15:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserRegisterDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -51367946812161705L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 二次密码
     */
    private String checkPassword;
}


