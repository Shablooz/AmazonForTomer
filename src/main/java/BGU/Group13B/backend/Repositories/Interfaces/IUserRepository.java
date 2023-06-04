package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.User.UserCard;

import java.util.HashMap;
import java.util.List;

public interface IUserRepository {

    User getUser(int userId);

    void removeUser(int userId);

    void addUser(int userId,User user);

    //checks if user with the following username exists if it does - the method returns the user
    User checkIfUserExists(String username) throws IllegalArgumentException;

    //check if user with this email exists
    boolean checkIfUserWithEmailExists(String email) throws IllegalArgumentException;

    //returns the users id
    int getUserId(User user) throws IllegalArgumentException;

    //returns an id for a new user - this id is unique
    int getNewUserId();

    //TODO in the future this will be replaced with taking a user by username in the database - tomer use this method
    User getUserByUsername(String username);

    int getUserIdByUsername(String username);

    HashMap<Integer, String> getUserIdsToUsernamesMapper(List<Integer> userIds);


    List<UserCard> getAllUserCards();
}
