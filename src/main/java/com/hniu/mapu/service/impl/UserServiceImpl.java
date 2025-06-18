package com.hniu.mapu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hniu.mapu.mapper.UserMapper;
import com.hniu.mapu.pojo.entity.User;
import com.hniu.mapu.service.UserService;
import com.hniu.mapu.utils.PasswordEncodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 用户服务实现类
 * @author jiujiu
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 根据ID查询用户
	 * @param id 用户ID
	 * @return 用户信息
	 */
	@Override
	public User getUserById(String id) {
		log.info("查询用户信息，用户ID：{}", id);
		return userMapper.selectById(id);
	}
	
	/**
	 * 根据用户名查询用户
	 * @param username 用户名
	 * @return 用户信息
	 */
	@Override
	public User getUserByUsername(String username) {
		log.info("根据用户名查询用户，用户名：{}", username);
		return userMapper.selectByUsername(username);
	}
	
	/**
	 * 查询所有用户
	 * @return 用户列表
	 */
	@Override
	public List<User> getAllUsers() {
		log.info("查询所有用户");
		return userMapper.selectAll();
	}
	
	/**
	 * 用户注册
	 * @param user 用户信息
	 * @return 注册结果
	 */
	@Override
	public Integer register(User user) {
		log.info("用户注册，用户名：{}", user.getUsername());
		
		// 检查用户名是否已存在
		User existUser = userMapper.selectByUsername(user.getUsername());
		if (existUser != null) {
			return 0;
		}
		
		// 设置用户信息
		user.setId(UUID.randomUUID().toString());
		user.setPassword(PasswordEncodeUtils.encoder(user.getPassword()));
		user.setRole(0); // 默认为普通用户
		user.setIsEnabled(1);
		user.setCreateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());
		
		return userMapper.insert(user);
	}
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户信息
	 */
	@Override
	public User login(String username, String password) {
		log.info("用户登录，用户名：{}", username);
		
		User user = userMapper.selectByUsername(username);
		if (user == null) {
			throw new RuntimeException("用户不存在");
		}
		
		if (user.getIsEnabled() == 0) {
			throw new RuntimeException("账号已被禁用");
		}
		
		if (!PasswordEncodeUtils.matches(password, user.getPassword())) {
			throw new RuntimeException("密码错误");
		}
		
		return user;
	}
	
	/**
	 * 更新用户信息
	 * @param user 用户信息
	 * @return 更新结果
	 */
	@Override
	public boolean updateUser(User user) {
		log.info("更新用户信息，用户ID：{}", user.getId());
		return userMapper.update(user) > 0;
	}
	
	/**
	 * 删除用户
	 * @param id 用户ID
	 * @return 删除结果
	 */
	@Override
	public boolean deleteUser(String id) {
		log.info("删除用户，用户ID：{}", id);
		return userMapper.deleteById(id) > 0;
	}
	
	/**
	 * 冻结/解冻用户
	 * @param id 用户ID
	 * @param status 用户状态
	 * @return 操作结果
	 */
	@Override
	public boolean updateUserStatus(String id, Integer status) {
		log.info("更新用户状态，用户ID：{}，状态：{}", id, status);
		return userMapper.updateStatus(id, status) > 0;
	}
	
	/**
	 * 分页查询用户列表
	 * @param pageNum 页码
	 * @param pageSize 页大小
	 * @param keyword 搜索关键词
	 * @return 用户列表
	 */
	@Override
	public PageInfo<User> getUserList(Integer pageNum, Integer pageSize, String keyword) {
		log.info("分页查询用户列表，页码：{}，页大小：{}，关键词：{}", pageNum, pageSize, keyword);
		
		PageHelper.startPage(pageNum, pageSize);
		List<User> users;
		
		if (keyword != null && !keyword.trim().isEmpty()) {
			users = userMapper.searchUsers(keyword.trim());
		} else {
			users = userMapper.selectAll();
		}
		
		return new PageInfo<>(users);
	}
	
	/**
	 * 获取用户总数
	 * @return 用户总数
	 */
	@Override
	public long getTotalCount() {
		log.info("获取用户总数");
		return userMapper.getTotalCount();
	}
	
	/**
	 * 根据条件查询用户列表
	 * @param params 查询参数
	 * @return 用户列表
	 */
	@Override
	public List<User> findByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件查询用户列表，参数：{}", params);
		return userMapper.findByConditions(params);
	}
	
	/**
	 * 根据条件统计用户数量
	 * @param params 查询参数
	 * @return 用户数量
	 */
	@Override
	public long countByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件统计用户数量，参数：{}", params);
		return userMapper.countByConditions(params);
	}
}