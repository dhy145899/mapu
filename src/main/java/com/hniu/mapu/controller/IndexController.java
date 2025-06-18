package com.hniu.mapu.controller;

import com.github.pagehelper.PageInfo;
import com.hniu.mapu.pojo.entity.ArticleCategory;
import com.hniu.mapu.pojo.vo.ArticleVo;
import com.hniu.mapu.service.ArticleCategoryService;
import com.hniu.mapu.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * 首页控制器
 * @author jiujiu
 */
@Slf4j
@Controller
public class IndexController {
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCategoryService categoryService;
	
	/**
	 * 首页 - 展示文章列表
	 * @param pageNum 页码
	 * @param pageSize 每页大小
	 * @param model 模型
	 * @return 首页模板
	 */
	@GetMapping("/")
	public String index(Model model, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "6") Integer pageSize) {
		// 获取所有分类
		List<ArticleCategory> categories = categoryService.getAllCategories();
		model.addAttribute("categories", categories);
		
		// 获取最新文章列表（分页）
		PageInfo<ArticleVo> pageInfo = articleService.getPublishedArticles(pageNum, pageSize);
		model.addAttribute("articles", pageInfo.getList());
		model.addAttribute("pageInfo", pageInfo);
		
		return "index";
	}
}