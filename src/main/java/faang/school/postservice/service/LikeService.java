package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final CommentService commentService;
    private final PostService postService;


    public List<UserDto> getLikesByPostId(Long postId) {
        postService.getPostById(postId);
        List<Like> likes = likeRepository.findByPostId(postId);
        List<UserDto> result = findUsersWhoLiked(likes);
        log.info("Corrected {} users", result.size());
        return result;
    }

    public List<UserDto> getLikesByCommentId(Long commentId) {
        commentService.checkCommentExists(commentId);
        List<Like> likes = likeRepository.findByCommentId(commentId);
        List<UserDto> result = findUsersWhoLiked(likes);
        log.info("Corrected {} users", result.size());
        return result;
    }

    private List<UserDto> findUsersWhoLiked(List<Like> likes) {
        List<Long> usersId = likes.stream().map(Like::getUserId).toList();
        List<UserDto> result = new ArrayList<>();
        int subListSize = 100;
        for (int i = 0; i < usersId.size(); i += subListSize) {
            ArrayList<Long> subList = new ArrayList<>();
            for (int j = i; j < i + subListSize && j < usersId.size(); j++) {
                subList.add(usersId.get(j));
            }
            result.addAll(userServiceClient.getUsersByIds(subList));
        }
        return result;
    }
}
