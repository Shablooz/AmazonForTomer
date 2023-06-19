package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.service.*;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;
import BGU.Group13B.backend.Repositories.Interfaces.IMessageRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IUserPermissionRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.Review;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.BroadCaster;
//eyal import
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Entity
@Table(name = "users")
public class User {

    @Transient
    private IPurchaseHistoryRepository purchaseHistoryRepository;


    @Id
    private int userId;

    @Transient
    private IMessageRepository messageRepository;

    @Transient
    private IStoreRepository storeRepository;
    @Transient
    private IUserPermissionRepository userPermissionRepository;// v
    @Transient
    private UserPermissions userPermissions;// var v
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Cart cart;
    @Transient
    private Market market;

    private int messageId;
    private String userName;
    @Transient
    private Message regularMessageToReply; //need to map
    private String password;

    private LocalDateTime dateOfBirth;
    private String email;

    private String answer1;
    private String answer2;
    private String answer3;

    //TODO: show the messages upon registering
    @Transient
    private static final String question1 = "What is your favorite color?";
    @Transient
    private static final String question2 = "What is your favorite food?";
    @Transient
    private static final String question3 = "What is your favorite book?";
    //eyal addition

    private volatile boolean isLoggedIn;

    @Transient
    private String adminIdentifier = "Admin";
    private boolean messageNotification;
    private boolean reviewedStoreNotification;

