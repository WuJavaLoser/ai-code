package com.yt.aicode.basic.service;

import com.yt.aicode.basic.common.PageResult;
import com.yt.aicode.basic.entity.User;
import com.yt.aicode.dto.UserLoginDto;
import com.yt.aicode.dto.UserQueryDto;
import com.yt.aicode.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户(User)表服务接口
 *
 * @author makejava
 * @since 2025-09-22 11:15:43
 */
public interface UserService {


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userLoginDto 用户登录请求
     * @param request      请求
     * @return 脱敏后的用户信息
     */
     UserVo userLogin(UserLoginDto userLoginDto, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 注销结果
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 登录请求
     * @return 登录结果
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    User queryById(Long id);

    /**
     * 分页查询
     */
    PageResult<User> queryByPage(UserQueryDto userQueryDto);



    /**
     * 修改数据
     */
    Boolean update(User user);
    
    /**
     * 插入数据
     */
    Boolean insert(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);


}

