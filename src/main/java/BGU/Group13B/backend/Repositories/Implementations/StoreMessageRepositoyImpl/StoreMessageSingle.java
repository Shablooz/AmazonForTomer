package BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;

import java.util.concurrent.ConcurrentHashMap;

public class StoreMessageSingle implements IStoreMessagesRepository {

    ConcurrentHashMap<Integer/*store Id */,StoreMessageRepositoryNonPersist> implementations;


    public StoreMessageSingle() {
        implementations = new ConcurrentHashMap<>();
    }


    @Override
    public void sendMassage(Message message, int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        implementations.get(storeId).sendMassage(message,storeId,userId);
    }

    @Override
    public Message readUnreadMassage(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        return implementations.get(storeId).readUnreadMassage(storeId,userId);
    }

    @Override
    public Message readReadMassage(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        return implementations.get(storeId).readReadMassage(storeId,userId);
    }

    @Override
    public void markAsRead(String senderId, int massageId, int userId) {
        implementations.putIfAbsent(massageId,new StoreMessageRepositoryNonPersist());
        implementations.get(massageId).markAsRead(senderId,massageId,userId);
    }

    @Override
    public void refreshOldMassage(int storeId, int userId) {
        implementations.putIfAbsent(storeId,new StoreMessageRepositoryNonPersist());
        implementations.get(storeId).refreshOldMassage(storeId,userId);
    }
}
