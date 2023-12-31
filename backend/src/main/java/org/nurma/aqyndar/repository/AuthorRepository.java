package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    @Query("SELECT COUNT(p) FROM Poem p WHERE p.author.id = :authorId")
    int countPoemsByAuthorId(@Param("authorId") int authorId);
}
