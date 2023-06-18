package BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryService;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

@Entity
public class MessageRepositorySingle implements IMessageRepository {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Transient
    private boolean saveMode;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "MessageRepositorySingle_unreadMessages",
            joinColumns = {@JoinColumn(name = "MessageRepositorySingle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "DequeMessage_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "userName")
    Map<String, DequeMessage> unreadMessages;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "MessageRepositorySingle_readMessages",
            joinColumns = {@JoinColumn(name = "MessageRepositorySingle_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "DequeMessage_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "userName")
    Map<String, DequeMessage> readMessages;

    @Transient
    Map<String, Iterator<Message>> readMessagesIterator;


    public MessageRepositorySingle() {
        unreadMessages = new ConcurrentHashMap<>();
        readMessages = new ConcurrentHashMap<>();
        readMessagesIterator = new ConcurrentHashMap<>();
        this.saveMode= true;
    }
    public MessageRepositorySingle(boolean saveMode) {
        unreadMessages = new ConcurrentHashMap<>();
        readMessages = new ConcurrentHashMap<>();
        readMessagesIterator = new ConcurrentHashMap<>();
        this.saveMode= saveMode;
    }

    @Override
    public void sendMassage(Message message) {
        addEntryIfNotExist(message.getReceiverId());
        unreadMessages.get(message.getReceiverId()).add(message);

    }

    @Override
    public Message readUnreadMassage(String receiverId) {
       addEntryIfNotExist(receiverId);
         if(unreadMessages.get(receiverId).isEmpty())
              throw new IllegalArgumentException("no unread messages");
        return unreadMessages.get(receiverId).peek();
    }

    @Override
    public Message readReadMassage(String receiverId) {
        addEntryIfNotExist(receiverId);
        readMessagesIterator.putIfAbsent(receiverId,readMessages.get(receiverId).iterator());
        if(!readMessagesIterator.get(receiverId).hasNext())
            throw new IllegalArgumentException("no read messages/refresh to the beginning");
        return readMessagesIterator.get(receiverId).next();
    }

    @Override
    public void markAsRead(String receiverId,String senderId, int messageId) {
      addEntryIfNotExist(receiverId);

        Message first = unreadMessages.get(receiverId).peek();
        if(first==null) throw new IllegalArgumentException("No unread messages");
        if((!first.getSenderId().equals(senderId) || first.getMessageId()!=messageId))
            throw new IllegalArgumentException("fail to mark message as read");
        if(!unreadMessages.get(receiverId).remove(first))
            throw new IllegalArgumentException("fail to mark message as read");

        readMessages.get(receiverId).add(first);
    }

    @Override
    public void refreshOldMessages(String  receiverId) {
        addEntryIfNotExist(receiverId);
        readMessagesIterator.put(receiverId,readMessages.get(receiverId).iterator());
    }


    private boolean checkIfExist(String receiverId)
    {
        return unreadMessages.containsKey(receiverId)||readMessages.containsKey(receiverId);
    }
    private void addEntryIfNotExist(String receiverId)
    {
            unreadMessages.putIfAbsent(receiverId, new DequeMessage());
            readMessages.putIfAbsent(receiverId, new DequeMessage());
    }
    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(MessageRepositorySingleService.class).save(this);
    }
    public void setSaveMode(boolean saved) {
        this.saveMode = saved;
    }

    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, DequeMessage> getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Map<String, DequeMessage> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Map<String, DequeMessage> getReadMessages() {
        return readMessages;
    }

    public void setReadMessages(Map<String, DequeMessage> readMessages) {
        this.readMessages = readMessages;
    }

    public void setReadMessagesIterator(Map<String, Iterator<Message>> readMessagesIterator) {
        this.readMessagesIterator = readMessagesIterator;
    }

    public Map<String, Iterator<Message>> getReadMessagesIterator() {
        return readMessagesIterator;
    }

    public void setReadMessagesIterator(ConcurrentHashMap<String, Iterator<Message>> readMessagesIterator) {
        this.readMessagesIterator = readMessagesIterator;
    }
}
