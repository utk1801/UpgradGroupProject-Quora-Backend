package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionsEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     * This method receives the Answer object to be persisted in the database
     *
     * @param answerEntity
     */
    public void createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
    }

    /**
     * This method fetches the answerEntity for the given Uuid from the database
     *
     * @param answerUuid
     * @return
     */
    public AnswerEntity getAnswerByUuid(String answerUuid) {
        try {
            TypedQuery<AnswerEntity> answerByUuidQuery =
                    entityManager.createNamedQuery("findAnswerByUuid", AnswerEntity.class);
            answerByUuidQuery.setParameter("uuid", answerUuid);
            return answerByUuidQuery.getSingleResult();
        } catch (NoResultException nrex) {
            return null;
        }
    }

    /**
     * This method retrives list of answers for the given question
     *
     * @param questionsEntity
     * @return
     */
    public List<AnswerEntity> getAnswerByQUuid(QuestionsEntity questionsEntity) {
        try {
            TypedQuery<AnswerEntity> answerByQuidQuery =
                    entityManager.createNamedQuery("findAnswersByQuuid", AnswerEntity.class);
            answerByQuidQuery.setParameter("question_id", questionsEntity.getUuid());
            return answerByQuidQuery.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * This calls the JPA method to update an answer
     *
     * @param updatedAnswerEntity
     */
    public void updateAnswer(AnswerEntity updatedAnswerEntity) {
        entityManager.merge(updatedAnswerEntity);
    }
    /**
     * This method calls the JPA method to delete an answer
     *
     * @param answerEntity
     */
    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }
}
