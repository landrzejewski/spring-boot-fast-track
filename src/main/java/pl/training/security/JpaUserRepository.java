package pl.training.security;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByName(String username);

}
