package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {
    @Query("""
            SELECT a.id FROM Annotation a
            WHERE a.user.id = :userId
            """)
    List<Integer> findIdsByUserId(int userId);

    void deleteByUserId(int id);
}
