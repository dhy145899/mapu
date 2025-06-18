package com.hniu.mapu.service.impl;

import com.hniu.mapu.mapper.ArticleCommentMapper;
import com.hniu.mapu.pojo.entity.ArticleComment;
import com.hniu.mapu.service.ArticleCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文章评论服务实现类
 * @author jiujiu
 */
@Slf4j
@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {
	
	@Autowired
	private ArticleCommentMapper commentMapper;
	
	/**
	 * 查询文章的评论列表（树形结构）
	 * @param articleId 文章ID
	 * @return 评论列表
	 */
	@Override
	public List<ArticleComment> getCommentsByArticleId(String articleId) {
		log.info("查询文章评论，文章ID：{}", articleId);
		
		// 查询所有评论
		List<ArticleComment> allComments = commentMapper.selectCommentsByArticleId(articleId);
		
		// 构建树形结构
		return buildCommentTree(allComments);
	}
	
	/**
	 * 添加评论
	 * @param comment 评论信息
	 * @return 添加结果
	 */
	@Override
	public boolean addComment(ArticleComment comment) {
		log.info("添加评论，文章ID：{}，用户ID：{}", comment.getArticleId(), comment.getUserId());
		
		comment.setId(UUID.randomUUID().toString());
		comment.setCreateTime(LocalDateTime.now());
		
		return commentMapper.insert(comment) > 0;
	}
	
	/**
	 * 删除评论
	 * @param id 评论ID
	 * @return 删除结果
	 */
	@Override
	public boolean deleteComment(String id) {
		log.info("删除评论，评论ID：{}", id);
		return commentMapper.deleteById(id) > 0;
	}
	
	/**
	 * 构建评论树形结构
	 * @param comments 评论列表
	 * @return 树形结构评论列表
	 */
	@Override
	public List<ArticleComment> buildCommentTree(List<ArticleComment> comments) {
		List<ArticleComment> topComments = new ArrayList<>();
		Map<String, List<ArticleComment>> childrenMap = new HashMap<>();
		
		// 分组：顶级评论和子评论
		for (ArticleComment comment : comments) {
			if (comment.getParentId() == null) {
				// 顶级评论
				topComments.add(comment);
			} else {
				// 子评论
				childrenMap.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>()).add(comment);
			}
		}
		
		// 为每个顶级评论设置子评论
		for (ArticleComment topComment : topComments) {
			setChildren(topComment, childrenMap);
		}
		
		return topComments;
	}
	
	/**
	 * 递归设置子评论
	 * @param comment 父评论
	 * @param childrenMap 子评论映射
	 */
	private void setChildren(ArticleComment comment, Map<String, List<ArticleComment>> childrenMap) {
		List<ArticleComment> children = childrenMap.get(comment.getId());
		if (children != null && !children.isEmpty()) {
			comment.setChildren(children);
			// 递归设置子评论的子评论
			for (ArticleComment child : children) {
				setChildren(child, childrenMap);
			}
		}
	}
	
	/**
	 * 获取评论总数
	 * @return 评论总数
	 */
	@Override
	public long getTotalCount() {
		log.info("获取评论总数");
		return commentMapper.getTotalCount();
	}
	
	/**
	 * 根据条件查询评论列表
	 * @param params 查询参数
	 * @return 评论列表
	 */
	@Override
	public List<ArticleComment> findByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件查询评论列表，参数：{}", params);
		return commentMapper.findByConditions(params);
	}
	
	/**
	 * 根据条件统计评论数量
	 * @param params 查询参数
	 * @return 评论数量
	 */
	@Override
	public long countByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件统计评论数量，参数：{}", params);
		return commentMapper.countByConditions(params);
	}
}