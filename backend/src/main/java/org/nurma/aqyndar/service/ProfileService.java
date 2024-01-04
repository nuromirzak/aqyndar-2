package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurma.aqyndar.dto.response.GetLikesResponse;
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.repository.AnnotationRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.repository.ReactionRepository;
import org.nurma.aqyndar.repository.UserRepository;
import org.nurma.aqyndar.util.EntityToDTOMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ReactionRepository reactionRepository;
    private final AnnotationRepository annotationRepository;
    private final PoemRepository poemRepository;
    private static final String USER_NOT_FOUND = "User with email %s not found";

    public GetWhoResponse getCurrentUser() {
        User user = authService.getCurrentUserEntity();

        return EntityToDTOMapper.mapUserToGetWhoResponse(user);
    }

    public GetWhoResponse getUser(final int id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFound(USER_NOT_FOUND.formatted(id));
        }

        User user = optionalUser.get();

        return EntityToDTOMapper.mapUserToGetWhoResponse(user);
    }

    public GetLikesResponse getLikes(final int id) {
        GetLikesResponse response = new GetLikesResponse();

        List<Integer> poemIds = poemRepository.findIdsByUserId(id);

        Integer poemLikes = reactionRepository.countLikesOfUser(ReactedEntity.POEM, poemIds);
        Integer poemDislikes = reactionRepository.countDislikesOfUser(ReactedEntity.POEM, poemIds);

        List<Integer> annotationIds = annotationRepository.findIdsByUserId(id);

        Integer annotationLikes = reactionRepository.countLikesOfUser(ReactedEntity.ANNOTATION, annotationIds);
        Integer annotationDislikes = reactionRepository.countDislikesOfUser(ReactedEntity.ANNOTATION, annotationIds);

        response.setPoemLikes(poemLikes);
        response.setPoemDislikes(poemDislikes);
        response.setAnnotationLikes(annotationLikes);
        response.setAnnotationDislikes(annotationDislikes);

        return response;
    }
}
