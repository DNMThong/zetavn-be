package com.zetavn.api.repository;

import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.PostLikeEntity;
import com.zetavn.api.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    List<PostLikeEntity> getAllByPostEntity(PostEntity postEntity);
    Integer countByPostEntity (PostEntity postEntity);

    PostLikeEntity findPostLikeEntityByPostEntityAndUserEntity(PostEntity postEntity, UserEntity userEntity);

}
