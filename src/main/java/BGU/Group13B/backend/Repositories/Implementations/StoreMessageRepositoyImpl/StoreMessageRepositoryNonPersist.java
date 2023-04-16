package BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreMessageRepositoryNonPersist implements IStoreMessagesRepository{

    ConcurrentLinkedDeque<Message> unreadMessages = new ConcurrentLinkedDeque<>();
    ConcurrentHashMap<Integer,Message> oldMessages = new ConcurrentHashMap<>();
    ConcurrentHashMap<String,Integer> placeInOldMessages = new ConcurrentHashMap<>();
    AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void sendMassage(Message message, int storeId, String userName) {
        unreadMessages.add(message);
    }

    @Override
    public Message readUnreadMassage(int storeId, String userName) {
        if(unreadMessages.isEmpty()) throw new IllegalArgumentException("No unread messages");
        return unreadMessages.peek();
    }


    /*
    * This function will return the next unread message Per user If there is no more old messages it will throw an exception
     */
    @Override
    public Message readReadMassage(int storeId, String userName) {
        placeInOldMessages.putIfAbsent(userName,1);
        if(counter.get()<=placeInOldMessages.get(userName)) throw new IllegalArgumentException("You have no more old massages to read");

        Message message = oldMessages.get(placeInOldMessages.get(userName));
        placeInOldMessages.put(userName,placeInOldMessages.get(userName)+1);
        return message;
    }

    @Override
    public void markAsRead(String senderId, int massageId, String userName) {
        Message first= unreadMessages.peek();

        if(first==null) throw new IllegalArgumentException("No unread messages");
        if(!unreadMessages.remove(first)) throw new IllegalArgumentException("message is not in unread messages");

        oldMessages.put(counter.getAndIncrement(),first);
    }

    @Override
    public void refreshOldMassage(int storeId, String userName) {
        placeInOldMessages.put(userName,1);
    }








}
