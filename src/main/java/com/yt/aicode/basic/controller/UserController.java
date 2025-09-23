package com.yt.aicode.basic.controller;

import com.yt.aicode.annotation.AuthCheck;
import com.yt.aicode.basic.common.*;
import com.yt.aicode.basic.entity.User;
import com.yt.aicode.basic.service.UserService;
import com.yt.aicode.convert.UserConvert;
import com.yt.aicode.dto.*;
import com.yt.aicode.exception.BusinessException;
import com.yt.aicode.exception.ErrorCode;
import com.yt.aicode.exception.ThrowUtils;
import com.yt.aicode.basic.common.SnowflakeIdGenerator;
import com.yt.aicode.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户(User)表控制层
 *
 * @author makejava
 * @since 2025-09-22 11:15:41
 */
@RestController
@RequestMapping("/user")
public class UserController {

    final String DEFAULT_PASSWORD = "ai-code"+"12345678";
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;
    
    /**
     * 雪花算法ID生成器
     */
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterDto.getUserAccount(), userRegisterDto.getUserPassword(), userRegisterDto.getCheckPassword());
        return Result.success(result);
    }

    /**
     *
     * @param userLoginDto 用户登录请求
     * @param request 请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserVo> userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginDto == null, ErrorCode.PARAMS_ERROR);
        UserVo user = userService.userLogin(userLoginDto, request);
        return Result.success(user);
    }

    /**
     *
     * @param request 请求
     * @return 注销结果
     */
    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return Result.success(result);
    }

    @GetMapping("/get/login")
    public Result<UserVo> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        UserVo userVo=UserConvert.INSTANCE.yToVo(loginUser);
        return Result.success(userVo);
    }


    /**
     * 创建用户
     *
     * @param userAddDto 添加用户
     * @return 注册结果
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Long> addUser(@RequestBody UserAddDto userAddDto) {
        ThrowUtils.throwIf(userAddDto == null, ErrorCode.PARAMS_ERROR);
        User user = UserConvert.INSTANCE.addToY(userAddDto);
        // 生成雪花算法ID
        Long userId = snowflakeIdGenerator.generateId();
        user.setId(userId);
        // 默认密码 12345678
        String encryptPassword = DigestUtils.md5DigestAsHex((DEFAULT_PASSWORD).getBytes());
        user.setUserPassword(encryptPassword);
        // 设置默认值
        user.setIsDelete(1);
        boolean result = userService.insert(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(user.getId());
    }


    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.queryById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return Result.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public Result<UserVo> getUserVoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.queryById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        UserVo userVo=UserConvert.INSTANCE.yToVo(user);
        return Result.success(userVo);
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean rs = this.userService.deleteById(deleteRequest.getId());
        return Result.success(rs);
    }

    /**
     * 保存用户信息
     *
     * @param user 实体
     * @return 编辑结果
     */
    @PutMapping("/save")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> save(@RequestBody User user) {
        return Result.success(this.userService.update(user));
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        if (userUpdateDto == null || userUpdateDto.getId() == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User user =UserConvert.INSTANCE.updateToY(userUpdateDto);
        boolean result = userService.update(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     * @param userQueryDto 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<PageResult<UserVo>> listUserVoByPage(@RequestBody UserQueryDto userQueryDto) {
        ThrowUtils.throwIf(userQueryDto == null, ErrorCode.PARAMS_ERROR);
        PageResult<User> pageResult = userService.queryByPage(userQueryDto);
        // 数据脱敏
        List<UserVo> userVOList = pageResult.getRecords()
                .stream()
                .map(UserConvert.INSTANCE::yToVo)
                .collect(Collectors.toList());
        // 构建返回的分页结果
        PageResult<UserVo> userVoPageResult = new PageResult<>();
        userVoPageResult.setRecords(userVOList);
        userVoPageResult.setPageNo(pageResult.getPageNo());
        userVoPageResult.setPageSize(pageResult.getPageSize());
        userVoPageResult.setTotal(pageResult.getTotal());
        return Result.success(userVoPageResult);
    }
}


