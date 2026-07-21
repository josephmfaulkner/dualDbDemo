package com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.josephmfaulkner.dualDBDemo.exceptions.PostNotFoundException;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models.PostEntity;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
            
@Repository("postRepositoryDynamoDb")
public class PostRepositoryDynamoDb implements PostRepository {
    
    private final DynamoDbTable<PostEntity> postTable;

    public PostRepositoryDynamoDb(DynamoDbEnhancedClient enhancedClient) {
        this.postTable = enhancedClient.table("Posts", TableSchema.fromBean(PostEntity.class));
    }

    public List<Post> getAllPosts() {
        return postTable.scan().items().stream()
                .map(PostMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Post getPostById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        PostEntity postEntity = postTable.getItem(key);
        if (postEntity == null) {
            throw new PostNotFoundException(id);
        }
        return PostMapper.toRecord(postEntity);
    }

    public Post savePost(Post newPost) {
        PostEntity postEntity = PostMapper.toEntity(newPost);
        postEntity.setId(UUID.randomUUID().toString());
        postTable.putItem(postEntity);
        return PostMapper.toRecord(postEntity);
    }

    public Post updatePost(Post existingPost) {
        PostEntity postEntity = PostMapper.toEntity(existingPost);
        // First, verify the post exists.
        getPostById(postEntity.getId());
        PostEntity updatedEntity = postTable.updateItem(postEntity);
        return PostMapper.toRecord(updatedEntity);
    }

    public void deletePost(String id) {
        PostEntity deletedPost = postTable.deleteItem(Key.builder().partitionValue(id).build());

        if (deletedPost == null) {
            throw new PostNotFoundException(id);
        }
    }

}
