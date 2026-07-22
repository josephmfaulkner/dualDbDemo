package com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "COMMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Transient
    @Builder.Default
    private boolean isNewRecord = false;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNewRecord;
    }

}
