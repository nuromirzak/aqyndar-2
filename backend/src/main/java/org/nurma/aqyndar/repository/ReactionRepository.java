package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Reaction;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    Optional<Reaction> findByReactedEntityAndReactedEntityIdAndUserId(ReactedEntity reactedEntity, int reactedEntityId,
                                                                      int userId);

    @Query("""
            SELECT COUNT(r) FROM Reaction r
                WHERE r.reactedEntity = :reactedEntity
                AND r.reactedEntityId = :reactedEntityId
                AND r.reactionType = 1
                """)
    int countLikes(@Param("reactedEntity") ReactedEntity reactedEntity, @Param("reactedEntityId") int reactedEntityId);

    @Query("""
            SELECT COUNT(r) FROM Reaction r
                WHERE r.reactedEntity = :reactedEntity
                AND r.reactedEntityId = :reactedEntityId
                AND r.reactionType = -1
                """)
    int countDislikes(@Param("reactedEntity") ReactedEntity reactedEntity,
                      @Param("reactedEntityId") int reactedEntityId);
}
