package com.blog.adapter.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    private Long total;
    private List<UserInfoResponse> usersInfo;
}
