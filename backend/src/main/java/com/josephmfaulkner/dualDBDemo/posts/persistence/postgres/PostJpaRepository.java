package com.josephmfaulkner.dualDBDemo.posts.persistence.postgres;

import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.models.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<PostEntity, String> {

}