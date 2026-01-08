package pl.training.payments.adapters.persistence.jpa;

import org.springframework.data.repository.CrudRepository;

public interface JpaCardRepository extends CrudRepository<CardEntity, String> /*Repository<CardEntity, String>*/ {
}
