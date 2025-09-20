package com.beibeijava.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${beibei.upload.path:uploads}")
    private String uploadPath;

    @Value("${beibei.upload.avatar-path:avatars}")
    private String avatarPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像文件的静态资源访问 - 使用项目根目录的绝对路径
        String projectDir = System.getProperty("user.dir");
        Path uploadBasePath = Paths.get(projectDir, uploadPath).toAbsolutePath();
        String avatarLocation = "file:" + uploadBasePath.toString() + "/";

        registry.addResourceHandler("/" + avatarPath + "/**")
                .addResourceLocations(avatarLocation + avatarPath + "/")
                .setCachePeriod(3600); // 缓存1小时

        // 可以添加更多静态资源配置
        // registry.addResourceHandler("/static/**")
        //         .addResourceLocations("classpath:/static/");
    }
}