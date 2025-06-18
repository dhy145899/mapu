package com.hniu.mapu.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章分类实体类
 * @author jiujiu
 */
@Data
public class Category {
	/**
	 * 分类ID
	 */
	private String id;
	
	/**
	 * 分类名称
	 */
	private String name;
	
	/**
	 * 分类描述
	 */
	private String description;
	
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	
	/**
	 * 状态：0-禁用，1-启用
	 */
	private Integer status;
}