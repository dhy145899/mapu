package com.hniu.mapu.controller;

import com.hniu.mapu.common.Result;
import com.hniu.mapu.pojo.entity.*;
import com.hniu.mapu.pojo.vo.ArticleVo;
import com.hniu.mapu.service.*;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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
	 * 管理员登录处理
	 * @param username 用户名
	 * @param password 密码
	 * @param session 会话
	 * @return 登录结果
	 */
	@PostMapping("/login")
	@ResponseBody
	public Result<String> adminLogin(@RequestParam String username,
									 @RequestParam String password,
									 HttpSession session) {
		log.info("管理员登录，用户名：{}", username);
		
		try {
			User user = userService.login(username, password);
			if (user != null) {
				// 检查用户角色，只允许管理员登录
				if (user.getRole() != 1) {
					return Result.error("此账号不是管理员账号");
				}
				
				session.setAttribute("userId", user.getId());
				session.setAttribute("username", user.getNickname() != null && !user.getNickname().trim().isEmpty() ? user.getNickname() : user.getUsername());
				session.setAttribute("userRole", user.getRole());
				
				// 管理员登录成功
				return Result.success("/admin", "管理员登录成功");
			} else {
				return Result.error("用户名或密码错误");
			}
		} catch (Exception e) {
			log.error("管理员登录失败", e);
			return Result.error("登录失败");
		}
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
		
		// 添加当前用户信息到模型
		String currentUserId = (String) session.getAttribute("userId");
		model.addAttribute("currentUserId", currentUserId);
		
		try {
			// 获取用户列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("username", username);
			params.put("nickname", nickname);
			params.put("role", role);
			params.put("isEnabled", isEnabled);
			params.put("offset", page * size);
			params.put("size", size);
			
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
	 * @param author 作者搜索条件
	 * @param categoryId 类别搜索条件
	 * @param status 状态搜索条件
	 * @return 文章管理页面
	 */
	@GetMapping("/articles")
	public String articlesPage(Model model, HttpSession session,
							   @RequestParam(defaultValue = "0") int page,
							   @RequestParam(defaultValue = "12") int size,
							   @RequestParam(required = false) String title,
							   @RequestParam(required = false) String author,
							   @RequestParam(required = false) String categoryId,
							   @RequestParam(required = false) Integer status) {
		log.info("访问文章管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取文章列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("title", title);
			params.put("authorName", author);
			
			// 根据categoryId查找categoryName
			String categoryName = null;
			if (categoryId != null && !categoryId.trim().isEmpty()) {
				ArticleCategory category = articleCategoryService.getCategoryById(categoryId);
				if (category != null) {
					categoryName = category.getName();
				}
			}
			params.put("categoryName", categoryName);
			params.put("status", status);
			params.put("offset", page * size);
			params.put("size", size);
			
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
	 * 文章详情页面
	 * @param id 文章ID
	 * @param model 模型
	 * @param session 会话
	 * @return 文章详情页面
	 */
	@GetMapping("/articles/{id}")
	public String articleDetailPage(@PathVariable String id, Model model, HttpSession session) {
		log.info("访问文章详情页面，文章ID：{}", id);
		
		try {
			// 检查管理员权限
			Integer userRole = (Integer) session.getAttribute("userRole");
			if (userRole == null || userRole != 1) {
				return "redirect:/login";
			}
			
			// 获取文章详情
			String userId = (String) session.getAttribute("userId");
			ArticleVo article = articleService.getArticleDetail(id, userId);
			
			model.addAttribute("article", article);
			
		} catch (Exception e) {
			log.error("获取文章详情失败", e);
			model.addAttribute("article", null);
		}
		
		return "admin/article-detail";
	}
	
	/**
	 * 类别管理页面
	 * @param model 模型
	 * @param session 会话
	 * @param name 类别名称搜索条件
	 * @return 类别管理页面
	 */
	@GetMapping("/categories")
	public String categoriesPage(Model model, HttpSession session,
								 @RequestParam(required = false) String name) {
		log.info("访问类别管理页面");
		
		try {
			// 获取所有类别列表（不分页）
			List<ArticleCategory> categories;
			long totalCount;
			
			if (name != null && !name.trim().isEmpty()) {
				// 如果有搜索条件，使用条件查询
				Map<String, Object> params = new java.util.HashMap<>();
				params.put("name", name);
				categories = articleCategoryService.findByConditions(params);
				totalCount = articleCategoryService.countByConditions(params);
			} else {
				// 获取所有类别
				categories = articleCategoryService.getAllCategories();
				totalCount = categories.size();
			}
			
			// 为每个类别添加文章数量信息
			Map<String, Long> categoryArticleCountMap = new HashMap<>();
			for (ArticleCategory category : categories) {
				long articleCount = articleService.countByCategoryId(category.getId());
				categoryArticleCountMap.put(category.getId(), articleCount);
			}
			
			model.addAttribute("categories", categories);
			model.addAttribute("categoryArticleCountMap", categoryArticleCountMap);
			model.addAttribute("totalCount", totalCount);
			model.addAttribute("searchName", name);
			
		} catch (Exception e) {
			log.error("获取类别列表失败", e);
			model.addAttribute("categories", new java.util.ArrayList<>());
			model.addAttribute("categoryArticleCountMap", new HashMap<>());
			model.addAttribute("totalCount", 0);
			model.addAttribute("searchName", name);
		}
		
		return "admin/categories";
	}
	
	/**
	 * 根据ID获取类别信息
	 * @param id 类别ID
	 * @return 类别信息
	 */
	@GetMapping("/categories/{id}")
	@ResponseBody
	public Result<ArticleCategory> getCategoryById(@PathVariable String id) {
		log.info("根据ID获取类别，ID：{}", id);
		
		ArticleCategory category = articleCategoryService.getCategoryById(id);
		return category != null ? Result.success(category) : Result.error("类别不存在");
	}
	
	/**
	 * 创建类别
	 * @param category 类别信息
	 * @return 操作结果
	 */
	@PostMapping("/categories")
	@ResponseBody
	public Result<String> createCategory(@RequestBody ArticleCategory category) {
		log.info("创建文章类别，类别名称：{}", category.getName());
		
		try {
			// 检查类别名称是否已存在
			if (articleCategoryService.existsByName(category.getName())) {
				return Result.error("类别名称已存在");
			}
			
			boolean success = articleCategoryService.createCategory(category);
			return success ? Result.success("创建成功") : Result.error("创建失败");
		} catch (Exception e) {
			log.error("创建类别失败", e);
			return Result.error("创建失败");
		}
	}
	
	/**
	 * 更新类别
	 * @param id 类别ID
	 * @param category 类别信息
	 * @return 操作结果
	 */
	@PutMapping("/categories/{id}")
	@ResponseBody
	public Result<String> updateCategory(@PathVariable String id, @RequestBody ArticleCategory category) {
		category.setId(id);
		log.info("更新文章类别，ID：{}，类别名称：{}", id, category.getName());
		
		try {
			// 检查类别是否存在
			ArticleCategory existingCategory = articleCategoryService.getCategoryById(id);
			if (existingCategory == null) {
				return Result.error("类别不存在");
			}
			
			// 检查类别名称是否已被其他类别使用
			ArticleCategory categoryByName = articleCategoryService.getCategoryByName(category.getName());
			if (categoryByName != null && !categoryByName.getId().equals(id)) {
				return Result.error("类别名称已存在");
			}
			
			boolean success = articleCategoryService.updateCategory(category);
			return success ? Result.success("更新成功") : Result.error("更新失败");
		} catch (Exception e) {
			log.error("更新类别失败", e);
			return Result.error("更新失败");
		}
	}
	
	/**
	 * 删除类别
	 * @param id 类别ID
	 * @return 操作结果
	 */
	@DeleteMapping("/categories/{id}")
	@ResponseBody
	public Result<String> deleteCategory(@PathVariable String id) {
		log.info("删除文章类别，ID：{}", id);
		
		try {
			// 检查类别是否存在
			ArticleCategory category = articleCategoryService.getCategoryById(id);
			if (category == null) {
				return Result.error("类别不存在");
			}
			
			// 检查该类别下是否有文章
			long articleCount = articleService.countByCategoryId(id);
			if (articleCount > 0) {
				return Result.error("该类别下还有 " + articleCount + " 篇文章，无法删除");
			}
			
			boolean success = articleCategoryService.deleteCategory(id);
			return success ? Result.success("删除成功") : Result.error("删除失败");
		} catch (Exception e) {
			log.error("删除类别失败", e);
			return Result.error("删除失败");
		}
	}
	
	/**
	 * 批量删除类别
	 * @param categoryIds 类别ID列表
	 * @return 操作结果
	 */
	@PostMapping("/categories/batch-delete")
	@ResponseBody
	public Result<String> batchDeleteCategories(@RequestBody List<String> categoryIds) {
		log.info("批量删除文章类别，数量：{}", categoryIds.size());
		
		try {
			// 检查每个类别下是否有文章
			List<String> categoriesWithArticles = new ArrayList<>();
			for (String categoryId : categoryIds) {
				long articleCount = articleService.countByCategoryId(categoryId);
				if (articleCount > 0) {
					ArticleCategory category = articleCategoryService.getCategoryById(categoryId);
					if (category != null) {
						categoriesWithArticles.add(category.getName() + "(" + articleCount + "篇文章)");
					}
				}
			}
			
			if (!categoriesWithArticles.isEmpty()) {
				return Result.error("以下类别下还有文章，无法删除：" + String.join("、", categoriesWithArticles));
			}
			
			boolean success = articleCategoryService.batchDeleteCategories(categoryIds);
			return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
		} catch (Exception e) {
			log.error("批量删除类别失败", e);
			return Result.error("批量删除失败");
		}
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
			params.put("size", size);
			
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
	 * 根据ID获取评论
	 * @param id 评论ID
	 * @return 评论信息
	 */
	@GetMapping("/comments/{id}")
	@ResponseBody
	public Result<ArticleComment> getCommentById(@PathVariable String id) {
		log.info("获取评论详情，评论ID：{}", id);
		
		try {
			ArticleComment comment = articleCommentService.getCommentById(id);
			if (comment != null) {
				return Result.success(comment);
			} else {
				return Result.error("评论不存在");
			}
		} catch (Exception e) {
			log.error("获取评论详情失败", e);
			return Result.error("获取评论详情失败");
		}
	}
	
	/**
	 * 更新评论
	 * @param comment 评论信息
	 * @return 操作结果
	 */
	@PostMapping("/comments/update")
	@ResponseBody
	public Result<String> updateComment(@RequestBody ArticleComment comment) {
		log.info("更新评论，评论ID：{}", comment.getId());
		
		try {
			boolean success = articleCommentService.updateComment(comment);
			return success ? Result.success("更新成功") : Result.error("更新失败");
		} catch (Exception e) {
			log.error("更新评论失败", e);
			return Result.error("更新失败");
		}
	}
	
	/**
	 * 删除评论
	 * @param id 评论ID
	 * @return 操作结果
	 */
	@DeleteMapping("/comments/{id}")
	@ResponseBody
	public Result<String> deleteComment(@PathVariable String id) {
		log.info("删除评论，评论ID：{}", id);
		
		try {
			boolean success = articleCommentService.deleteComment(id);
			return success ? Result.success("删除成功") : Result.error("删除失败");
		} catch (Exception e) {
			log.error("删除评论失败", e);
			return Result.error("删除失败");
		}
	}
	
	/**
	 * 批量删除评论
	 * @param commentIds 评论ID列表
	 * @return 操作结果
	 */
	@PostMapping("/comments/batch-delete")
	@ResponseBody
	public Result<String> batchDeleteComments(@RequestBody List<String> commentIds) {
		log.info("批量删除评论，数量：{}", commentIds.size());
		
		try {
			boolean success = articleCommentService.batchDeleteComments(commentIds);
			return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
		} catch (Exception e) {
			log.error("批量删除评论失败", e);
			return Result.error("批量删除失败");
		}
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
								@RequestParam(required = false) String articleTitle,
								@RequestParam(required = false) String articleAuthor) {
		log.info("访问收藏管理页面，页码：{}，大小：{}", page, size);
		
		try {
			// 获取收藏列表
			Map<String, Object> params = new java.util.HashMap<>();
			params.put("username", username);
			params.put("articleTitle", articleTitle);
			params.put("articleAuthor", articleAuthor);
			params.put("offset", page * size);
			params.put("size", size);
			
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
    
    /**
     * 删除收藏记录
     * @param id 收藏ID
     * @return 删除结果
     */
    @PostMapping("/favorites/delete/{id}")
    @ResponseBody
    public Result<String> deleteFavorite(@PathVariable String id) {
        log.info("删除收藏记录，ID：{}", id);
        
        try {
            boolean success = articleFavoriteService.deleteFavoriteById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除收藏记录失败，ID：{}", id, e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除收藏记录
     * @param favoriteIds 收藏ID列表
     * @return 删除结果
     */
    @PostMapping("/favorites/batchDelete")
    @ResponseBody
    public Result<String> batchDeleteFavorites(@RequestBody List<String> favoriteIds) {
        log.info("批量删除收藏记录，数量：{}", favoriteIds.size());
        
        try {
            if (favoriteIds == null || favoriteIds.isEmpty()) {
                return Result.error("请选择要删除的收藏记录");
            }
            
            boolean success = articleFavoriteService.batchDeleteFavorites(favoriteIds);
            if (success) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除收藏记录失败", e);
            return Result.error("批量删除失败：" + e.getMessage());
			}
		}

	
	/**
	 * 管理员退出登录
	 * @param session 会话
	 * @return 重定向到管理员登录页面
	 */
	@GetMapping("/logout")
	public String adminLogout(HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		log.info("管理员退出登录，用户ID：{}", userId);

		session.invalidate();
		return "redirect:/admin/login";
	}
}