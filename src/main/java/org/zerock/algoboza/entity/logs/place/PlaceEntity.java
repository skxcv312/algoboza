package org.zerock.algoboza.entity.logs.place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.zerock.algoboza.entity.base.BaseEntity;
import org.zerock.algoboza.entity.logs.EventEntity;

@Entity
@Table(name = "place")
@SuperBuilder
@NoArgsConstructor
@Getter
public class PlaceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @Column(nullable = false)
    private String searchText;


}
