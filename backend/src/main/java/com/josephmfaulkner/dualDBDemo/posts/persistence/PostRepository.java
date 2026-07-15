package com.josephmfaulkner.dualDBDemo.posts.persistence;

import java.util.List;

import com.josephmfaulkner.dualDBDemo.posts.dto.Post;

public interface PostRepository {

    public List<Post> getAllPosts();

    public Post getPostById(String id);

    public Post savePost(Post newPost);

    public Post updatePost(Post existingPost);

    public void deletePost(String id);

}
