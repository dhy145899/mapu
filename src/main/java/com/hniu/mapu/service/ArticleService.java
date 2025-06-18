package com.hniu.mapu.service;

import com.github.pagehelper.PageInfo;
import com.hniu.mapu.pojo.dto.ArticleSearchDto;
import com.hniu.mapu.pojo.entity.Article;
import com.hniu.mapu.pojo.vo.ArticleVo;
import java.util.List;

/**
 * 文章服务接口
 * @author jiujiu
 */
public interface ArticleService {
	
	/**
	 * 根据ID查询文章详情
	 * @param id 文章ID
	 * @param userId 当前用户ID（用于判断是否已点赞收藏）
	 * @return 文章详情
	 */
	ArticleVo getArticleDetail(String id, String userId);
	
	/**
	 * 查询已发布的文章列表
	 * @param pageNum 页码
	 * @param pageSize 每页大小
	 * @return 文章列表
	 */
	PageInfo<ArticleVo> getPublishedArticles(Integer pageNum, Integer pageSize);
	
	/**
	 * 搜索文章
	 * @param searchDto 搜索条件
	 * @return 文章列表
	 */
	PageInfo<ArticleVo> searchArticles(ArticleSearchDto searchDto);
	
	/**
	 * 根据用户ID查询文章
	 * @param userId 用户ID
	 * @return 文章列表
	 */
	List<ArticleVo> getArticlesByUserId(String userId);
	
	/**
	 * 获取最新文章列表
	 * @param limit 限制数量
	 * @return 文章列表
	 */
	List<ArticleVo> getLatestArticles(Integer limit);
	
	/**
	 * 创建文章
	 * @param article 文章信息
	 * @return 创建成功返回文章ID，失败返回null
	 */
	String createArticle(Article article);
	
	/**
	 * 更新文章
	 * @param article 文章信息
	 * @return 更新结果
	 */
	boolean updateArticle(Article article);
	
	/**
	 * 删除文章
	 * @param id 文章ID
	 * @return 删除结果
	 */
	boolean deleteArticle(String id);
	
	/**
	 * 更新文章状态
	 * @param id 文章ID
	 * @param status 状态
	 * @return 更新结果
	 */
	boolean updateArticleStatus(String id, Integer status);
	

	

	
	/**
	 * 解析文章内容（分离文字和图片）
	 * @param content 文章内容
	 * @return 解析后的文章VO
	 */
	ArticleVo parseArticleContent(String content);
	
	/**
	 * 获取文章总数
	 * @return 文章总数
	 */
	long getTotalCount();
	
	/**
	 * 根据条件查询文章列表
	 * @param params 查询参数
	 * @return 文章列表
	 */
	List<Article> findByConditions(java.util.Map<String, Object> params);
	
	/**
	 * 根据条件统计文章数量
	 * @param params 查询参数
	 * @return 文章数量
	 */
	long countByConditions(java.util.Map<String, Object> params);
	
	/**
	 * 批量更新文章状态
	 * @param articleIds 文章ID列表
	 * @param status 状态
	 * @return 更新结果
	 */
	boolean batchUpdateStatus(List<String> articleIds, Integer status);
	
	/**
	 * 批量删除文章
	 * @param articleIds 文章ID列表
	 * @return 删除结果
	 */
	boolean batchDelete(List<String> articleIds);

}