package com.hniu.mapu.mapper;

import com.hniu.mapu.pojo.entity.ArticleComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文章评论数据访问层
 * @author jiujiu
 */
@Mapper
public interface ArticleCommentMapper {
	
	/**
	 * 根据ID查询评论
	 * @param id 评论ID
	 * @return 评论信息
	 */
	ArticleComment selectById(@Param("id") String id);
	
	/**
	 * 查询文章的顶级评论
	 * @param articleId 文章ID
	 * @return 顶级评论列表
	 */
	List<ArticleComment> selectTopCommentsByArticleId(@Param("articleId") String articleId);
	
	/**
	 * 查询评论的子评论
	 * @param parentId 父评论ID
	 * @return 子评论列表
	 */
	List<ArticleComment> selectChildCommentsByParentId(@Param("parentId") String parentId);
	
	/**
	 * 查询文章的所有评论（包含用户信息）
	 * @param articleId 文章ID
	 * @return 评论列表
	 */
	List<ArticleComment> selectCommentsByArticleId(@Param("articleId") String articleId);
	
	/**
	 * 插入评论
	 * @param comment 评论信息
	 * @return 影响行数
	 */
	int insert(ArticleComment comment);
	
	/**
	 * 更新评论
	 * @param comment 评论信息
	 * @return 影响行数
	 */
	int update(ArticleComment comment);
	
	/**
	 * 删除评论
	 * @param id 评论ID
	 * @return 影响行数
	 */
	int deleteById(@Param("id") String id);
	
	/**
	 * 统计文章评论数
	 * @param articleId 文章ID
	 * @return 评论数
	 */
	int countByArticleId(@Param("articleId") String articleId);
	
	/**
	 * 获取评论总数
	 * @return 评论总数
	 */
	long getTotalCount();
	
	/**
	 * 根据条件查询评论列表
	 * @param params 查询参数
	 * @return 评论列表
	 */
	List<ArticleComment> findByConditions(java.util.Map<String, Object> params);
	
	/**
	 * 根据条件统计评论数量
	 * @param params 查询参数
	 * @return 评论数量
	 */
	long countByConditions(java.util.Map<String, Object> params);
}