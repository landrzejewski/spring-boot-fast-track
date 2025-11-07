package pl.training.security.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

}
