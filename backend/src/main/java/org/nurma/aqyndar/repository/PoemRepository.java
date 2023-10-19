package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Poem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoemRepository extends JpaRepository<Poem, Integer> {
    Poem findByAuthorId(int authorId);
}
