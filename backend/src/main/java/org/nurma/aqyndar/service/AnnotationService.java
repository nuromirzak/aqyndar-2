package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.PatchAnnotationRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.entity.Annotation;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.repository.AnnotationRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.repository.ReactionRepository;
import org.nurma.aqyndar.util.EntityToDTOMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnotationService {
    private final AnnotationRepository annotationRepository;
    private final PoemRepository poemRepository;
    private final ReactionRepository reactionRepository;
    private static final String ANNOTATION_NOT_FOUND = "Annotation with id %s not found";
    private static final String POEM_NOT_FOUND = "Poem with id %s not found";
    private final AuthService authService;

    public GetAnnotationResponse getAnnotationById(final int id) {
        Optional<Annotation> annotationOptional = annotationRepository.findById(id);

        if (annotationOptional.isEmpty()) {
            throw new ResourceNotFound(ANNOTATION_NOT_FOUND.formatted(id));
        }

        return EntityToDTOMapper.mapAnnotationToGetAnnotationResponse(annotationOptional.get());
    }

    public GetAnnotationResponse createAnnotation(final CreateAnnotationRequest request) {
        Annotation annotation = new Annotation();
        annotation.setContent(request.getContent());
        annotation.setStartRangeIndex(request.getStartRangeIndex());
        annotation.setEndRangeIndex(request.getEndRangeIndex());

        Optional<Poem> poemOptional = poemRepository.findById(request.getPoemId());

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(request.getPoemId()));
        }

        Poem poem = poemOptional.get();
        annotation.setPoem(poem);

        User user = authService.getCurrentUserEntity();
        annotation.setUser(user);

        poem.getAnnotations().add(annotation);

        Annotation savedAnnotation = annotationRepository.save(annotation);

        return EntityToDTOMapper.mapAnnotationToGetAnnotationResponse(savedAnnotation);
    }

    public GetAnnotationResponse updateAnnotation(final int id, final PatchAnnotationRequest request) {
        Optional<Annotation> annotationOptional = annotationRepository.findById(id);

        if (annotationOptional.isEmpty()) {
            throw new ResourceNotFound(ANNOTATION_NOT_FOUND.formatted(id));
        }

        Annotation annotation = annotationOptional.get();

        if (request.getContent() != null) {
            annotation.setContent(request.getContent());
        }

        if (request.getStartRangeIndex() != null) {
            annotation.setStartRangeIndex(request.getStartRangeIndex());
        }

        if (request.getEndRangeIndex() != null) {
            annotation.setEndRangeIndex(request.getEndRangeIndex());
        }

        if (request.getPoemId() != null) {
            Optional<Poem> poemOptional = poemRepository.findById(request.getPoemId());

            if (poemOptional.isEmpty()) {
                throw new ResourceNotFound(POEM_NOT_FOUND.formatted(request.getPoemId()));
            }

            Poem poem = poemOptional.get();
            annotation.setPoem(poem);
        }

        return EntityToDTOMapper.mapAnnotationToGetAnnotationResponse(annotationRepository.save(annotation));
    }

    public DeleteResponse deleteAnnotation(final int id) {
        Optional<Annotation> annotationOptional = annotationRepository.findById(id);

        if (annotationOptional.isEmpty()) {
            throw new ResourceNotFound(ANNOTATION_NOT_FOUND.formatted(id));
        }

        List<Integer> reactionIds =
                reactionRepository.findIdsByReactedEntityAndReactedEntityId(ReactedEntity.ANNOTATION, id);
        reactionRepository.deleteAllById(reactionIds);

        annotationRepository.deleteById(id);

        return new DeleteResponse();
    }
}
