package com.josephmfaulkner.dualDBDemo.posts;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public Post getPostById(String id) {
        return postRepository.getPostById(id);
    }

    public Post createPost(Post post) {
        // Future business logic (e.g., validation, notifications) would go here.
        return postRepository.savePost(post);
    }

    public void deletePost(String id) {
        postRepository.deletePost(id);
    }
}
