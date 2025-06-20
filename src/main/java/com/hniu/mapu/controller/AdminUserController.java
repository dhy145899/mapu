package com.hniu.mapu.controller;

import com.hniu.mapu.pojo.entity.User;
import com.hniu.mapu.common.Result;
import com.hniu.mapu.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import io.micrometer.common.util.StringUtils;

/**
 * 管理员用户管理控制器
 * 负责处理用户的增删改查等管理操作
 * 
 * @author jiujiu
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 获取单个用户信息
	 * @param id 用户ID
	 * @param session 会话
	 * @return 用户信息
	 */
	@GetMapping("/{id}")
	public Result<User> getUserById(@PathVariable String id, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		try {
			User user = userService.findById(id);
			if (user == null) {
				return Result.error(404, "用户不存在");
			}
			return Result.success(user);
		} catch (Exception e) {
			log.error("获取用户信息失败，用户ID：{}", id, e);
			return Result.error(500, "获取用户信息失败");
		}
	}
	
	/**
	 * 添加用户
	 * @param user 用户信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping
	public Result<String> addUser(@RequestBody User user, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		try {
			// 验证必填字段
			if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
				return Result.error(400, "邮箱不能为空");
			}
			
			// 检查用户名是否已存在
			User existingUser = userService.findByUsername(user.getUsername());
			if (existingUser != null) {
				return Result.error(400, "用户名已存在");
			}
			
			// 设置用户ID和时间
			user.setId(UUID.randomUUID().toString());
			user.setCreateTime(LocalDateTime.now());
			user.setUpdateTime(LocalDateTime.now());
			
			// 设置默认值
			if (user.getIsEnabled() == null) {
				user.setIsEnabled(1);
			}
			if (user.getRole() == null) {
				user.setRole(0);
			}
			if (user.getGender() == null) {
				user.setGender(0);
			}
			// 如果没有输入昵称，则自动生成
			if (StringUtils.isBlank(user.getNickname())) {
				user.setNickname("用户" + new Date().getTime());
			}
			
			boolean success = userService.createUser(user);
			if (success) {
				log.info("管理员添加用户成功，用户名：{}", user.getUsername());
				return Result.success("添加用户成功");
			} else {
				return Result.error(500, "添加用户失败");
			}
		} catch (Exception e) {
			log.error("添加用户失败，用户名：{}", user.getUsername(), e);
			return Result.error(500, "添加用户失败");
		}
	}
	
	/**
	 * 更新用户信息
	 * @param id 用户ID
	 * @param user 用户信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PutMapping("/{id}")
	public Result<String> updateUser(@PathVariable String id, @RequestBody User user, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		try {
			// 验证必填字段
			if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
				return Result.error(400, "邮箱不能为空");
			}
			
			// 检查用户是否存在
			User existingUser = userService.findById(id);
			if (existingUser == null) {
				return Result.error(404, "用户不存在");
			}
			
			// 管理员不能修改其他管理员的信息，但可以修改自己的信息
			if (existingUser.getRole() != null && existingUser.getRole() == 1 && !userId.equals(id)) {
				return Result.error(403, "不能修改其他管理员用户信息");
			}
			
			// 设置用户ID和更新时间
			user.setId(id);
			user.setUpdateTime(LocalDateTime.now());
			
			// 如果密码为空，则不更新密码
			if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
				user.setPassword(null);
			}
			
			boolean success = userService.updateUser(user);
			if (success) {
				log.info("管理员更新用户信息成功，用户ID：{}", id);
				return Result.success("更新用户信息成功");
			} else {
				return Result.error(500, "更新用户信息失败");
			}
		} catch (Exception e) {
			log.error("更新用户信息失败，用户ID：{}", id, e);
			return Result.error(500, "更新用户信息失败");
		}
	}
	
	/**
	 * 切换用户状态（启用/禁用）
	 * @param id 用户ID
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/{id}/toggle-status")
	public Result<String> toggleUserStatus(@PathVariable String id, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		try {
			// 检查用户是否存在
			User existingUser = userService.findById(id);
			if (existingUser == null) {
				return Result.error(404, "用户不存在");
			}
			
			// 管理员不能禁用其他管理员
			if (existingUser.getRole() != null && existingUser.getRole() == 1) {
				return Result.error(403, "不能禁用管理员用户");
			}
			
			// 切换状态
			int newStatus = (existingUser.getIsEnabled() != null && existingUser.getIsEnabled() == 1) ? 0 : 1;
			boolean success = userService.updateUserStatus(id, newStatus);
			
			if (success) {
				String action = newStatus == 1 ? "启用" : "禁用";
				log.info("管理员{}用户成功，用户ID：{}", action, id);
				return Result.success(action + "用户成功");
			} else {
				return Result.error(500, "操作失败");
			}
		} catch (Exception e) {
			log.error("切换用户状态失败，用户ID：{}", id, e);
			return Result.error(500, "操作失败");
		}
	}
	
	/**
	 * 删除用户
	 * @param id 用户ID
	 * @param session 会话
	 * @return 操作结果
	 */
	@DeleteMapping("/{id}")
	public Result<String> deleteUser(@PathVariable String id, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		try {
			// 检查用户是否存在
			User existingUser = userService.findById(id);
			if (existingUser == null) {
				return Result.error(404, "用户不存在");
			}
			
			// 管理员不能删除其他管理员
			if (existingUser.getRole() != null && existingUser.getRole() == 1) {
				return Result.error(403, "不能删除管理员用户");
			}
			
			boolean success = userService.deleteUser(id);
			if (success) {
				log.info("管理员删除用户成功，用户ID：{}", id);
				return Result.success("删除用户成功");
			} else {
				return Result.error(500, "删除用户失败");
			}
		} catch (Exception e) {
			log.error("删除用户失败，用户ID：{}", id, e);
			return Result.error(500, "删除用户失败");
		}
	}
	
	/**
	 * 批量启用用户
	 * @param userIds 用户ID列表
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/batch-enable")
	public Result<String> batchEnableUsers(@RequestBody List<String> userIds, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		if (userIds == null || userIds.isEmpty()) {
			return Result.error(400, "请选择要启用的用户");
		}
		
		try {
			boolean success = userService.batchUpdateUserStatus(userIds, 1);
			if (success) {
				log.info("管理员批量启用用户成功，用户数量：{}", userIds.size());
				return Result.success("批量启用用户成功");
			} else {
				return Result.error(500, "批量启用用户失败");
			}
		} catch (Exception e) {
			log.error("批量启用用户失败", e);
			return Result.error(500, "批量启用用户失败");
		}
	}
	
	/**
	 * 批量禁用用户
	 * @param userIds 用户ID列表
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/batch-disable")
	public Result<String> batchDisableUsers(@RequestBody List<String> userIds, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		if (userIds == null || userIds.isEmpty()) {
			return Result.error(400, "请选择要禁用的用户");
		}
		
		try {
			boolean success = userService.batchUpdateUserStatus(userIds, 0);
			if (success) {
				log.info("管理员批量禁用用户成功，用户数量：{}", userIds.size());
				return Result.success("批量禁用用户成功");
			} else {
				return Result.error(500, "批量禁用用户失败");
			}
		} catch (Exception e) {
			log.error("批量禁用用户失败", e);
			return Result.error(500, "批量禁用用户失败");
		}
	}
	
	/**
	 * 批量删除用户
	 * @param userIds 用户ID列表
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/batch-delete")
	public Result<String> batchDeleteUsers(@RequestBody List<String> userIds, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		Integer userRole = (Integer) session.getAttribute("userRole");
		
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		if (userRole == null || userRole != 1) {
			return Result.error(403, "权限不足");
		}
		
		if (userIds == null || userIds.isEmpty()) {
			return Result.error(400, "请选择要删除的用户");
		}
		
		try {
			boolean success = userService.batchDeleteUsers(userIds);
			if (success) {
				log.info("管理员批量删除用户成功，用户数量：{}", userIds.size());
				return Result.success("批量删除用户成功");
			} else {
				return Result.error(500, "批量删除用户失败");
			}
		} catch (Exception e) {
			log.error("批量删除用户失败", e);
			return Result.error(500, "批量删除用户失败");
		}
	}
}