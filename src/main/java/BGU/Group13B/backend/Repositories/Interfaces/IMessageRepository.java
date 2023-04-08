package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;

import java.util.List;

public interface IMessageRepository {
    void sendMassage(Message message);
    Message readUnreadMassage(String receiverId);
    Message readReadMassage(String receiverId);

    void markAsRead(Message message);
    void pointToNewMessages(String receiverId);



}
