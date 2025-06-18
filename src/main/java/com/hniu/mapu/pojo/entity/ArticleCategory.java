package com.hniu.mapu.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章类别实体类
 * @author jiujiu
 */
@Data
public class ArticleCategory {
	/**
	 * 类别ID
	 */
	private String id;
	
	/**
	 * 类别名称（如小说、论文、日常）
	 */
	private String name;
	
	/**
	 * 类别描述
	 */
	private String description;
	
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
}