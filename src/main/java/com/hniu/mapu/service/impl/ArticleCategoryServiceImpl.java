package com.hniu.mapu.service.impl;

import com.hniu.mapu.mapper.ArticleCategoryMapper;
import com.hniu.mapu.pojo.entity.ArticleCategory;
import com.hniu.mapu.service.ArticleCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 文章类别服务实现类
 * @author jiujiu
 */
@Slf4j
@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {
	
	@Autowired
	private ArticleCategoryMapper categoryMapper;
	
	/**
	 * 根据ID查询类别
	 * @param id 类别ID
	 * @return 类别信息
	 */
	@Override
	public ArticleCategory getCategoryById(String id) {
		log.info("查询类别信息，类别ID：{}", id);
		return categoryMapper.selectById(id);
	}
	
	/**
	 * 查询所有类别
	 * @return 类别列表
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ArticleCategory> getAllCategories() {
		log.info("查询所有类别");
		return categoryMapper.selectAll();
	}
	
	/**
	 * 创建类别
	 * @param category 类别信息
	 * @return 创建结果
	 */
	@Override
	public boolean createCategory(ArticleCategory category) {
		log.info("创建类别，类别名称：{}", category.getName());
		
		// 检查类别名称是否已存在
		if (existsByName(category.getName())) {
			throw new RuntimeException("类别名称已存在");
		}
		
		category.setId(UUID.randomUUID().toString());
		category.setCreateTime(LocalDateTime.now());
		
		return categoryMapper.insert(category) > 0;
	}
	
	/**
	 * 更新类别
	 * @param category 类别信息
	 * @return 更新结果
	 */
	@Override
	public boolean updateCategory(ArticleCategory category) {
		log.info("更新类别，类别ID：{}", category.getId());
		
		// 检查类别名称是否已存在（排除自己）
		ArticleCategory existCategory = categoryMapper.selectByName(category.getName());
		if (existCategory != null && !existCategory.getId().equals(category.getId())) {
			throw new RuntimeException("类别名称已存在");
		}
		
		return categoryMapper.update(category) > 0;
	}
	
	/**
	 * 删除类别
	 * @param id 类别ID
	 * @return 删除结果
	 */
	@Override
	public boolean deleteCategory(String id) {
		log.info("删除类别，类别ID：{}", id);
		return categoryMapper.deleteById(id) > 0;
	}
	
	/**
	 * 检查类别名称是否存在
	 * @param name 类别名称
	 * @return 是否存在
	 */
	@Override
	public boolean existsByName(String name) {
		ArticleCategory category = categoryMapper.selectByName(name);
		return category != null;
	}
	
	/**
	 * 根据名称查询类别
	 * @param name 类别名称
	 * @return 类别信息
	 */
	@Override
	public ArticleCategory getCategoryByName(String name) {
		log.info("根据名称查询类别，类别名称：{}", name);
		return categoryMapper.selectByName(name);
	}
	
	/**
	 * 获取类别总数
	 * @return 类别总数
	 */
	@Override
	public long getTotalCount() {
		log.info("获取类别总数");
		return categoryMapper.getTotalCount();
	}
	
	/**
	 * 根据条件查询类别列表
	 * @param params 查询参数
	 * @return 类别列表
	 */
	@Override
	public List<ArticleCategory> findByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件查询类别列表，参数：{}", params);
		return categoryMapper.findByConditions(params);
	}
	
	/**
	 * 根据条件统计类别数量
	 * @param params 查询参数
	 * @return 类别数量
	 */
	@Override
	public long countByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件统计类别数量，参数：{}", params);
		return categoryMapper.countByConditions(params);
	}
}