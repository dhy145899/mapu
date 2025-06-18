package com.hniu.mapu.pojo.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章搜索DTO
 * @author jiujiu
 */
@Data
public class ArticleSearchDto {
	/**
	 * 搜索关键词
	 */
	private String keyword;
	
	/**
	 * 文章类别ID
	 */
	private String categoryId;
	
	/**
	 * 排序方式：create_time-创建时间，comment_count-评论数
	 */
	private String sortBy;
	
	/**
	 * 排序方向：asc-升序，desc-降序
	 */
	private String sortOrder;
	
	/**
	 * 开始日期
	 */
	private String startDate;
	
	/**
	 * 结束日期
	 */
	private String endDate;
	
	/**
	 * 页码
	 */
	private Integer pageNum = 1;
	
	/**
	 * 每页大小
	 */
	private Integer pageSize = 10;
}