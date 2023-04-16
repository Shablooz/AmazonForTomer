package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.ICartRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
//eyal import
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

    private final String adminIdentifier = "admin";


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
    public boolean isAdmin(){
        return this.userPermissions.getUserPermissionStatus() == UserPermissions.UserPermissionStatus.ADMIN;
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



    //#28
    public void openComplaint(String header,String complaint) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can open complaints");
        messageRepository.sendMassage( Message.constractMessage(this.userName,messageId, header,complaint , "Admin"));
    }

    //#47
    public Message getComplaint() throws NoPermissionException{
        if(!isAdmin())
            throw new NoPermissionException("Only admin can read complaints");
       return messageRepository.readUnreadMassage(adminIdentifier);
    }
    //#47
    public void markMessageAsRead(String receiverId,String senderId,int messageId)  throws NoPermissionException{
        if(!isAdmin())
            throw new NoPermissionException("Only admin can mark as read complaints");

        messageRepository.markAsRead(receiverId,senderId,messageId);
    }
    //#47
    public void sendMassageAdmin(String receiverId,String header,String massage) throws NoPermissionException {
        if(!isAdmin())
            throw new NoPermissionException("Only admin can send massages");
        messageRepository.sendMassage(Message.constractMessage(this.userName,messageId, header,massage , receiverId));
    }
    //#47
    public void answerComplaint(String answer) throws NoPermissionException{
        if(!isAdmin())
            throw new NoPermissionException("Only admin can answer complaints");
        messageRepository.markAsRead(currentMessageToReply.getReceiverId(),currentMessageToReply.getSenderId(),currentMessageToReply.getMessageId());
        messageRepository.sendMassage(Message.constractMessage(this.userName,messageId, "RE: "+ currentMessageToReply.getHeader(),answer , currentMessageToReply.getSenderId()));
    }

    public Message readMassage(String receiverId) throws NoPermissionException {
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can read massages");

        Message message=  messageRepository.readReadMassage(receiverId);
        messageRepository.markAsRead(message.getReceiverId(),message.getSenderId(),message.getMessageId());
        currentMessageToReply=message;
        return message;
    }

    //27
    public void logout(){
        this.isLoggedIn = false;
    }


    public void sendMassageStore(String header,String massage,int storeId) {
        market.sendMassage(Message.constractMessage(this.userName,getAndIncrementMessageId(), header,massage , String.valueOf(storeId)),this.userName,storeId);
    }
    //42
    public Message readUnreadMassageStore(int storeId) throws NoPermissionException {
        Message message= market.getUnreadMessages(this.userName,storeId);
        currentMessageToReply=message;
        return message;
    }
    //42
    public Message readReadMassageStore(int storeId)throws NoPermissionException {
        return market.getUnreadMessages(this.userName,storeId);
    }
    //42
    public void answerQuestionStore(String answer)throws NoPermissionException
    {
        assert currentMessageToReply.getReceiverId().matches("-?\\d+");
        market.markAsCompleted(currentMessageToReply.getSenderId(), currentMessageToReply.getMessageId(),this.userName,Integer.parseInt(currentMessageToReply.getReceiverId()));
        messageRepository.sendMassage(Message.constractMessage(this.userName,getAndIncrementMessageId(), "RE: "+ currentMessageToReply.getHeader(),answer , currentMessageToReply.getSenderId()));
    }
    //42
    public void refreshOldMessageStore(int storeId)throws NoPermissionException {
        market.refreshMessages(this.userName,storeId);
    }

    private int getAndIncrementMessageId() {
        return messageId++;
    }
}
