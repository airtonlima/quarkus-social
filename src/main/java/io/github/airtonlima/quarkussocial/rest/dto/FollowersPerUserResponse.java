package io.github.airtonlima.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {
    private Integer followersCount;
    private List<FollowerResponse> followers;
}
