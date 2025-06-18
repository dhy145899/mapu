package com.hniu.mapu.mapper;

import com.hniu.mapu.pojo.entity.ArticleFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文章收藏数据访问层
 * @author jiujiu
 */
@Mapper
public interface ArticleFavoriteMapper {
	
	/**
	 * 查询用户是否已收藏文章
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 收藏记录
	 */
	ArticleFavorite selectByUserAndArticle(@Param("userId") String userId, @Param("articleId") String articleId);
	
	/**
	 * 查询用户收藏的文章列表
	 * @param userId 用户ID
	 * @return 收藏列表
	 */
	List<ArticleFavorite> selectByUserId(@Param("userId") String userId);
	
	/**
	 * 插入收藏记录
	 * @param articleFavorite 收藏信息
	 * @return 影响行数
	 */
	int insert(ArticleFavorite articleFavorite);
	
	/**
	 * 删除收藏记录
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 影响行数
	 */
	int deleteByUserAndArticle(@Param("userId") String userId, @Param("articleId") String articleId);
	
	/**
	 * 统计文章收藏数
	 * @param articleId 文章ID
	 * @return 收藏数
	 */
	int countByArticleId(@Param("articleId") String articleId);
	
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