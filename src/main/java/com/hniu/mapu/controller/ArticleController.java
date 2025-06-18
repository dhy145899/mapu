package com.hniu.mapu.controller;

import com.github.pagehelper.PageInfo;
import com.hniu.mapu.common.Result;
import com.hniu.mapu.pojo.dto.ArticleSearchDto;
import com.hniu.mapu.pojo.dto.FavoriteResponseDto;
import com.hniu.mapu.pojo.entity.Article;
import com.hniu.mapu.pojo.entity.ArticleComment;
import com.hniu.mapu.pojo.vo.ArticleVo;
import com.hniu.mapu.pojo.entity.ArticleCategory;
import com.hniu.mapu.service.ArticleService;
import com.hniu.mapu.service.ArticleCommentService;
import com.hniu.mapu.service.ArticleFavoriteService;
import com.hniu.mapu.service.ArticleCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.util.StringUtils;

import java.util.List;

/**
 * 文章控制器
 * @author jiujiu
 */
@Slf4j
@Controller
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCommentService commentService;
	
	@Autowired
	private ArticleFavoriteService favoriteService;
	
	@Autowired
	private ArticleCategoryService categoryService;
	

	/**
	 * 搜索文章
	 * @param searchDto 搜索条件
	 * @param model 模型
	 * @return 搜索结果页面
	 */
	/*@GetMapping("/search")
	public String searchArticles(ArticleSearchDto searchDto, Model model) {
		log.info("搜索文章，搜索条件：{}", searchDto);
		
		PageInfo<ArticleVo> pageInfo = articleService.searchArticles(searchDto);
		model.addAttribute("articles", pageInfo.getList());
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("searchDto", searchDto);
		model.addAttribute("categories", categoryService.getAllCategories());
		
		return "article/search";
	}*/
	@GetMapping("/search")
	public String searchArticles(ArticleSearchDto searchDto, Model model) {
		log.info("搜索文章，搜索条件：{}", searchDto);

		PageInfo<ArticleVo> pageInfo = articleService.searchArticles(searchDto);
		List<ArticleVo> list = pageInfo.getList();
		model.addAttribute("articles", list);
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("searchDto", searchDto);

		// 计算所有文章的总数
		int totalCount = list.size();
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("categories", categoryService.getAllCategories());

		// 处理空结果情况
		String activeCategoryId = null;
		if (!list.isEmpty()) {
			activeCategoryId = list.get(0).getCategoryId();
			for (ArticleVo articleVo : list) {
				if (!StringUtils.equals(activeCategoryId, articleVo.getCategoryId())) {
					activeCategoryId = null;
					break;
				}
			}
		}

		model.addAttribute("categoryId", activeCategoryId);
		return "article/search";
	}
	
	/**
	 * 文章详情页
	 * @param id 文章ID
	 * @param model 模型
	 * @param session 会话
	 * @return 文章详情页面
	 */
	@GetMapping("/detail/{id}")
	public String articleDetail(@PathVariable String id, Model model, HttpSession session) {
		log.info("查看文章详情，文章ID：{}", id);
		
		String userId = (String) session.getAttribute("userId");
		ArticleVo article = articleService.getArticleDetail(id, userId);
		
		// 获取文章评论
		List<ArticleComment> comments = commentService.getCommentsByArticleId(id);
		
		// 获取用户收藏状态
		boolean isFavorited = false;
		if (userId != null) {
			isFavorited = favoriteService.isFavorited(userId, id);
		}
		
		model.addAttribute("article", article);
		model.addAttribute("comments", comments);
		model.addAttribute("isFavorited", isFavorited);
		return "article/detail";
	}
	
	/**
	 * 文章评论页
	 * @param articleId 文章ID
	 * @param model 模型
	 * @return 评论页面
	 */
	@GetMapping("/comments/{articleId}")
	public String articleComments(@PathVariable String articleId, Model model) {
		log.info("查看文章评论，文章ID：{}", articleId);
		
		List<ArticleComment> comments = commentService.getCommentsByArticleId(articleId);
		model.addAttribute("comments", comments);
		model.addAttribute("articleId", articleId);
		
		return "comments";
	}
	
	/**
	 * 用户收藏页
	 * @param model 模型
	 * @param session 会话
	 * @return 收藏页面
	 */
	@GetMapping("/favorites")
	public String userFavorites(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		log.info("查看用户收藏，用户ID：{}", userId);
		
		List<ArticleVo> favorites = favoriteService.getFavoriteArticlesByUserId(userId);
		model.addAttribute("articles", favorites);
		model.addAttribute("totalFavorites", favorites.size());
		
		return "article/favorites";
	}
	

	
	/**
	 * 收藏/取消收藏文章
	 * @param articleId 文章ID
	 * @param session HTTP会话
	 * @return 操作结果
	 */
	@PostMapping("/{articleId}/favorite")
	@ResponseBody
	public Result<FavoriteResponseDto> toggleFavorite(@PathVariable String articleId, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		boolean success = favoriteService.toggleFavorite(userId, articleId);
		if (success) {
			// 获取当前收藏状态和收藏数量
			boolean favorited = favoriteService.isFavorited(userId, articleId);
			Integer favoriteCount = favoriteService.getFavoriteCount(articleId);
			
			FavoriteResponseDto responseDto = new FavoriteResponseDto();
			responseDto.setFavorited(favorited);
			responseDto.setFavoriteCount(favoriteCount);
			
			return Result.success(responseDto);
		} else {
			return Result.error("操作失败");
		}
	}
	
	/**
	 * 添加评论
	 * @param comment 评论信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/comment")
	@ResponseBody
	public Result<?> addComment(@RequestBody ArticleComment comment, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		log.info("接收到评论数据 - 文章ID：{}，父评论ID：{}，内容：{}", comment.getArticleId(), comment.getParentId(), comment.getContent());
		
		if (comment.getArticleId() == null || comment.getArticleId().trim().isEmpty()) {
			log.error("文章ID为空，无法添加评论");
			return Result.error("文章ID不能为空");
		}
		
		comment.setUserId(userId);
		log.info("添加评论，用户ID：{}，文章ID：{}", userId, comment.getArticleId());
		
		boolean success = commentService.addComment(comment);
		return success ? Result.success(success) : Result.error("评论失败");
	}
	
	/**
	 * 文章编辑页面
	 * @param model 模型
	 * @param session 会话
	 * @return 编辑页面
	 */
	@GetMapping("/edit")
	public String editPage(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		// 获取所有分类
		List<ArticleCategory> categories = categoryService.getAllCategories();
		model.addAttribute("categories", categories);
		
		return "article/edit";
	}
	
	/**
	 * 文章编辑页面（编辑指定文章）
	 * @param id 文章ID
	 * @param model 模型
	 * @param session 会话
	 * @return 编辑页面
	 */
	@GetMapping("/edit/{id}")
	public String editArticlePage(@PathVariable String id, Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		// 获取文章信息
		ArticleVo article = articleService.getArticleDetail(id, userId);
		if (article == null) {
			return "redirect:/article/manage";
		}
		
		// 检查权限（只能编辑自己的文章或管理员）
		Integer userRole = (Integer) session.getAttribute("userRole");
		// 检查是否为管理员或文章作者
		if (!article.getAuthorId().equals(userId) && (userRole == null || userRole != 1)) {
			return "redirect:/article/manage";
		}
		
		// 获取所有分类
		List<ArticleCategory> categories = categoryService.getAllCategories();
		model.addAttribute("article", article);
		model.addAttribute("categories", categories);
		
		return "article/edit";
	}
	
	/**
	 * 文章管理页面
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
		
		// 获取用户的文章列表
		List<ArticleVo> articles = articleService.getArticlesByUserId(userId);
		model.addAttribute("articles", articles);
		model.addAttribute("currentPage", "manage");
		
		return "article/manage";
	}
	
	/**
	 * 创建文章
	 * @param article 文章信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/create")
	@ResponseBody
	public Result<String> createArticle(@RequestBody Article article, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		article.setAuthorId(userId);
		log.info("创建文章，用户ID：{}，文章标题：{}", userId, article.getTitle());
		
		try {
			String articleId = articleService.createArticle(article);
			return articleId != null ? Result.success(articleId) : Result.error("文章创建失败");
		} catch (Exception e) {
			log.error("创建文章失败", e);
			return Result.error("创建失败：" + e.getMessage());
		}
	}
	
	/**
	 * 更新文章
	 * @param id 文章ID
	 * @param article 文章信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PutMapping("/{id}")
	@ResponseBody
	public Result<String> updateArticle(@PathVariable String id, @RequestBody Article article, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		// 检查文章是否存在
		ArticleVo existingArticle = articleService.getArticleDetail(id, userId);
		if (existingArticle == null) {
			return Result.error("文章不存在");
		}
		
		// 检查权限
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (!existingArticle.getAuthorId().equals(userId) && (userRole == null || userRole != 1)) {
			return Result.error(403, "无权限操作");
		}
		
		article.setId(id);
		log.info("更新文章，用户ID：{}，文章ID：{}", userId, id);
		
		try {
			boolean success = articleService.updateArticle(article);
			return success ? Result.success(id) : Result.error("文章更新失败");
		} catch (Exception e) {
			log.error("更新文章失败", e);
			return Result.error("更新失败：" + e.getMessage());
		}
	}
	
	/**
	 * 删除文章
	 * @param id 文章ID
	 * @param session 会话
	 * @return 操作结果
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public Result<String> deleteArticle(@PathVariable String id, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		// 检查文章是否存在
		ArticleVo article = articleService.getArticleDetail(id, userId);
		if (article == null) {
			return Result.error("文章不存在");
		}
		
		// 检查权限
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (!article.getAuthorId().equals(userId) && (userRole == null || userRole != 1)) {
			return Result.error(403, "无权限操作");
		}
		
		log.info("删除文章，用户ID：{}，文章ID：{}", userId, id);
		
		try {
			boolean success = articleService.deleteArticle(id);
			return success ? Result.success("文章删除成功") : Result.error("文章删除失败");
		} catch (Exception e) {
			log.error("删除文章失败", e);
			return Result.error("删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 更新文章状态
	 * @param id 文章ID
	 * @param status 状态
	 * @param session 会话
	 * @return 操作结果
	 */
	@PutMapping("/{id}/status")
	@ResponseBody
	public Result<String> updateArticleStatus(@PathVariable String id, @RequestParam Integer status, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		// 检查文章是否存在
		ArticleVo article = articleService.getArticleDetail(id, userId);
		if (article == null) {
			return Result.error("文章不存在");
		}
		
		// 检查权限
		Integer userRole = (Integer) session.getAttribute("userRole");
		if (!article.getAuthorId().equals(userId) && (userRole == null || userRole != 1)) {
			return Result.error(403, "无权限操作");
		}
		
		log.info("更新文章状态，用户ID：{}，文章ID：{}，状态：{}", userId, id, status);
		
		try {
			boolean success = articleService.updateArticleStatus(id, status);
			return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
		} catch (Exception e) {
			log.error("更新文章状态失败", e);
			return Result.error("更新失败：" + e.getMessage());
		}
	}
}
