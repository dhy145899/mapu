package com.hniu.mapu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hniu.mapu.mapper.ArticleMapper;
import com.hniu.mapu.mapper.ArticleFavoriteMapper;
import com.hniu.mapu.pojo.dto.ArticleSearchDto;
import com.hniu.mapu.pojo.entity.Article;
import com.hniu.mapu.pojo.entity.ArticleFavorite;
import com.hniu.mapu.pojo.vo.ArticleVo;
import com.hniu.mapu.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章服务实现类
 * @author jiujiu
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
	
	@Autowired
	private ArticleMapper articleMapper;
	
	@Autowired
	private ArticleFavoriteMapper articleFavoriteMapper;
	
	/**
	 * 根据ID查询文章详情
	 * @param id 文章ID
	 * @param userId 当前用户ID（用于判断是否已点赞收藏）
	 * @return 文章详情
	 */
	@Override
	public ArticleVo getArticleDetail(String id, String userId) {
		log.info("查询文章详情，文章ID：{}，用户ID：{}", id, userId);
		
		Article article = articleMapper.selectDetailById(id);
		if (article == null) {
			throw new RuntimeException("文章不存在");
		}
		
		ArticleVo articleVo = convertToVo(article);
		
		// 解析文章内容
		ArticleVo parsedContent = parseArticleContent(article.getContent());
		articleVo.setTextParts(parsedContent.getTextParts());
		articleVo.setImageParts(parsedContent.getImageParts());
		
		// 查询统计信息
		Article statistics = articleMapper.selectStatistics(id);
		articleVo.setCommentCount(statistics.getCommentCount());
		articleVo.setFavoriteCount(statistics.getFavoriteCount());
		
		// 查询用户是否已收藏
		if (userId != null) {
			ArticleFavorite favorite = articleFavoriteMapper.selectByUserAndArticle(userId, id);
			articleVo.setIsFavorited(favorite != null);
		}
		
		return articleVo;
	}
	
	/**
	 * 获取已发布的文章列表（分页）
	 * @param pageNum 页码
	 * @param pageSize 每页大小
	 * @return 文章列表
	 */
	@Override
	@Transactional(readOnly = true)
	public PageInfo<ArticleVo> getPublishedArticles(Integer pageNum, Integer pageSize) {
		log.info("查询已发布文章列表，页码：{}，每页大小：{}", pageNum, pageSize);
		
		PageHelper.startPage(pageNum, pageSize);
		List<Article> articles = articleMapper.selectPublishedArticles();
		
		List<ArticleVo> articleVos = new ArrayList<>();
		for (Article article : articles) {
			ArticleVo vo = convertToVo(article);
			articleVos.add(vo);
		}
		
		return new PageInfo<>(articleVos);
	}
	
	/**
	 * 搜索文章
	 * @param searchDto 搜索条件
	 * @return 文章列表
	 */
	@Override
	public PageInfo<ArticleVo> searchArticles(ArticleSearchDto searchDto) {
		log.info("搜索文章，搜索条件：{}", searchDto);
		
		PageHelper.startPage(searchDto.getPageNum(), searchDto.getPageSize());
		List<Article> articles = articleMapper.searchArticles(searchDto);
		
		List<ArticleVo> articleVos = new ArrayList<>();
		for (Article article : articles) {
			ArticleVo vo = convertToVo(article);
			articleVos.add(vo);
		}
		
		return new PageInfo<>(articleVos);
	}
	
	/**
	 * 根据用户ID查询文章
	 * @param userId 用户ID
	 * @return 文章列表
	 */
	@Override
	public List<ArticleVo> getArticlesByUserId(String userId) {
		log.info("根据用户ID查询文章，用户ID：{}", userId);
		List<Article> articles = articleMapper.selectByUserId(userId);
		
		List<ArticleVo> articleVos = new ArrayList<>();
		for (Article article : articles) {
			ArticleVo vo = convertToVo(article);
			articleVos.add(vo);
		}
		
		return articleVos;
	}
	
	/**
	 * 创建文章
	 * @param article 文章信息
	 * @return 创建成功返回文章ID，失败返回null
	 */
	@Override
	public String createArticle(Article article) {
		log.info("创建文章，标题：{}", article.getTitle());
		
		String articleId = UUID.randomUUID().toString();
		article.setId(articleId);
		article.setCreateTime(LocalDateTime.now());
		article.setUpdateTime(LocalDateTime.now());
		

		
		return articleMapper.insert(article) > 0 ? articleId : null;
	}
	
	/**
	 * 更新文章
	 * @param article 文章信息
	 * @return 更新结果
	 */
	@Override
	public boolean updateArticle(Article article) {
		log.info("更新文章，文章ID：{}", article.getId());
		
		article.setUpdateTime(LocalDateTime.now());
		

		
		return articleMapper.update(article) > 0;
	}
	
	/**
	 * 删除文章
	 * @param id 文章ID
	 * @return 删除结果
	 */
	@Override
	@Transactional
	public boolean deleteArticle(String id) {
		log.info("删除文章，文章ID：{}", id);
		
		// 删除文章相关的评论
		List<String> articleIds = Arrays.asList(id);
		articleMapper.batchDeleteCommentsByArticleIds(articleIds);
		log.info("删除文章相关评论，文章ID：{}", id);
		
		// 删除文章相关的收藏
		articleMapper.batchDeleteFavoritesByArticleIds(articleIds);
		log.info("删除文章相关收藏，文章ID：{}", id);
		
		// 删除文章
		return articleMapper.deleteById(id) > 0;
	}
	
	/**
	 * 更新文章状态
	 * @param id 文章ID
	 * @param status 状态
	 * @return 更新结果
	 */
	@Override
	public boolean updateArticleStatus(String id, Integer status) {
		log.info("更新文章状态，文章ID：{}，状态：{}", id, status);
		return articleMapper.updateStatus(id, status) > 0;
	}
	

	

	
	/**
	 * 获取最新文章列表
	 * @param limit 限制数量
	 * @return 文章列表
	 */
	@Override
	public List<ArticleVo> getLatestArticles(Integer limit) {
		log.info("获取最新文章列表，限制数量：{}", limit);
		List<Article> articles = articleMapper.selectLatestArticles(limit);
		
		List<ArticleVo> articleVos = new ArrayList<>();
		for (Article article : articles) {
			ArticleVo vo = convertToVo(article);
			articleVos.add(vo);
		}
		
		return articleVos;
	}
	

	
	/**
	 * 解析文章内容（分离文字和图片）
	 * @param content 文章内容
	 * @return 解析后的文章VO
	 */
	@Override
	public ArticleVo parseArticleContent(String content) {
		ArticleVo articleVo = new ArticleVo();
		List<String> textParts = new ArrayList<>();
		List<String> imageParts = new ArrayList<>();
		
		if (content == null || content.trim().isEmpty()) {
			articleVo.setTextParts(textParts);
			articleVo.setImageParts(imageParts);
			return articleVo;
		}
		
		// 使用正则表达式匹配图片标签
		Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);
		Matcher imgMatcher = imgPattern.matcher(content);
		
		// 提取图片URL
		while (imgMatcher.find()) {
			String imgSrc = imgMatcher.group(1);
			imageParts.add(imgSrc);
		}
		
		// 移除HTML标签，提取纯文本
		String textContent = content.replaceAll("<[^>]+>", "");
		// 按段落分割文本
		String[] paragraphs = textContent.split("\\n+");
		for (String paragraph : paragraphs) {
			String trimmed = paragraph.trim();
			if (!trimmed.isEmpty()) {
				textParts.add(trimmed);
			}
		}
		
		articleVo.setTextParts(textParts);
		articleVo.setImageParts(imageParts);
		return articleVo;
	}
	
	/**
	 * 转换Article为ArticleVo
	 * @param article 文章实体
	 * @return 文章VO
	 */
	private ArticleVo convertToVo(Article article) {
		ArticleVo vo = new ArticleVo();
		BeanUtils.copyProperties(article, vo);
		
		if (article.getAuthor() != null) {
			vo.setAuthorName(article.getAuthor().getNickname() != null && !article.getAuthor().getNickname().trim().isEmpty() ? article.getAuthor().getNickname() : article.getAuthor().getUsername());
			vo.setAuthorAvatar(article.getAuthor().getAvatar());
		}
		
		if (article.getCategory() != null) {
			vo.setCategoryId(article.getCategory().getId());
			vo.setCategoryName(article.getCategory().getName());
		}
		
		return vo;
	}
	
	/**
	 * 获取文章总数
	 * @return 文章总数
	 */
	@Override
	public long getTotalCount() {
		log.info("获取文章总数");
		return articleMapper.getTotalCount();
	}
	
	/**
	 * 根据条件查询文章列表
	 * @param params 查询参数
	 * @return 文章列表
	 */
	@Override
	public List<Article> findByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件查询文章列表，参数：{}", params);
		return articleMapper.findByConditions(params);
	}
	
	/**
	 * 根据条件统计文章数量
	 * @param params 查询参数
	 * @return 文章数量
	 */
	@Override
	public long countByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件统计文章数量，参数：{}", params);
		return articleMapper.countByConditions(params);
	}
	
	@Override
	public long countByCategoryId(String categoryId) {
		if (categoryId == null || categoryId.trim().isEmpty()) {
			return 0;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("categoryId", categoryId);
		return articleMapper.countByConditions(params);
	}
	
	/**
	 * 批量更新文章状态
	 * @param articleIds 文章ID列表
	 * @param status 状态
	 * @return 更新结果
	 */
	@Override
	@Transactional
	public boolean batchUpdateStatus(List<String> articleIds, Integer status) {
		log.info("批量更新文章状态，文章ID列表：{}，状态：{}", articleIds, status);
		
		if (articleIds == null || articleIds.isEmpty()) {
			return false;
		}
		
		try {
			int result = articleMapper.batchUpdateStatus(articleIds, status);
			return result > 0;
		} catch (Exception e) {
			log.error("批量更新文章状态失败", e);
			return false;
		}
	}
	
	/**
	 * 批量删除文章
	 * @param articleIds 文章ID列表
	 * @return 删除结果
	 */
	@Override
	@Transactional
	public boolean batchDelete(List<String> articleIds) {
		log.info("批量删除文章，文章ID列表：{}", articleIds);
		
		if (articleIds == null || articleIds.isEmpty()) {
			return false;
		}
		
		try {
			// 先删除相关的评论和收藏
			articleMapper.batchDeleteCommentsByArticleIds(articleIds);
			articleMapper.batchDeleteFavoritesByArticleIds(articleIds);
			
			// 再删除文章
			int result = articleMapper.batchDeleteArticles(articleIds);
			return result > 0;
		} catch (Exception e) {
			log.error("批量删除文章失败", e);
			return false;
		}
	}
	
	/**
	 * 根据ID查询文章
	 * @param id 文章ID
	 * @return 文章信息
	 */
	@Override
	public Article getArticleById(String id) {
		log.info("根据ID查询文章，文章ID：{}", id);
		
		if (id == null || id.trim().isEmpty()) {
			log.warn("文章ID为空");
			return null;
		}
		
		try {
			Article article = articleMapper.selectById(id);
			log.info("查询文章结果：{}", article != null ? "找到文章" : "文章不存在");
			return article;
		} catch (Exception e) {
			log.error("根据ID查询文章失败，文章ID：{}", id, e);
			return null;
		}
	}
}