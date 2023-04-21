package BGU.Group13B.backend.User;


import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.data.util.Pair;
//eyal import
import java.util.List;
import java.util.regex.Pattern;

public class User {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;

    private final int userId;
    private final IMessageRepository messageRepository;
    private final UserPermissions userPermissions;
    private final Cart cart;
    private final Market market;
    private int messageId;
    private String userName;
    private Message currentMessageToReply;
    private String password;

    private String email;

    private String answer1;
    private String answer2;
    private String answer3;

    //TODO: show the messages upon registering
    private static final String question1 = "What is your favorite color?";
    private static final String question2 = "What is your favorite food?";
    private static final String question3 = "What is your favorite book or movie?";
    //eyal addition
    private volatile boolean isLoggedIn;

    private final String adminIdentifier = "admin";


    public User(int userId) {

        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.userId = userId;
        this.messageRepository = SingletonCollection.getMessageRepository();
        this.userPermissions = new UserPermissions();
        this.cart = new Cart(userId);
        this.market = SingletonCollection.getMarket();
        this.userName = "";
        this.password = "";
        this.email = "";

        //do not change those fields!
        this.answer1 = "";
        this.answer2 = "";
        this.answer3 = "";

        this.isLoggedIn = false;
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }


    public boolean isRegistered() {
        return this.userPermissions.getUserPermissionStatus() == UserPermissions.UserPermissionStatus.MEMBER ||
                this.userPermissions.getUserPermissionStatus() == UserPermissions.UserPermissionStatus.ADMIN;
    }

    public boolean isAdmin() {
        return this.userPermissions.getUserPermissionStatus() == UserPermissions.UserPermissionStatus.ADMIN;
    }

    //#15
    //returns User on success (for future functionalities)
    public User register(String userName, String password, String email,String answer1,String answer2,String answer3) {
        checkRegisterInfo(userName,password,email);
        //updates the user info upon registration - no longer a guest
        updateUserDetail(userName, password, email,answer1,answer2,answer3);
        this.userPermissions.register();
        return this;
    }
    private void checkRegisterInfo(String userName,String password, String email){
        String usernameRegex = "^[a-zA-Z0-9_-]{4,16}$"; // 4-16 characters, letters/numbers/underscore/hyphen
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$"; // need at least 8 characters, 1 uppercase, 1 lowercase, 1 number)
        String emailRegex = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"; // checks email validation
        if (!Pattern.matches(usernameRegex, userName)) {
            throw new IllegalArgumentException("Invalid username. Username must be 4-16 characters long and can only contain letters, numbers, underscores, or hyphens.");
        }
        if (!Pattern.matches(passwordRegex, password)) {
            throw new IllegalArgumentException("Invalid password. at least 8 characters, 1 uppercase, 1 lowercase, 1 number");
        }
        if (!Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("Invalid email.");
        }
    }

    //function that currently only used in register, but is cna function as a setter
    //TODO change following fields in the database
    private void updateUserDetail(String userName, String password, String email,String answer1,String answer2,String answer3) {
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.userName = userName;
        this.password = password;
    }


