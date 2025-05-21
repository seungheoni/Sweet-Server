package com.example.ptpt.service.impl;

import com.example.ptpt.dto.request.FeedRequest;
import com.example.ptpt.dto.response.CommentResponse;
import com.example.ptpt.dto.response.FeedDetailResponse;
import com.example.ptpt.dto.response.FeedLikeResponse;
import com.example.ptpt.dto.response.FeedResponse;
import com.example.ptpt.entity.Feed;
import com.example.ptpt.entity.FeedImages;
import com.example.ptpt.entity.FeedLikes;
import com.example.ptpt.entity.Users;
import com.example.ptpt.enums.FeedType;
import com.example.ptpt.enums.FeedVisibility;
import com.example.ptpt.repository.*;
import com.example.ptpt.service.FeedService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
    private final FeedLikeRepository feedLikeRepository;
    private final FollowsRepository followRepository;
    private static final int SUMMARY_MAX_LENGTH = 10;

    @Value("${ptpt.upload.imagePath:classpath:/img/feed}")
    private String imageUploadPath;

    @Value("${ptpt.upload.urlPrefix:/feeds/images/}")
    private String imageUrlPrefix;

    @Override
    public Page<FeedResponse> getFeeds(Pageable pageable,FeedType type) {
        // 유저Id 임시 조치.
        Long currentUserId = 1L;

        // 1) 팔로잉 ID 리스트 조회
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(currentUserId);

        Page<Feed> feedPage;
        if (type == FeedType.FOLLOWING) {
            // 팔로우한 사람들의 피드
            // empty 방어: followingIds 가 비어 있으면 빈 결과를 내려야 하므로 불가능한 ID(-1)로 쿼리
            List<Long> ids = followingIds.isEmpty() ? Collections.singletonList(-1L) : followingIds;
            feedPage = feedRepository.findByUserIdIn(ids, pageable);

        } else {
            // 팔로우하지 않은 사람들의 피드
            // 유저 본인 피드도 제외하고 싶으면 followingIds 에 currentUserId 추가
            List<Long> excludeIds = new ArrayList<>(followingIds);
            excludeIds.add(currentUserId);
            feedPage = feedRepository.findByUserIdNotIn(excludeIds, pageable);
        }

        return feedPage.map(this::convertToDto);
    }

    @Override
    public FeedDetailResponse getFeedById(Long id) {
        Feed feed = feedRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found with id: " + id));

        List<FeedImages> feedImages = feedImagesRepository.findByFeed(feed);
        List<String> imageUrls = feedImages.stream()
                .map(fi -> imageUrlPrefix + fi.getImageUrl())
                .collect(Collectors.toList());
        return convertToDetailDto(feed, imageUrls);
    }

    @Override
    public FeedResponse createFeed(FeedRequest feedRequest) {
        Feed feed = new Feed();
        feed.setTitle(feedRequest.getTitle());
        feed.setContent(feedRequest.getContent());

        Users dummyUser = usersRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Dummy user not found. Please create a dummy user with id=1."));
        feed.setUser(dummyUser);

        if (feedRequest.getVisibility() == null) {
            feed.setVisibility(FeedVisibility.공개);
        } else {
            feed.setVisibility(feedRequest.getVisibility());
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
            feed.setVisibility(feedRequest.getVisibility());

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

                String publicUrl = imageUrlPrefix + originalFilename;

                FeedImages feedImage = FeedImages.builder()
                        .feed(feed)
                        .imageUrl(originalFilename)
                        .build();
                feedImagesRepository.save(feedImage);

                imageUrls.add(publicUrl);
            } catch (IOException e) {
                throw new RuntimeException("File upload failed for " + originalFilename, e);
            }
        }
        return imageUrls;
    }

    private FeedDetailResponse convertToDetailDto(Feed feed, List<String> imageUrls) {

        long likeCount = feedLikeRepository.countByFeed(feed);
        //인증 연동시 currentUserId 토큰에서 가져온 값으로 바꿀것
        Long currentUserId = 1L;
        boolean isLiked = usersRepository.findById(currentUserId)
                .map(user -> feedLikeRepository.existsByFeedAndUser(feed, user))
                .orElse(false);
        String firstLikedUserName = feedLikeRepository
                .findFirstByFeedOrderByCreatedAtAsc(feed)
                .map(fl -> fl.getUser().getUsername())
                .orElse("");

        // 모킹용 최상위 댓글 3개
        List<CommentResponse> topComments = List.of(
                CommentResponse.builder()
                        .commentId(1L)
                        .userId(201L)
                        .userName("vurivuri")
                        .text("소통해요~")
                        .createdAt(Instant.now().minusSeconds(3600))
                        .build(),
                CommentResponse.builder()
                        .commentId(2L)
                        .userId(202L)
                        .userName("fitness_zzang")
                        .text("휴식시간 분배는 어떻게 하시나요")
                        .createdAt(Instant.now().minusSeconds(1800))
                        .build(),
                CommentResponse.builder()
                        .commentId(3L)
                        .userId(203L)
                        .userName("Idol_PP")
                        .text("저희 모임 참여하지 않으실래요?")
                        .createdAt(Instant.now().minusSeconds(600))
                        .build()
        );

        return FeedDetailResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .authorId(feed.getUser() != null ? feed.getUser().getId() : null)
                .authorName(feed.getUser().getUsername())
                .imageUrls(imageUrls)
                .authorProfileImageUrl("/feeds/images/health.png")
                .feedContent(feed.getContent())
                .visibility(feed.getVisibility())
                .likeCount(likeCount)
                .isLikedByCurrentUser(isLiked)
                .firstLikedUserName(firstLikedUserName)
                .commentCount(8L)
                .topComments(topComments)
                .shareCount(3L)
                .feedContent(feed.getContent())
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build();
    }

    private FeedResponse convertToDto(Feed feed) {
        long likeCount = feedLikeRepository.countByFeed(feed);
        //인증 연동시 currentUserId 토큰에서 가져온 값으로 바꿀것
        Long currentUserId = 1L;
        boolean isLiked = usersRepository.findById(currentUserId)
                .map(user -> feedLikeRepository.existsByFeedAndUser(feed, user))
                .orElse(false);
        String firstLikedUserName = feedLikeRepository
                .findFirstByFeedOrderByCreatedAtAsc(feed)
                .map(fl -> fl.getUser().getUsername())
                .orElse("");

        List<String> imageUrls = feedImagesRepository.findByFeed(feed).stream()
                .map(fi -> imageUrlPrefix + fi.getImageUrl())
                .collect(Collectors.toList());

        return FeedResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .authorId(feed.getUser() != null ? feed.getUser().getId() : null)
                .authorName(feed.getUser().getUsername())
                .imageUrls(imageUrls)
                .authorProfileImageUrl("/feeds/images/health.png")
                .visibility(feed.getVisibility())
                .likeCount(likeCount)
                .isLikedByCurrentUser(isLiked)
                .firstLikedUserName(firstLikedUserName)
                .commentCount(30L)
                .shareCount(3L)
                .feedContent(feed.getContent())
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build();
    }



    @Override
    public void likeFeed(Long feedId, Long userId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found: " + feedId));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (feedLikeRepository.existsByFeedAndUser(feed, user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Like already exists");
        }

        FeedLikes like = new FeedLikes();
        like.setFeed(feed);
        like.setUser(user);
        feedLikeRepository.save(like);
    }

    // **unlikeFeed 메서드 수정**
    @Override
    @Transactional
    public void unlikeFeed(Long feedId, Long userId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("Feed not found: " + feedId));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        if (!feedLikeRepository.existsByFeedAndUser(feed, user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Like does not exist");
        }

        feedLikeRepository.deleteByFeedIdAndUserId(feedId, userId);
    }

    @Override
    public List<FeedLikeResponse> getFeedLikes(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found: " + feedId));

        return feedLikeRepository.findByFeedOrderByCreatedAtDesc(feed)
                .stream()
                .map(fl -> new FeedLikeResponse(
                        fl.getUser().getId(),
                        fl.getUser().getUsername(),
                        fl.getUser().getProfileImage(),   // Users 엔티티에 프로필 필드가 있다면
                        fl.getCreatedAt()
                ))
                .toList();
    }
}