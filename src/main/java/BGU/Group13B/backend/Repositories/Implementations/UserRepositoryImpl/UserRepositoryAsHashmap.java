package BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.User.UserCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        return null;
    }

    @Override
    public boolean checkIfUserWithEmailExists(String email) throws IllegalArgumentException {
        for (Map.Entry<Integer, User> entry : integerUserHashMap.entrySet()){
            if(email.equals(entry.getValue().getEmail()))
                return true;
        }
        return false;
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


    //TODO in the future this will be replaced with taking a user by username in the database - tomer use this method
    @Override
    public User getUserByUsername(String username){
        for (User user : integerUserHashMap.values()){
            if(user.getUserName().equals(username))
                return user;
        }
        return null;
    }

    @Override
    public int getUserIdByUsername(String username) {
        User user = getUserByUsername(username);
        if(user == null)
            throw new IllegalArgumentException("cannot find user with this username");

        return getUserId(user);
    }

    @Override
    public HashMap<Integer, String> getUserIdsToUsernamesMapper(List<Integer> userIds) {
        HashMap<Integer, String> userIdsToUsernames = new HashMap<>();
        for (Integer userId : userIds){
            User user = getUser(userId);
            if(user == null)
                throw new IllegalArgumentException("cannot find user with the id " + userId);
            userIdsToUsernames.put(userId, user.getUserName());
        }
        return userIdsToUsernames;
    }

    @Override
    public List<UserCard> getAllUserCards(){
        List<UserCard> userCards = new ArrayList<>();
        for (User user : integerUserHashMap.values()){
            userCards.add(user.getUserCard());
        }
        return userCards;
    }

}
