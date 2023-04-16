package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;


import java.util.List;

public interface IStoreMessagesRepository {

    void sendMassage(Message message, int storeId,String userName);

    Message readUnreadMassage(int storeId,String userName);
    Message readReadMassage(int storeId,String userName);

    void markAsRead(String senderId, int massageId,String userName);
    void refreshOldMassage(int storeId,String userName);

}
