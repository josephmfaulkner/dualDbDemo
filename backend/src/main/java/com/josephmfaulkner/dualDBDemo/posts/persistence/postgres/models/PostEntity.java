package com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "POST")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity implements Persistable<String> {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CommentEntity> comments;

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
