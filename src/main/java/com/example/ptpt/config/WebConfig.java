package com.example.ptpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 피드 이미지
    @Value("${ptpt.upload.urlPrefix}")
    private String feedUrlPrefix;
    @Value("${ptpt.upload.imagePath}")
    private String feedImagePath;

    // 프로필 이미지
    @Value("${ptpt.upload.profileUrlPrefix}")
    private String profileUrlPrefix;
    @Value("${ptpt.upload.profileImagePath}")
    private String profileImagePath;
    /**
     * 개발용 피드 이미지 관련 불러오기 설정 (urlPrefix, imagePath 프로퍼티 사용)
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1) 피드 이미지 핸들러 등록
        registry.addResourceHandler(toPattern(feedUrlPrefix))
                .addResourceLocations(toLocation(feedImagePath));

        // 2) 프로필 이미지 핸들러 등록
        registry.addResourceHandler(toPattern(profileUrlPrefix))
                .addResourceLocations(toLocation(profileImagePath));
    }

    /**
     * "/prefix/**" 형태의 패턴으로 변환
     */
    private String toPattern(String prefix) {
        String p = prefix.endsWith("/") ? prefix : prefix + "/";
        return p + "**";
    }

    /**
     * 파일 시스템 또는 classpath 리소스 경로로 변환
     */
    private String toLocation(String path) {
        return path.endsWith("/") ? path : path + "/";
    }
}
