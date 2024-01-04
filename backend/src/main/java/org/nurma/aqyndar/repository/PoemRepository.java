package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Poem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PoemRepository extends JpaRepository<Poem, Integer> {
    Poem findByAuthorId(int authorId);

    @Query("""
            SELECT p.id FROM Poem p
            WHERE p.user.id = :userId
            """)
    List<Integer> findIdsByUserId(int userId);

    @Query("""
        SELECT p.title FROM Poem p
        WHERE p.id = :poemId
        """)
    String findTitleById(@Param("poemId") Integer poemId);

    void deleteByUserId(int id);
}
