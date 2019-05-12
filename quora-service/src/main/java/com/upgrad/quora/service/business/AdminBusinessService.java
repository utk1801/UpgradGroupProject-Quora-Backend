package com.upgrad.quora.service.business;

//
//@Service
//public class AdminBusinessService {
//
//    @Autowired
//    private AdminDao adminDao;
//
//    //This method is implemented for the purpose of deleteing the user from the database.
//    //This method uses adminDao to make contact with the database and to fetch the details of the user to be deleted and as well as the authtoken of the requesting user.
//    //once the adminDao returns the corresponding variables are checked such as if the requesting user is admin or nonadmin signedin or signedout.
//    //If all condition are satisfied then the user is allowed to fetch the details of the user to deleted and the user is deleted.
//
//    @Transactional(propagation = Propagation.REQUIRED)
//    public UserEntity deleteUser(final String uuid, final String authorizationToken)throws UserNotFoundException, AuthorizationFailedException {
//        UserAuthTokenEntity userAuthEntity = adminDao.getAuthToken(authorizationToken);
//
//        if (userAuthEntity == null) {//checking if user is not signed in.
//            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
//        }else if (userAuthEntity.getLogoutAt()!= null) {//checking if user is signedout.
//            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
//        }
//
//        String role = userAuthEntity.getUser().getRole();
//
//        if(role.equals("admin")) {//checking if the role of the user is admin.
//            UserEntity deletedUser = adminDao.deleteUser(uuid);
//            if (deletedUser == null) {//checking if the user to be deleted exist in the user table.
//                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
//            }
//            return deletedUser;
//        }
//        throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
//    }
//
//
//}
