package com.hniu.mapu.mapper;

import com.hniu.mapu.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户数据访问层
 * @author jiujiu
 */
@Mapper
public interface UserMapper {
	
	/**
	 * 根据ID查询用户
	 * @param id 用户ID
	 * @return 用户信息
	 */
	User selectById(@Param("id") String id);
	
	/**
	 * 根据用户名查询用户
	 * @param username 用户名
	 * @return 用户信息
	 */
	User selectByUsername(@Param("username") String username);
	
	/**
	 * 查询所有用户
	 * @return 用户列表
	 */
	List<User> selectAll();
	
	/**
	 * 插入用户
	 * @param user 用户信息
	 * @return 影响行数
	 */
	int insert(User user);
	
	/**
	 * 更新用户
	 * @param user 用户信息
	 * @return 影响行数
	 */
	int update(User user);
	
	/**
	 * 删除用户
	 * @param id 用户ID
	 * @return 影响行数
	 */
	int deleteById(@Param("id") String id);
	
	/**
	 * 更新用户状态
	 * @param id 用户ID
	 * @param status 用户状态
	 * @return 影响行数
	 */
	int updateStatus(@Param("id") String id, @Param("status") Integer status);
	
	/**
	 * 搜索用户
	 * @param keyword 搜索关键词
	 * @return 用户列表
	 */
	List<User> searchUsers(@Param("keyword") String keyword);
	
	/**
	 * 获取用户总数
	 * @return 用户总数
	 */
	long getTotalCount();
	
	/**
	 * 根据条件查询用户列表
	 * @param params 查询参数
	 * @return 用户列表
	 */
	List<User> findByConditions(java.util.Map<String, Object> params);
	
	/**
	 * 根据条件统计用户数量
	 * @param params 查询参数
	 * @return 用户数量
	 */
	long countByConditions(java.util.Map<String, Object> params);
}