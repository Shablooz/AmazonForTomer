package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.storePackage.Product;
import org.mindrot.jbcrypt.BCrypt;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IUserPermissionRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
//eyal import
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class User {

    private final IPurchaseHistoryRepository purchaseHistoryRepository;

    private final int userId;
    private final IMessageRepository messageRepository;
    private final IUserPermissionRepository userPermissionRepository;
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

    private final String adminIdentifier = "Admin";


    public User(int userId) {
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.userId = userId;
        this.messageRepository = SingletonCollection.getMessageRepository();
        this.userPermissionRepository = SingletonCollection.getUserPermissionRepository();
        UserPermissions userPermissions1 = userPermissionRepository.getUserPermission(userId);
        if (userPermissions1 == null) {
            userPermissions1 = new UserPermissions();
            userPermissionRepository.addUserPermission(userId, userPermissions1);
        }
        this.userPermissions = userPermissions1;
        this.cart = new Cart(userId);
        this.market = SingletonCollection.getMarket();
        this.userName = "";
        this.password = "";
        this.email = "";
        //do not change those fields!
        this.answer1 = "";
        this.answer2 = "";
        this.answer3 = "";
        this.messageId = 1;
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
    public User register(String userName, String password, String email, String answer1, String answer2, String answer3) {
        checkRegisterInfo(userName, password, email);
        //updates the user info upon registration - no longer a guest
        updateUserDetail(userName, password, email, answer1, answer2, answer3);
        this.userPermissions.register(this.userId);
        return this;
    }

    private void checkRegisterInfo(String userName, String password, String email) {
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
    private void updateUserDetail(String userName, String password, String email, String answer1, String answer2, String answer3) {
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.userName = userName;
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public void login(String userName, String password, String answer1, String answer2, String answer3) {
        //second username check is for security
        if (!((this.userName.equals(userName)))) {
            throw new IllegalArgumentException("incorrect username");
        }
        if (!verifyPassword(password, this.password)) {
            throw new IllegalArgumentException("incorrect password");
        }
        if (!this.answer1.equals(answer1) || !this.answer2.equals(answer2) || !this.answer3.equals(answer3)) {
            throw new IllegalArgumentException("wrong answers on security questions!");
        }
        this.isLoggedIn = true;

    }

    public String getUserName() {
        return userName;
    }


    //#28
    public void openComplaint(String header, String complaint) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can open complaints");
        messageRepository.sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, complaint, adminIdentifier));
    }

    //#47
    public synchronized Message getComplaint() throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can read complaints");
        Message message = messageRepository.readUnreadMassage(adminIdentifier);
        currentMessageToReply = message;
        return message;
    }

    //#47
    public void markMessageAsReadAdmin(String receiverId, String senderId, int messageId) throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can mark as read complaints");

        messageRepository.markAsRead(receiverId, senderId, messageId);
    }

    //#47
    public void sendMassageAdmin(String receiverId, String header, String massage) throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can send massages");
        messageRepository.sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, massage, receiverId));
    }

    //#47
    public void answerComplaint(String answer) throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can answer complaints");
        if (currentMessageToReply == null)
            throw new IllegalArgumentException("no complaint to answer");
        messageRepository.markAsRead(currentMessageToReply.getReceiverId(), currentMessageToReply.getSenderId(), currentMessageToReply.getMessageId());
        messageRepository.sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), "RE: " + currentMessageToReply.getHeader(), answer, currentMessageToReply.getSenderId()));
        currentMessageToReply = null;
    }

    public Message readMassage() throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can read massages");

        Message message = messageRepository.readUnreadMassage(this.userName);
        messageRepository.markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
        currentMessageToReply = message;
        return message;
    }

    //27
    public void logout() {
        if (!isLoggedIn)
            throw new IllegalArgumentException("already logged out!");
        this.isLoggedIn = false;
    }


    public void sendMassageStore(String header, String massage, int storeId) {
        market.sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, massage, String.valueOf(storeId)), userId, storeId);
    }

    //42
    public Message readUnreadMassageStore(int storeId) throws NoPermissionException {
        Message message = market.getUnreadMessages(this.userId, storeId);
        currentMessageToReply = message;
        return message;
    }
    //42

    public Message readReadMassageStore(int storeId) throws NoPermissionException {
        return market.getUnreadMessages(this.userId, storeId);
    }

    //42
    public void answerQuestionStore(String answer) throws NoPermissionException {
        if (currentMessageToReply == null)
            throw new IllegalArgumentException("no message to reply to");
        assert currentMessageToReply.getReceiverId().matches("-?\\d+");
        market.markAsCompleted(currentMessageToReply.getSenderId(), currentMessageToReply.getMessageId(), this.userId, Integer.parseInt(currentMessageToReply.getReceiverId()));
        messageRepository.sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), "RE: " + currentMessageToReply.getHeader(), answer, currentMessageToReply.getSenderId()));
        currentMessageToReply = null;
    }

    //42
    public void refreshOldMessageStore(int storeId) throws NoPermissionException {
        market.refreshMessages(this.userId, storeId);
    }

    private int getAndIncrementMessageId() {
        return messageId++;
    }

    //#25
    public void addReview(String review, int storeId, int productId) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can add reviews");
        market.addReview(review, storeId, productId, this.userId);
    }

    //#25
    public void removeReview(int storeId, int productId) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can remove reviews");
        market.removeReview(storeId, productId, this.userId);
    }

    //#25
    public Review getReview(int storeId, int productId) {
        return market.getReview(storeId, productId, this.userId);
    }

    //#26
    public float getProductScore(int storeId, int productId) {
        return market.getProductScore(storeId, productId);
    }

    public void addAndSetProductScore(int storeId, int productId, int score) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can add scores");
        market.addAndSetProductScore(storeId, productId, this.userId, score);
    }

    public void removeProductScore(int storeId, int productId) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can remove scores");
        market.removeProductScore(storeId, productId, userId);
    }

    public void addStoreScore(int storeId, int score) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can add scores to stores");
        market.addStoreScore(userId, storeId, score);
    }

    public void removeStoreScore(int storeId) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can remove scores from stores");
        market.removeStoreScore(userId, storeId);
    }

    public void modifyStoreScore(int storeId, int score) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can modify scores of stores");
        market.modifyStoreScore(userId, storeId, score);
    }

    public float getStoreScore(int storeId) {
        return market.getStoreScore(storeId);
    }

    public double purchaseCart(String creditCardNumber, String creditCardMonth,
                               String creditCardYear, String creditCardHolderFirstName,
                               String creditCardCcv, String id,
                               HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                               String/*store coupons*/ storeCoupon) throws PurchaseFailedException {
        return cart.purchaseCart(creditCardNumber,
                creditCardMonth, creditCardYear,
                creditCardHolderFirstName,
                creditCardCcv, id,
                productsCoupons,
                storeCoupon);
    }

    public void purchaseCart(String creditCardNumber, String creditCardMonth,
                             String creditCardYear, String creditCardHolderFirstName,
                             String creditCardCcv, String id) throws PurchaseFailedException {
        cart.purchaseCart(creditCardNumber,
                creditCardMonth, creditCardYear,
                creditCardHolderFirstName,
                creditCardCcv, id);
    }
    public double startPurchaseBasketTransaction(HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                                 String/*store coupons*/ storeCoupon) throws PurchaseFailedException {
        return cart.startPurchaseBasketTransaction(productsCoupons, storeCoupon);
    }


    public String getCartDescription() {
        return cart.getCartDescription();
    }

    public List<Product> getCartContent() {
        return cart.getCartContent();
    }

    public List<BasketProduct> getCartBasketProducts() {
        return cart.getCartBasketProducts();
    }


    public boolean SecurityAnswer1Exists() {
        return !answer1.equals("");
    }

    public boolean SecurityAnswer2Exists() {
        return !answer2.equals("");
    }

    public boolean SecurityAnswer3Exists() {
        return !answer3.equals("");
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

    public void setPermissions(UserPermissions.UserPermissionStatus status) {
        this.userPermissions.setUserPermissionStatus(status);
    }

    public UserPermissions.UserPermissionStatus getStatus() {
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


    public void addToCart(int storeId, int productId) {
        cart.addProductToCart(storeId, productId);
    }

    public void addPermission(int storeId, UserPermissions.StoreRole storeRole) {
        userPermissions.updateRoleInStore(storeId, storeRole);
    }

    public void deletePermission(int storeId) {
        userPermissions.deletePermission(storeId);
    }

    public UserPermissions getUserPermissions() {
        return userPermissions;
    }


    public void removeBasket(int basketId) {
        cart.removeBasket(userId, basketId);
    }


    public List<Integer> getFailedProducts(int storeId) {
        return cart.getFailedProducts(storeId, userId);
    }

    public List<Product> getAllFailedProductsAfterPayment() {
        return cart.getAllFailedProductsAfterPayment();
    }

    public double getTotalPriceOfCart() {
        return cart.getTotalPriceOfCartBeforeDiscount();
    }

    public void cancelPurchase() {
        cart.cancelPurchase();
    }
}
