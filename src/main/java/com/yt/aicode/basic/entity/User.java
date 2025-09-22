package com.yt.aicode.basic.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户(User)实体类
 *
 * @author makejava
 * @since 2025-09-22 12:51:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -82669967938209006L;
    /**
     * id
     */
        private Long id;
        /**
     * 账号
     */
        private String userAccount;
        /**
     * 密码
     */
        private String userPassword;
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
        /**
     * 编辑时间
     */
        private LocalDateTime editTime;
        /**
     * 创建时间
     */
        private LocalDateTime createTime;
        /**
     * 更新时间
     */
        private LocalDateTime updateTime;
        /**
     * 是否删除
     */
        private Integer isDelete =1 ;
    }
