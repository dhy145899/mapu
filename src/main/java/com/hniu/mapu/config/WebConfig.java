package com.hniu.mapu.config;

import com.hniu.mapu.common.AdminInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 用于配置拦截器等Web相关配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加管理员权限拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                    "/admin/**",           // 管理后台路径
                    "/category/manage",    // 分类管理
                    "/user/manage",        // 用户管理
                    //"/article/manage",     // 文章管理
                    "/user/list",          // 用户列表
                    "/user/enable",        // 启用/禁用用户
                    "/user/disable",       // 启用/禁用用户
                    "/user/delete",        // 删除用户
                    "/category/add",       // 添加分类
                    "/category/update",    // 更新分类
                    "/category/delete"     // 删除分类
                )
                .excludePathPatterns(
                    "/login",              // 登录页面
                    "/admin/login",        // 管理员登录页面
                    "/register",           // 注册页面
                    "/static/**",          // 静态资源
                    "/css/**",             // CSS文件
                    "/js/**",              // JS文件
                    "/images/**",          // 图片文件
                    "/uploads/**"          // 上传文件
                );
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的静态资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}