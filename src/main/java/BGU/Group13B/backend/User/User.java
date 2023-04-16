package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;
import jdk.jshell.spi.ExecutionControl;
import java.io.NotActiveException;
//eyal import
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private final ICartRepository cartRepository;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;
    private final Market market;
    private int messageId;
    private final String userName;

    private Message currentMessageToReply;

    private final String password;
    //eyal addition
    private boolean isLoggedIn;


    //creation of a new user
    public User(IPurchaseHistoryRepository purchaseHistoryRepository, ICartRepository cartRepository, IMessageRepository messageRepository, UserPermissions userPermissions, Market market,String userName,String password) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.cartRepository = cartRepository;
        this.messageRepository = messageRepository;
        this.userPermissions = userPermissions;
        this.market = market;
        this.userName = userName;
        this.password = password;
        this.isLoggedIn = false;
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isRegistered(){
        return this.userPermissions.getUserPermissionStatus() == UserPermissions.UserPermissionStatus.MEMBER ||
                this.userPermissions.getUserPermissionStatus() == UserPermissions.UserPermissionStatus.ADMIN;
    }

    //#15
    //returns User on success (for future functionalities)
    public User register(String userName, String password, String email) {
        String usernameRegex = "^[a-zA-Z0-9_-]{4,16}$"; // 4-16 characters, letters/numbers/underscore/hyphen
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$"; // need at least 8 characters, 1 uppercase, 1 lowercase, 1 number)
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$"; // checks email validation
        if (!Pattern.matches(usernameRegex, userName)) {
            throw new IllegalArgumentException("Invalid username. Username must be 4-16 characters long and can only contain letters, numbers, underscores, or hyphens.");
        }
        if (!Pattern.matches(passwordRegex, password)) {
            throw new IllegalArgumentException("Invalid password. at least 8 characters, 1 uppercase, 1 lowercase, 1 number");
        }
        if (!Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("Invalid email.");
        }
        this.userPermissions.register();
        return this;
    }

    public void login(String userName,String password){
        //second username check for security
        if (this.userName.equals(userName) && this.password.equals(password)){
            this.isLoggedIn = true;
            return;
        }
        throw new IllegalArgumentException("incorrect username or password");
    }

    public String getUserName() {
        return userName;
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

    public void logout(){
        this.isLoggedIn = false;
    }

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
