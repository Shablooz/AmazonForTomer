package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.User;

public interface IUserRepository {

    User getUser(int userId);

    void removeUser(int userId);

    void addUser(int userId,User user);

    //checks if user with the following username exists if it does - returns the user
    User checkIfUserExists(String username) throws IllegalArgumentException;

    //returns the users id
    int getUserId(User user) throws IllegalArgumentException;
}
