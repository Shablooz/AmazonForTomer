package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.User;

public interface IUserRepository {

    User getUser(int userId);

    void addUser(int userId,User user);
}
