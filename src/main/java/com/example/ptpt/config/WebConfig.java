package com.example.ptpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${ptpt.upload.urlPrefix}")
    private String feedUrlPrefix;
    @Value("${ptpt.upload.imagePath}")
    private String feedImagePath;

    @Value("${ptpt.upload.profileUrlPrefix}")
    private String profileUrlPrefix;
    @Value("${ptpt.upload.profileImagePath}")
    private String profileImagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // feed 이미지 핸들러
        registry.addResourceHandler(ensureSlash(feedUrlPrefix) + "**")
                .addResourceLocations(ensureTrailingSlash(feedImagePath));

        // profile 이미지 핸들러
        registry.addResourceHandler(ensureSlash(profileUrlPrefix) + "**")
                .addResourceLocations(ensureTrailingSlash(profileImagePath));
    }

    // prefix가 "/" 로 끝나지 않으면 붙여주기
    private String ensureSlash(String prefix) {
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }

    // location이 "/" 로 끝나지 않으면 붙여주기
    private String ensureTrailingSlash(String location) {
        return location.endsWith("/") ? location : location + "/";
    }
}