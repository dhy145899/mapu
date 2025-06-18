package com.hniu.mapu.service;

import com.hniu.mapu.pojo.entity.User;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * @author jiujiu
 */
public interface UserService {
	
	/**
	 * 根据ID查询用户
	 * @param id 用户ID
	 * @return 用户信息
	 */
	User getUserById(String id);
	
	/**
	 * 根据用户名查询用户
	 * @param username 用户名
	 * @return 用户信息
	 */
	User getUserByUsername(String username);
	
	/**
	 * 查询所有用户
	 * @return 用户列表
	 */
	List<User> getAllUsers();
	
	/**
	 * 用户注册
	 * @param user 用户信息
	 * @return 注册结果
	 */
	Integer register(User user);
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户信息
	 */
	User login(String username, String password);
	
	/**
	 * 更新用户信息
	 * @param user 用户信息
	 * @return 更新结果
	 */
	boolean updateUser(User user);
	
	/**
	 * 删除用户
	 * @param id 用户ID
	 * @return 删除结果
	 */
	boolean deleteUser(String id);
	
	/**
	 * 冻结/解冻用户
	 * @param id 用户ID
	 * @param status 用户状态
	 * @return 操作结果
	 */
	boolean updateUserStatus(String id, Integer status);
	
	/**
	 * 分页查询用户列表
	 * @param pageNum 页码
	 * @param pageSize 页大小
	 * @param keyword 搜索关键词
	 * @return 用户列表
	 */
	PageInfo<User> getUserList(Integer pageNum, Integer pageSize, String keyword);
	
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
	List<User> findByConditions(Map<String, Object> params);
	
	/**
	 * 根据条件统计用户数量
	 * @param params 查询参数
	 * @return 用户数量
	 */
	long countByConditions(Map<String, Object> params);
}