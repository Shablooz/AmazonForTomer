package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IAuctionRepository;
import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageRepositoryNonPersist;
import BGU.Group13B.backend.Repositories.Interfaces.IBIDRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountsRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Discounts.Discount;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.permissions.DefaultManagerFunctionality;
import BGU.Group13B.backend.storePackage.permissions.DefaultOwnerFunctionality;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.time.LocalDateTime;
import java.util.Set;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Store implements Comparable<Store> {
    private final IProductRepository productRepository;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final AlertManager alertManager;
    private final StorePermission storePermission;
    private final IStoreDiscountsRepository storeDiscounts;
    private final IStoreMessagesRepository storeMessagesRepository;
    private final AddToUserCart addToUserCart;
    private final IBIDRepository bidRepository;
    private final int storeId;


    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private int rank;
    private String storeName;
    private String category;
    private final IAuctionRepository auctionRepository;



    public Store(int storeId, int founderId, String storeName, String category) {
        this.auctionRepository = SingletonCollection.getAuctionRepository();
        this.productRepository = SingletonCollection.getProductRepository();
        this.bidRepository = SingletonCollection.getBidRepository();
        this.deliveryAdapter = SingletonCollection.getDeliveryAdapter();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.alertManager = SingletonCollection.getAlertManager();
        this.addToUserCart = SingletonCollection.getAddToUserCart();
        this.storeMessagesRepository = SingletonCollection.getStoreMessagesRepository();
        this.storeDiscounts = SingletonCollection.getStoreDiscountsRepository();
        this.discountPolicy = new DiscountPolicy();
        this.purchasePolicy = new PurchasePolicy();
        this.storeId = storeId;
        this.storeName = storeName;
        this.category = category;
        this.storePermission = new StorePermission(founderId);
        this.rank = 0;
    }

    //used only for testing
    public Store(int storeId, String storeName, String category, IProductRepository productRepository,
                 PurchasePolicy purchasePolicy, DiscountPolicy discountPolicy, DeliveryAdapter deliveryAdapter,
                 PaymentAdapter paymentAdapter, AlertManager alertManager, StorePermission storePermission,
                 IStoreDiscountsRepository storeDiscounts, AddToUserCart addToUserCart, IBIDRepository bidRepository,
                 StoreMessageRepositoryNonPersist storeMessagesRepository, IAuctionRepository auctionRepository) {
        this.productRepository = productRepository;
        this.purchasePolicy = purchasePolicy;
        this.discountPolicy = discountPolicy;
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.alertManager = alertManager;
        this.storePermission = storePermission;
        this.storeDiscounts = storeDiscounts;
        this.storeMessagesRepository = storeMessagesRepository;
        this.addToUserCart = addToUserCart;
        this.bidRepository = bidRepository;
        this.storeId = storeId;
        this.rank=0;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.storeName = storeName;
        this.category = category;
        this.auctionRepository = auctionRepository;
        this.rank = 0;
    }



    public void sendMassage(Message message,String userName,int userId){
        storeMessagesRepository.sendMassage(message,this.storeId,userName);
    }

    @DefaultOwnerFunctionality
    public Message getUnreadMessages(String userName,int userId)throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userName + " has no permission to read message of store " + this.storeId);

        return storeMessagesRepository.readUnreadMassage(this.storeId,userName);
    }
    @DefaultOwnerFunctionality
    public Message getReadMessages(String userName,int userId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userName + " has no permission to read message of store " + this.storeId);
        return storeMessagesRepository.readReadMassage(this.storeId,userName);
    }
    @DefaultOwnerFunctionality
    public void markAsCompleted(String senderId,int messageId,String userName,int userId) throws NoPermissionException{
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userName + " has no permission to mark message as complete of store: " + this.storeId);
        storeMessagesRepository.markAsRead(senderId,messageId,userName);
    }

    @DefaultOwnerFunctionality
    public void refreshMessages(String userName,int userId) throws NoPermissionException{
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userName + " has no permission to handle message of store " + this.storeId);
        storeMessagesRepository.refreshOldMassage(this.storeId,userName);
    }


    public void addReview(String review, int userId, int productId) {
        if(purchaseHistoryRepository.isPurchase(userId,this.storeId,productId))
            throw new IllegalArgumentException("User with id: "+userId+" did not purchase product with id: "+productId+" from store: "+this.storeId);
      Product product = productRepository.getProduct(productId, this.storeId);
      if(product==null)
          throw new IllegalArgumentException("Product with id: "+productId+" does not exist in store: "+this.storeId);
      product.addReview(review, userId);
    }
    public void removeReview(int userId, int productId) {
        Product product = productRepository.getProduct(productId, this.storeId);
        if(product==null)
            throw new IllegalArgumentException("Product with id: "+productId+" does not exist in store: "+this.storeId);
        product.removeReview(userId);
    }
    public Review getReview(int userId, int productId) {
        Product product = productRepository.getProduct(productId, this.storeId);
        if(product==null)
            throw new IllegalArgumentException("Product with id: "+productId+" does not exist in store: "+this.storeId);
        return product.getReview(userId);
    }
    public float getProductScore(int productId) {
        Product product = productRepository.getProduct(productId, this.storeId);
        if(product==null)
            throw new IllegalArgumentException("Product with id: "+productId+" does not exist in store: "+this.storeId);
        return product.getProductScore();
    }
    public void addAndSetProductScore(int productId,int userId, int score) {
        if(purchaseHistoryRepository.isPurchase(userId,this.storeId,productId))
            throw new IllegalArgumentException("User with id: "+userId+" did not purchase product with id: "+productId+" from store: "+this.storeId);
        Product product = productRepository.getProduct(productId, this.storeId);
        if(product==null)
            throw new IllegalArgumentException("Product with id: "+productId+" does not exist in store: "+this.storeId);
        product.addAndSetScore(userId,score);
    }
    public void removeProductScore(int productId,int userId) {
        Product product = productRepository.getProduct(productId, this.storeId);
        if(product==null)
            throw new IllegalArgumentException("Product with id: "+productId+" does not exist in store: "+this.storeId);
        product.removeProductScore(userId);
    }


    //todo: complete the function
    public void addProduct(Product product, int userId) {


    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void addProduct(int userId, String productName, int quantity, double price) throws NoPermissionException {
        /*
         * check if the user has permission to add product
         * */
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userId + " has no permission to add product to store " + this.storeId);

        /*Product product = new Product(productName, -1*//*todo*//*, price, quantity);
        productRepository.add(product);*/

    }

    public double calculatePriceOfBasket(double totalAmountBeforeStoreDiscountPolicy,
                                         ConcurrentLinkedQueue<BasketProduct> successfulProducts,
                                         String storeCoupon) {
        double totalAmount = totalAmountBeforeStoreDiscountPolicy;
        for (Discount discount : storeDiscounts.getStoreDiscounts(storeId).
                orElseThrow(() -> new RuntimeException("Store with id " + storeId + " does not exist"))) {

            totalAmount = discount.applyStoreDiscount(totalAmount, successfulProducts, storeCoupon);
        }
        return totalAmount;

    }





    public static String getCurrentMethodName() {
        return StorePermission.getFunctionName(
                Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void purchaseProposalSubmit(int userId, int productId, double proposedPrice, int amount) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        if (!this.storePermission.checkPermission(userId))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + userId + " has no permission to add product to store " + this.storeId);

        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        if (proposedPrice <= 0)
            throw new IllegalArgumentException("Price must be positive");

        bidRepository.addBID(userId, productId, proposedPrice, amount);

        /*
         * Send alert to all the permitted Managers
         * */
        Set<Integer> userIds = this.storePermission.getAllUsersWithPermission(Store.getCurrentMethodName());
        for (Integer id : userIds) {//wait for the interface in AlertManager.java to finish
            alertManager.sendAlert(id, "User " + userId + " has submitted a purchase proposal for product " + productId + " in store " + this.storeId);
            //fixme!!!!!!!!!
        }
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void purchaseProposalApprove(int managerId, int bidId) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        if (!this.storePermission.checkPermission(managerId))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + managerId + " has no permission to add product to store " + this.storeId);
        BID currentBid = bidRepository.getBID(bidId).
                orElseThrow(() -> new IllegalArgumentException("There is no such bid for store " + this.storeId));

        if (currentBid.isRejected())
            alertManager.sendAlert(managerId, "The bid for product " + currentBid.getProductId() + " in store " + this.storeId + " has been rejected already");
        currentBid.approve(managerId);
        Set<Integer> managers = storePermission.getAllUsersWithPermission("purchaseProposalSubmit");

        if (currentBid.approvedByAll(managers)) {
            addToUserCart.apply(currentBid.getUserId(), storeId, currentBid.getProductId(), currentBid.getAmount());
        }
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void purchaseProposalReject(int managerId, int bidId) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        if (!this.storePermission.checkPermission(managerId))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + managerId + " has no permission to reject a purchase proposal in the store: " + this.storeId);
        BID currentBid = bidRepository.getBID(bidId).orElseThrow(() -> new IllegalArgumentException("There is no such bid for store " + this.storeId));
        currentBid.reject();//good for concurrency edge cases
        bidRepository.removeBID(bidId);
    }

    @Override
    public int compareTo(Store o) {
        return Integer.compare(this.storeId, o.storeId);
    }

    public int getRank() {
        return rank;
    }

    //only members of the store can create an auction purchase
    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void auctionPurchase(int userId, int productId, double newPrice) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userId + " has no permission to create an auction purchase in the store: " + this.storeId);
        auctionRepository.updateAuction(productId, this.storeId, newPrice, userId);
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public PublicAuctionInfo getAuctionInfo(int userId, int productId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId))
            throw new NoPermissionException("User " + userId + " has no permission to get auction info in the store: " + this.storeId);
        return auctionRepository.getAuctionInfo(productId, this.storeId).orElseThrow(() ->
                new IllegalArgumentException("There is no such auction for product " + productId + " in store " + this.storeId));
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void createAuctionForProduct(int storeManagerId, int productId, double startingPrice, LocalDateTime lastDate) throws NoPermissionException {
        if (!this.storePermission.checkPermission(storeManagerId))
            throw new NoPermissionException("User " + storeManagerId + " has no permission to create an auction in the store: " + this.storeId);
        auctionRepository.addNewAuctionForAProduct(productId, startingPrice, this.storeId, lastDate);
    }
}
