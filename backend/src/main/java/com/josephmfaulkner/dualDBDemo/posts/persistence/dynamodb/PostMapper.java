package com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb;

import com.josephmfaulkner.dualDBDemo.posts.dto.Comment;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models.CommentEntity;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models.PostEntity;

import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    public static Post toRecord(PostEntity entity) {
        List<Comment> comments = entity.getComments() == null ? null :
                entity.getComments().stream()
                        .map((comment) -> toRecordComment(comment))
                        .collect(Collectors.toList());
        return new Post(entity.getId(), entity.getTitle(), entity.getContent(), comments);
    }

    public static PostEntity toEntity(Post record) {
        List<CommentEntity> commentEntities = record.comments() == null ? null :
                record.comments().stream()
                        .map((comment) -> toEntityComment(comment))
                        .collect(Collectors.toList());
        return PostEntity.builder()
                .id(record.id())
                .title(record.title())
                .content(record.content())
                .comments(commentEntities)
                .build();
    }

    private static Comment toRecordComment(CommentEntity entity) {
        return new Comment(entity.getId(), entity.getContent());
    }

    private static CommentEntity toEntityComment(Comment record) {
        return new CommentEntity(record.id(), record.content());
    }
}
