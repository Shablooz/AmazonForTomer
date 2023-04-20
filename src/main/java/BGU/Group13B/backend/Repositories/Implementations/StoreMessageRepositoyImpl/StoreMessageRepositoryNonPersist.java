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
    ConcurrentHashMap<Integer/*userId*/,Integer> placeInOldMessages = new ConcurrentHashMap<>();
    AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void sendMassage(Message message, int storeId, int userId) {
        unreadMessages.add(message);
    }

    @Override
    public Message readUnreadMassage(int storeId, int userId) {
        if(unreadMessages.isEmpty()) throw new IllegalArgumentException("No unread messages");
        return unreadMessages.peek();
    }


    /*
    * This function will return the next unread message Per user If there is no more old messages it will throw an exception
     */
    @Override
    public Message readReadMassage(int storeId, int userId) {
        placeInOldMessages.putIfAbsent(userId,1);
        if(counter.get()<=placeInOldMessages.get(userId)) throw new IllegalArgumentException("You have no more old massages to read");

        Message message = oldMessages.get(placeInOldMessages.get(userId));
        placeInOldMessages.put(userId,placeInOldMessages.get(userId)+1);
        return message;
    }

    @Override
    public void markAsRead(String senderId, int messageId, int userId) {
        Message first= unreadMessages.peek();

        if(first==null) throw new IllegalArgumentException("No unread messages");
        if((first.getSenderId()!=senderId || first.getMessageId()!=messageId)|| !unreadMessages.remove(first))
            throw new IllegalArgumentException("Not able to mark message as read");

        oldMessages.put(counter.getAndIncrement(),first);
    }

    @Override
    public void refreshOldMassage(int storeId, int userId) {
        placeInOldMessages.put(userId,1);
    }








}
