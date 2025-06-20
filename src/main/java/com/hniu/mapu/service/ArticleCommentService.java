package com.hniu.mapu.service;

import com.hniu.mapu.pojo.entity.ArticleComment;
import java.util.List;

/**
 * 文章评论服务接口
 * @author jiujiu
 */
public interface ArticleCommentService {
	
	/**
	 * 查询文章的评论列表（树形结构）
	 * @param articleId 文章ID
	 * @return 评论列表
	 */
	List<ArticleComment> getCommentsByArticleId(String articleId);
	
	/**
	 * 根据ID获取评论
	 * @param id 评论ID
	 * @return 评论信息
	 */
	ArticleComment getCommentById(String id);
	
	/**
	 * 添加评论
	 * @param comment 评论信息
	 * @return 添加结果
	 */
	boolean addComment(ArticleComment comment);
	
	/**
	 * 删除评论
	 * @param id 评论ID
	 * @return 删除结果
	 */
	boolean deleteComment(String id);
	
	/**
	 * 更新评论
	 * @param comment 评论信息
	 * @return 更新结果
	 */
	boolean updateComment(ArticleComment comment);
	
	/**
	 * 批量删除评论
	 * @param commentIds 评论ID列表
	 * @return 删除结果
	 */
	boolean batchDeleteComments(List<String> commentIds);
	
	/**
	 * 构建评论树形结构
	 * @param comments 评论列表
	 * @return 树形结构评论列表
	 */
	List<ArticleComment> buildCommentTree(List<ArticleComment> comments);
	
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