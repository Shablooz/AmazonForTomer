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

    private Message currentReadMessage;


    public User(IPurchaseHistoryRepository purchaseHistoryRepository, ICartRepository cartRepository, IMessageRepository messageRepository, UserPermissions userPermissions, Market market) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.cartRepository = cartRepository;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
        this.market = market;
    }



    public void openComplaint(String header,String complaint) {
        //TODO:need to check permission
        messageRepository.sendMassage(new Message(this.userName,messageId, header,complaint , "Admin"));
    }
    public Message getComplaint() {
        //TODO:need to check permission
       return messageRepository.readUnreadMassage("Admin");
    }
    public void markMessageAsRead(Message message) {
        //TODO:need to check permission
        messageRepository.markAsRead(message);
    }
    public void sendMassage(String receiverId,String header,String massage) {
        //TODO:need to check permission only admin can send massage to user
        messageRepository.sendMassage(new Message(this.userName,messageId, header,massage , receiverId));
    }
    public void answerComplaint(String answer) {
        messageRepository.markAsRead(currentReadMessage);
        messageRepository.sendMassage(new Message(this.userName,messageId, "RE: "+currentReadMessage.getHeader(),answer , currentReadMessage.getSenderId()));
    }
    public Message readMassage(String receiverId) {
        //TODO:need to check permission registed user can read only his massages
        Message message=  messageRepository.readReadMassage(receiverId);
        messageRepository.markAsRead(message);
        return message;
    }
}
