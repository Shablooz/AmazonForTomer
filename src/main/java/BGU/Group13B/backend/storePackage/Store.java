package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageRepositoryNonPersist;
import BGU.Group13B.backend.Repositories.Implementations.StorePermissionsRepositoryImpl.StorePermissionsRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.newDiscoutns.PurchasePolicy;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.DiscountPolicy;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.permissions.*;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.BroadCaster;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import BGU.Group13B.service.info.StoreInfo;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Entity

public class Store {

    @Transient
    private IConditionRepository conditionRepository;
    @Transient
    private DiscountPolicy discountPolicy;
    @Transient
    private IProductRepository productRepository;
    @Transient
    private DeliveryAdapter deliveryAdapter;
    @Transient
    private PaymentAdapter paymentAdapter;
    @Transient
    private AlertManager alertManager; // not needed
    @Transient
    private StorePermission storePermission; //v
    @Transient
    private IStoreMessagesRepository storeMessagesRepository;
    @Transient
    private AddToUserCart addToUserCart;
    @Transient
    private IBIDRepository bidRepository;
    @Transient
    private IUserRepository userRepository;// v
    @Transient
    private IStorePermissionsRepository storePermissionsRepository; //v
    @Id
    private int storeId;

    @Transient
    private IPurchaseHistoryRepository purchaseHistoryRepository;

    private String storeName;
    private String category;
    @Transient
    private IAuctionRepository auctionRepository;
    @Transient
    private IStoreScore storeScore; //v

    private boolean hidden = false;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private PurchasePolicy purchasePolicy;

    public Store(int storeId, int founderId, String storeName, String category) {
        this.auctionRepository = SingletonCollection.getAuctionRepository();
        this.productRepository = SingletonCollection.getProductRepository();
        this.bidRepository = SingletonCollection.getBidRepository();
        this.deliveryAdapter = SingletonCollection.getDeliveryAdapter();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.alertManager = SingletonCollection.getAlertManager();
        this.addToUserCart = SingletonCollection.getAddToUserCart();
        this.storeMessagesRepository = SingletonCollection.getStoreMessagesRepository();
        this.userRepository = SingletonCollection.getUserRepository();
        this.storePermissionsRepository = SingletonCollection.getStorePermissionRepository();
        this.storeId = storeId;
        this.storeName = storeName;
        this.category = category;
        StorePermission storePermission1 = getStorePermissionsRepository().getStorePermission(storeId);
        if (storePermission1 == null) {
            storePermission1 = new StorePermission(founderId);
            getStorePermissionsRepository().addStorePermission(storeId, storePermission1);
        }
        this.storePermission = storePermission1;
        getUserRepo().getUser(founderId).addPermission(storeId, UserPermissions.StoreRole.FOUNDER);
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.storeScore = SingletonCollection.getStoreScoreRepository();

        this.conditionRepository = SingletonCollection.getConditionRepository();
        this.discountPolicy = new DiscountPolicy(storeId);
        this.purchasePolicy = new PurchasePolicy(storeId);
    }

    //used only for testing
    public Store(int storeId, String storeName, String category, IProductRepository productRepository,
                 DeliveryAdapter deliveryAdapter, PaymentAdapter paymentAdapter, AlertManager alertManager, StorePermission storePermission,
                 AddToUserCart addToUserCart, IBIDRepository bidRepository,
                 StoreMessageRepositoryNonPersist storeMessagesRepository, IAuctionRepository auctionRepository, IPurchaseHistoryRepository purchaseHistoryRepository,
                 IStoreScore storeScore) {
        this.productRepository = productRepository;
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.alertManager = alertManager;
        this.storePermission = storePermission;
        this.storeMessagesRepository = storeMessagesRepository;
        this.addToUserCart = addToUserCart;
        this.bidRepository = bidRepository;
        this.storeId = storeId;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.storeName = storeName;
        this.category = category;
        this.auctionRepository = auctionRepository;
        this.storeScore = storeScore;
        this.userRepository = null;
        this.storePermissionsRepository = new StorePermissionsRepositoryAsHashmap(false); //added to trick the system
        this.conditionRepository = null;
        this.purchasePolicy = new PurchasePolicy(storeId);
        this.discountPolicy = new DiscountPolicy(storeId);
    }

