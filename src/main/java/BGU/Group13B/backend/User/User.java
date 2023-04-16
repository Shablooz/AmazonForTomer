package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private final ICartRepository cartRepository;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;
    private final Market market;
    private int messageId;
    private String userName;

    private Message currentMessageToReply;


    public User(IPurchaseHistoryRepository purchaseHistoryRepository, ICartRepository cartRepository, IMessageRepository messageRepository, UserPermissions userPermissions, Market market) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.cartRepository = cartRepository;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
        this.market = market;
    }



    public void openComplaint(String header,String complaint) {
        //TODO:need to check permission
        messageRepository.sendMassage( Message.constractMessage(this.userName,messageId, header,complaint , "Admin"));
    }
    public Message getComplaint() {
        //TODO:need to check permission
       return messageRepository.readUnreadMassage("Admin");
    }
    public void markMessageAsRead(Message message) {
        //TODO:need to check permission
        messageRepository.markAsRead(message);
    }
    public void sendMassageAdmin(String receiverId,String header,String massage) {
        //TODO:need to check permission only admin can send massage to user
        messageRepository.sendMassage(Message.constractMessage(this.userName,messageId, header,massage , receiverId));
    }
    public void answerComplaint(String answer) {
        messageRepository.markAsRead(currentMessageToReply);
        messageRepository.sendMassage(Message.constractMessage(this.userName,messageId, "RE: "+ currentMessageToReply.getHeader(),answer , currentMessageToReply.getSenderId()));
    }
    public Message readMassage(String receiverId) {
        //TODO:need to check permission registed user can read only his massages
        Message message=  messageRepository.readReadMassage(receiverId);
        messageRepository.markAsRead(message);
        currentMessageToReply=message;
        return message;
    }

    public void sendMassageStore(String header,String massage,int storeId) {
        market.sendMassage(Message.constractMessage(this.userName,getAndIncrementMessageId(), header,massage , String.valueOf(storeId)),this.userName,storeId);
    }

    public Message readUnreadMassageStore(int storeId) {
        Message message= market.getUnreadMessages(this.userName,storeId);
        currentMessageToReply=message;
        return message;
    }
    public Message readReadMassageStore(int storeId) {
        return market.getUnreadMessages(this.userName,storeId);
    }

    public void answerQuestionStore(String answer)
    {
        assert currentMessageToReply.getReceiverId().matches("-?\\d+");
        market.markAsCompleted(currentMessageToReply.getSenderId(), currentMessageToReply.getMessageId(),this.userName,Integer.parseInt(currentMessageToReply.getReceiverId()));
        messageRepository.sendMassage(Message.constractMessage(this.userName,getAndIncrementMessageId(), "RE: "+ currentMessageToReply.getHeader(),answer , currentMessageToReply.getSenderId()));
    }
    public void refreshOldMessageStore(int storeId) {
        market.refreshMessages(this.userName,storeId);
    }

    private int getAndIncrementMessageId() {
        return messageId++;
    }
}
