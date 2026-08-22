package com.raid.lmee.repos;

import com.raid.lmee.domain.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAllByFavouriteClubsId(UUID id);

}
