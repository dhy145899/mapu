package com.hniu.mapu.controller;

import com.hniu.mapu.common.Result;
import com.hniu.mapu.pojo.entity.User;
import com.hniu.mapu.service.UserService;
import com.github.pagehelper.PageInfo;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 * @author jiujiu
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	/**
	 * 用户注册页面
	 * @return 注册页面
	 */
	@GetMapping("/register")
	public String registerPage() {
		return "user/register";
	}
	
	/**
	 * 用户登录页面
	 * @return 登录页面
	 */
	@GetMapping("/login")
	public String loginPage() {
		return "user/login";
	}
	
	/**
	 * 用户个人中心页面
	 * @param model 模型
	 * @param session 会话
	 * @return 个人中心页面
	 */
	@GetMapping("/profile")
	public String profilePage(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		User user = userService.getUserById(userId);
		model.addAttribute("user", user);
		return "user/profile";
	}
	
	/**
	 * 用户注册
	 * @param user 用户信息
	 * @return 操作结果
	 */
	@PostMapping("/register")
	@ResponseBody
	public Result<String> register(@RequestBody User user) {
		log.info("用户注册，用户名：{}", user.getUsername());
		
		if (StringUtils.isBlank(user.getNickname())) {
			user.setNickname("用户" + new Date().getTime());
		}
		int success = userService.register(user);
		return success == 1 ? Result.success() : Result.error("注册失败");
	}

	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/login")
	@ResponseBody
	public Result<String> login(@RequestParam String username,
								@RequestParam String password,
								HttpSession session) {
		log.info("用户登录，用户名：{}", username);
		
		try {
			User user = userService.login(username, password);
			if (user != null) {
				session.setAttribute("userId", user.getId());
				session.setAttribute("username", user.getNickname() != null && !user.getNickname().trim().isEmpty() ? user.getNickname() : user.getUsername());
				session.setAttribute("userRole", user.getRole());
				
				// 根据用户角色返回不同的跳转信息
			if (user.getRole() == 1) {
				// 管理员用户，返回管理端跳转URL
				return Result.success("/admin", "管理员登录成功");
			} else {
				// 普通用户，返回用户端跳转URL
				return Result.success("/", "登录成功");
			}
			} else {
				return Result.error("用户名或密码错误");
			}
		} catch (Exception e) {
			log.error("用户登录失败", e);
			return Result.error("登录失败");
		}
	}
	
	/**
	 * 用户退出登录
	 * @param session 会话
	 * @return 重定向到首页
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		log.info("用户退出登录，用户ID：{}", userId);
		
		session.invalidate();
		return "redirect:/";
	}
	
	/**
	 * 更新用户信息
	 * @param user 用户信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/update")
	@ResponseBody
	public Result<String> updateUser(@RequestBody User user, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		user.setId(userId);
		log.info("更新用户信息，用户ID：{}", userId);
		
		try {
			boolean success = userService.updateUser(user);
			return success ? Result.success("更新成功") : Result.error("更新失败");
		} catch (Exception e) {
			log.error("更新用户信息失败", e);
			return Result.error("更新失败");
		}
	}
	
	/**
	 * 更新用户基本信息（表单提交）
	 * @param user 用户信息
	 * @param session 会话
	 * @param redirectAttributes 重定向属性
	 * @return 重定向到个人资料页面
	 */
	@PostMapping("/update-info")
	public String updateUserInfo(User user, HttpSession session, RedirectAttributes redirectAttributes) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		user.setId(userId);
		log.info("更新用户基本信息，用户ID：{}", userId);
		
		try {
			boolean success = userService.updateUser(user);
			if (success) {
				redirectAttributes.addFlashAttribute("success", "个人信息更新成功");
			} else {
				redirectAttributes.addFlashAttribute("error", "个人信息更新失败");
			}
		} catch (Exception e) {
			log.error("更新用户基本信息失败", e);
			redirectAttributes.addFlashAttribute("error", "个人信息更新失败：" + e.getMessage());
		}
		
		return "redirect:/user/profile";
	}
	
	/**
	 * 修改密码
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/changePassword")
	@ResponseBody
	public Result<String> changePassword(@RequestParam String oldPassword,
										 @RequestParam String newPassword,
										 HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		log.info("修改密码，用户ID：{}", userId);
		
		try {
			// 验证旧密码
			User user = userService.getUserById(userId);
			if (!user.getPassword().equals(oldPassword)) {
				return Result.error("旧密码错误");
			}
			
			// 更新密码
			user.setPassword(newPassword);
			boolean success = userService.updateUser(user);
			return success ? Result.success("密码修改成功") : Result.error("密码修改失败");
		} catch (Exception e) {
			log.error("修改密码失败", e);
			return Result.error("密码修改失败");
		}
	}
	
	/**
	 * 检查用户名是否存在
	 * @param username 用户名
	 * @return 检查结果
	 */
	@GetMapping("/checkUsername")
	@ResponseBody
	public Result<Boolean> checkUsername(@RequestParam String username) {
		log.info("检查用户名是否存在：{}", username);
		
		User user = userService.getUserByUsername(username);
		boolean exists = user != null;
		return Result.success(exists);
	}
	
	/**
	 * 获取当前登录用户信息
	 * @param session 会话
	 * @return 用户信息
	 */
	@GetMapping("/current")
	@ResponseBody
	public Result<User> getCurrentUser(HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "未登录");
		}
		
		User user = userService.getUserById(userId);
		// 不返回密码
		user.setPassword(null);
		return Result.success(user);
	}
	
	/**
	 * 用户管理页面
	 * @param model 模型
	 * @param session 会话
	 * @return 管理页面
	 */
	@GetMapping("/manage")
	public String managePage(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		// 检查是否为管理员
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (userRole == null || userRole != 1) {
			model.addAttribute("error", "权限不足，仅管理员可访问");
			return "error/403";
		}
		
		// 获取用户列表
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		
		return "user/manage";
	}
	
	/**
	 * 获取用户列表
	 * @param pageNum 页码
	 * @param pageSize 页大小
	 * @param keyword 搜索关键词
	 * @param session 会话
	 * @return 用户列表
	 */
	@GetMapping("/list")
	@ResponseBody
	public Result<PageInfo<User>> getUserList(@RequestParam(defaultValue = "1") int pageNum,
											  @RequestParam(defaultValue = "10") int pageSize,
											  @RequestParam(required = false) String keyword,
											  HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		// 检查是否为管理员
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (userRole == null || userRole != 1) {
			return Result.error("权限不足，仅管理员可访问");
		}
		
		log.info("获取用户列表，页码：{}，页大小：{}，关键词：{}", pageNum, pageSize, keyword);
		
		try {
			PageInfo<User> pageInfo = userService.getUserList(pageNum, pageSize, keyword);
			// 清除密码信息
			pageInfo.getList().forEach(user -> user.setPassword(null));
			return Result.success(pageInfo);
		} catch (Exception e) {
			log.error("获取用户列表失败", e);
			return Result.error("获取用户列表失败");
		}
	}
	
	/**
	 * 更新用户状态（冻结/解冻）
	 * @param id 用户ID
	 * @param status 状态（0-正常，1-冻结）
	 * @param session 会话
	 * @return 操作结果
	 */
	@PutMapping("/{id}/status")
	@ResponseBody
	public Result<String> updateUserStatus(@PathVariable String id, @RequestParam Integer status, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		// 检查是否为管理员
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (userRole == null || userRole != 1) {
			return Result.error("权限不足，仅管理员可访问");
		}
		
		// 不能操作自己
		if (id.equals(userId)) {
			return Result.error("不能操作自己的账号");
		}
		
		log.info("更新用户状态，操作者：{}，目标用户：{}，状态：{}", userId, id, status);
		
		try {
			boolean success = userService.updateUserStatus(id, status);
			String statusText = status == 0 ? "解冻" : "冻结";
			return success ? Result.success(statusText + "成功") : Result.error(statusText + "失败");
		} catch (Exception e) {
			log.error("更新用户状态失败", e);
			return Result.error("操作失败：" + e.getMessage());
		}
	}
	
	/**
	 * 删除用户（封号）
	 * @param id 用户ID
	 * @param session 会话
	 * @return 操作结果
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public Result<String> deleteUser(@PathVariable String id, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		// 检查是否为管理员
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (userRole == null || userRole != 1) {
			return Result.error("权限不足，仅管理员可访问");
		}
		
		// 不能删除自己
		if (id.equals(userId)) {
			return Result.error("不能删除自己的账号");
		}
		
		log.info("删除用户，操作者：{}，目标用户：{}", userId, id);
		
		try {
			boolean success = userService.deleteUser(id);
			return success ? Result.success("用户删除成功") : Result.error("用户删除失败");
		} catch (Exception e) {
			log.error("删除用户失败", e);
			return Result.error("删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 上传头像
	 * @param avatar 头像文件
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/upload-avatar")
	@ResponseBody
	public Map<String, Object> uploadAvatar(@RequestParam("avatar") MultipartFile avatar, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			response.put("success", false);
			response.put("message", "请先登录");
			return response;
		}
		
		log.info("用户上传头像，用户ID：{}", userId);
		
		try {
			// 验证文件
			if (avatar.isEmpty()) {
				response.put("success", false);
				response.put("message", "请选择头像文件");
				return response;
			}
			
			// 验证文件类型
			String contentType = avatar.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				response.put("success", false);
				response.put("message", "请上传图片文件");
				return response;
			}
			
			// 验证文件大小（2MB）
			if (avatar.getSize() > 2 * 1024 * 1024) {
				response.put("success", false);
				response.put("message", "头像文件大小不能超过2MB");
				return response;
			}
			
			// 生成唯一文件名
			String originalFilename = avatar.getOriginalFilename();
			String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
			String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
			
			// 确保上传目录存在
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			// 保存文件
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			// 生成访问URL
			String avatarUrl = "/uploads/" + fileName;
			
			// 更新用户头像信息到数据库
			User user = new User();
			user.setId(userId);
			user.setAvatar(avatarUrl);
			user.setUpdateTime(java.time.LocalDateTime.now());
			
			boolean updateSuccess = userService.updateUser(user);
			if (!updateSuccess) {
				// 如果数据库更新失败，删除已上传的文件
				Files.deleteIfExists(filePath);
				response.put("success", false);
				response.put("message", "头像保存失败");
				return response;
			}
			
			log.info("用户头像上传成功，用户ID：{}，文件路径：{}", userId, avatarUrl);
			
			response.put("success", true);
			response.put("message", "头像上传成功");
			response.put("avatarUrl", avatarUrl);
			return response;
			
		} catch (Exception e) {
			log.error("头像上传失败，用户ID：{}", userId, e);
			response.put("success", false);
			response.put("message", "头像上传失败：" + e.getMessage());
			return response;
		}
	}
}
