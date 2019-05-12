package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method= RequestMethod.POST,path="/signup")
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)
    {
        final UsersEntity usersEntity=new UsersEntity();
        usersEntity.setUuid(UUID.randomUUID().toString());
        usersEntity.setFirstName(signupUserRequest.getFirstName());
        usersEntity.setLastName(signupUserRequest.getLastName());
        usersEntity.setUserName(signupUserRequest.getUserName());
        usersEntity.setEmail(signupUserRequest.getEmailAddress());
        usersEntity.setPassword(signupUserRequest.getPassword());
        usersEntity.setSalt("Utkarsh123!");
        usersEntity.setCountry(signupUserRequest.getCountry());
        usersEntity.setAboutMe(signupUserRequest.getAboutMe());
        usersEntity.setDob(signupUserRequest.getDob());
        usersEntity.setRole("nonadmin");
        usersEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UsersEntity createdUserEntity= userBusinessService.signup(usersEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse,HttpStatus.CREATED);
    }

//    @RequestMapping(method = RequestMethod.POST,path="/signin")
//    public
}
