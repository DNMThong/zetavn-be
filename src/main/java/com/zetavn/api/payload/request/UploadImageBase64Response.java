package com.zetavn.api.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UploadImageBase64Response {
    String[] images;
}
