package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;

import java.util.List;
/*
*
* */
public interface IMessageRepository {
    void sendMassage(Message message,int receiverId);
    List<Message> getUnreadMessages(int receiverId);
    void markUsRead(Message message,int receiverId);
}
