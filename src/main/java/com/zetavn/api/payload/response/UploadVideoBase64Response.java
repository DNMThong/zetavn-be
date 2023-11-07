package com.zetavn.api.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UploadVideoBase64Response {
    String videos[];
}