    public void login(String userName, String password, String answer1,String answer2,String answer3) {
        //second username check is for security
        if (!((this.userName.equals(userName) && this.password.equals(password)))){
            throw new IllegalArgumentException("incorrect username or password");
        }
        this.isLoggedIn = true;
        if(!this.answer1.equals(answer1) || !this.answer2.equals(answer2) || !this.answer3.equals(answer3)){
            throw new IllegalArgumentException("wrong answers on security questions!");
        }
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
    public synchronized Message getComplaint() throws NoPermissionException{
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
        market.sendMassage(Message.constractMessage(this.userName,getAndIncrementMessageId(), header,massage , String.valueOf(storeId)),userId,storeId);
    }
    //42
    public Message readUnreadMassageStore(int storeId) throws NoPermissionException {
        Message message= market.getUnreadMessages(this.userId,storeId);
        currentMessageToReply=message;
        return message;
    }
    //42

    public Message readReadMassageStore(int storeId)throws NoPermissionException {
        return market.getUnreadMessages(this.userId,storeId);
    }
    //42
    public void answerQuestionStore(String answer)throws NoPermissionException
    {
        assert currentMessageToReply.getReceiverId().matches("-?\\d+");
        market.markAsCompleted(currentMessageToReply.getSenderId(), currentMessageToReply.getMessageId(),this.userId,Integer.parseInt(currentMessageToReply.getReceiverId()));
        messageRepository.sendMassage(Message.constractMessage(this.userName,getAndIncrementMessageId(), "RE: "+ currentMessageToReply.getHeader(),answer , currentMessageToReply.getSenderId()));
    }
    //42
    public void refreshOldMessageStore(int storeId)throws NoPermissionException {
        market.refreshMessages(this.userId,storeId);
    }

    private int getAndIncrementMessageId() {
        return messageId++;
    }

    //#25
    public void addReview(String review, int storeId, int productId) throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can add reviews");
        market.addReview(review,storeId,productId,this.userId);
    }
    //#25
    public void removeReview(int storeId, int productId)throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can remove reviews");
        market.removeReview(storeId,productId,this.userId);
    }
    //#25
    public Review getReview(int storeId, int productId){
        return market.getReview(storeId,productId,this.userId);
    }
    //#26
    public float getProductScore(int storeId,int productId){
        return market.getProductScore(storeId,productId);
    }

    public void addAndSetProductScore(int storeId, int productId, int score) throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can add scores");
        market.addAndSetProductScore(storeId,productId,this.userId,score);
    }
    public void removeProductScore(int storeId, int productId)throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can remove scores");
        market.removeProductScore(storeId,productId,userId);
    }
    public void addStoreScore(int storeId ,int score) throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can add scores to stores");
        market.addStoreScore(userId,storeId,score);
    }

    public void removeStoreScore(int storeId) throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can remove scores from stores");
        market.removeStoreScore(userId,storeId);
    }

    public void modifyStoreScore(int storeId, int score)throws NoPermissionException{
        if(!isRegistered())
            throw new NoPermissionException("Only registered users can modify scores of stores");
        market.modifyStoreScore(userId,storeId,score);
    }

    public float getStoreScore(int storeId){
        return market.getStoreScore(storeId);
    }

    void purchaseCart(String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCcv, String id, String creditCardType) {
        cart.purchaseCart(address, creditCardNumber, creditCardMonth, creditCardYear, creditCardHolderFirstName, creditCardHolderLastName, creditCardCcv, id, creditCardType);
    }


    public String getCartContent() {
        return cart.getCartContent();
    }


    public boolean SecurityAnswer1Exists(){
        return answer1.equals("") == false;
    }
    public boolean SecurityAnswer2Exists(){
        return answer2.equals("") == false;
    }
    public boolean SecurityAnswer3Exists(){
        return answer3.equals("") == false;
    }



    public Cart getCart() {
        return cart;
    }

    public void addProductToCart(int productId, int storeId) throws Exception {
        market.isProductAvailable(productId, storeId);
        cart.addProductToCart(productId, storeId);
    }


    public void removeProductFromCart(int storeId, int productId) throws Exception {
        cart.removeProduct(storeId, productId);
    }

    public void changeProductQuantityInCart(int storeId, int productId, int quantity) throws Exception {
        cart.changeProductQuantity(storeId, productId, quantity);
    }

    public void setPermissions(UserPermissions.UserPermissionStatus status){
        this.userPermissions.setUserPermissionStatus(status);
    }

    public UserPermissions.UserPermissionStatus getStatus(){
        return this.userPermissions.getUserPermissionStatus();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }

    public List<Pair<Integer, String>> getStoresAndRoles() {
        return this.userPermissions.getStoresAndRoles();
    }


}
