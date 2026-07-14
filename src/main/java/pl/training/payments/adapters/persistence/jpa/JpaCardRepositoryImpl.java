package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class JpaCardRepositoryImpl implements JpaCardRepositoryExtensions {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CardEntity> findByCardNumber(String cardNumber) {
        return entityManager.createNamedQuery(CardEntity.BY_CARD_NUMBER, CardEntity.class)
                .setParameter("cardNumber", cardNumber)
                .getResultList();
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