    public User(int userId) {
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.userId = userId;
        this.messageRepository = SingletonCollection.getMessageRepository();
        this.userPermissionRepository = SingletonCollection.getUserPermissionRepository();
        this.storeRepository = SingletonCollection.getStoreRepository();
        UserPermissions userPermissions1 = getUserPermissionRepository().getUserPermission(userId);
        if (userPermissions1 == null) {
            userPermissions1 = new UserPermissions(userId);
            getUserPermissionRepository().addUserPermission(userId, userPermissions1);
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
        this.messageNotification = false;
        this.reviewedStoreNotification = false;
    }

    public User() {
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.userId = 0;
        this.messageRepository = SingletonCollection.getMessageRepository();
        this.userPermissionRepository = SingletonCollection.getUserPermissionRepository();

        this.userPermissions = null; //need to map
        this.cart = new Cart(userId); //need to map
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
        this.messageNotification = false;
        this.reviewedStoreNotification = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }


    public boolean isRegistered() {
        return this.getUserPermissions().getUserPermissionStatus() == UserPermissions.UserPermissionStatus.MEMBER ||
                this.getUserPermissions().getUserPermissionStatus() == UserPermissions.UserPermissionStatus.ADMIN;
    }

    public boolean isAdmin() {
        return this.getUserPermissions().getUserPermissionStatus() == UserPermissions.UserPermissionStatus.ADMIN;
    }

    //#15
    //returns User on success (for future functionalities)
    public User register(String userName, String password, String email, String answer1, String answer2, String answer3, LocalDateTime birthdate) {
        checkRegisterInfo(userName, password, email, birthdate);
        //updates the user info upon registration - no longer a guest
        updateUserDetail(userName, password, email, answer1, answer2, answer3, birthdate);
        this.getUserPermissions().register(this.userId);
        return this;
    }

    private void checkRegisterInfo(String userName, String password, String email, LocalDateTime birthdate) {
        String usernameRegex = "^[a-zA-Z0-9_-]{4,16}$"; // 4-16 characters, letters/numbers/underscore/hyphen
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$"; // need at least 8 characters, 1 uppercase, 1 lowercase, 1 number)
        String emailRegex = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"; // checks email validation
        if (birthdate == null) {
            throw new IllegalArgumentException("enter birthdate bro");
        }
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
    private void updateUserDetail(String userName, String password, String email, String answer1, String answer2, String answer3,
                                  LocalDateTime birthdate) {
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.userName = userName;
        this.dateOfBirth = birthdate;
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
        if (isLoggedIn())
            throw new IllegalArgumentException("User is already logged in!");

        this.isLoggedIn = true;
        save();


    }

    public void fetchMessages() throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can fetch messages");
        if (getAndSetMessageNotification()) {
            BroadCaster.broadcast(this.userId, "New Message");
        }
        if (getAndSetReviewedStoreNotification()) {
            BroadCaster.broadcast(this.userId, "New Review");
        }
    }

    public String getUserName() {
        return userName;
    }


    //#28
    public void openComplaint(String header, String complaint) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can open complaints");
        getMessageRepository().sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, complaint, adminIdentifier));

    }

    //#47
    public synchronized Message getComplaint() throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can read complaints");
        Message message = getMessageRepository().readUnreadMassage(adminIdentifier);
        regularMessageToReply = message;
        return message;
    }

    //#47
    public void markMessageAsReadAdmin(String receiverId, String senderId, int messageId) throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can mark as read complaints");

        Message message = getMessageRepository().markAsRead(receiverId, senderId, messageId);
        getMessageRepository().markAsReadHelper(receiverId,message);
    }

    //#47
    public void sendMassageAdmin(String receiverId, String header, String massage) throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can send massages");
        getMessageRepository().sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, massage, receiverId));
        User receiverNext = SingletonCollection.getUserRepository().getUserByUsername(receiverId);
        //if(!PushNotification.pushNotification("New Message",receiverNext.getUserId()))
        if (!receiverNext.isLoggedIn() || !BroadCaster.broadcast(receiverNext.userId, "New Message"))
            receiverNext.setMessageNotification(true);
        System.out.println("ReceiverNext: " + receiverNext.userName + " LoggedIn: " + receiverNext.isLoggedIn());
        System.out.println("Status: " + receiverNext.getMessageNotification());
    }

    //tdsfds
    //#47
    public void answerComplaint(String answer) throws NoPermissionException {
        if (!isAdmin())
            throw new NoPermissionException("Only admin can answer complaints");
        if (regularMessageToReply == null)
            throw new IllegalArgumentException("no complaint to answer");
        Message message = getMessageRepository().markAsRead(regularMessageToReply.getReceiverId(), regularMessageToReply.getSenderId(), regularMessageToReply.getMessageId());
        getMessageRepository().markAsReadHelper(regularMessageToReply.getReceiverId(),message);
        getMessageRepository().sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), "RE: " + regularMessageToReply.getHeader(), answer, regularMessageToReply.getSenderId()));
        User receiverNext = SingletonCollection.getUserRepository().getUserByUsername(regularMessageToReply.getSenderId());
        if (!receiverNext.isLoggedIn() || !BroadCaster.broadcast(receiverNext.userId, "New Message"))
            receiverNext.setMessageNotification(true);
        regularMessageToReply = null;
    }

    public void clearMessageToReply() {
        regularMessageToReply = null;
    }

    public Message readMassage() throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can read massages");

        Message message = getMessageRepository().readUnreadMassage(this.userName);
        Message message1= getMessageRepository().markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
        getMessageRepository().markAsReadHelper(message.getReceiverId(),message1);
        regularMessageToReply = message;
        return message;
    }

    public void replayMessage(String answer) throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can read massages");
        if (regularMessageToReply == null)
            throw new IllegalArgumentException("no message to answer");
        getMessageRepository().sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), "RE: " + regularMessageToReply.getHeader(), answer, regularMessageToReply.getSenderId()));
        User receiverNext = SingletonCollection.getUserRepository().getUserByUsername(regularMessageToReply.getSenderId());

        if (!receiverNext.isLoggedIn() || !BroadCaster.broadcast(receiverNext.userId, "New Message"))
            receiverNext.setMessageNotification(true);
        regularMessageToReply = null;
    }

    public void sendMassageBroad(String receiverName, String header, String massage) throws NoPermissionException {
        getMessageRepository().sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, massage, receiverName));
        User receiverNext = SingletonCollection.getUserRepository().getUserByUsername(receiverName);
        if (!BroadCaster.broadcast(receiverNext.userId, "New Message"))
            receiverNext.setMessageNotification(true);
        System.out.println("Status: " + receiverNext.getMessageNotification());
    }

    public Message readOldMessage() throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can read massages");

        return getMessageRepository().readReadMassage(this.userName);
    }

    public void refreshOldMessage() throws NoPermissionException {
        if (!isRegistered())
            throw new NoPermissionException("Only registered users can read massages");

        getMessageRepository().refreshOldMessages(this.userName);
    }

    //27
    public void logout() {
        logoutNoSave();
        save();
    }

    public void logoutNoSave(){
        if (!isLoggedIn)
            throw new IllegalArgumentException("already logged out!");
        this.isLoggedIn = false;
    }


    public void sendMassageStore(String header, String massage, int storeId) throws NoPermissionException {
        market.sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), header, massage, String.valueOf(storeId)), userId, storeId);
    }

    //42
    public Message readUnreadMassageStore(int storeId) throws NoPermissionException {
        Message message = market.getUnreadMessages(this.userId, storeId);
        regularMessageToReply = message;
        return message;
    }
    //42

    public Message readReadMassageStore(int storeId) throws NoPermissionException {
        return market.getReadMessages(this.userId, storeId);
    }

    //42
    public void answerQuestionStore(String answer) throws NoPermissionException {
        if (regularMessageToReply == null)
            throw new IllegalArgumentException("no message to reply to");
        assert regularMessageToReply.getReceiverId().matches("-?\\d+");
        market.markAsCompleted(regularMessageToReply.getSenderId(), regularMessageToReply.getMessageId(), this.userId, Integer.parseInt(regularMessageToReply.getReceiverId()));
        getMessageRepository().sendMassage(Message.constractMessage(this.userName, getAndIncrementMessageId(), "RE: " + regularMessageToReply.getHeader(), answer, regularMessageToReply.getSenderId()));
        User receiverNext = SingletonCollection.getUserRepository().getUserByUsername(regularMessageToReply.getSenderId());
        if (!BroadCaster.broadcast(receiverNext.userId, "New Message"))
            receiverNext.setMessageNotification(true);
        regularMessageToReply = null;
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
    public Review getReview(int storeId, int productId) throws NoPermissionException {
        return market.getReview(storeId, productId, this.userId);
    }

    public List<Review> getAllReviews(int storeId, int productId, int userId) throws NoPermissionException {
        return market.getAllReviews(productId, storeId);
    }

    //#26
    public float getProductScore(int storeId, int productId) throws NoPermissionException {
        return market.getProductScore(storeId, productId, userId);
    }

    public float getProductScoreUser(int userIdTarget, int storeId, int productId) throws NoPermissionException {
        return market.getProductScoreUser(storeId, productId, userIdTarget);
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

    public synchronized double purchaseCart(String creditCardNumber, String creditCardMonth,
                                            String creditCardYear, String creditCardHolderFirstName,
                                            String creditCardCcv, String id,
                                            HashMap<Integer/*productId*/, String/*productDiscountCode*/> productsCoupons,
                                            String/*store coupons*/ storeCoupon) throws PurchaseFailedException, NoPermissionException {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
        return cart.purchaseCart(creditCardNumber,
                creditCardMonth, creditCardYear,
                creditCardHolderFirstName,
                creditCardCcv, id,
                productsCoupons,
                storeCoupon);
    }

    public void purchaseCart(String creditCardNumber,
                             String creditCardMonth, String creditCardYear,
                             String creditCardHolderFirstName,
                             String creditCardCVV, String id,
                             String address, String city, String country,
                             String zip) throws PurchaseFailedException, NoPermissionException {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
        cart.purchaseCart(
                creditCardNumber, creditCardMonth,
                creditCardYear, creditCardHolderFirstName,
                creditCardCVV, id,
                address, city,
                country, zip);
    }

    public Pair<Double, List<BasketProduct>> startPurchaseBasketTransaction(List<String> coupons) throws PurchaseFailedException, NoPermissionException {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
        return cart.startPurchaseBasketTransaction(new UserInfo(this.dateOfBirth), coupons);
    }


    public String getCartDescription() throws NoPermissionException {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
        return cart.getCartDescription();
    }

    public List<Product> getCartContent() throws NoPermissionException {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
        return cart.getCartContent();
    }

    public List<BasketProduct> getCartBasketProducts() throws NoPermissionException {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
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
        if (cart.getUserId() == 0)
            cart.setUserId(this.userId);
        return cart;
    }


    public void addProductToCart(int productId, int storeId) throws Exception {
        market.isProductAvailable(productId, storeId);
        getCart().addProductToCart(productId, storeId);
    }


    public void removeProductFromCart(int storeId, int productId) throws Exception {
        getCart().removeProduct(storeId, productId);
    }

    public void changeProductQuantityInCart(int storeId, int productId, int quantity) throws Exception {
        if (isRegistered() && !isLoggedIn)
            throw new NoPermissionException("Only logged in users can purchase cart");
        getCart().changeProductQuantity(storeId, productId, quantity);
    }

    public void setPermissions(UserPermissions.UserPermissionStatus status) {
        this.getUserPermissions().setUserPermissionStatus(status);
    }

    public UserPermissions.UserPermissionStatus getStatus() {
        return this.getUserPermissions().getUserPermissionStatus();
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
        return this.getUserPermissions().getStoresAndRoles();
    }


    public void addToCart(int storeId, int productId, int amount, double newPrice) {
        getCart().addProductToCart(storeId, productId, amount, newPrice);
    }

    public void addPermission(int storeId, UserPermissions.StoreRole storeRole) {
        getUserPermissions().updateRoleInStore(storeId, storeRole);
    }

    public void deletePermission(int storeId) {
        getUserPermissions().deletePermission(storeId);
    }

    public UserPermissions getUserPermissions() {
        userPermissions = SingletonCollection.getUserPermissionRepository().getUserPermission(userId);
        return userPermissions;
    }

    public void addIndividualPermission(int storeId, UserPermissions.IndividualPermission individualPermission) {
        getUserPermissions().addIndividualPermission(storeId, individualPermission);
    }

    public void deleteIndividualPermission(int storeId, UserPermissions.IndividualPermission individualPermission) {
        getUserPermissions().deleteIndividualPermission(storeId, individualPermission);
    }


    public void removeAllIndividualPermissions(int storeId) {
        getUserPermissions().removeAllIndividualPermissions(storeId);
    }


    public void removeBasket(int basketId) {
        getCart().removeBasket(userId, basketId);
    }


    public List<Integer> getFailedProducts(int storeId) {
        return getCart().getFailedProducts(storeId, userId);
    }

    public synchronized boolean getMessageNotification() {
        return messageNotification;
    }

    public synchronized boolean getAndSetMessageNotification() {
        boolean messageNotification = this.messageNotification;
        this.messageNotification = false;
        return messageNotification;
    }

    public synchronized void setMessageNotification(boolean notifications) {
        this.messageNotification = notifications;
    }

    public synchronized boolean getReviewedStoreNotification() {
        return reviewedStoreNotification;
    }

    public synchronized boolean getAndSetReviewedStoreNotification() {
        boolean reviewedStoreNotification = this.reviewedStoreNotification;
        this.reviewedStoreNotification = false;
        return reviewedStoreNotification;
    }

    public synchronized void setReviewedStoreNotification(boolean notifications) {
        this.reviewedStoreNotification = notifications;
    }


    public List<Product> getAllFailedProductsAfterPayment() {
        return getCart().getAllFailedProductsAfterPayment();
    }

    public double getTotalPriceOfCart() {
        return getCart().getTotalPriceOfCartBeforeDiscount();
    }

    public void cancelPurchase() {
        getCart().cancelPurchase();
    }

    public List<PurchaseHistory> getPurchaseHistory() {
        return SingletonCollection.getPurchaseHistoryRepository().getAllPurchases(userId);
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }


    public UserCard getUserCard() {
        List<Pair<Integer, String>> pairs = getUserPermissions().getStoresAndRoles();
        StringBuilder stringBuilder = new StringBuilder();
        for (Pair<Integer, String> pair : pairs) {
            stringBuilder.append(" Store: ");
            stringBuilder.append(getStoreRepository().getStore(pair.getFirst()).getStoreName());
            stringBuilder.append(" Role: ");
            stringBuilder.append(pair.getSecond());
        }
        return new UserCard(userId, userName, email, stringBuilder.toString());
    }

    public UserPermissions.PopulationStatus getPopulationStatus() {
        return getUserPermissions().getPopulationStatus();
    }

    public void deleteStores(int adminId) throws NoPermissionException {
        market.removeMemberStores(adminId, userId);
    }

    public void clearCart() {
        getCart().clearCart();
    }

    public void removeBasketProducts(List<Pair<Integer, Integer>> productStoreList) {
        getCart().removeBasketProducts(productStoreList);
    }

    public void removeBasketProduct(int productId, int storeId) throws Exception {
        getCart().removeProduct(storeId, productId);
    }

    public void clearUserStorePermissions(int storeId) {
        getUserPermissions().clearUserStorePermissions(storeId);
    }

    public void clearPermissions(int adminId) throws NoPermissionException, ChangePermissionException {
        getUserPermissionRepository().deletePermissions(adminId, userId);
    }


    //getters and setters


    public IPurchaseHistoryRepository getPurchaseHistoryRepository() {
        return purchaseHistoryRepository;
    }

    public void setPurchaseHistoryRepository(IPurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        this.cart.setUserId(userId);
    }

    public IMessageRepository getMessageRepository() {
        messageRepository= SingletonCollection.getMessageRepository();
        return messageRepository;
    }

    public void setMessageRepository(IMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public IUserPermissionRepository getUserPermissionRepository() {
        userPermissionRepository = SingletonCollection.getUserPermissionRepository();
        return userPermissionRepository;
    }

    public void setUserPermissionRepository(IUserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    public void setUserPermissions(UserPermissions userPermissions) {
        this.userPermissions = userPermissions;
    }


    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Message getRegularMessageToReply() {
        return regularMessageToReply;
    }

    public void setRegularMessageToReply(Message regularMessageToReply) {
        this.regularMessageToReply = regularMessageToReply;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getAdminIdentifier() {
        return adminIdentifier;
    }

    public void setAdminIdentifier(String adminIdentifier) {
        this.adminIdentifier = adminIdentifier;
    }

    public boolean isMessageNotification() {
        return messageNotification;
    }

    public boolean isReviewedStoreNotification() {
        return reviewedStoreNotification;
    }

    public IStoreRepository getStoreRepository() {
        this.storeRepository = SingletonCollection.getStoreRepository();
        return this.storeRepository;
    }

    private void save(){
        SingletonCollection.getUserRepository().save();
    }
}
