package com.hniu.mapu.mapper;

import com.hniu.mapu.pojo.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文章类别数据访问层
 * @author jiujiu
 */
@Mapper
public interface ArticleCategoryMapper {
	
	/**
	 * 根据ID查询类别
	 * @param id 类别ID
	 * @return 类别信息
	 */
	ArticleCategory selectById(@Param("id") String id);
	
	/**
	 * 查询所有类别
	 * @return 类别列表
	 */
	List<ArticleCategory> selectAll();
	
	/**
	 * 根据名称查询类别
	 * @param name 类别名称
	 * @return 类别信息
	 */
	ArticleCategory selectByName(@Param("name") String name);
	
	/**
	 * 插入类别
	 * @param category 类别信息
	 * @return 影响行数
	 */
	int insert(ArticleCategory category);
	
	/**
	 * 更新类别
	 * @param category 类别信息
	 * @return 影响行数
	 */
	int update(ArticleCategory category);
	
	/**
	 * 删除类别
	 * @param id 类别ID
	 * @return 影响行数
	 */
	int deleteById(@Param("id") String id);
	
	/**
	 * 获取类别总数
	 * @return 类别总数
	 */
	long getTotalCount();
	
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