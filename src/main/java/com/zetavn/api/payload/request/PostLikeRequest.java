package com.zetavn.api.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeRequest {

    String postId;

    String userId;

}
