package com.yt.aicode.basic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yt.aicode.basic.common.PageResult;
import com.yt.aicode.basic.dao.UserDao;
import com.yt.aicode.basic.entity.User;
import com.yt.aicode.basic.service.UserService;
import com.yt.aicode.convert.UserConvert;
import com.yt.aicode.dto.UserLoginDto;
import com.yt.aicode.dto.UserQueryDto;
import com.yt.aicode.enums.UserRoleEnum;
import com.yt.aicode.exception.BusinessException;
import com.yt.aicode.exception.ErrorCode;
import com.yt.aicode.basic.common.SnowflakeIdGenerator;
import com.yt.aicode.vo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;

import static com.yt.aicode.basic.common.UserConstant.USER_LOGIN_STATE;

/**
 * 用户(User)表服务实现类
 *
 * @author makejava
 * @since 2025-09-22 11:15:44
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    final String SALT = "ai-code";

    @Resource
    private UserDao userDao;
    
    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 检查是否重复
        User user = new User();
        user.setUserAccount(userAccount);
        long count = userDao.count(user);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }
        // 3. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserPassword(encryptPassword);
        // 4. 生成雪花算法ID
        Long userId = snowflakeIdGenerator.generateId();
        user.setId(userId);
        // 5. 插入数据
        user.setUserName("null");
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setIsDelete(0);
        boolean saveResult = userDao.insert(user)>0;
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public UserVo userLogin(UserLoginDto userLoginDto, HttpServletRequest request) {
        User user = UserConvert.INSTANCE.loginToY(userLoginDto);
        //1.校验参数
        if(StrUtil.hasBlank(user.getUserAccount(),user.getUserPassword()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名或密码不能为空");
        if(user.getUserAccount().length()<4)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
        if (user.getUserPassword().length() < 8 )
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        //2.加密
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes()));
        //查看用户是否存在
        long count = userDao.count(user);
        if (count == 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        User u = userDao.query(user);
        //如果用户存在记录用户状态
        request.getSession().setAttribute(USER_LOGIN_STATE,u);
        return UserConvert.INSTANCE.yToVo(u);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User user= (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user==null || user.getId() == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        //从数据库中查询
        User newUser = userDao.queryById(user.getId());
        if(newUser==null || newUser.getId() == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        return newUser;
    }

    @Override
    public User queryById(Long id) {
        return userDao.queryById(id);
    }


    /**
     * 分页查询
     */
    @Override
    public PageResult<User> queryByPage(UserQueryDto userQueryDto) {
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setPageNo(userQueryDto.getPageNo());
        pageResult.setPageSize(userQueryDto.getPageSize());
        User user = UserConvert.INSTANCE.queryDtoToY(userQueryDto);
        long total = this.userDao.count(user);
        if (total == 0) {
            pageResult.setTotal(0L);
            pageResult.setRecords(Collections.emptyList());
            return pageResult;
        }
        int offset = (userQueryDto.getPageNo() - 1) * userQueryDto.getPageSize();
        List<User> records = this.userDao.queryAllByLimit(user, offset, userQueryDto.getPageSize());
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        return pageResult;
    }

    @Override
    public Boolean update(User user) {
      return this.userDao.update(user)>0;
    }
    
    @Override
    public Boolean insert(User user) {
        return this.userDao.insert(user)>0;
    }

    @Override
    public boolean deleteById(Long id) {
        User user = new User();
        user.setId(id);
        user.setIsDelete(1);
        return this.userDao.update(user)>0;
    }
}
