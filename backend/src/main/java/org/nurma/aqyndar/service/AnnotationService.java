package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.PatchAnnotationRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.entity.Annotation;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.repository.AnnotationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnotationService {
    private final AnnotationRepository annotationRepository;
    private static final String ANNOTATION_NOT_FOUND = "Annotation with id %s not found";

    public GetAnnotationResponse getAnnotationById(final int id) {
        Optional<Annotation> annotationOptional = annotationRepository.findById(id);

        if (annotationOptional.isEmpty()) {
            throw new ResourceNotFound(ANNOTATION_NOT_FOUND.formatted(id));
        }

        Annotation annotation = annotationOptional.get();
        GetAnnotationResponse getAnnotationResponse = new GetAnnotationResponse();
        getAnnotationResponse.setId(annotation.getId());
        getAnnotationResponse.setContent(annotation.getContent());
        return getAnnotationResponse;
    }

    public GetAnnotationResponse createAnnotation(final CreateAnnotationRequest request) {
        Annotation annotation = new Annotation();
        annotation.setContent(request.getContent());

        Annotation savedAnnotation = annotationRepository.save(annotation);

        GetAnnotationResponse getAnnotationResponse = new GetAnnotationResponse();
        getAnnotationResponse.setId(savedAnnotation.getId());
        getAnnotationResponse.setContent(savedAnnotation.getContent());
        return getAnnotationResponse;
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

        Annotation savedAnnotation = annotationRepository.save(annotation);
        GetAnnotationResponse getAnnotationResponse = new GetAnnotationResponse();
        getAnnotationResponse.setId(savedAnnotation.getId());
        getAnnotationResponse.setContent(savedAnnotation.getContent());
        return getAnnotationResponse;
    }

    public DeleteResponse deleteAnnotation(final int id) {
        Optional<Annotation> annotationOptional = annotationRepository.findById(id);

        if (annotationOptional.isEmpty()) {
            throw new ResourceNotFound(ANNOTATION_NOT_FOUND.formatted(id));
        }

        annotationRepository.deleteById(id);

        return new DeleteResponse();
    }
}
