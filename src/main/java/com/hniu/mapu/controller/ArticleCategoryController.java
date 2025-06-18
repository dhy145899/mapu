package com.hniu.mapu.controller;

import com.hniu.mapu.common.Result;
import com.hniu.mapu.pojo.entity.ArticleCategory;
import com.hniu.mapu.service.ArticleCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * 文章类别控制器
 * @author jiujiu
 */
@Slf4j
@Controller
@RequestMapping("/category")
public class ArticleCategoryController {
	
	@Autowired
	private ArticleCategoryService categoryService;
	
	/**
	 * 类别管理页面
	 * @param model 模型
	 * @param session 会话
	 * @return 类别管理页面
	 */
	@GetMapping("/manage")
	public String managePage(Model model, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/user/login";
		}
		
		List<ArticleCategory> categories = categoryService.getAllCategories();
		model.addAttribute("categories", categories);
		return "category/manage";
	}
	
	/**
	 * 获取所有类别
	 * @return 类别列表
	 */
	@GetMapping("/list")
	@ResponseBody
	public Result<List<ArticleCategory>> getAllCategories() {
		log.info("获取所有文章类别");
		
		List<ArticleCategory> categories = categoryService.getAllCategories();
		return Result.success(categories);
	}
	
	/**
	 * 根据ID获取类别
	 * @param id 类别ID
	 * @return 类别信息
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public Result<ArticleCategory> getCategoryById(@PathVariable String id) {
		log.info("根据ID获取类别，ID：{}", id);
		
		ArticleCategory category = categoryService.getCategoryById(id);
		return category != null ? Result.success(category) : Result.error("类别不存在");
	}
	
	/**
	 * 创建类别
	 * @param category 类别信息
	 * @param session 会话
	 * @return 操作结果
	 */
	@PostMapping("/create")
	@ResponseBody
	public Result<String> createCategory(@RequestBody ArticleCategory category, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		log.info("创建文章类别，类别名称：{}", category.getName());
		
		try {
			// 检查类别名称是否已存在
			if (categoryService.existsByName(category.getName())) {
				return Result.error("类别名称已存在");
			}
			
			boolean success = categoryService.createCategory(category);
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
	 * @param session 会话
	 * @return 操作结果
	 */
	@PutMapping("/{id}")
	@ResponseBody
	public Result<String> updateCategory(@PathVariable String id,
										 @RequestBody ArticleCategory category,
										 HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		category.setId(id);
		log.info("更新文章类别，ID：{}，类别名称：{}", id, category.getName());
		
		try {
			// 检查类别是否存在
			ArticleCategory existingCategory = categoryService.getCategoryById(id);
			if (existingCategory == null) {
				return Result.error("类别不存在");
			}
			
			// 检查类别名称是否已被其他类别使用
			ArticleCategory categoryByName = categoryService.getCategoryByName(category.getName());
			if (categoryByName != null && !categoryByName.getId().equals(id)) {
				return Result.error("类别名称已存在");
			}
			
			boolean success = categoryService.updateCategory(category);
			return success ? Result.success("更新成功") : Result.error("更新失败");
		} catch (Exception e) {
			log.error("更新类别失败", e);
			return Result.error("更新失败");
		}
	}
	
	/**
	 * 删除类别
	 * @param id 类别ID
	 * @param session 会话
	 * @return 操作结果
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public Result<String> deleteCategory(@PathVariable String id, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		if (userId == null) {
			return Result.error(401, "请先登录");
		}
		
		log.info("删除文章类别，ID：{}", id);
		
		try {
			// 检查类别是否存在
			ArticleCategory category = categoryService.getCategoryById(id);
			if (category == null) {
				return Result.error("类别不存在");
			}
			
			boolean success = categoryService.deleteCategory(id);
			return success ? Result.success("删除成功") : Result.error("删除失败");
		} catch (Exception e) {
			log.error("删除类别失败", e);
			return Result.error("删除失败");
		}
	}
	
	/**
	 * 检查类别名称是否存在
	 * @param name 类别名称
	 * @return 检查结果
	 */
	@GetMapping("/checkName")
	@ResponseBody
	public Result<Boolean> checkCategoryName(@RequestParam String name) {
		log.info("检查类别名称是否存在：{}", name);
		
		boolean exists = categoryService.existsByName(name);
		return Result.success(exists);
	}
}