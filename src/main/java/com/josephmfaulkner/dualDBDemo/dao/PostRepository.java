package com.josephmfaulkner.dualDBDemo.dao;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.josephmfaulkner.dualDBDemo.exceptions.PostNotFoundException;
import com.josephmfaulkner.dualDBDemo.models.Post;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class PostRepository {
    
    private final DynamoDbTable<Post> postTable;

    public PostRepository(DynamoDbEnhancedClient enhancedClient) {
        this.postTable = enhancedClient.table("Posts", TableSchema.fromBean(Post.class));
    }

    public List<Post> getAllPosts() {
        return postTable.scan().items().stream().collect(Collectors.toList());
    }

    public Post getPostById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Post post = postTable.getItem(key);
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return post;
    }

    public Post savePost(Post post) {
        post.setId(UUID.randomUUID().toString());
        postTable.putItem(post);
        return post;
    }

    public Post updatePost(Post post) {
        // First, verify the post exists.
        getPostById(post.getId());
        return postTable.updateItem(post);
    }

    public void deletePost(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Post deletedPost = postTable.deleteItem(Key.builder().partitionValue(id).build());

        if (deletedPost == null) {
            throw new PostNotFoundException(id);
        }
    }
}
