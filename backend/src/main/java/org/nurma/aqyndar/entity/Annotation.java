package org.nurma.aqyndar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "annotation")
@NoArgsConstructor
@Getter
@Setter
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "start_range_index", nullable = false)
    private int startRangeIndex;

    @Column(name = "end_range_index", nullable = false)
    private int endRangeIndex;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "poem_id", nullable = false)
    private Poem poem;
}
