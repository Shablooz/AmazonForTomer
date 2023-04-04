package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.User.User;

import java.util.List;

public class UserRepositoryAsList implements IUserRepository {
    private final List<User> users;

    public UserRepositoryAsList(List<User> users) {
        this.users = users;
    }
}
