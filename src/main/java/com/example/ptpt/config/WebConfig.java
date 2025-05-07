package com.example.ptpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${ptpt.upload.urlPrefix}")
    private String imageUrlPrefix;

    @Value("${ptpt.upload.imagePath}")
    private String imageUploadPath;

    /**
     * 개발용 피드 이미지 관련 불러오기 설정 (urlPrefix, imagePath 프로퍼티 사용)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = imageUrlPrefix.endsWith("/")
                ? imageUrlPrefix
                : imageUrlPrefix + "/";
        String mappingPattern = prefix + "**";

        String location = imageUploadPath.endsWith("/")
                ? imageUploadPath
                : imageUploadPath + "/";

        registry.addResourceHandler(mappingPattern)
                .addResourceLocations(location);
    }
}
