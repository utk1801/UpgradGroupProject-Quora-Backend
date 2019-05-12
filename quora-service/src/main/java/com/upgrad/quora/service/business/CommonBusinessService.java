package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonBusinessService {

    @Autowired
    private CommonDao commonDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserProfile(final String uuid){
        return commonDao.getUserProfile(uuid);
    }
}
