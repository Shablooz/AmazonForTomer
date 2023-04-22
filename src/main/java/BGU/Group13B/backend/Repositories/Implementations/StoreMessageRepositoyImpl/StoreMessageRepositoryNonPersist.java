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

    ConcurrentLinkedDeque<Message> unreadMessages;
    ConcurrentHashMap<Integer,Message> oldMessages;
    ConcurrentHashMap<Integer/*userId*/,Integer> placeInOldMessages;
    AtomicInteger counter;

    public StoreMessageRepositoryNonPersist() {
        unreadMessages = new ConcurrentLinkedDeque<>();
        oldMessages = new ConcurrentHashMap<>();
        placeInOldMessages = new ConcurrentHashMap<>();
        counter = new AtomicInteger(1);
    }
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
    public void markAsRead(int storeId,String senderId, int messageId, int userId) {
        Message first= unreadMessages.peek();

        if(first==null) throw new IllegalArgumentException("No unread messages");
        if((!first.getSenderId().equals(senderId) || first.getMessageId()!=messageId))
            throw new IllegalArgumentException("The message you are trying to mark as read is not the first message");
        if(!unreadMessages.remove(first))
            throw new IllegalArgumentException("The message already marked as read");

        oldMessages.put(counter.getAndIncrement(),first);
    }

    @Override
    public void refreshOldMassage(int storeId, int userId) {
        placeInOldMessages.put(userId,1);
    }

    @Override
    public int getUnreadMessagesSize(int storeId, int userId) {
        return unreadMessages.size();
    }

    @Override
    public int getReadMessagesSize(int storeId, int userId) {
        return oldMessages.size();
    }


}
