package com.josephmfaulkner.dualDBDemo.posts.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record Post(
    String id,
    String title,
    String content,
    List<Comment> comments
) {
}