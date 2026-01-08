package pl.training.payments.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaCardRepository extends JpaRepository<CardEntity, String> /*CrudRepository<CardEntity, String>*/ /*Repository<CardEntity, String>*/ {
}
