package org.nurma.aqyndar.repository;

import org.nurma.aqyndar.entity.Reaction;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
    int countLikesForEntity(@Param("reactedEntity") ReactedEntity reactedEntity,
                            @Param("reactedEntityId") int reactedEntityId);

    @Query("""
            SELECT COUNT(r) FROM Reaction r
                WHERE r.reactedEntity = :reactedEntity
                AND r.reactedEntityId = :reactedEntityId
                AND r.reactionType = -1
                """)
    int countDislikesForEntity(@Param("reactedEntity") ReactedEntity reactedEntity,
                               @Param("reactedEntityId") int reactedEntityId);

    @Query("""
            SELECT COUNT(*) FROM Reaction r
            WHERE r.reactedEntity = :entity
            AND r.reactedEntityId IN :entityIds
            AND r.reactionType = 1
            """)
    Integer countLikesOfUser(@Param("entity") ReactedEntity entity, @Param("entityIds") Iterable<Integer> entityIds);

    @Query("""
            SELECT COUNT(*) FROM Reaction r
            WHERE r.reactedEntity = :entity
            AND r.reactedEntityId IN :entityIds
            AND r.reactionType = -1
            """)
    Integer countDislikesOfUser(@Param("entity") ReactedEntity entity, @Param("entityIds") Iterable<Integer> entityIds);

    @Query("""
            SELECT r.reactedEntityId, SUM(r.reactionType) as reactionSum FROM Reaction r
            WHERE r.reactedEntity = :entity
            GROUP BY r.reactedEntityId
            ORDER BY reactionSum DESC
            """)
    List<Object[]> findTopEntities(@Param("entity") ReactedEntity entity);

    @Query("""
            SELECT r.id FROM Reaction r
            WHERE r.reactedEntity = :reactedEntity
            AND r.reactedEntityId = :reactedEntityId
            """)
    List<Integer> findIdsByReactedEntityAndReactedEntityId(@Param("reactedEntity") ReactedEntity reactedEntity,
                                                           @Param("reactedEntityId") int reactedEntityId);

    void deleteByUserId(int userId);
}
