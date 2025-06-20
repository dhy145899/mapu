package com.hniu.mapu.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章收藏实体类
 * @author jiujiu
 */
@Data
public class ArticleFavorite {
	/**
	 * 收藏ID
	 */
	private String id;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 文章ID
	 */
	private String articleId;
	
	/**
	 * 收藏时间
	 */
	private LocalDateTime createTime;
	
	// 关联对象
	/**
	 * 文章信息
	 */
	private Article article;
	
	/**
	 * 作者信息
	 */
	private User author;
	
	/**
	 * 分类信息
	 */
	private ArticleCategory category;
	
	// 管理端显示用的冗余字段
	/**
	 * 收藏用户名（冗余字段，用于管理端显示）
	 */
	private String username;
	
	/**
	 * 文章标题（冗余字段，用于管理端显示）
	 */
	private String articleTitle;
	
	/**
	 * 文章作者（冗余字段，用于管理端显示）
	 */
	private String articleAuthor;
}