package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.User.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepositoryAsHashmap implements IUserRepository {
    private final ConcurrentHashMap<Integer,User> integerUserHashMap;

    //this integer is the max id that exists in the system
    private AtomicInteger maxId;

    public UserRepositoryAsHashmap() {
        this.integerUserHashMap = new ConcurrentHashMap<>();

        //TODO when we add the database this maxid should be the highest id we have in the database
        maxId = new AtomicInteger(0);
    }


    @Override
    public User getUser(int userId) {
        return integerUserHashMap.get(userId);
    }

    @Override
    public void removeUser(int userId) {
        this.integerUserHashMap.remove(userId);
    }

    @Override
    public void addUser(int userId, User user) {
        this.integerUserHashMap.put(userId,user);
        maxId.getAndIncrement();
    }

    @Override
    public User checkIfUserExists(String username){
        User user = getUserByUsername(username);
        if(user != null){
            return user;
        }
        throw new IllegalArgumentException("cannot find user with this username");
    }


    @Override
    public int getUserId(User user) throws IllegalArgumentException{

        for (Map.Entry<Integer, User> entry : integerUserHashMap.entrySet()){
            if(user.getUserName().equals(entry.getValue().getUserName()))
                return entry.getKey();
        }
        throw new IllegalArgumentException("cannot find user with this username");
    }

    @Override
    public int getNewUserId() {
        return maxId.get() + 1;
    }


    //TODO in the future this will be replaced with taking a user by username in the database - this is prototype for testing
    private User getUserByUsername(String username){
        for (User user : integerUserHashMap.values()){
            if(user.getUserName().equals(username))
                return user;
        }
        return null;
    }

}
