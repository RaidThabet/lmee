package com.raid.lmee.repos;

import com.raid.lmee.domain.Club;
import com.raid.lmee.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakId(UUID keycloakId);

    List<User> findAllByFavouriteClubsId(UUID id);

    @Query("select c from User u join u.favouriteClubs c where u.keycloakId = :userId")
    List<Club> findFavouriteClubsByUserId(@Param("userId") UUID userId);
}
