package pl.training.payments.adapters.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface MongoCardRepository extends MongoRepository<CardDocument, String> {

    Optional<CardDocument> findByNumber(String cardNumber);

}
