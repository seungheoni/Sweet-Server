package com.example.ptpt.service.impl;

import com.example.ptpt.dto.request.FeedRequest;
import com.example.ptpt.dto.response.FeedDetailResponse;
import com.example.ptpt.dto.response.FeedResponse;
import com.example.ptpt.entity.ExerciseDetails;
import com.example.ptpt.entity.Feed;
import com.example.ptpt.entity.FeedImages;
import com.example.ptpt.entity.UserEntity;
import com.example.ptpt.enums.FeedVisibility;
import com.example.ptpt.repository.FeedImagesRepository;
import com.example.ptpt.repository.FeedRepository;
import com.example.ptpt.repository.UsersRepository;
import com.example.ptpt.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.ptpt.dto.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedRepository feedRepository;
    private final UsersRepository usersRepository;
    private final FeedImagesRepository feedImagesRepository;

    @Value("${ptpt.upload.imagePath:classpath:img/feed}")
    private String imageUploadPath;

    @Override
    public Page<FeedResponse> getFeeds(Pageable pageable) {
        Page<Feed> feedPage = feedRepository.findAll(pageable);
        return feedPage.map(this::convertToDto);
    }

    @Override
    public FeedDetailResponse getFeedById(Long id) {
        Optional<Feed> optionalFeed = feedRepository.findById(id);
        if (optionalFeed.isPresent()) {
            Feed feed = optionalFeed.get();
            List<FeedImages> feedImages = feedImagesRepository.findByFeed(feed);
            List<String> imageUrls = feedImages.stream()
                    .map(FeedImages::getImageUrl)
                    .collect(Collectors.toList());
            return convertToDetailDto(feed, imageUrls);
        }
        throw new RuntimeException("Feed not found with id: " + id);
    }

    @Override
    public FeedResponse createFeed(FeedRequest feedRequest) {
        Feed feed = new Feed();
        feed.setTitle(feedRequest.getTitle());
        feed.setContent(feedRequest.getContent());

        UserEntity dummyUser = usersRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Dummy user not found. Please create a dummy user with id=1."));
        feed.setUser(dummyUser);

        if (feedRequest.getVisibility() == null) {
            feed.setVisibility(FeedVisibility.공개);
        } else {
            feed.setVisibility(feedRequest.getVisibility());
        }

        if (feedRequest.getExerciseDetails() != null) {
            ExerciseDetailsRequest detailsReq = feedRequest.getExerciseDetails();
            ExerciseDetails exerciseDetails = ExerciseDetails.builder()
                    .duration(detailsReq.getDuration())
                    .location(detailsReq.getLocation())
                    .build();
            feed.setExerciseDetails(exerciseDetails);
            exerciseDetails.setFeed(feed);
        }

        Feed savedFeed = feedRepository.save(feed);
        return convertToDto(savedFeed);
    }

    @Override
    public FeedResponse updateFeed(Long id, FeedRequest feedRequest) {
        Optional<Feed> optionalFeed = feedRepository.findById(id);
        if (optionalFeed.isPresent()) {
            Feed feed = optionalFeed.get();
            feed.setTitle(feedRequest.getTitle());
            feed.setContent(feedRequest.getContent());

            Feed updatedFeed = feedRepository.save(feed);
            return convertToDto(updatedFeed);
        }
        return null;
    }

    @Override
    public void deleteFeed(Long id) {
        Feed feed = feedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feed not found"));

        feedImagesRepository.findByFeed(feed).forEach(this::deletePhysicalFile);

        feedRepository.delete(feed);
    }

    private File getUploadDir() {

        if (imageUploadPath.startsWith("classpath:")) {
            String path = imageUploadPath.substring("classpath:".length());
            Resource resource = new ClassPathResource(path);
            try {
                File uploadDir = resource.getFile();
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                return uploadDir;
            } catch (IOException e) {
                throw new RuntimeException("클래스패스에서 이미지 업로드 디렉토리를 가져올 수 없습니다.", e);
            }
        } else {
            File uploadDir = new File(imageUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            return uploadDir;
        }
    }

    private void deletePhysicalFile(FeedImages feedImages) {

        String fileName = feedImages.getImageUrl();
        if (fileName != null) {
            File uploadDir = getUploadDir();
            File fileToDelete = new File(uploadDir, fileName);
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    log.info("Successfully deleted file: {}", fileToDelete.getAbsolutePath());
                } else {
                    log.error("Failed to delete file: {}", fileToDelete.getAbsolutePath());
                }
            } else {
                log.warn("File not found: {}", fileToDelete.getAbsolutePath());
            }
        } else {
            log.warn("삭제할 파일명이 없습니다.");
        }
    }

    @Override
    public List<String> uploadImages(Long feedId, List<MultipartFile> files) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new RuntimeException("Feed not found with id: " + feedId));

        List<String> imageUrls = new ArrayList<>();
        File uploadDir = getUploadDir();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                continue;
            }
            File destination = new File(uploadDir, originalFilename);
            try {
                file.transferTo(destination);
                String imageUrl = originalFilename;
                FeedImages feedImage = FeedImages.builder()
                        .feed(feed)
                        .imageUrl(imageUrl)
                        .build();
                feedImagesRepository.save(feedImage);
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("File upload failed for " + originalFilename, e);
            }
        }
        return imageUrls;
    }

    private FeedDetailResponse convertToDetailDto(Feed feed, List<String> imageUrls) {
        ExerciseDetailsRequest detailsDTO = ExerciseDetailsRequest.builder().exerciseType(List.of("swimming")).build();
        return FeedDetailResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .content(feed.getContent())
                .authorId(feed.getUser() != null ? feed.getUser().getId() : null)
                .visibility(feed.getVisibility())
                .exerciseDetails(detailsDTO)
                .imageUrls(imageUrls)
                .build();
    }

    private FeedResponse convertToDto(Feed feed) {
        ExerciseDetails exerciseDetails = feed.getExerciseDetails();
        ExerciseDetailsRequest detailsDTO = ExerciseDetailsRequest.builder()
                .duration(exerciseDetails.getDuration())
                .location(exerciseDetails.getLocation())
                .build();

        List<FeedImages> feedImages = feedImagesRepository.findByFeed(feed);
        List<String> imageUrls = feedImages.stream()
                .map(FeedImages::getImageUrl)
                .collect(Collectors.toList());

        return FeedResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .authorId(feed.getUser() != null ? feed.getUser().getId() : null)
                .visibility(feed.getVisibility())
                .exerciseDetails(detailsDTO)
                .imageUrls(imageUrls)
                .build();
    }
}