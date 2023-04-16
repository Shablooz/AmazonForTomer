package BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class MessageRepositoryAsList implements IMessageRepository {
    ConcurrentMap<String, ConcurrentLinkedDeque<Message>> unreadMessages;
    ConcurrentMap<String, ConcurrentLinkedQueue<Message>> readMessages;
    ConcurrentMap<String,Iterator<Message>> unReadMessagesIterator;
    ConcurrentMap<String, Iterator<Message>> readMessagesIterator;

    @Override
    public void sendMassage(Message message) {
        addEntryIfNotExist(message.getReceiverId());
        unreadMessages.get(message.getReceiverId()).add(message);
    }

    @Override
    public Message readUnreadMassage(String receiverId) {
        if(!checkIfExist(receiverId))
            throw new IllegalArgumentException("No messages for receiverId");
        if(!unreadMessages.containsKey(receiverId))
            unReadMessagesIterator.put(receiverId,unreadMessages.get(receiverId).iterator());
        if(!unReadMessagesIterator.get(receiverId).hasNext())
            throw new IllegalArgumentException("no unread messages");
        return unReadMessagesIterator.get(receiverId).next();
    }

    @Override
    public Message readReadMassage(String receiverId) {
        if(!checkIfExist(receiverId))
            throw new IllegalArgumentException("No messages for receiverId");
        if(!readMessagesIterator.containsKey(receiverId))
            readMessagesIterator.put(receiverId,readMessages.get(receiverId).iterator());
        if(!readMessagesIterator.get(receiverId).hasNext())
            throw new IllegalArgumentException("no read messages");
        return readMessagesIterator.get(receiverId).next();
    }

    @Override
    public void markAsRead(Message message) {
        String receiverId = message.getReceiverId();
        if(!checkIfExist(receiverId))
            throw new IllegalArgumentException("receiverId not exist");
        if(!unreadMessages.get(receiverId).remove(message))
            throw new IllegalArgumentException("fail to mark message as read");
        readMessages.get(receiverId).add(message);
    }

    @Override
    public void pointToNewMessages(String  receiverId) {
        if(!checkIfExist(receiverId))
            throw new IllegalArgumentException("receiverId not exist");
        readMessagesIterator.put(receiverId,readMessages.get(receiverId).iterator());
    }


    private boolean checkIfExist(String receiverId)
    {
        return unreadMessages.containsKey(receiverId)||readMessages.containsKey(receiverId);
    }
    private void addEntryIfNotExist(String receiverId)
    {
        if(!checkIfExist(receiverId)) {
            unreadMessages.put(receiverId, new ConcurrentLinkedDeque<>());
            readMessages.put(receiverId, new ConcurrentLinkedQueue<>());
        }
    }
}
