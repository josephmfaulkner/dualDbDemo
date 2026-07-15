package com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class PostEntity {
    private String id;
    private String title;
    private String content;

    private List<CommentEntity> comments;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }
}
