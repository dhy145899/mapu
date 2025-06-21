package com.hniu.mapu.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象
 * @author jiujiu
 */
@Data
public class ArticleVo {
	/**
	 * 文章ID
	 */
	private String id;
	
	/**
	 * 文章标题
	 */
	private String title;
	
	/**
	 * 文章简介
	 */
	private String summary;
	
	/**
	 * 文章内容（富文本）
	 */
	private String content;
	
	/**
	 * 文章内容（纯文本部分）
	 */
	private List<String> textParts;
	
	/**
	 * 文章图片列表
	 */
	private List<String> imageParts;
	
	/**
	 * 作者ID
	 */
	private String authorId;
	
	/**
	 * 作者用户名
	 */
	private String authorName;
	
	/**
	 * 作者头像
	 */
	private String authorAvatar;
	
	/**
	 * 类别ID
	 */
	private String categoryId;
	
	/**
	 * 类别名称
	 */
	private String categoryName;
	
	/**
	 * 文章状态 (0:草稿 1:已发布 2:已隐藏)
	 */
	private Integer status;
	
	/**
	 * 评论数
	 */
	private Integer commentCount;
	
	/**
	 * 收藏数
	 */
	private Integer favoriteCount;
	
	/**
	 * 是否已收藏
	 */
	private Boolean isFavorited;
	

	
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	
	/**
	 * 收藏时间
	 */
	private LocalDateTime favoriteTime;
}