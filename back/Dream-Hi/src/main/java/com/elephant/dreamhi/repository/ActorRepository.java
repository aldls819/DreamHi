package com.elephant.dreamhi.repository;

import com.elephant.dreamhi.model.entity.ActorProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<ActorProfile, Long>, ActorRepositoryCustom {

    Optional<ActorProfile> findByUser_Id(Long id);

    Optional<ActorProfile> findByIdAndUser_Id(Long id, Long userId);

}
