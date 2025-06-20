package com.hniu.mapu.controller;

import com.hniu.mapu.pojo.entity.Article;
import com.hniu.mapu.pojo.entity.ArticleCategory;
import com.hniu.mapu.pojo.vo.ArticleVo;
import com.hniu.mapu.service.ArticleService;
import com.hniu.mapu.service.ArticleCategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员文章API控制器
 * 处理管理员对文章的操作
 * 
 * @author jiujiu
 */
@Slf4j
@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private ArticleCategoryService categoryService;

    /**
     * 更新文章
     * @param id 文章ID
     * @param article 文章信息
     * @param session 会话
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable String id, @RequestBody Article article, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "无权限访问"));
            }
            
            article.setId(id);
            boolean success = articleService.updateArticle(article);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "文章更新成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("success", false, "message", "文章更新失败"));
            }
            
        } catch (Exception e) {
            log.error("更新文章失败", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "更新文章失败"));
        }
    }

    /**
     * 删除文章
     * @param id 文章ID
     * @param session 会话
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable String id, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "无权限访问"));
            }
            
            boolean success = articleService.deleteArticle(id);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "文章删除成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("success", false, "message", "文章删除失败"));
            }
            
        } catch (Exception e) {
            log.error("删除文章失败", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "删除文章失败"));
        }
    }

    /**
     * 批量将文章改为草稿状态
     * @param articleIds 文章ID列表
     * @param session 会话
     * @return 操作结果
     */
    @PostMapping("/batch-to-draft")
    public ResponseEntity<?> batchToDraft(@RequestBody List<String> articleIds, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权限访问"));
            }
            
            if (articleIds == null || articleIds.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "请选择要操作的文章"));
            }
            
            // 批量更新文章状态为草稿(0)
            boolean success = articleService.batchUpdateStatus(articleIds, 0);
            
            if (success) {
                return ResponseEntity.ok(Map.of("code", 200, "message", "批量改为草稿成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "批量改为草稿失败"));
            }
            
        } catch (Exception e) {
            log.error("批量改为草稿失败", e);
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "批量改为草稿失败"));
        }
    }

    /**
     * 批量删除文章
     * @param articleIds 文章ID列表
     * @param session 会话
     * @return 操作结果
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<?> batchDelete(@RequestBody List<String> articleIds, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权限访问"));
            }
            
            if (articleIds == null || articleIds.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "请选择要删除的文章"));
            }
            
            boolean success = articleService.batchDelete(articleIds);
            
            if (success) {
                return ResponseEntity.ok(Map.of("code", 200, "message", "批量删除成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "批量删除失败"));
            }
            
        } catch (Exception e) {
            log.error("批量删除文章失败", e);
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "批量删除失败"));
        }
    }

    /**
     * 批量发布文章
     * @param articleIds 文章ID列表
     * @param session 会话
     * @return 操作结果
     */
    @PostMapping("/batch-publish")
    public ResponseEntity<?> batchPublish(@RequestBody List<String> articleIds, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权限访问"));
            }
            
            if (articleIds == null || articleIds.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "请选择要操作的文章"));
            }
            
            // 批量更新文章状态为已发布(1)
            boolean success = articleService.batchUpdateStatus(articleIds, 1);
            
            if (success) {
                return ResponseEntity.ok(Map.of("code", 200, "message", "批量发布成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "批量发布失败"));
            }
            
        } catch (Exception e) {
            log.error("批量发布失败", e);
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "批量发布失败"));
        }
    }

    /**
     * 将单个文章改成草稿
     * @param id 文章ID
     * @param session 会话
     * @return 操作结果
     */
    @PutMapping("/{id}/to-draft")
    public ResponseEntity<?> toDraft(@PathVariable String id, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权限访问"));
            }
            
            // 更新文章状态为草稿(0)
            boolean success = articleService.updateArticleStatus(id, 0);
            
            if (success) {
                return ResponseEntity.ok(Map.of("code", 200, "message", "改成草稿成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "改成草稿失败"));
            }
            
        } catch (Exception e) {
            log.error("改成草稿失败", e);
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "改成草稿失败"));
        }
    }

    /**
     * 发布单个文章
     * @param id 文章ID
     * @param session 会话
     * @return 操作结果
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publish(@PathVariable String id, HttpSession session) {
        try {
            // 检查管理员权限
            Integer userRole = (Integer) session.getAttribute("userRole");
            if (userRole == null || userRole != 1) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权限访问"));
            }
            
            // 更新文章状态为已发布(1)
            boolean success = articleService.updateArticleStatus(id, 1);
            
            if (success) {
                return ResponseEntity.ok(Map.of("code", 200, "message", "发布成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("code", 400, "message", "发布失败"));
            }
            
        } catch (Exception e) {
            log.error("发布失败", e);
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "发布失败"));
        }
    }
}