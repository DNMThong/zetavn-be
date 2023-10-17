package com.zetavn.api.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadResponse {
    String id;
    String url;
    String type;
    Integer width;
    Integer height;
}
