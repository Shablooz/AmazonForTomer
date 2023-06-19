package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;

import java.util.List;
/*
*
* */
public interface IMessageRepository {
    void sendMassage(Message message);
    Message readUnreadMassage(String receiverId);
    Message readReadMassage(String receiverId);

    Message markAsRead(String receiver,String senderId, int massageId);
    void markAsReadHelper(String receiverId,Message message);
    void refreshOldMessages(String receiverId);

    void setSaveMode(boolean saved);

}
