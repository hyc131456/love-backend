package com.loveapp.config;

import com.loveapp.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import java.io.File;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Value("${upload.path:./uploads/}")
    private String uploadPath;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录注册接口
                        "/user/login",
                        "/user/register",
                        "/user/mockLogin",
                        "/user/wxLogin",
                        // 健康检查
                        "/health",
                        // Swagger
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        // 静态资源
                        "/static/**",
                        "/favicon.ico",
                        // 用户上传文件目录
                        "/uploads/**"
                );
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取绝对路径，确保与上传时保持一致
        String absolutePath = new File(uploadPath).getAbsolutePath();
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        
        // 将 /uploads/** 映射到物理绝对路径下
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
