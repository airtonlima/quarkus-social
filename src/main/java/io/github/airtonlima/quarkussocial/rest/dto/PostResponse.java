package io.github.airtonlima.quarkussocial.rest.dto;

import io.github.airtonlima.quarkussocial.rest.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String text;
    private LocalDateTime createdAt;

    public static PostResponse fromEntity(Post post) {
        PostResponse response = new PostResponse();
        response.setText(post.getText());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}
