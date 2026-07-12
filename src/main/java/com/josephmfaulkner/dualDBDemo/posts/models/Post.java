package com.josephmfaulkner.dualDBDemo.posts.models;

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
public class Post {
    private String id;
    private String title;
    private String content;

    private List<Comment> comments;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }
}
