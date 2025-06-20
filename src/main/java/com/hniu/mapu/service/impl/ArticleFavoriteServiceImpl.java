package com.hniu.mapu.service.impl;

import com.hniu.mapu.mapper.ArticleFavoriteMapper;
import com.hniu.mapu.pojo.entity.ArticleFavorite;
import com.hniu.mapu.pojo.vo.ArticleVo;
import com.hniu.mapu.service.ArticleFavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文章收藏服务实现类
 * @author jiujiu
 */
@Slf4j
@Service
public class ArticleFavoriteServiceImpl implements ArticleFavoriteService {
	
	@Autowired
	private ArticleFavoriteMapper favoriteMapper;
	
	/**
	 * 查询用户收藏的文章列表
	 * @param userId 用户ID
	 * @return 收藏的文章列表
	 */
	@Override
	public List<ArticleVo> getFavoriteArticlesByUserId(String userId) {
		log.info("查询用户收藏文章，用户ID：{}", userId);
		
		List<ArticleFavorite> favorites = favoriteMapper.selectByUserId(userId);
		List<ArticleVo> articleVos = new ArrayList<>();
		
		for (ArticleFavorite favorite : favorites) {
			if (favorite.getArticle() != null) {
				ArticleVo vo = new ArticleVo();
				BeanUtils.copyProperties(favorite.getArticle(), vo);
				
				if (favorite.getArticle().getAuthor() != null) {
					vo.setAuthorName(favorite.getArticle().getAuthor().getNickname() != null && !favorite.getArticle().getAuthor().getNickname().trim().isEmpty() ? favorite.getArticle().getAuthor().getNickname() : favorite.getArticle().getAuthor().getUsername());
					vo.setAuthorAvatar(favorite.getArticle().getAuthor().getAvatar());
				}
				
				if (favorite.getArticle().getCategory() != null) {
				vo.setCategoryName(favorite.getArticle().getCategory().getName());
			}
			
			// 设置收藏时间
			vo.setFavoriteTime(favorite.getCreateTime());
			
			articleVos.add(vo);
			}
		}
		
		return articleVos;
	}
	
	/**
	 * 检查用户是否已收藏文章
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 是否已收藏
	 */
	@Override
	public boolean isFavorited(String userId, String articleId) {
		ArticleFavorite favorite = favoriteMapper.selectByUserAndArticle(userId, articleId);
		return favorite != null;
	}
	
	/**
	 * 添加收藏
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 添加结果
	 */
	@Override
	public boolean addFavorite(String userId, String articleId) {
		log.info("添加收藏，用户ID：{}，文章ID：{}", userId, articleId);
		
		// 检查是否已收藏
		if (isFavorited(userId, articleId)) {
			throw new RuntimeException("已收藏该文章");
		}
		
		ArticleFavorite favorite = new ArticleFavorite();
		favorite.setId(UUID.randomUUID().toString());
		favorite.setUserId(userId);
		favorite.setArticleId(articleId);
		favorite.setCreateTime(LocalDateTime.now());
		
		return favoriteMapper.insert(favorite) > 0;
	}
	
	/**
	 * 取消收藏
	 * @param userId 用户ID
	 * @param articleId 文章ID
	 * @return 取消结果
	 */
	@Override
	public boolean removeFavorite(String userId, String articleId) {
		try {
			return favoriteMapper.deleteByUserAndArticle(userId, articleId) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean toggleFavorite(String userId, String articleId) {
		try {
			if (isFavorited(userId, articleId)) {
				return removeFavorite(userId, articleId);
			} else {
				return addFavorite(userId, articleId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Integer getFavoriteCount(String articleId) {
		try {
			return favoriteMapper.countByArticleId(articleId);
		} catch (Exception e) {
			log.error("获取文章收藏数量失败，文章ID：{}", articleId, e);
			return 0;
		}
	}
	
	/**
	 * 获取收藏总数
	 * @return 收藏总数
	 */
	@Override
	public long getTotalCount() {
		log.info("获取收藏总数");
		return favoriteMapper.getTotalCount();
	}
	
	/**
	 * 根据条件查询收藏列表
	 * @param params 查询参数
	 * @return 收藏列表
	 */
	@Override
	public List<ArticleFavorite> findByConditions(java.util.Map<String, Object> params) {
		log.info("根据条件查询收藏列表，参数：{}", params);
		return favoriteMapper.findByConditions(params);
	}
	
	/**
     * 根据条件统计收藏数量
     * @param params 查询参数
     * @return 收藏数量
     */
    @Override
    public long countByConditions(java.util.Map<String, Object> params) {
        log.info("根据条件统计收藏数量，参数：{}", params);
        return favoriteMapper.countByConditions(params);
    }
    
    /**
     * 根据ID删除收藏记录
     * @param id 收藏ID
     * @return 删除结果
     */
    @Override
    public boolean deleteFavoriteById(String id) {
        log.info("删除收藏记录，ID：{}", id);
        
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        try {
            return favoriteMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("删除收藏记录失败，ID：{}", id, e);
            return false;
        }
    }
    
    /**
     * 批量删除收藏记录
     * @param favoriteIds 收藏ID列表
     * @return 删除结果
     */
    @Override
    public boolean batchDeleteFavorites(List<String> favoriteIds) {
        log.info("批量删除收藏记录，数量：{}", favoriteIds.size());
        
        if (favoriteIds == null || favoriteIds.isEmpty()) {
            return false;
        }
        
        try {
            return favoriteMapper.batchDeleteByIds(favoriteIds) > 0;
        } catch (Exception e) {
            log.error("批量删除收藏记录失败", e);
            return false;
        }
    }
}