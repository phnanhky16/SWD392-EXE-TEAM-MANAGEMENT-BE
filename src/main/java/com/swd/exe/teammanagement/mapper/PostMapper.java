package com.swd.exe.teammanagement.mapper;

import com.swd.exe.teammanagement.dto.request.PostRequest;
import com.swd.exe.teammanagement.dto.response.PostResponse;
import com.swd.exe.teammanagement.entity.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
    Post toPost(PostRequest request);
    List<PostResponse> toPostResponseList(List<Post> posts);
}
