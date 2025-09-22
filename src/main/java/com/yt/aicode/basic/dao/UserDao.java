package com.yt.aicode.basic.dao;

import com.yt.aicode.basic.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户(User)表数据库访问层
 *
 * @author makejava
 * @since 2025-09-22 13:50:22
 */
public interface UserDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Long id);

    /**
     * 通过实体作为筛选条件查询单条数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User query(User user);

    /**
     * 统计总行数
     *
     * @param user 查询条件
     * @return 总行数
     */
    long count(@Param("user") User user);

    /**
     * 查询指定行数据
     *
     * @param user 查询条件
     * @param offset     起始位置
     * @param pageSize   查询条数
     * @return 对象列表
     */
    List<User> queryAllByLimit(@Param("user") User user,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    int insert(User user);
    int insertBatch(@Param("entities") List<User> entities);
    int insertOrUpdateBatch(@Param("entities") List<User> entities);
    int update(User user);
    int deleteById(Long id);
}
