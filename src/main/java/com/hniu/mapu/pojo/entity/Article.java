package com.hniu.mapu.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章实体类
 * @author jiujiu
 */
@Data
public class Article {
	/**
	 * 文章ID
	 */
	private String id;
	
	/**
	 * 作者ID
	 */
	private String userId;
	
	/**
	 * 作者ID别名（用于兼容性）
	 */
	public String getAuthorId() {
		return this.userId;
	}
	
	public void setAuthorId(String authorId) {
		this.userId = authorId;
	}
	
	/**
	 * 类别ID
	 */
	private String categoryId;
	
	/**
	 * 文章标题
	 */
	private String title;
	
	/**
	 * 文章简介
	 */
	private String summary;
	
	/**
	 * 文章内容
	 */
	private String content;
	
	/**
	 * 状态：0-审核中，1-已发布，2-审核未通过，3-草稿
	 */
	private Integer status;
	
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	

	
	/**
	 * 是否允许评论：0-不允许，1-允许
	 */
	private Integer allowComments;
	

	
	// 关联对象
	/**
	 * 作者信息
	 */
	private User author;
	
	/**
	 * 类别信息
	 */
	private ArticleCategory category;
	

	
	/**
	 * 评论数
	 */
	private Integer commentCount;
	
	/**
	 * 收藏数
	 */
	private Integer favoriteCount;
}