package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.GetLikesResponse;
import org.nurma.aqyndar.dto.response.GetReactionResponse;
import org.nurma.aqyndar.dto.response.TopEntityResponse;
import org.nurma.aqyndar.dto.response.UpdateReactionResponse;
import org.nurma.aqyndar.entity.Annotation;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.entity.Reaction;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.ReactionType;
import org.nurma.aqyndar.entity.enums.TopEntity;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.exception.ValidationException;
import org.nurma.aqyndar.repository.AnnotationRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.repository.ReactionRepository;
import org.nurma.aqyndar.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final AnnotationRepository annotationRepository;
    private final PoemRepository poemRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ProfileService profileService;
    private static final String POEM_NOT_FOUND = "Poem with id %s not found";
    private static final String ANNOTATION_NOT_FOUND = "Annotation with id %s not found";
    private static final String ENTITY_TYPE_NOT_FOUND = "Type of entity %s not found";
    private static final String REACTION_TYPE_NOT_FOUND = "Reaction type %s not found";

    public UpdateReactionResponse updateReaction(final UpdateReactionRequest updateReactionRequest) {
        int reactedEntityId = updateReactionRequest.getReactedEntityId();

        ReactedEntity reactedEntity;
        try {
            reactedEntity = ReactedEntity.valueOf(updateReactionRequest.getReactedEntity());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(ENTITY_TYPE_NOT_FOUND.formatted(updateReactionRequest.getReactedEntity()));
        }

        ReactionType reactionType;
        try {
            reactionType = ReactionType.fromValue(updateReactionRequest.getReactionType());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(REACTION_TYPE_NOT_FOUND.formatted(updateReactionRequest.getReactionType()));
        }

        if (reactedEntity == ReactedEntity.POEM) {
            Optional<Poem> poemOptional = poemRepository.findById(reactedEntityId);
            if (poemOptional.isEmpty()) {
                throw new ResourceNotFound(POEM_NOT_FOUND.formatted(reactedEntityId));
            }
        } else if (reactedEntity == ReactedEntity.ANNOTATION) {
            Optional<Annotation> annotationOptional = annotationRepository.findById(reactedEntityId);
            if (annotationOptional.isEmpty()) {
                throw new ResourceNotFound(ANNOTATION_NOT_FOUND.formatted(reactedEntityId));
            }
        }

        User user = authService.getCurrentUserEntity();

        Reaction reaction = reactionRepository.findByReactedEntityAndReactedEntityIdAndUserId(
                reactedEntity,
                reactedEntityId,
                user.getId()
        ).orElse(new Reaction());

        reaction.setReactedEntity(reactedEntity);
        reaction.setReactedEntityId(reactedEntityId);
        reaction.setReactionType(reactionType.getValue());
        reaction.setUser(user);

        Reaction savedReaction = reactionRepository.save(reaction);

        UpdateReactionResponse updateReactionResponse = new UpdateReactionResponse();
        updateReactionResponse.setId(savedReaction.getId());
        updateReactionResponse.setReactedEntity(savedReaction.getReactedEntity());
        updateReactionResponse.setReactedEntityId(savedReaction.getReactedEntityId());
        updateReactionResponse.setReactionType(ReactionType.fromValue(savedReaction.getReactionType()));
        updateReactionResponse.setUserId(savedReaction.getUser().getId());

        return updateReactionResponse;
    }

    public GetReactionResponse getReaction(final ReactedEntity reactedEntity, final int reactedEntityId) {
        int likes = reactionRepository.countLikesForEntity(reactedEntity, reactedEntityId);
        int dislikes = reactionRepository.countDislikesForEntity(reactedEntity, reactedEntityId);

        return new GetReactionResponse(dislikes, likes);
    }

    public List<TopEntityResponse> getTopEntities(final TopEntity topEntity) {
        if (topEntity == TopEntity.POEM) {
            return getTopPoems();
        } else {
            return getTopUsers();
        }
    }

    private List<TopEntityResponse> getTopPoems() {
        List<Object[]> topEntities = reactionRepository.findTopEntities(ReactedEntity.POEM);

        List<Integer> ids = new ArrayList<>();
        List<Integer> likes = new ArrayList<>();

        for (Object[] entityData : topEntities) {
            Integer entityId = (Integer) entityData[0];
            Integer reactionSum = ((Long) entityData[1]).intValue();

            ids.add(entityId);
            likes.add(reactionSum);
        }

        List<String> titles = ids.stream().map(poemRepository::findTitleById).toList();

        List<TopEntityResponse> topPoems = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            TopEntityResponse topEntityResponse = new TopEntityResponse();
            topEntityResponse.setId(ids.get(i));
            topEntityResponse.setName(titles.get(i));
            topEntityResponse.setReactionSum(likes.get(i));

            topPoems.add(topEntityResponse);
        }

        return topPoems;
    }

    private List<TopEntityResponse> getTopUsers() {
        List<User> users = userRepository.findAll();

        List<TopEntityResponse> topUsers = new ArrayList<>();

        for (User user : users) {
            TopEntityResponse topEntityResponse = new TopEntityResponse();

            topEntityResponse.setId(user.getId());
            topEntityResponse.setName(user.getEmail());


            GetLikesResponse getLikesResponse = profileService.getLikes(user.getId());
            topEntityResponse.setReactionSum(getLikesResponse.getSum());

            topUsers.add(topEntityResponse);
        }

        topUsers.sort((o1, o2) -> o2.getReactionSum() - o1.getReactionSum());

        return topUsers;
    }
}
