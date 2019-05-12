package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericExceptionCode;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionsEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * This service class has methods related to business rules of following functionalities
 * create/edit/delete/get questions
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class QuestionBusinessService {

    @Autowired private QuestionDao questionDao;

    @Autowired private UserDao userDao;

    /**
     * This method is used to manage business rules to create/post a new question, and handles various
     * scenarios
     *
     * @param questionsEntity
     * @param accessToken
     * @throws AuthorizationFailedException
     */
    public void createQuestionService(QuestionsEntity questionsEntity, String accessToken)
            throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
        }

        if ((userAuthTokenEntity.getLogoutAt() != null)) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_002_POST_QUESTION.getCode(),
                    GenericExceptionCode.ATHR_002_POST_QUESTION.getDescription());
        }

        questionsEntity.setUserEntity(userAuthTokenEntity.getUsers());
        questionDao.createQuestion(questionsEntity);
    }

    /**
     * This method is used to manage business rules to edit the posted questions and handles multiple
     * scenarios
     *
     * @param questionEntity
     * @param accessToken
     * @return
     * @throws InvalidQuestionException
     * @throws AuthorizationFailedException
     */
    public void editQuestionService(String accessToken, QuestionsEntity questionEntity)
            throws InvalidQuestionException, AuthorizationFailedException {
        final QuestionsEntity existingQuestionEntity =
                questionDao.getQuestionByUuid(questionEntity.getUuid());

        final UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
        } else if ((userAuthTokenEntity.getLogoutAt() != null)
                && userAuthTokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_002_EDIT_QUESTION.getCode(),
                    GenericExceptionCode.ATHR_002_EDIT_QUESTION.getDescription());
        } else if (existingQuestionEntity != null && !existingQuestionEntity
                .getUserEntity()
                .getId()
                .equals(userAuthTokenEntity.getUsers().getId())) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_003_QUES_EDIT.getCode(),
                    GenericExceptionCode.ATHR_003_QUES_EDIT.getDescription());
        } else if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        existingQuestionEntity.setContent(questionEntity.getContent());
        updateQuestion(existingQuestionEntity);
    }

    /**
     * This method is used to manage business rules to persist the update questionEntity using JPA
     * method
     *
     * @param updatedQuestionsEntity
     */
    public void updateQuestion(QuestionsEntity updatedQuestionsEntity) {
        questionDao.editQuestion(updatedQuestionsEntity);
    }

    /**
     * This method is used to handle business rules to get all the questions in quora application
     *
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     */
    public List<QuestionsEntity> getQuestionList(String accessToken)
            throws AuthorizationFailedException {

        UserAuthTokenEntity tokenEntity = userDao.getAuthToken(accessToken);

        if (tokenEntity == null) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
        } else if (tokenEntity.getLogoutAt() != null
                && tokenEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_002_QUES_GET.getCode(),
                    GenericExceptionCode.ATHR_002_QUES_GET.getDescription());
        }

        return questionDao.getAllQuestions();
    }

    /**
     * This method is used to manage business rules to delete the posted question and handles multiple
     * scenarios
     *
     * @param uuid
     * @param accessToken
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    public void deleteQuestionByUuid(String uuid, String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
        }

        if (hasUserSignedOut(userAuthTokenEntity.getLogoutAt())) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_002_QUES_DELETE.getCode(),
                    GenericExceptionCode.ATHR_002_QUES_DELETE.getDescription());
        }

        QuestionsEntity questionToDelete = questionDao.getQuestionByUuid(uuid);
        if (questionToDelete == null) {
            throw new InvalidQuestionException(
                    GenericExceptionCode.QUES_001.getCode(), GenericExceptionCode.QUES_001.getDescription());
        }
        UserEntity loggedInUser = userAuthTokenEntity.getUsers();
        UserEntity questionOwner = questionToDelete.getUserEntity();

        if (!questionOwner.getUuid().equals(loggedInUser.getUuid())
                && !("admin").equals(loggedInUser.getRole())) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_003_QUES_DELETE.getCode(),
                    GenericExceptionCode.ATHR_003_QUES_DELETE.getDescription());
        }
        questionDao.deleteQuestionByUuid(questionToDelete);
    }

    /**
     * This utility method checks if user has logged out
     *
     * @param loggedOutTime
     * @return
     */
    public boolean hasUserSignedOut(ZonedDateTime loggedOutTime) {
        return (loggedOutTime != null && ZonedDateTime.now().isAfter(loggedOutTime));
    }

    /**
     * This method is used to manage business rules for getting all the questions of a particular User
     *
     * @param userUuid
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public List<QuestionsEntity> getQuestionsForUserId(String userUuid, String accessToken)
            throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_001.getCode(), GenericExceptionCode.ATHR_001.getDescription());
        }
        if (hasUserSignedOut(userAuthTokenEntity.getLogoutAt())) {
            throw new AuthorizationFailedException(
                    GenericExceptionCode.ATHR_002_QUES_GET.getCode(),
                    GenericExceptionCode.ATHR_002_QUES_GET.getDescription());
        }
        UserEntity user = userDao.getUserbyUuid(userUuid);
        if (user == null) {
            throw new UserNotFoundException(
                    GenericExceptionCode.USR_001_QUES_GET_USER.getCode(),
                    GenericExceptionCode.USR_001_QUES_GET_USER.getDescription());
        }
        return questionDao.getQuestionsForUserId(userUuid);
    }
}
