package com.hniu.mapu.service;

import com.hniu.mapu.pojo.entity.ArticleFavorite;
import com.hniu.mapu.pojo.vo.ArticleVo;
import java.util.List;

/**
 * 文章收藏服务接口
 * @author jiujiu
 */
public interface ArticleFavoriteService {
	
	/**
	 * 查询用户收藏的文章列表
	 * @param userId 用户ID
	 * @return 收藏的文章列表
	 */
	List<ArticleVo> getFavoriteArticlesByUserId(String userId);
	
	/**
	 * 检查用户是否已收藏文章
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 是否已收藏
	 */
	boolean isFavorited(String userId, String articleId);
	
	/**
	 * 添加收藏
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 添加结果
	 */
	boolean addFavorite(String userId, String articleId);
	
	/**
	 * 取消收藏
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 取消结果
	 */
	boolean removeFavorite(String userId, String articleId);
	
	/**
	 * 切换收藏状态
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 操作结果
	 */
	boolean toggleFavorite(String userId, String articleId);
	
	/**
	 * 获取文章收藏数量
	 * @param articleId 文章ID
	 * @return 收藏数量
	 */
	Integer getFavoriteCount(String articleId);
	
	/**
	 * 获取收藏总数
	 * @return 收藏总数
	 */
	long getTotalCount();
	
	/**
	 * 根据条件查询收藏列表
	 * @param params 查询参数
	 * @return 收藏列表
	 */
	List<ArticleFavorite> findByConditions(java.util.Map<String, Object> params);
	
	/**
	 * 根据条件统计收藏数量
	 * @param params 查询参数
	 * @return 收藏数量
	 */
	long countByConditions(java.util.Map<String, Object> params);
}