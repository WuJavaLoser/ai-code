package com.yt.aicode.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户(User)实体类
 *
 * @author makejava
 * @since 2025-09-22 11:15:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -51367946812161705L;
    /**
     * id
     */
    private Long id;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin
     */
    private String userRole;

    private LocalDateTime createTime;

}


