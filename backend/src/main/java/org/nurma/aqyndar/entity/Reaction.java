package org.nurma.aqyndar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nurma.aqyndar.entity.enums.ReactedEntity;

@Entity
@Table(name = "reaction")
@NoArgsConstructor
@Getter
@Setter
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reacted_entity", nullable = false, length = 255)
    private ReactedEntity reactedEntity;

    @Column(name = "reacted_entity_id", nullable = false)
    private int reactedEntityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reacted_user_id", nullable = false)
    private User user;

    @Column(name = "reaction_type", nullable = false)
    private int reactionType;
}
