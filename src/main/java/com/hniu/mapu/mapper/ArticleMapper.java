package com.hniu.mapu.mapper;

import com.hniu.mapu.pojo.dto.ArticleSearchDto;
import com.hniu.mapu.pojo.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文章数据访问层
 * @author jiujiu
 */
@Mapper
public interface ArticleMapper {
	
	/**
	 * 根据ID查询文章
	 * @param id 文章ID
	 * @return 文章信息
	 */
	Article selectById(@Param("id") String id);
	
	/**
	 * 根据ID查询文章详情（包含作者和类别信息）
	 * @param id 文章ID
	 * @return 文章详情
	 */
	Article selectDetailById(@Param("id") String id);
	
	/**
	 * 查询已发布的文章列表
	 * @return 文章列表
	 */
	List<Article> selectPublishedArticles();
	
	/**
	 * 搜索文章
	 * @param searchDto 搜索条件
	 * @return 文章列表
	 */
	List<Article> searchArticles(ArticleSearchDto searchDto);
	
	/**
	 * 根据用户ID查询文章
	 * @param userId 用户ID
	 * @return 文章列表
	 */
	List<Article> selectByUserId(@Param("userId") String userId);
	
	/**
	 * 根据类别ID查询文章
	 * @param categoryId 类别ID
	 * @return 文章列表
	 */
	List<Article> selectByCategoryId(@Param("categoryId") String categoryId);
	
	/**
	 * 插入文章
	 * @param article 文章信息
	 * @return 影响行数
	 */
	int insert(Article article);
	
	/**
	 * 更新文章
	 * @param article 文章信息
	 * @return 影响行数
	 */
	int update(Article article);
	
	/**
	 * 删除文章
	 * @param id 文章ID
	 * @return 影响行数
	 */
	int deleteById(@Param("id") String id);
	
	/**
	 * 更新文章状态
	 * @param id 文章ID
	 * @param status 状态
	 * @return 影响行数
	 */
	int updateStatus(@Param("id") String id, @Param("status") Integer status);
	
	/**
	 * 查询文章统计信息（点赞数、评论数、收藏数）
	 * @param articleId 文章ID
	 * @return 文章信息
	 */
	Article selectStatistics(@Param("articleId") String articleId);
	
	/**
	 * 查询最新文章列表
	 * @param limit 限制数量
	 * @return 文章列表
	 */
	List<Article> selectLatestArticles(@Param("limit") Integer limit);
	
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
	 * @return 影响行数
	 */
	int batchUpdateStatus(@Param("articleIds") List<String> articleIds, @Param("status") Integer status);
	
	/**
	 * 批量删除文章
	 * @param articleIds 文章ID列表
	 * @return 影响行数
	 */
	int batchDeleteArticles(@Param("articleIds") List<String> articleIds);
	
	/**
	 * 根据文章ID批量删除评论
	 * @param articleIds 文章ID列表
	 * @return 影响行数
	 */
	int batchDeleteCommentsByArticleIds(@Param("articleIds") List<String> articleIds);
	
	/**
	 * 根据文章ID批量删除收藏
	 * @param articleIds 文章ID列表
	 * @return 影响行数
	 */
	int batchDeleteFavoritesByArticleIds(@Param("articleIds") List<String> articleIds);
}