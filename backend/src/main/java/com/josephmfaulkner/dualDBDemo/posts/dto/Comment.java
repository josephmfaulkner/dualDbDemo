package com.josephmfaulkner.dualDBDemo.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record Comment(
    Integer id,
    String content
) {
}