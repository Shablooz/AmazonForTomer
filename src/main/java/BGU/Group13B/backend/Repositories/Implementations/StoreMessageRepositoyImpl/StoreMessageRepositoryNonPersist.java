package BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;
import jakarta.persistence.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class StoreMessageRepositoryNonPersist implements IStoreMessagesRepository{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StoreMessageRepositoryNonPersist_unreadMessages",
            joinColumns = {@JoinColumn(name = "StoreMessageRepositoryNonPersist_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "senderId", referencedColumnName = "senderId"),
                    @JoinColumn(name = "massageId", referencedColumnName = "massageId")})
    List<Message> unreadMessages;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StoreMessageRepositoryNonPersist_oldMessages",
            joinColumns = {@JoinColumn(name = "StoreMessageRepositoryNonPersist_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "senderId", referencedColumnName = "senderId"),
                    @JoinColumn(name = "massageId", referencedColumnName = "massageId")})
    @MapKeyJoinColumn(name = "userId")
    Map<Integer,Message> oldMessages;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "StoreMessageRepositoryNonPersist_placeInOldMessages",
            joinColumns = {@JoinColumn(name = "StoreMessageRepositoryNonPersist_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "userId")
    @Column(name = "place")
    Map<Integer/*userId*/,Integer> placeInOldMessages;
    AtomicInteger counter;

    public StoreMessageRepositoryNonPersist() {
        unreadMessages = new LinkedList<>();
        oldMessages = new ConcurrentHashMap<>();
        placeInOldMessages = new ConcurrentHashMap<>();
        counter = new AtomicInteger(1);
    }
    @Override
    public synchronized void sendMassage(Message message, int storeId, int userId) {
        unreadMessages.add(message);
    }

    @Override
    public synchronized Message readUnreadMassage(int storeId, int userId) {
        if(unreadMessages.isEmpty()) throw new IllegalArgumentException("No unread messages");
        return unreadMessages.get(0);
    }


    /*
    * This function will return the next unread message Per user If there is no more old messages it will throw an exception
     */
    @Override
    public synchronized Message readReadMassage(int storeId, int userId) {
        placeInOldMessages.putIfAbsent(userId,1);
        if(counter.get()<=placeInOldMessages.get(userId)) throw new IllegalArgumentException("You have no more old massages to read");

        Message message = oldMessages.get(placeInOldMessages.get(userId));
        placeInOldMessages.put(userId,placeInOldMessages.get(userId)+1);
        return message;
    }

    @Override
    public synchronized void markAsRead(int storeId,String senderId, int messageId, int userId) {
        Message first= unreadMessages.get(0);

        if(first==null) throw new IllegalArgumentException("No unread messages");
        if((!first.getSenderId().equals(senderId) || first.getMessageId()!=messageId))
            throw new IllegalArgumentException("The message you are trying to mark as read is not the first message");
        if(!unreadMessages.remove(first))
            throw new IllegalArgumentException("The message already marked as read");

        oldMessages.put(counter.getAndIncrement(),first);
    }

    @Override
    public synchronized void refreshOldMassage(int storeId, int userId) {
        placeInOldMessages.put(userId,1);
    }

    @Override
    public synchronized int getUnreadMessagesSize(int storeId, int userId) {
        return unreadMessages.size();
    }

    @Override
    public synchronized int getReadMessagesSize(int storeId, int userId) {
        return oldMessages.size();
    }


    public void setSaveMode(boolean saveMode) {

    }
    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Message> getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(List<Message> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Map<Integer, Message> getOldMessages() {
        return oldMessages;
    }

    public void setOldMessages(Map<Integer, Message> oldMessages) {
        this.oldMessages = oldMessages;
    }

    public Map<Integer, Integer> getPlaceInOldMessages() {
        return placeInOldMessages;
    }

    public void setPlaceInOldMessages(Map<Integer, Integer> placeInOldMessages) {
        this.placeInOldMessages = placeInOldMessages;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }
}
