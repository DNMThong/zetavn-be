package com.zetavn.api.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PostLikeRequest {

    String postId;

    String userId;

}
