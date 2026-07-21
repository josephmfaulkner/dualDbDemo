package com.josephmfaulkner.dualDBDemo.posts.dto;


import lombok.Builder;


@Builder
public record Comment(
    String id,
    String content
) {
}