package com.hniu.mapu.pojo.dto;

import lombok.Data;

/**
 * 收藏响应数据传输对象
 * @author jiujiu
 */
@Data
public class FavoriteResponseDto {
	
	/**
	 * 是否已收藏
	 */
	private boolean favorited;
	
	/**
	 * 收藏总数
	 */
	private Integer favoriteCount;
	
	/**
	 * 构造方法
	 * @param favorited 是否已收藏
	 * @param favoriteCount 收藏总数
	 */
	public FavoriteResponseDto(boolean favorited, Integer favoriteCount) {
		this.favorited = favorited;
		this.favoriteCount = favoriteCount;
	}
	
	/**
	 * 默认构造方法
	 */
	public FavoriteResponseDto() {
	}
}