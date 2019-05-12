package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.UserDeleteService;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private UserDeleteService userDeleteService;

    @RequestMapping(method = RequestMethod.DELETE,path = "/admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId")final String userUuid, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        String[] bearerToken=accessToken.split("Bearer ");

        UsersEntity signedInUserEntity=commonBusinessService.getUser(userUuid,bearerToken[1]);
        UsersEntity userToDelete = userDeleteService.getUserByUuid(userUuid);
        final String uuid=userDeleteService.deleteUser(signedInUserEntity,userToDelete);
        UserDeleteResponse userDeleteResponse=new UserDeleteResponse().id(uuid).status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);

    }
}