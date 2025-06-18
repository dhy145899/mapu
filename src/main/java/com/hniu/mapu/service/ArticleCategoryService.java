package com.hniu.mapu.service;

import com.hniu.mapu.pojo.entity.ArticleCategory;
import java.util.List;

/**
 * 文章类别服务接口
 * @author jiujiu
 */
public interface ArticleCategoryService {
	
	/**
	 * 根据ID查询类别
	 * @param id 类别ID
	 * @return 类别信息
	 */
	ArticleCategory getCategoryById(String id);
	
	/**
	 * 查询所有类别
	 * @return 类别列表
	 */
	List<ArticleCategory> getAllCategories();
	
	/**
	 * 创建类别
	 * @param category 类别信息
	 * @return 创建结果
	 */
	boolean createCategory(ArticleCategory category);
	
	/**
	 * 更新类别
	 * @param category 类别信息
	 * @return 更新结果
	 */
	boolean updateCategory(ArticleCategory category);
	
	/**
	 * 删除类别
	 * @param id 类别ID
	 * @return 删除结果
	 */
	boolean deleteCategory(String id);
	
	/**
	 * 检查类别名称是否存在
	 * @param name 类别名称
	 * @return 是否存在
	 */
	boolean existsByName(String name);
	
	/**
	 * 根据名称查询类别
	 * @param name 类别名称
	 * @return 类别信息
	 */
	ArticleCategory getCategoryByName(String name);
	
	/**
	 * 获取类别总数
	 * @return 类别总数
	 */
	long getTotalCount();
	
	/**
	 * 查询所有类别（别名方法）
	 * @return 类别列表
	 */
	default List<ArticleCategory> findAll() {
		return getAllCategories();
	}
	
	/**
	 * 根据条件查询类别列表
	 * @param params 查询参数
	 * @return 类别列表
	 */
	List<ArticleCategory> findByConditions(java.util.Map<String, Object> params);
	
	/**
	 * 根据条件统计类别数量
	 * @param params 查询参数
	 * @return 类别数量
	 */
	long countByConditions(java.util.Map<String, Object> params);
}