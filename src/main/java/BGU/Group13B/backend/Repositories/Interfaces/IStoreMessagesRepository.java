package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;


import java.util.List;

public interface IStoreMessagesRepository {

    void sendMassage(Message message, int storeId,int userId);

    Message readUnreadMassage(int storeId,int userId);
    Message readReadMassage(int storeId,int userId);

    void markAsRead(int storeId,String senderId, int massageId,int userId);
    void refreshOldMassage(int storeId,int userId);

    int getUnreadMessagesSize(int storeId,int userId);
    int getReadMessagesSize(int storeId,int userId);

    void setSaveMode(boolean saveMode);
}