    public Store() { //maybe need to change nulls
        this.productRepository = null;
        this.deliveryAdapter = null;
        this.paymentAdapter = null;
        this.alertManager = null;
        this.storePermission = null;
        this.storeMessagesRepository = null;
        this.addToUserCart = null;
        this.bidRepository = null;
        this.storeId = 0;
        this.purchaseHistoryRepository = null;
        this.storeName = null;
        this.category = null;
        this.auctionRepository = null;
        this.storeScore = null;
        this.userRepository = null;
        this.storePermissionsRepository = null;
        this.conditionRepository = null;
        this.discountPolicy = new DiscountPolicy(storeId);
        this.purchasePolicy = null;
    }


    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public List<Integer> addOwner(int userId, int newOwnerId) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add an owner to store " + this.storeId);
        List<Integer> list = getStorePermission().newVoteOwnerPermission(newOwnerId, userId);
        Pair<Integer, Integer> pair = new Pair<>(newOwnerId, userId);
        voteForOwner(pair, userId, true);
        return list;
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public void voteForOwner(Pair<Integer, Integer> newAndAppointerIds, int voterId, boolean accept) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(voterId, hidden))
            throw new NoPermissionException("User " + voterId + " has no permission to vote for an owner to store " + this.storeId);
        if(getStorePermission().updateVote(newAndAppointerIds, voterId, accept)){
            getStorePermission().addOwnerPermission(newAndAppointerIds.getFirst(), newAndAppointerIds.getSecond());
            getUserRepo().getUser(newAndAppointerIds.getFirst()).addPermission(storeId, UserPermissions.StoreRole.OWNER);
        }
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public List<Pair<Integer, Integer>> getMyOpenVotes(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to view votes in store " + this.storeId);
        return getStorePermission().getMyOpenVotes(userId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public void removeOwner(int userId, int removeOwnerId) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove an owner to store " + this.storeId);
        Set<Integer> removeUsersList = getStorePermission().removeOwnerPermission(removeOwnerId, userId, false);
        for (Integer removeUserId : removeUsersList) {
            getUserRepo().getUser(removeUserId).deletePermission(storeId);
        }

    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public void addManager(int userId, int newManagerId) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add a manager to store " + this.storeId);
        getStorePermission().addManagerPermission(newManagerId, userId);
        getUserRepo().getUser(newManagerId).addPermission(storeId, UserPermissions.StoreRole.MANAGER);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public void removeManager(int userId, int removeManagerId) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove a manager to store " + this.storeId);
        getStorePermission().removeManagerPermission(removeManagerId, userId);
        getUserRepo().getUser(removeManagerId).deletePermission(storeId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public void addPermissionToManager(int userId, int managerId, UserPermissions.IndividualPermission individualPermission) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add permissions to managers in store " + this.storeId);
        getStorePermission().addIndividualPermission(managerId, individualPermission);
        getUserRepo().getUser(managerId).addIndividualPermission(storeId, individualPermission);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StaffPermission
    public void removePermissionFromManager(int userId, int managerId, UserPermissions.IndividualPermission individualPermission) throws NoPermissionException, ChangePermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove permissions from managers in store " + this.storeId);
        getStorePermission().removeIndividualPermission(managerId, individualPermission);
        getUserRepo().getUser(managerId).deleteIndividualPermission(storeId, individualPermission);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @WorkersInfoPermission
    public List<WorkerCard> getStoreWorkersInfo(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get info about the store workers " + this.storeId);
        return getStorePermission().getWorkersInfo();
    }

    //@DefaultFounderFunctionality
    //@DefaultOwnerFunctionality
    public List<Integer> getStoreOwners() throws NoPermissionException {
        /*if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get info about the store workers " + this.storeId);*/
        return getStorePermission().getStoreOwners();
    }

    public StorePermission getStorePermission() {
        if (getStorePermissionsRepository().getSaveMode())
            storePermission = getStorePermissionsRepository().getStorePermission(storeId);
        return storePermission;
    }

    public void sendMassage(Message message, int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        getStoreMessagesRepository().sendMassage(message, this.storeId, userId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @MessagesPermission
    public Message getUnreadMessages(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to read message of store " + this.storeId);

        return getStoreMessagesRepository().readUnreadMassage(this.storeId, userId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @MessagesPermission
    public Message getReadMessages(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to read message of store " + this.storeId);
        return getStoreMessagesRepository().readReadMassage(this.storeId, userId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @MessagesPermission
    public void markAsCompleted(String senderId, int messageId, int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to mark message as complete of store: " + this.storeId);
        getStoreMessagesRepository().markAsRead(this.storeId, senderId, messageId, userId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @MessagesPermission
    public void refreshMessages(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to handle message of store " + this.storeId);
        getStoreMessagesRepository().refreshOldMassage(this.storeId, userId);
    }



    public void addReview(String review, int userId, int productId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        if (!SingletonCollection.getPurchaseHistoryRepository().isPurchase(userId, this.storeId, productId))
            throw new IllegalArgumentException("User with id: " + userId + " did not purchase product with id: " + productId + " from store: " + this.storeId);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.addReview(review, userId);

        var storeOwnerIds = getStorePermission().getStoreOwners();
        for (int id : storeOwnerIds) {
            User receiver = SingletonCollection.getUserRepository().getUser(id);
            if (!BroadCaster.broadcast(receiver.getUserId(), "New Review"))
                receiver.setReviewedStoreNotification(true);
        }
        var storeFounderId = getStorePermission().getStoreFounder();
        User receiver = SingletonCollection.getUserRepository().getUser(storeFounderId);
        if (!BroadCaster.broadcast(receiver.getUserId(), "New Review"))
            receiver.setReviewedStoreNotification(true);
    }

    public void removeReview(int userId, int productId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.removeReview(userId);
    }

    public Review getReview(int userId, int productId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        return product.getReview(userId);
    }

    public List<Review> getAllReviews(int productId) {//TODO:maybe need to add validateStoreVisibility
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        return product.getAllReviews();
    }

    public float getProductScore(int productId, int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        return product.getProductScore();
    }

    public float getProductScoreUser(int productId, int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        return product.getProductScoreUser(userId);
    }

    public void addAndSetProductScore(int productId, int userId, int score) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeName);
        if (!SingletonCollection.getPurchaseHistoryRepository().isPurchase(userId, this.storeId, productId))
            throw new IllegalArgumentException("User" + " did not purchase product with name: " + product.getName() + " from store: " + this.storeName);

        product.addAndSetScore(userId, score);
    }

    public void removeProductScore(int productId, int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        Product product = getProductRepository().getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.removeProductScore(userId);
    }

    public void addStoreScore(int userId, int score) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        if (SingletonCollection.getPurchaseHistoryRepository().isPurchaseFromStore(userId, this.storeId))
            throw new IllegalArgumentException("User with id: " + userId + " did not purchase from store: " + storeId);
        getStoreScoreRepo().addStoreScore(userId, storeId, score);
    }

    public void removeStoreScore(int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        getStoreScoreRepo().removeStoreScore(userId, this.storeId);
    }

    public void modifyStoreScore(int userId, int score) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        getStoreScoreRepo().modifyStoreScore(userId, this.storeId, score);
    }

    public float getStoreScore() {
        return getStoreScoreRepo().getStoreScore(this.storeId);
    }


    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public synchronized int addProduct(int userId, String productName, String category, double price, int stockQuantity, String description) throws NoPermissionException {
        /*
         * check if the user has permission to add product
         * */
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product to store " + this.storeId);

        if (hidden)
            return this.getProductRepository().addHiddenProduct(storeId, productName, category, price, stockQuantity, description);

        return this.getProductRepository().addProduct(storeId, productName, category, price, stockQuantity, description).getProductId();
    }

    public double calculatePriceOfBasket(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) throws PurchaseExceedsPolicyException {
        if (hidden)
            throw new PurchaseExceedsPolicyException("This Store is hidden");

        purchasePolicy.satisfied(basketInfo, userInfo);

        return discountPolicy.calculatePriceOfBasket(basketInfo, userInfo, coupons); //to return
        //throw new NotImplementedException("add purchase policy checker");

        /*int quantity = successfulProducts.stream().mapToInt(BasketProduct::getQuantity).sum();
        double finalPrice = oldDiscountPolicy.applyAllDiscounts(totalAmountBeforeStoreDiscountPolicy, successfulProducts, storeCoupon);
        getPurchasePolicy().checkPolicy(quantity, finalPrice);
        return finalPrice;*/
    }


    public static String getCurrentMethodName() {
        return StorePermission.getFunctionName(
                Thread.currentThread().getStackTrace()[2].getMethodName());
    }


    public void purchaseProposalSubmit(int userId, int productId, double proposedPrice, int amount) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        if (proposedPrice <= 0)
            throw new IllegalArgumentException("Price must be positive");

        SingletonCollection.getBidRepository().addBID(userId, productId, proposedPrice, amount);

        /*
         * Send alert to all the permitted Managers
         * */
        //Set<Integer> userIds = this.storePermission.getAllUsersWithPermission(Store.getCurrentMethodName());
        List<Integer> userIds = this.getStorePermission().getAllUsersWithIndividualPermission(UserPermissions.IndividualPermission.AUCTION);
        for (Integer id : userIds) {//wait for the interface in AlertManager.java to finish
            //alertManager.sendAlert(id, "User " + userId + " has submitted a purchase proposal for product " + productId + " in store " + this.storeId);
            double originalPrice = getProductRepository().getProductById(productId).getPrice();
            BroadCaster.broadcast(id,
                    "BID[" + storeId + "," + productId + "]User has submitted a purchase proposal for product " + productId +
                            "\n In store " + this.storeName +
                            "\nOriginal price: " + originalPrice +
                            "\nProposed price: " + proposedPrice +
                            "\nAmount: " + amount + "\n");
        }
    }

    @DefaultFounderFunctionality
    @AuctionPermission
    @DefaultOwnerFunctionality
    public void purchaseProposalApprove(int managerId, int bidId) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        if (!this.getStorePermission().checkPermission(managerId, hidden))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + managerId + " has no permission to add product to store " + this.storeId);
        BID currentBid = SingletonCollection.getBidRepository().getBID(bidId).
                orElseThrow(() -> new IllegalArgumentException("There is no such bid for store " + this.storeId));

        if (currentBid.isRejected())//fixme, use BroadCaster.broadcast
            BroadCaster.broadcast(managerId,
                    "The bid for product " + getProductRepository().getProductById(currentBid.getProductId()).getName()
                            + " has been rejected already");
        //alertManager.sendAlert(managerId, "The bid for product " + currentBid.getProductId() + " in store " + this.storeId + " has been rejected already");
        currentBid.approve(managerId);
        Set<Integer> managers = getStorePermission().getAllUsersWithPermission("purchaseProposalSubmit");

        if (currentBid.approvedByAll(managers)) {
            BroadCaster.broadcast(currentBid.getUserId(),
                    "Your bid for product " + getProductRepository().getProductById(currentBid.getProductId()).getName()
                            + " in store " + this.storeName + " approved!, the item added to your cart");
            if(SingletonCollection.getStoreRepository().getSaveMode())
                SingletonCollection.getAddToUserCart().apply(currentBid.getUserId(), storeId, currentBid.getProductId(), currentBid.getAmount(), currentBid.getNewProductPrice());
            else
                addToUserCart.apply(currentBid.getUserId(), storeId, currentBid.getProductId(), currentBid.getAmount(), currentBid.getNewProductPrice());
            //todo send alert to the user that his bid has been approved
        }
    }

    @DefaultFounderFunctionality
    @AuctionPermission
    @DefaultOwnerFunctionality
    public void purchaseProposalReject(int managerId, int bidId) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        if (!this.getStorePermission().checkPermission(managerId, hidden))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + managerId + " has no permission to reject a purchase proposal in the store: " + this.storeId);
        BID currentBid = SingletonCollection.getBidRepository().getBID(bidId).orElseThrow(() -> new IllegalArgumentException("There is no such bid for store " + this.storeId));
        if (currentBid.isRejected())
            BroadCaster.broadcast(managerId,
                    "The bid for product " + getProductRepository().getProductById(currentBid.getProductId()).getName()
                            + " has been rejected already");

        currentBid.reject();//good for concurrency edge cases
        SingletonCollection.getBidRepository().removeBID(bidId);
        //todo send alert to the user that his bid has been rejected
    }

    //only members of the store can create an auction purchase
    @DefaultFounderFunctionality
    @AuctionPermission
    @DefaultOwnerFunctionality
    public void auctionPurchase(int userId, int productId, double newPrice) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to create an auction purchase in the store: " + this.storeId);
        auctionRepository.updateAuction(productId, this.storeId, newPrice, userId);
    }

    @DefaultFounderFunctionality
    @AuctionPermission
    @DefaultOwnerFunctionality
    public PublicAuctionInfo getAuctionInfo(int userId, int productId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get auction info in the store: " + this.storeId);
        return auctionRepository.getAuctionInfo(productId, this.storeId).orElseThrow(() ->
                new IllegalArgumentException("There is no such auction for product " + productId + " in store " + this.storeId));
    }

    @DefaultFounderFunctionality
    @AuctionPermission
    @DefaultOwnerFunctionality
    public void createAuctionForProduct(int storeManagerId, int productId, double startingPrice, LocalDateTime lastDate) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(storeManagerId, hidden))
            throw new NoPermissionException("User " + storeManagerId + " has no permission to create an auction in the store: " + this.storeId);
        auctionRepository.addNewAuctionForAProduct(productId, startingPrice, this.storeId, lastDate);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @AuctionPermission
    public void endAuctionForProduct(int storeManagerId, int productId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(storeManagerId, hidden))
            throw new NoPermissionException("User " + storeManagerId + " has no permission to remove an auction in the store: " + this.storeId);
        auctionRepository.endAuction(productId, this.storeId);
    }

    public synchronized void isProductAvailable(int productId) throws Exception {
        Product product = getProductRepository().getStoreProductById(productId, storeId);
        if (product.isHidden())
            throw new Exception("The product is hidden");
        if (product.isOutOfStock())
            throw new Exception("The product is out of stock");
    }

    public int getStoreId() {
        return storeId;
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public void setProductName(int userId, int productId, String name) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product name in the store: " + this.storeId);

        Product product = this.getProductRepository().getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setName(name);
        }
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public void setProductCategory(int userId, int productId, String category) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product category in the store: " + this.storeId);

        Product product = this.getProductRepository().getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setCategory(category);
        }
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public void setProductPrice(int userId, int productId, double price) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product price in the store: " + this.storeId);

        Product product = this.getProductRepository().getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setPrice(price);
        }
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public void setProductStockQuantity(int userId, int productId, int quantity) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product stock quantity in the store: " + this.storeId);

        Product product = this.getProductRepository().getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setStockQuantity(quantity);
        }
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public void setProductDescription(int userId, int productId, String description) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product description in the store: " + this.storeId);

        Product product = this.getProductRepository().getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setDescription(description);
        }
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StockPermission
    public void removeProduct(int userId, int productId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove product in the store: " + this.storeId);

        Product product = this.getProductRepository().getStoreProductById(productId, storeId);
        synchronized (product) {
            this.getProductRepository().removeStoreProduct(productId, storeId);
            product.delete();
            discountPolicy.removeProductDiscount(productId);
        }
    }

    public String getCategory() {
        return category;
    }

    public String getStoreName() {
        return storeName;
    }

    public StoreInfo getStoreInfo(int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        return new StoreInfo(this);
    }

    //TODO check
    public Product getStoreProduct(int userId, int productId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        return getProductRepository().getStoreProductById(productId, storeId);
    }

    public Set<Product> getAllStoreProducts(int userId) throws NoPermissionException {
        this.getStorePermission().validateStoreVisibility(userId, hidden);
        return getProductRepository().getStoreProducts(storeId).orElse(new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)));
    }

    @DefaultFounderFunctionality
    @FounderOnlyPermission
    public synchronized void hideStore(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to hide the store: " + this.storeId);

        if (this.hidden)
            throw new RuntimeException("The store " + this.storeName + " is already hidden");

        this.hidden = true;
        getProductRepository().hideAllStoreProducts(this.storeId);

        //notify all store owners and managers
        notifyAllWorkers(userId, "Store Closed", "The store " + this.storeName + " has been hidden");
        save();
    }

    //will send a message to all the store's workers except the user with the given id
    private void notifyAllWorkers(int userId, String msgHeader, String msgBody) throws NoPermissionException {
        User user = getUserRepo().getUser(userId);
        for (int workerId : this.getStorePermission().getWorkersInfo().stream().map(WorkerCard::userId).toList()) {
            if (workerId != userId) {
                String username = getUserRepo().getUser(workerId).getUserName();
                user.sendMassageBroad(username, msgHeader, msgBody);
            }
        }

    }

    @DefaultFounderFunctionality
    @FounderOnlyPermission
    public synchronized void unhideStore(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to unhide the store: " + this.storeId);

        if (!this.hidden)
            throw new RuntimeException("The store " + this.storeName + " isn't hidden");

        this.hidden = false;
        getProductRepository().unhideAllStoreProducts(this.storeId);

        //notify all store owners and managers
        notifyAllWorkers(userId, "Store Reopened", "The store " + this.storeName + " has been reopened");
        save();
    }

    public synchronized void deleteStore(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to delete the store: " + this.storeId);

        //delete data
        deleteAllStoreProducts(userId);
        deleteAllStorePermissions();
        discountPolicy.removeAllDiscounts();
        purchasePolicy.removeRoot();
        deleteAllStoreScores();

        //notify workers
        notifyAllWorkers(userId, "Store Deleted", "The store " + this.storeName + " has been deleted by and admin");
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @HistoryPermission
    @DefaultManagerFunctionality
    public List<PurchaseHistory> getStorePurchaseHistory(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get the store purchase history: " + this.storeId);

        return SingletonCollection.getPurchaseHistoryRepository().getStorePurchaseHistory(storeId);
    }

    private void deleteAllStoreScores() {
        getStoreScoreRepo().clearStoreScore(storeId);
    }

    private void deleteAllStoreProducts(int userId) throws NoPermissionException {
        for (Product p : getAllStoreProducts(userId))
            removeProduct(userId, p.getProductId());

    }

    private void deleteAllStorePermissions() {
        getStorePermissionsRepository().deleteStorePermissions(storeId);
        for (WorkerCard workerCard : getStorePermission().getWorkersInfo())
            getUserRepo().getUser(workerCard.userId()).clearUserStorePermissions(storeId);
        getStorePermission().emptyMaps();
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getStoreFounder() {
        return getStorePermission().getStoreFounder();
    }


    /**
     * <H1>Conditions</H1>
     */

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void setPurchasePolicyCondition(int userId, int conditionId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set the purchase policy in the store : " + this.storeId);

        purchasePolicy.setCondition(conditionId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addORCondition(int userId, int condition1, int condition2) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add OR condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addORCondition(condition1, condition2);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addANDCondition(int userId, int condition1, int condition2) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add AND condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addANDCondition(condition1, condition2);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addXORCondition(int userId, int condition1, int condition2) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add XOR condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addXORCondition(condition1, condition2);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addIMPLYCondition(int userId, int condition1, int condition2) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add IMPLY condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addIMPLYCondition(condition1, condition2);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStorePriceCondition(int userId, double lowerBound, double upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store price condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addStorePriceCondition(lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStorePriceCondition(int userId, double lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store price condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addStorePriceCondition(lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStoreQuantityCondition(int userId, int lowerBound, int upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store quantity condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addStoreQuantityCondition(lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStoreQuantityCondition(int userId, int lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store quantity condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addStoreQuantityCondition(lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryPriceCondition(int userId, String category, double lowerBound, double upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category price condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addCategoryPriceCondition(category, lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryPriceCondition(int userId, String category, double lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category price condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addCategoryPriceCondition(category, lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryQuantityCondition(int userId, String category, int lowerBound, int upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category quantity condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addCategoryQuantityCondition(category, lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryQuantityCondition(int userId, String category, int lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category quantity condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addCategoryQuantityCondition(category, lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addDateCondition(int userId, LocalDateTime lowerBound, LocalDateTime upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add date condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addDateCondition(lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addDateCondition(int userId, LocalDateTime lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add date condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addDateCondition(lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductPriceCondition(int userId, int productId, double lowerBound, double upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product price condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addProductPriceCondition(productId, lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductPriceCondition(int userId, int productId, double lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product price condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addProductPriceCondition(productId, lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductQuantityCondition(int userId, int productId, int lowerBound, int upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product quantity condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addProductQuantityCondition(productId, lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductQuantityCondition(int userId, int productId, int lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product quantity condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addProductQuantityCondition(productId, lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addTimeCondition(int userId, LocalDateTime lowerBound, LocalDateTime upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to time condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addTimeCondition(lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addTimeCondition(int userId, LocalDateTime lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " time condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addTimeCondition(lowerBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addUserAgeCondition(int userId, int lowerBound, int upperBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " user age condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addUserAgeCondition(lowerBound, upperBound);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addUserAgeCondition(int userId, int lowerBound) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " user age condition in the store: " + this.storeId);

        return SingletonCollection.getConditionRepository().addUserAgeCondition(lowerBound);
    }

    /**
     * <H1>Discounts</H1>
     */
    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStoreDiscount(int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String coupon) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store discount in the store: " + this.storeId);

        return discountPolicy.addStoreDiscount(conditionId, discountPercentage, expirationDate, coupon);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStoreDiscount(int userId, double discountPercentage, LocalDate expirationDate, String coupon) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store discount in the store: " + this.storeId);

        return discountPolicy.addStoreDiscount(discountPercentage, expirationDate, coupon);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStoreDiscount(int userId, int conditionId, double discountPercentage, LocalDate expirationDate) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store discount in the store: " + this.storeId);

        return discountPolicy.addStoreDiscount(conditionId, discountPercentage, expirationDate);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addStoreDiscount(int userId, double discountPercentage, LocalDate expirationDate) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store discount in the store: " + this.storeId);

        return discountPolicy.addStoreDiscount(discountPercentage, expirationDate);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryDiscount(int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category, String coupon) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category discount in the store: " + this.storeId);

        return discountPolicy.addCategoryDiscount(conditionId, discountPercentage, expirationDate, category, coupon);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryDiscount(int userId, double discountPercentage, LocalDate expirationDate, String category, String coupon) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category discount in the store: " + this.storeId);

        return discountPolicy.addCategoryDiscount(discountPercentage, expirationDate, category, coupon);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryDiscount(int userId, int conditionId, double discountPercentage, LocalDate expirationDate, String category) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category discount in the store: " + this.storeId);

        return discountPolicy.addCategoryDiscount(conditionId, discountPercentage, expirationDate, category);
    }


    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addCategoryDiscount(int userId, double discountPercentage, LocalDate expirationDate, String category) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add category discount in the store: " + this.storeId);

        return discountPolicy.addCategoryDiscount(discountPercentage, expirationDate, category);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductDiscount(int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product discount in the store: " + this.storeId);

        return discountPolicy.addProductDiscount(conditionId, discountPercentage, expirationDate, productId, coupon);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductDiscount(int userId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product discount in the store: " + this.storeId);

        return discountPolicy.addProductDiscount(discountPercentage, expirationDate, productId, coupon);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductDiscount(int userId, int conditionId, double discountPercentage, LocalDate expirationDate, int productId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product discount in the store: " + this.storeId);

        return discountPolicy.addProductDiscount(conditionId, discountPercentage, expirationDate, productId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public int addProductDiscount(int userId, double discountPercentage, LocalDate expirationDate, int productId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product discount in the store: " + this.storeId);

        return discountPolicy.addProductDiscount(discountPercentage, expirationDate, productId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public List<StoreDiscount> getStoreDiscounts(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get store discounts in the store: " + this.storeId);

        return discountPolicy.getStoreDiscounts();
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public StoreDiscount getDiscount(int userId, int discountId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get store discounts in the store: " + this.storeId);

        return discountPolicy.getDiscount(discountId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void removeDiscount(int userId, int discountId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove store discounts in the store: " + this.storeId);

        discountPolicy.removeDiscount(discountId);
    }

    /**
     * <H1>Discount Accumulation Tree</H1>
     */
    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void addDiscountAsRoot(int userId, int discountId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add discount as root in the store: " + this.storeId);
        discountPolicy.addDiscountAsRoot(discountId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void addDiscountToXORRoot(int userId, int discountId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add discount to XOR root in the store: " + this.storeId);
        discountPolicy.addDiscountToXORRoot(discountId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void addDiscountToMAXRoot(int userId, int discountId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add discount to MAX root in the store: " + this.storeId);
        discountPolicy.addDiscountToMAXRoot(discountId);

    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void addDiscountToADDRoot(int userId, int discountId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add discount to ADD root in the store: " + this.storeId);
        discountPolicy.addDiscountToADDRoot(discountId);

    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public DiscountAccumulationTreeInfo getDiscountAccumulationTree(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get discount accumulation tree in the store: " + this.storeId);
        return discountPolicy.getDiscountAccumulationTree();
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void deleteStoreAccumulationTree(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to delete discount accumulation tree in the store: " + this.storeId);
        discountPolicy.deleteStoreAccumulationTree();
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public Condition getPurchasePolicy(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get the purchase policy in the store: " + this.storeId);
        return purchasePolicy.getRootCondition();
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @PoliciesPermission
    public void resetPurchasePolicy(int userId) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to reset the purchase policy in the store: " + this.storeId);
        purchasePolicy.resetPurchasePolicy();
    }

    public void removeAllProducts(int userId) {
        getProductRepository().removeStoreProducts(storeId);
    }

    @DefaultFounderFunctionality
    @DefaultOwnerFunctionality
    @StatsPermission
    public double[] getStoreHistoryIncome(int userId, LocalDate startDate, LocalDate endDate) throws NoPermissionException {
        if (!this.getStorePermission().checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get the store history income in the store: " + this.storeId);

        double[] historyIncome = new double[(endDate.getYear() - startDate.getYear()) * 365 + (endDate.getDayOfYear() - startDate.getDayOfYear()) + 1];
        for (PurchaseHistory purchase : getPurchaseHistoryRepository().getStorePurchaseHistory(this.storeId)) {
            LocalDate purchaseDate = purchase.getLocalDate();
            if (!(purchaseDate.isBefore(startDate) || purchaseDate.isAfter(endDate)))
                historyIncome[(endDate.getYear() - purchaseDate.getYear()) * 365 + purchaseDate.getDayOfYear() - startDate.getDayOfYear()] += purchase.getPrice();
        }

        return historyIncome;
    }

    public boolean canViewStore(int userId) {
        return !hidden || getStorePermission().hasAccessWhileHidden(userId);
    }


    public IConditionRepository getConditionRepository() {
        return conditionRepository;
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }

    public IProductRepository getProductRepository() {
        if (productRepository == null || productRepository.getSaveMode()) //tomer check
            productRepository = SingletonCollection.getProductRepository();
        return productRepository;
    }

    public DeliveryAdapter getDeliveryAdapter() {
        return deliveryAdapter;
    }

    public PaymentAdapter getPaymentAdapter() {
        return paymentAdapter;
    }

    public AlertManager getAlertManager() {
        return alertManager;
    }

    public IStoreMessagesRepository getStoreMessagesRepository() {
        storeMessagesRepository = SingletonCollection.getStoreMessagesRepository();
        return storeMessagesRepository;
    }

    public AddToUserCart getAddToUserCart() {
        return addToUserCart;
    }

    public IBIDRepository getBidRepository() {
        return bidRepository;
    }

    public IUserRepository getUserRepository() {
        return userRepository;
    }

    public IStorePermissionsRepository getStorePermissionsRepository() {
        if (storePermissionsRepository == null || storePermissionsRepository.getSaveMode()) //tomer check
            storePermissionsRepository = SingletonCollection.getStorePermissionRepository();
        return storePermissionsRepository;
    }

    public IStoreScore getStoreScoreRepo() {
        storeScore = SingletonCollection.getStoreScoreRepository(); // maybe need check
        return storeScore;
    }

    public IUserRepository getUserRepo() {
        userRepository = SingletonCollection.getUserRepository();
        return userRepository;
    }


    public IPurchaseHistoryRepository getPurchaseHistoryRepository() {
        return SingletonCollection.getPurchaseHistoryRepository();
    }

    public IAuctionRepository getAuctionRepository() {
        return auctionRepository;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public PurchasePolicy getPurchasePolicy() {
        return purchasePolicy;
    }

    public void setConditionRepository(IConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public void setProductRepository(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void setDeliveryAdapter(DeliveryAdapter deliveryAdapter) {
        this.deliveryAdapter = deliveryAdapter;
    }

    public void setPaymentAdapter(PaymentAdapter paymentAdapter) {
        this.paymentAdapter = paymentAdapter;
    }

    public void setAlertManager(AlertManager alertManager) {
        this.alertManager = alertManager;
    }

    public void setStorePermission(StorePermission storePermission) {
        this.storePermission = storePermission;
    }

    public void setStoreMessagesRepository(IStoreMessagesRepository storeMessagesRepository) {
        this.storeMessagesRepository = storeMessagesRepository;
    }

    public void setAddToUserCart(AddToUserCart addToUserCart) {
        this.addToUserCart = addToUserCart;
    }

    public void setBidRepository(IBIDRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setStorePermissionsRepository(IStorePermissionsRepository storePermissionsRepository) {
        this.storePermissionsRepository = storePermissionsRepository;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setPurchaseHistoryRepository(IPurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAuctionRepository(IAuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public void setStoreScore(IStoreScore storeScore) {
        this.storeScore = storeScore;
    }

    public void setPurchasePolicy(PurchasePolicy purchasePolicy) {
        this.purchasePolicy = purchasePolicy;
    }

    private void save(){
        SingletonCollection.getStoreRepository().save();
    }


}
