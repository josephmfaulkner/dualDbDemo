package com.josephmfaulkner.dualDBDemo.posts.persistence.postgres;

import com.josephmfaulkner.dualDBDemo.exceptions.PostNotFoundException;
import com.josephmfaulkner.dualDBDemo.posts.dto.Comment;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.models.CommentEntity;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.models.PostEntity;

import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("postRepositoryPostgres")
public class PostRepositoryPostgres implements PostRepository {

    private final PostJpaRepository postJpaRepository;

    public PostRepositoryPostgres(PostJpaRepository postJpaRepository) {
        this.postJpaRepository = postJpaRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        return postJpaRepository.findAll().stream()
                .map(this::toRecord)
                .collect(Collectors.toList());
    }

    @Override
    public Post getPostById(String id) {
        PostEntity postEntity = postJpaRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return toRecord(postEntity);
    }

    @Override
    @Transactional
    public Post savePost(Post newPost) {
        PostEntity postEntity = toEntity(newPost);


        boolean existsInPostgres = postEntity.getId() != null && postJpaRepository.existsById(postEntity.getId());
        postEntity.setNewRecord(!existsInPostgres);
        
        PostEntity savedEntity = postJpaRepository.save(postEntity);
        
        return toRecord(savedEntity);
    }

    @Override
    public Post updatePost(Post existingPost) {
        return this.savePost(existingPost);
    }

    @Override
    public void deletePost(String id) {
        if (!postJpaRepository.existsById(id)) {
            throw new PostNotFoundException(id);
        }
        postJpaRepository.deleteById(id);
    }

    private Post toRecord(PostEntity entity) {
        List<Comment> comments = entity.getComments() == null ? null :
                entity.getComments().stream()
                        .map(this::toRecordComment)
                        .collect(Collectors.toList());
        return new Post(entity.getId(), entity.getTitle(), entity.getContent(), comments);
    }

    private PostEntity toEntity(Post record) {
        PostEntity postEntity = PostEntity.builder()
                .id(record.id() != null ? record.id() : UUID.randomUUID().toString())
                .title(record.title())
                .content(record.content())
                .build();

        if (record.comments() != null) {
            List<CommentEntity> commentEntities = record.comments().stream()
                    .map(comment -> toEntityComment(comment, postEntity))
                    .collect(Collectors.toList());
            postEntity.setComments(commentEntities);
        }

        return postEntity;
    }

    private Comment toRecordComment(CommentEntity entity) {
        return new Comment(entity.getId(), entity.getContent());
    }

    private CommentEntity toEntityComment(Comment record, PostEntity postEntity) {
        return CommentEntity.builder()
                .id(record.id() != null ? record.id() : UUID.randomUUID().toString())
                .content(record.content())
                .post(postEntity)
                .build();
    }
}
