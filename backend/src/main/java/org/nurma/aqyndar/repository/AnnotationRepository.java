package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {
}
