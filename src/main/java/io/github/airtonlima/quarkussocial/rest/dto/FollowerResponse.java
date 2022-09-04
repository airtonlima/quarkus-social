package io.github.airtonlima.quarkussocial.rest.dto;

import io.github.airtonlima.quarkussocial.rest.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private Long userId;
    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Follower follower) {
       this(follower.getId(), follower.getFollower().getId(), follower.getFollower().getName());
    }

    public FollowerResponse(Long id, Long userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }
}
