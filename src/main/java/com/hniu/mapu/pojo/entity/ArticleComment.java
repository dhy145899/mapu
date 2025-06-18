package com.hniu.mapu.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章评论实体类
 * @author jiujiu
 */
@Data
public class ArticleComment {
	/**
	 * 评论ID
	 */
	private String id;
	
	/**
	 * 评论用户ID
	 */
	private String userId;
	
	/**
	 * 文章ID
	 */
	private String articleId;
	
	/**
	 * 父评论ID（NULL表示顶级评论）
	 */
	private String parentId;
	
	/**
	 * 评论内容
	 */
	private String content;
	
	/**
	 * 评论时间
	 */
	private LocalDateTime createTime;
	
	// 关联对象
	/**
	 * 评论用户信息
	 */
	private User user;
	
	/**
	 * 子评论列表
	 */
	private List<ArticleComment> children;
}