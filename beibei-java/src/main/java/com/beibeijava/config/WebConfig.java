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

    @Value("${beibei.upload.files-path:files}")
    private String filesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录的绝对路径
        String projectDir = System.getProperty("user.dir");

        // 对于 beibei-java 子目录
        String javaProjectDir = projectDir.endsWith("beibei-java") ? projectDir
                : Paths.get(projectDir, "beibei-java").toString();

        Path uploadBasePath = Paths.get(javaProjectDir, uploadPath).toAbsolutePath();
        String uploadLocation = "file:" + uploadBasePath.toString() + "/";

        System.out.println("项目目录: " + javaProjectDir);
        System.out.println("上传文件根路径: " + uploadBasePath.toString());

        // 配置 /avatars/** 路径
        registry.addResourceHandler("/" + avatarPath + "/**")
                .addResourceLocations(uploadLocation + avatarPath + "/")
                .setCachePeriod(3600); // 缓存1小时

        // 配置 /files/** 路径（用于评价图片等）
        registry.addResourceHandler("/" + filesPath + "/**")
                .addResourceLocations(uploadLocation + filesPath + "/")
                .setCachePeriod(3600); // 缓存1小时

        // 配置 /uploads/** 路径（用于访问所有上传文件）
        registry.addResourceHandler("/" + uploadPath + "/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600); // 缓存1小时

        // 可以添加更多静态资源配置
        // registry.addResourceHandler("/static/**")
        // .addResourceLocations("classpath:/static/");
    }
}