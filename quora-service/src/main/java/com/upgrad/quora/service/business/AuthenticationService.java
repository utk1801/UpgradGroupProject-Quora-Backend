package com.upgrad.quora.service.business;

import org.springframework.stereotype.Service;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUsername(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        final String encyptedPassword= passwordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(encyptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider=new JwtTokenProvider(encyptedPassword);
            UserAuthEntity userAuthTokenByUuid = userDao.getUserAuthTokenByUuid(userEntity.getUuid());
            UserAuthEntity userAuthEntity=new UserAuthEntity();
            userAuthEntity.setUser_id(userEntity);
            final ZonedDateTime now=ZonedDateTime.now();
            final ZonedDateTime expiresAt=now.plusHours(8);
            userAuthEntity.setAccess_token(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
            userAuthEntity.setUuid(userEntity.getUuid());
            userAuthEntity.setExpires_at(expiresAt);
            userAuthEntity.setLogin_at(now);

            if(userAuthTokenByUuid == null) {
                userDao.createAuthToken(userAuthEntity);
            }else{
                userDao.updateUserEntity(userAuthEntity);
            }

            return userAuthEntity;
        }else{
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }
}
