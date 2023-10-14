package com.zetavn.api.model.dto;

import com.zetavn.api.model.entity.PostActivityEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostActivityParentDto {
    private PostActivityEntity postActivity;
    private List<PostActivityEntity> postActivityList;
}
