package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;


import java.util.List;

public interface IStoreMessagesRepository {

    List<Message> unreadMassages(int storeId,int numOfMessages);
    void markAsCompleted(Message message);

    void addMessage(int storeId,Message message);
    void removeMassage(int storeId,String senderId,int massageId);
}
