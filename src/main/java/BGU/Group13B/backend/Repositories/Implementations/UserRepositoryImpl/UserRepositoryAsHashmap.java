package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.User.User;

import java.util.HashMap;
import java.util.List;

public class UserRepositoryAsHashmap implements IUserRepository {
    private final HashMap<Integer,User> integerUserHashMap;

    public UserRepositoryAsHashmap(HashMap<Integer,User> integerUserHashMap) {
        this.integerUserHashMap = integerUserHashMap;
    }


    @Override
    public User getUser(int userId) {
        return integerUserHashMap.get(userId);
    }

    @Override
    public void addUser(int userId, User user) {
        this.integerUserHashMap.put(userId,user);
    }
}
