package com.hniu.mapu.pojo.entity;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author jiujiu
 */
@Data
@ToString
public class User {
	/**
	 * 用户ID
	 */
	private String id;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 密码（加密存储）
	 */
	private String password;
	
	/**
	 * 头像URL
	 */
	private String avatar;
	
	/**
	 * 性别：0-未知，1-男，2-女
	 */
	private Integer gender;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 角色：0-普通用户，1-管理员
	 */
	private Integer role;
	
	/**
	 * 账号是否启用：0-禁用，1-启用
	 */
	private Integer isEnabled;
	
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
}