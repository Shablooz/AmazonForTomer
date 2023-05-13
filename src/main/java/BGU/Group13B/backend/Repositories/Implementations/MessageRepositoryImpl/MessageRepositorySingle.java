package BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.User.Message;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageRepositorySingle implements IMessageRepository {

    ConcurrentHashMap<String, ConcurrentLinkedDeque<Message>> unreadMessages;
    ConcurrentHashMap<String, ConcurrentLinkedQueue<Message>> readMessages;


    ConcurrentHashMap<String, Iterator<Message>> readMessagesIterator;


    public MessageRepositorySingle() {
        unreadMessages = new ConcurrentHashMap<>();
        readMessages = new ConcurrentHashMap<>();
        readMessagesIterator = new ConcurrentHashMap<>();
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
            unreadMessages.putIfAbsent(receiverId, new ConcurrentLinkedDeque<>());
            readMessages.putIfAbsent(receiverId, new ConcurrentLinkedQueue<>());
    }
}
