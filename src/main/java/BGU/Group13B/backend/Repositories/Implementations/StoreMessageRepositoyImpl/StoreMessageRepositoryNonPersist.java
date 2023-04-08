package BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;

public class StoreMessageRepositoryNonPersist implements IStoreMessagesRepository{

    ConcurrentLinkedDeque<Message> unreadMessages = new ConcurrentLinkedDeque<>();
    ConcurrentSkipListSet<Message> readMessages = new ConcurrentSkipListSet<>((x,y)->x.getMassageId()-y.getMassageId());
    @Override
    public List<Message> unreadMassages(int storeId, int numOfMessages) {
        if(numOfMessages<0)
            throw new IllegalArgumentException("numOfMessages must be positive");
        List<Message> toReturn = new ArrayList<>(numOfMessages);
        for(Message m:unreadMessages)
        {
            if(numOfMessages==0)
                break;
            toReturn.add(m);
            numOfMessages--;
        }
        return toReturn;
    }

    @Override
    public void markAsCompleted(Message message) {
        if(!unreadMessages.remove(message))
            throw new IllegalArgumentException("message not exist or already read");
        readMessages.add(message);
    }

    @Override
    public void addMessage(int storeId, Message message) {
        if(unreadMessages.contains(message)||readMessages.contains(message))
            throw new IllegalArgumentException("message already exist");
    }

    @Override
    public void removeMassage(int storeId, int senderId, int massageId) {
        if(!unreadMessages.removeIf(m->m.getSenderId()==senderId&&m.getMassageId()==massageId))
            throw new IllegalArgumentException("message not exist or already read");
    }


}
