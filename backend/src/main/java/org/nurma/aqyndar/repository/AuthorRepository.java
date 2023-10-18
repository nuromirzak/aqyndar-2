package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
