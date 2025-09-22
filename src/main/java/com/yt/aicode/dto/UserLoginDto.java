package com.yt.aicode.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wys17
 */
@Data
public class UserLoginDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}