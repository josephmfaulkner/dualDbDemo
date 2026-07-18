package com.josephmfaulkner.dualDBDemo.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record Comment(
    String id,
    String content
) {
}