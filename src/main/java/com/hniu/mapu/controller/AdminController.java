package com.hniu.mapu.controller;

import com.hniu.mapu.pojo.entity.*;
import com.hniu.mapu.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台控制器
 * 负责处理管理员相关的页面请求和数据操作
 * 
 * @author jiujiu
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCategoryService articleCategoryService;
	
	@Autowired
	private ArticleCommentService articleCommentService;
	
	@Autowired
	private ArticleFavoriteService articleFavoriteService;
	
	/**
	 * 管理后台首页
	 * @param model 模型
	 * @param session 会话
	 * @return 管理后台首页
	 */
	@GetMapping({"", "/", "/index"})
	public String index(Model model, HttpSession session) {
		log.info("访问管理后台首页");
		
		try {
			// 获取统计数据
			long userCount = userService.getTotalCount();
			long articleCount = articleService.getTotalCount();
			long commentCount = articleCommentService.getTotalCount();
			long categoryCount = articleCategoryService.getTotalCount();
			
			model.addAttribute("userCount", userCount);
			model.addAttribute("articleCount", articleCount);
			model.addAttribute("commentCount", commentCount);
			model.addAttribute("categoryCount", categoryCount);
			
		} catch (Exception e) {
			log.error("获取统计数据失败", e);
			// 设置默认值
			model.addAttribute("userCount", 0);
			model.addAttribute("articleCount", 0);
			model.addAttribute("commentCount", 0);
			model.addAttribute("categoryCount", 0);
		}
		
		return "admin/index";
	}
	
	/**
	 * 管理员登录页面
	 * @return 管理员登录页面
	 */
	@GetMapping("/login")
	public String adminLoginPage() {
		return "admin/login";
	}
	
	/**
	 * 检查管理员登录状态并跳转
	 * @param session 会话
	 * @return 重定向路径
	 */
	@GetMapping("/check-login")
	public String checkLogin(HttpSession session) {
		Integer userRole = (Integer) session.getAttribute("userRole");
		String userId = (String) session.getAttribute("userId");
		
		if (userId == null) {
			return "redirect:/admin/login";
		}
		
		if (userRole != null && userRole == 1) {
			return "redirect:/admin";
		} else {
			return "redirect:/";
		}
	}
	
	/**
	 * 用户管理页面
	 * @param model 模型
	 * @param session 会话
	 * @param page 页码
	 * @param size 每页大小
	 * @param username 用户名搜索条件
	 * @param nickname 昵称搜索条件
	 * @param role 角色搜索条件
	 * @param isEnabled 状态搜索条件
	 * @return 用户管理页面
	 */
	@GetMapping("/users")
	public String usersPage(Model model, HttpSession session,
							@RequestParam(defaultValue = "0") int page,
							@RequestParam(defaultValue = "10") int size,
							@RequestParam(required = false) String username,
							@RequestParam(required = false) String nickname,
							@RequestParam(required = false) Integer role,
							@RequestParam(required = false) Integer isEnabled) {
		log.info("访问用户管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取用户列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("username", username);
			params.put("nickname", nickname);
			params.put("role", role);
			params.put("isEnabled", isEnabled);
			params.put("offset", page * size);
			params.put("limit", size);
			
			List<User> users = userService.findByConditions(params);
			long totalCount = userService.countByConditions(params);
			int totalPages = (int) Math.ceil((double) totalCount / size);
			
			model.addAttribute("users", users);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", totalCount);
			
		} catch (Exception e) {
			log.error("获取用户列表失败", e);
			model.addAttribute("users", new java.util.ArrayList<>());
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", 0);
		}
		
		return "admin/users";
	}
	
	/**
	 * 文章管理页面
	 * @param model 模型
	 * @param session 会话
	 * @param page 页码
	 * @param size 每页大小
	 * @param title 标题搜索条件
	 * @param username 作者搜索条件
	 * @param categoryId 类别搜索条件
	 * @param status 状态搜索条件
	 * @return 文章管理页面
	 */
	@GetMapping("/articles")
	public String articlesPage(Model model, HttpSession session,
							   @RequestParam(defaultValue = "0") int page,
							   @RequestParam(defaultValue = "10") int size,
							   @RequestParam(required = false) String title,
							   @RequestParam(required = false) String username,
							   @RequestParam(required = false) Integer categoryId,
							   @RequestParam(required = false) Integer status) {
		log.info("访问文章管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取文章列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("title", title);
			params.put("username", username);
			params.put("categoryId", categoryId);
			params.put("status", status);
			params.put("offset", page * size);
			params.put("limit", size);
			
			List<Article> articles = articleService.findByConditions(params);
			long totalCount = articleService.countByConditions(params);
			int totalPages = (int) Math.ceil((double) totalCount / size);
			
			// 获取类别列表用于搜索下拉框
			List<ArticleCategory> categories = articleCategoryService.findAll();
			
			model.addAttribute("articles", articles);
			model.addAttribute("categories", categories);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", totalCount);
			
		} catch (Exception e) {
			log.error("获取文章列表失败", e);
			model.addAttribute("articles", new java.util.ArrayList<>());
			model.addAttribute("categories", new java.util.ArrayList<>());
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", 0);
		}
		
		return "admin/articles";
	}
	
	/**
	 * 类别管理页面
	 * @param model 模型
	 * @param session 会话
	 * @param page 页码
	 * @param size 每页大小
	 * @param name 类别名称搜索条件
	 * @return 类别管理页面
	 */
	@GetMapping("/categories")
	public String categoriesPage(Model model, HttpSession session,
								 @RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "10") int size,
								 @RequestParam(required = false) String name) {
		log.info("访问类别管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取类别列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("name", name);
			params.put("offset", page * size);
			params.put("limit", size);
			
			List<ArticleCategory> categories = articleCategoryService.findByConditions(params);
			long totalCount = articleCategoryService.countByConditions(params);
			int totalPages = (int) Math.ceil((double) totalCount / size);
			
			model.addAttribute("categories", categories);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", totalCount);
			
		} catch (Exception e) {
			log.error("获取类别列表失败", e);
			model.addAttribute("categories", new java.util.ArrayList<>());
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", 0);
		}
		
		return "admin/categories";
	}
	
	/**
	 * 评论管理页面
	 * @param model 模型
	 * @param session 会话
	 * @param page 页码
	 * @param size 每页大小
	 * @param content 评论内容搜索条件
	 * @param username 用户名搜索条件
	 * @param articleTitle 文章标题搜索条件
	 * @return 评论管理页面
	 */
	@GetMapping("/comments")
	public String commentsPage(Model model, HttpSession session,
							   @RequestParam(defaultValue = "0") int page,
							   @RequestParam(defaultValue = "10") int size,
							   @RequestParam(required = false) String content,
							   @RequestParam(required = false) String username,
							   @RequestParam(required = false) String articleTitle) {
		log.info("访问评论管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取评论列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("content", content);
			params.put("username", username);
			params.put("articleTitle", articleTitle);
			params.put("offset", page * size);
			params.put("limit", size);
			
			List<ArticleComment> comments = articleCommentService.findByConditions(params);
			long totalCount = articleCommentService.countByConditions(params);
			int totalPages = (int) Math.ceil((double) totalCount / size);
			
			model.addAttribute("comments", comments);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", totalCount);
			
		} catch (Exception e) {
			log.error("获取评论列表失败", e);
			model.addAttribute("comments", new java.util.ArrayList<>());
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", 0);
		}
		
		return "admin/comments";
	}
	
	/**
	 * 收藏管理页面
	 * @param model 模型
	 * @param session 会话
	 * @param page 页码
	 * @param size 每页大小
	 * @param username 用户名搜索条件
	 * @param articleTitle 文章标题搜索条件
	 * @return 收藏管理页面
	 */
	@GetMapping("/favorites")
	public String favoritesPage(Model model, HttpSession session,
								@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size,
								@RequestParam(required = false) String username,
								@RequestParam(required = false) String articleTitle) {
		log.info("访问收藏管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取收藏列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("username", username);
			params.put("articleTitle", articleTitle);
			params.put("offset", page * size);
			params.put("limit", size);
			
			List<ArticleFavorite> favorites = articleFavoriteService.findByConditions(params);
			long totalCount = articleFavoriteService.countByConditions(params);
			int totalPages = (int) Math.ceil((double) totalCount / size);
			
			model.addAttribute("favorites", favorites);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", totalCount);
			
		} catch (Exception e) {
			log.error("获取收藏列表失败", e);
			model.addAttribute("favorites", new java.util.ArrayList<>());
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("pageSize", size);
			model.addAttribute("totalCount", 0);
		}
		
		return "admin/favorites";
	}
}