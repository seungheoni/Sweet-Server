package com.example.ptpt.service.impl;

import com.example.ptpt.dto.request.CommentRequest;
import com.example.ptpt.dto.request.FeedRequest;
import com.example.ptpt.dto.response.CommentResponse;
import com.example.ptpt.dto.response.FeedDetailResponse;
import com.example.ptpt.dto.response.FeedLikeResponse;
import com.example.ptpt.dto.response.FeedResponse;
import com.example.ptpt.entity.*;
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
    private final CommentRepository commentRepository;
    private static final int SUMMARY_MAX_LENGTH = 10;

    @Value("${ptpt.upload.imagePath:classpath:/img/feed}")
    private String imageUploadPath;

    @Value("${ptpt.upload.urlPrefix:/feeds/images/}")
    private String imageUrlPrefix;

    @Value("${ptpt.upload.profileUrlPrefix:/profiles/images/}")
    private String profileUrlPrefix;

    @Override
    public Page<FeedResponse> getFeeds(Pageable pageable, FeedType type) {
        Long currentUserId = 1L;
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(currentUserId);

        Page<Feed> feedPage;
        if (type == FeedType.FOLLOWING) {
            List<Long> ids = followingIds.isEmpty() ? Collections.singletonList(-1L) : followingIds;
            feedPage = feedRepository.findByUserIdIn(ids, pageable);
        } else {
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

        feed.setVisibility(Optional.ofNullable(feedRequest.getVisibility()).orElse(FeedVisibility.공개));

        Feed savedFeed = feedRepository.save(feed);
        return convertToDto(savedFeed);
    }

    @Override
    public FeedResponse updateFeed(Long id, FeedRequest feedRequest) {
        Feed feed = feedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feed not found"));
        feed.setTitle(feedRequest.getTitle());
        feed.setContent(feedRequest.getContent());
        feed.setVisibility(feedRequest.getVisibility());

        Feed updatedFeed = feedRepository.save(feed);
        return convertToDto(updatedFeed);
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
                if (!uploadDir.exists()) uploadDir.mkdirs();
                return uploadDir;
            } catch (IOException e) {
                throw new RuntimeException("Unable to get upload directory from classpath.", e);
            }
        } else {
            File uploadDir = new File(imageUploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            return uploadDir;
        }
    }

    private void deletePhysicalFile(FeedImages feedImages) {
        String fileName = feedImages.getImageUrl();
        if (fileName != null) {
            File uploadDir = getUploadDir();
            File fileToDelete = new File(uploadDir, fileName);
            if (fileToDelete.exists() && fileToDelete.delete()) {
                log.info("Deleted file: {}", fileToDelete.getAbsolutePath());
            } else {
                log.warn("Failed to delete file: {}", fileToDelete.getAbsolutePath());
            }
        } else {
            log.warn("No filename to delete.");
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
            if (originalFilename == null || originalFilename.isEmpty()) continue;
            File destination = new File(uploadDir, originalFilename);
            try {
                file.transferTo(destination);
                String publicUrl = imageUrlPrefix + originalFilename;
                feedImagesRepository.save(FeedImages.builder()
                        .feed(feed)
                        .imageUrl(originalFilename)
                        .build());
                imageUrls.add(publicUrl);
            } catch (IOException e) {
                throw new RuntimeException("File upload failed for " + originalFilename, e);
            }
        }
        return imageUrls;
    }

    private FeedDetailResponse convertToDetailDto(Feed feed, List<String> imageUrls) {
        // 좋아요 수 집계
        long likeCount = feedLikeRepository.countByFeed(feed);
        // 현재 사용자 ID (예시로 하드코딩)
        Long currentUserId = 1L;
        // 현재 사용자의 좋아요 여부
        boolean isLiked = usersRepository.findById(currentUserId)
                .map(user -> feedLikeRepository.existsByFeedAndUser(feed, user))
                .orElse(false);

        // 첫 번째 좋아요 사용자 이름 및 프로필 이미지 URL 조회
        Optional<FeedLikes> firstLikeOpt = feedLikeRepository.findFirstByFeedOrderByCreatedAtAsc(feed);
        String firstLikedUserName = firstLikeOpt.map(fl -> fl.getUser().getUsername()).orElse("");
        String firstLikedUserProfileImageUrl = firstLikeOpt
                .map(fl -> profileUrlPrefix + fl.getUser().getProfileImage())
                .orElse("");

        // 상위 3개 댓글 조회 및 DTO 변환
        List<CommentResponse> topComments = commentRepository
                .findByFeedOrderByCreatedAtAsc(feed, Pageable.ofSize(3))
                .stream()
                .map(c -> CommentResponse.builder()
                        .commentId(c.getId())
                        .userId(c.getUser().getId())
                        .userName(c.getUser().getUsername())
                        .profileImageUrl(profileUrlPrefix + c.getUser().getProfileImage())
                        .text(c.getText())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // 최종 DTO 빌드
        return FeedDetailResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .authorId(feed.getUser().getId())
                .authorName(feed.getUser().getUsername())
                .authorProfileImageUrl(profileUrlPrefix + feed.getUser().getProfileImage())
                .imageUrls(imageUrls)
                .feedContent(feed.getContent())
                .visibility(feed.getVisibility())
                .likeCount(likeCount)
                .isLikedByCurrentUser(isLiked)
                .firstLikedUserName(firstLikedUserName)
                .firstLikedUserProfileImageUrl(firstLikedUserProfileImageUrl)
                .commentCount(commentRepository.countByFeedId(feed.getId()))
                .topComments(topComments)
                .shareCount(3L)
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build();
    }

    private FeedResponse convertToDto(Feed feed) {
        long likeCount = feedLikeRepository.countByFeed(feed);
        Long currentUserId = 1L;
        boolean isLiked = usersRepository.findById(currentUserId)
                .map(user -> feedLikeRepository.existsByFeedAndUser(feed, user))
                .orElse(false);

        // 첫 번째 좋아요 사용자의 이름과 이미지 URL 조회
        Optional<FeedLikes> firstLikeOpt = feedLikeRepository.findFirstByFeedOrderByCreatedAtAsc(feed);
        String firstLikedUserName = firstLikeOpt.map(fl -> fl.getUser().getUsername()).orElse("");
        String firstLikedUserProfileImageUrl = firstLikeOpt
                .map(fl -> profileUrlPrefix + fl.getUser().getProfileImage())
                .orElse("");

        List<String> imageUrls = feedImagesRepository.findByFeed(feed).stream()
                .map(fi -> imageUrlPrefix + fi.getImageUrl())
                .collect(Collectors.toList());

        return FeedResponse.builder()
                .id(feed.getId())
                .title(feed.getTitle())
                .authorId(feed.getUser().getId())
                .authorName(feed.getUser().getUsername())
                .authorProfileImageUrl(profileUrlPrefix + feed.getUser().getProfileImage())
                .imageUrls(imageUrls)
                .visibility(feed.getVisibility())
                .likeCount(likeCount)
                .isLikedByCurrentUser(isLiked)
                .firstLikedUserName(firstLikedUserName)
                .firstLikedUserProfileImageUrl(firstLikedUserProfileImageUrl)  // 여기에 추가
                .commentCount(commentRepository.countByFeedId(feed.getId()))
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
                        profileUrlPrefix + fl.getUser().getProfileImage(),
                        fl.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public Page<CommentResponse> getComments(Long feedId, Pageable pageable) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found: " + feedId));

        return commentRepository.findByFeedOrderByCreatedAtAsc(feed, pageable)
                .map(c -> CommentResponse.builder()
                        .commentId(c.getId())
                        .userId(c.getUser().getId())
                        .userName(c.getUser().getUsername())
                        .profileImageUrl(profileUrlPrefix + c.getUser().getProfileImage())
                        .text(c.getText())
                        .createdAt(c.getCreatedAt())
                        .build()
                );
    }

    @Override
    public CommentResponse createComment(Long feedId, Long userId, CommentRequest request) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found: " + feedId));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        Comment comment = Comment.builder()
                .feed(feed)
                .user(user)
                .text(request.getText())
                .build();
        Comment saved = commentRepository.save(comment);

        return CommentResponse.builder()
                .commentId(saved.getId())
                .userId(user.getId())
                .userName(user.getUsername())
                .profileImageUrl(profileUrlPrefix + user.getProfileImage())
                .text(saved.getText())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: " + commentId));
        comment.setText(request.getText());
        Comment updated = commentRepository.save(comment);

        return CommentResponse.builder()
                .commentId(updated.getId())
                .userId(updated.getUser().getId())
                .userName(updated.getUser().getUsername())
                .profileImageUrl(profileUrlPrefix + updated.getUser().getProfileImage())
                .text(updated.getText())
                .createdAt(updated.getCreatedAt())
                .build();
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
}