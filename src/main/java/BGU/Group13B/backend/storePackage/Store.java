package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageRepositoryNonPersist;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.discountPolicies.StoreDiscountPolicy;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.permissions.DefaultManagerFunctionality;
import BGU.Group13B.backend.storePackage.permissions.DefaultOwnerFunctionality;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.backend.storePackage.permissions.*;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;
import BGU.Group13B.service.info.StoreInfo;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;


public class Store {
    private final IProductRepository productRepository;
    private final IStorePurchasePolicyRepository storePurchasePolicyRepository;
    private final IProductPurchasePolicyRepository productPurchasePolicyRepository;
    private final StoreDiscountPolicy discountPolicy;
    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final AlertManager alertManager;
    private final StorePermission storePermission;
    private final IStoreMessagesRepository storeMessagesRepository;
    private final AddToUserCart addToUserCart;
    private final IBIDRepository bidRepository;
    private final IUserRepository userRepository;
    private final IStorePermissionsRepository storePermissionsRepository;
    private final IUserPermissionRepository userPermissionRepository;
    private final int storeId;


    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    private String storeName;
    private String category;
    private final IAuctionRepository auctionRepository;
    private IStoreScore storeScore;

    private boolean hidden = false;

    /*
    * when purchase policy conflicts are allowed, a product can have a purchase policy
    * that conflicts with the store purchase policy in a way that makes it so no user can buy the product
    */
    private boolean purchasePolicyConflictsAllowed = true; //conflicts are allowed by default



    public Store(int storeId, int founderId, String storeName, String category) {
        this.auctionRepository = SingletonCollection.getAuctionRepository();
        this.productRepository = SingletonCollection.getProductRepository();
        this.bidRepository = SingletonCollection.getBidRepository();
        this.deliveryAdapter = SingletonCollection.getDeliveryAdapter();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.alertManager = SingletonCollection.getAlertManager();
        this.addToUserCart = SingletonCollection.getAddToUserCart();
        this.storeMessagesRepository = SingletonCollection.getStoreMessagesRepository();
        this.storePurchasePolicyRepository = SingletonCollection.getStorePurchasePolicyRepository();
        this.productPurchasePolicyRepository = SingletonCollection.getProductPurchasePolicyRepository();
        this.discountPolicy = new StoreDiscountPolicy(storeId);
        this.userRepository = SingletonCollection.getUserRepository();
        this.storePermissionsRepository = SingletonCollection.getStorePermissionRepository();
        this.userPermissionRepository = SingletonCollection.getUserPermissionRepository();
        this.storeId = storeId;
        this.storeName = storeName;
        this.category = category;
        StorePermission storePermission1 = storePermissionsRepository.getStorePermission(storeId);
        if(storePermission1 == null){
            storePermission1 = new StorePermission(founderId);
            storePermissionsRepository.addStorePermission(storeId, storePermission1);
        }
        this.storePermission = storePermission1;
        userRepository.getUser(founderId).addPermission(storeId, UserPermissions.StoreRole.FOUNDER);
        this.purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();
        this.storeScore = SingletonCollection.getStoreScoreRepository();

        this.storePurchasePolicyRepository.insertPurchasePolicy(new PurchasePolicy(this.storeId));
    }

    //used only for testing
    public Store(int storeId, String storeName, String category, IProductRepository productRepository,
                 DeliveryAdapter deliveryAdapter, PaymentAdapter paymentAdapter, AlertManager alertManager, StorePermission storePermission,
                 AddToUserCart addToUserCart, IBIDRepository bidRepository,
                 StoreMessageRepositoryNonPersist storeMessagesRepository, IAuctionRepository auctionRepository, IPurchaseHistoryRepository purchaseHistoryRepository,
                 IStoreScore storeScore, IStorePurchasePolicyRepository storePurchasePolicyRepository, IProductPurchasePolicyRepository productPurchasePolicyRepository) {
        this.productRepository = productRepository;
        this.storePurchasePolicyRepository = storePurchasePolicyRepository;
        this.productPurchasePolicyRepository = productPurchasePolicyRepository;
        this.discountPolicy = new StoreDiscountPolicy(storeId);
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
        this.userPermissionRepository = null;
        this.storePermissionsRepository = null;
    }

    @DefaultOwnerFunctionality
    public void addOwner(int userId, int newOwnerId) throws NoPermissionException, ChangePermissionException {
        /*
         * check if the user has permission to add owner
         * */
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add an owner to store " + this.storeId);
        storePermission.addOwnerPermission(newOwnerId, userId);
        userRepository.getUser(newOwnerId).addPermission(storeId, UserPermissions.StoreRole.OWNER);
    }

    @DefaultOwnerFunctionality
    public void removeOwner(int userId, int removeOwnerId) throws NoPermissionException, ChangePermissionException {
        /*
         * check if the user has permission to add owner
         * */
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add an owner to store " + this.storeId);
        List<Integer> removeUsersList = storePermission.removeOwnerPermission(removeOwnerId, userId, false);
        for(Integer removeUserId: removeUsersList){
            userRepository.getUser(removeUserId).deletePermission(storeId);
        }

    }

    @DefaultOwnerFunctionality
    public void addManager(int userId, int newManagerId) throws NoPermissionException, ChangePermissionException {
        /*
         * check if the user has permission to add manager
         * */
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add an manager to store " + this.storeId);
        storePermission.addManagerPermission(newManagerId, userId);
        userRepository.getUser(newManagerId).addPermission(storeId, UserPermissions.StoreRole.MANAGER);
    }

    @DefaultOwnerFunctionality
    public void removeManager(int userId, int removeManagerId) throws NoPermissionException, ChangePermissionException {
        /*
         * check if the user has permission to add manager
         * */
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add a manager to store " + this.storeId);
        storePermission.removeManagerPermission(removeManagerId, userId);
        userRepository.getUser(removeManagerId).deletePermission(storeId);
    }

    @DefaultOwnerFunctionality
    public void getStoreWorkersInfo(int userId) throws NoPermissionException {
        /*
         * check if the user has permission to add manager
         * */
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add an manager to store " + this.storeId);
        List<WorkerCard> workerCards = storePermission.getWorkersInfo(); //TODO: pass it on
    }

    public StorePermission getStorePermission(){
        return storePermission;
    }

    public void sendMassage(Message message, int userId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        storeMessagesRepository.sendMassage(message, this.storeId, userId);
    }

    @DefaultOwnerFunctionality
    public Message getUnreadMessages( int userId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to read message of store " + this.storeId);

        return storeMessagesRepository.readUnreadMassage(this.storeId, userId);
    }

    @DefaultOwnerFunctionality
    public Message getReadMessages(int userId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to read message of store " + this.storeId);
        return storeMessagesRepository.readReadMassage(this.storeId, userId);
    }

    @DefaultOwnerFunctionality
    public void markAsCompleted(String senderId, int messageId, int userId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to mark message as complete of store: " + this.storeId);
        storeMessagesRepository.markAsRead(this.storeId,senderId, messageId, userId);
    }

    @DefaultOwnerFunctionality
    public void refreshMessages(int userId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to handle message of store " + this.storeId);
        storeMessagesRepository.refreshOldMassage(this.storeId, userId);
    }


    public void addReview(String review, int userId, int productId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        if (purchaseHistoryRepository.isPurchase(userId, this.storeId, productId))
            throw new IllegalArgumentException("User with id: " + userId + " did not purchase product with id: " + productId + " from store: " + this.storeId);
        Product product = productRepository.getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.addReview(review, userId);
    }

    public void removeReview(int userId, int productId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        Product product = productRepository.getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.removeReview(userId);
    }

    public Review getReview(int userId, int productId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        Product product = productRepository.getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        return product.getReview(userId);
    }

    public float getProductScore(int productId, int userId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        Product product = productRepository.getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        return product.getProductScore();
    }

    public void addAndSetProductScore(int productId, int userId, int score) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        if (purchaseHistoryRepository.isPurchase(userId, this.storeId, productId))
            throw new IllegalArgumentException("User with id: " + userId + " did not purchase product with id: " + productId + " from store: " + this.storeId);
        Product product = productRepository.getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.addAndSetScore(userId, score);
    }

    public void removeProductScore(int productId, int userId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        Product product = productRepository.getStoreProductById(productId, this.storeId);
        if (product == null)
            throw new IllegalArgumentException("Product with id: " + productId + " does not exist in store: " + this.storeId);
        product.removeProductScore(userId);
    }

    public void addStoreScore(int userId, int score) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        if (purchaseHistoryRepository.isPurchaseFromStore(userId, this.storeId))
            throw new IllegalArgumentException("User with id: " + userId + " did not purchase from store: " + storeId);
        storeScore.addStoreScore(userId, storeId, score);
    }

    public void removeStoreScore(int userId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        storeScore.removeStoreScore(userId, this.storeId);
    }

    public void modifyStoreScore(int userId, int score) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        storeScore.modifyStoreScore(userId, this.storeId, score);
    }

    public float getStoreScore(){
        return storeScore.getStoreScore(this.storeId);
    }


    /*WARNING:default manager functionality is only 4.12: "answer store questions & appeals feature"
       and 4.13: "get store purchase history feature"  **/
    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public synchronized int addProduct(int userId, String productName, String category, double price, int stockQuantity, String description) throws NoPermissionException {
        /*
         * check if the user has permission to add product
         * */
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product to store " + this.storeId);

        if(hidden)
            return this.productRepository.addHiddenProduct(storeId, productName, category, price, stockQuantity, description);

        return this.productRepository.addProduct(storeId, productName, category, price, stockQuantity, description).getProductId();
    }

    public double calculatePriceOfBasket(double totalAmountBeforeStoreDiscountPolicy,
                                         ConcurrentLinkedQueue<BasketProduct> successfulProducts,
                                         String storeCoupon) throws PurchaseExceedsPolicyException {
        if(hidden)
            throw new PurchaseExceedsPolicyException("This Store is hidden");
        int quantity = successfulProducts.stream().mapToInt(BasketProduct::getQuantity).sum();
        double finalPrice = discountPolicy.applyAllDiscounts(totalAmountBeforeStoreDiscountPolicy, successfulProducts, storeCoupon);
        getPurchasePolicy().checkPolicy(quantity, finalPrice);
        return finalPrice;
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
        if (!this.storePermission.checkPermission(userId, hidden))//the user should be loggedIn with permissions
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
        if (!this.storePermission.checkPermission(managerId, hidden))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + managerId + " has no permission to add product to store " + this.storeId);
        BID currentBid = bidRepository.getBID(bidId).
                orElseThrow(() -> new IllegalArgumentException("There is no such bid for store " + this.storeId));

        if (currentBid.isRejected())
            alertManager.sendAlert(managerId, "The bid for product " + currentBid.getProductId() + " in store " + this.storeId + " has been rejected already");
        currentBid.approve(managerId);
        Set<Integer> managers = storePermission.getAllUsersWithPermission("purchaseProposalSubmit");

        if (currentBid.approvedByAll(managers)) {
            addToUserCart.apply(currentBid.getUserId(), storeId, currentBid.getProductId());
        }
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void purchaseProposalReject(int managerId, int bidId) throws NoPermissionException {
        /*
         * check if the user has permission to purchase proposal
         * */
        if (!this.storePermission.checkPermission(managerId, hidden))//the user should be loggedIn with permissions
            throw new NoPermissionException("User " + managerId + " has no permission to reject a purchase proposal in the store: " + this.storeId);
        BID currentBid = bidRepository.getBID(bidId).orElseThrow(() -> new IllegalArgumentException("There is no such bid for store " + this.storeId));
        currentBid.reject();//good for concurrency edge cases
        bidRepository.removeBID(bidId);
    }

    //only members of the store can create an auction purchase
    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void auctionPurchase(int userId, int productId, double newPrice) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to create an auction purchase in the store: " + this.storeId);
        auctionRepository.updateAuction(productId, this.storeId, newPrice, userId);
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public PublicAuctionInfo getAuctionInfo(int userId, int productId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to get auction info in the store: " + this.storeId);
        return auctionRepository.getAuctionInfo(productId, this.storeId).orElseThrow(() ->
                new IllegalArgumentException("There is no such auction for product " + productId + " in store " + this.storeId));
    }

    @DefaultManagerFunctionality
    @DefaultOwnerFunctionality
    public void createAuctionForProduct(int storeManagerId, int productId, double startingPrice, LocalDateTime lastDate) throws NoPermissionException {
        if (!this.storePermission.checkPermission(storeManagerId, hidden))
            throw new NoPermissionException("User " + storeManagerId + " has no permission to create an auction in the store: " + this.storeId);
        auctionRepository.addNewAuctionForAProduct(productId, startingPrice, this.storeId, lastDate);
    }
    @DefaultOwnerFunctionality
    @DefaultManagerFunctionality
    public void endAuctionForProduct(int storeManagerId, int productId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(storeManagerId, hidden))
            throw new NoPermissionException("User " + storeManagerId + " has no permission to remove an auction in the store: " + this.storeId);
        auctionRepository.endAuction(productId, this.storeId);
    }

    public synchronized void isProductAvailable(int productId) throws Exception {
        Product product = productRepository.getStoreProductById(productId, storeId);
        if(product.isHidden())
            throw new Exception("The product is hidden");
        if (product.isOutOfStock())
            throw new Exception("The product is out of stock");
    }

    public int getStoreId() {
        return storeId;
    }

    @DefaultOwnerFunctionality
    public void setProductName(int userId, int productId, String name) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product name in the store: " + this.storeId);

        Product product = this.productRepository.getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setName(name);
        }
    }

    @DefaultOwnerFunctionality
    public void setProductCategory(int userId, int productId, String category) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product category in the store: " + this.storeId);

        Product product = this.productRepository.getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setCategory(category);
        }
    }

    @DefaultOwnerFunctionality
    public void setProductPrice(int userId, int productId, double price) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product price in the store: " + this.storeId);

        Product product = this.productRepository.getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setPrice(price);
        }
    }

    @DefaultOwnerFunctionality
    public void setProductStockQuantity(int userId, int productId, int quantity) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product stock quantity in the store: " + this.storeId);

        Product product = this.productRepository.getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setStockQuantity(quantity);
        }
    }

    @DefaultOwnerFunctionality
    public void setProductDescription(int userId, int productId, String description) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set product description in the store: " + this.storeId);

        Product product = this.productRepository.getStoreProductById(productId, storeId);
        synchronized (product) {
            product.setDescription(description);
        }
    }

    @DefaultOwnerFunctionality
    public void removeProduct(int userId, int productId) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove product in the store: " + this.storeId);

        Product product = this.productRepository.getStoreProductById(productId, storeId);
        synchronized (product) {
            this.productRepository.removeStoreProduct(productId, storeId);
            product.markAsDeleted();
        }
    }

    public String getCategory() {
        return category;
    }

    public String getStoreName() {
        return storeName;
    }

    public StoreInfo getStoreInfo(int userId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        return new StoreInfo(this);
    }

    public Product getStoreProduct(int userId, int productId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        return productRepository.getStoreProductById(productId, storeId);
    }

    public Set<Product> getAllStoreProducts(int userId) throws NoPermissionException {
        this.storePermission.validateStoreVisibility(userId, hidden);
        return productRepository.getStoreProducts(storeId).orElse(new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)));
    }

    private PurchasePolicy getPurchasePolicy() {
        return storePurchasePolicyRepository.getPurchasePolicy(storeId);
    }


    //only the store founder can do this function
    public void allowPurchasePolicyConflicts(int userId) throws NoPermissionException {
        //check user permission
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to allow purchase policy conflicts in the store: " + this.storeId);

        this.purchasePolicyConflictsAllowed = true;
    }

    //only the store founder can do this function
    public void disallowPurchasePolicyConflicts(int userId) throws NoPermissionException {
        //check user permission
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to disallow purchase policy conflicts in the store: " + this.storeId);

        this.purchasePolicyConflictsAllowed = false;
    }

    //only the store founder can do this function
    public void setStorePurchaseQuantityUpperBound(int userId, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase quantity upper bound in the store: " + this.storeId);

        PurchasePolicy storePurchasePolicy = getPurchasePolicy();
        synchronized (storePurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                storePurchasePolicy.setQuantityUpperBound(upperBound);
                return;
            }

            //the store can't have an upper bound lower than a product's lower bound
            storePurchasePolicy.checkConflictsAndSetQuantityUpperBound(
                    upperBound, this.productPurchasePolicyRepository.getStoreProductsMergedPurchaseQuantityBounders(storeId)
            );
        }
    }

    //only the store founder can do this function
    public void setStorePurchaseQuantityLowerBound(int userId, int lowerBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase quantity lower bound in the store: " + this.storeId);

        PurchasePolicy storePurchasePolicy = getPurchasePolicy();
        synchronized (storePurchasePolicy){
            if(purchasePolicyConflictsAllowed){
                storePurchasePolicy.setQuantityLowerBound(lowerBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            storePurchasePolicy.checkConflictsAndSetQuantityLowerBound(
                    lowerBound, this.productPurchasePolicyRepository.getStoreProductsMergedPurchaseQuantityBounders(storeId)
            );
        }
    }

    //only the store founder can do this function
    public void setStorePurchaseQuantityBounds(int userId, int lowerBound, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase quantity bounds in the store: " + this.storeId);

        PurchasePolicy storePurchasePolicy = getPurchasePolicy();
        synchronized (storePurchasePolicy){
            if(purchasePolicyConflictsAllowed){
                storePurchasePolicy.setQuantityBounds(lowerBound, upperBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            //the store can't have an upper bound lower than a product's lower bound
            storePurchasePolicy.checkConflictsAndSetQuantityBounds(
                    lowerBound, upperBound, this.productPurchasePolicyRepository.getStoreProductsMergedPurchaseQuantityBounders(storeId)
            );


        }
    }

    //only the store founder can do this function
    public void setStorePurchasePriceUpperBound(int userId, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase price upper bound in the store: " + this.storeId);

        PurchasePolicy storePurchasePolicy = getPurchasePolicy();
        synchronized (storePurchasePolicy) {
            if (purchasePolicyConflictsAllowed) {
                storePurchasePolicy.setPriceUpperBound(upperBound);
                return;
            }

            //the store can't have an upper bound lower than a product's lower bound
            storePurchasePolicy.checkConflictsAndSetPriceUpperBound(
                    upperBound, this.productPurchasePolicyRepository.getStoreProductsMergedPurchasePriceBounders(storeId)
            );
        }
    }

    //only the store founder can do this function
    public void setStorePurchasePriceLowerBound(int userId, int lowerBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase price lower bound in the store: " + this.storeId);

        PurchasePolicy storePurchasePolicy = getPurchasePolicy();
        synchronized (storePurchasePolicy) {
            if (purchasePolicyConflictsAllowed) {
                storePurchasePolicy.setPriceLowerBound(lowerBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            storePurchasePolicy.checkConflictsAndSetPriceLowerBound(
                    lowerBound, this.productPurchasePolicyRepository.getStoreProductsMergedPurchasePriceBounders(storeId)
            );
        }
    }

    //only the store founder can do this function
    public void setStorePurchasePriceBounds(int userId, int lowerBound, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase price bounds in the store: " + this.storeId);

        PurchasePolicy storePurchasePolicy = getPurchasePolicy();
        synchronized (storePurchasePolicy) {
            if (purchasePolicyConflictsAllowed) {
                storePurchasePolicy.setPriceBounds(lowerBound, upperBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            //the store can't have an upper bound lower than a product's lower bound
            storePurchasePolicy.checkConflictsAndSetPriceBounds(
                    lowerBound, upperBound, this.productPurchasePolicyRepository.getStoreProductsMergedPurchasePriceBounders(storeId)
            );
        }
    }

    //only the store founder can do this function
    public void setProductPurchaseQuantityUpperBound(int userId, int productId, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase quantity upper bound in the store: " + this.storeId);

        PurchasePolicy productPurchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(this.storeId, productId);
        synchronized (productPurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                productPurchasePolicy.setQuantityUpperBound(upperBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            productPurchasePolicy.checkConflictsAndSetQuantityUpperBound(
                    upperBound, this.getPurchasePolicy().getQuantityBounder()
            );
        }
    }

    //only the store founder can do this function
    public void setProductPurchaseQuantityLowerBound(int userId, int productId, int lowerBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase quantity lower bound in the store: " + this.storeId);

        PurchasePolicy productPurchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(this.storeId, productId);
        synchronized (productPurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                productPurchasePolicy.setQuantityLowerBound(lowerBound);
                return;
            }

            //the store can't have an upper bound lower than a product's lower bound
            productPurchasePolicy.checkConflictsAndSetQuantityLowerBound(
                    lowerBound, this.getPurchasePolicy().getQuantityBounder()
            );
        }
    }

    //only the store founder can do this function
    public void setProductPurchaseQuantityBounds(int userId, int productId, int lowerBound, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase quantity bounds in the store: " + this.storeId);

        PurchasePolicy productPurchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(this.storeId, productId);
        synchronized (productPurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                productPurchasePolicy.setQuantityBounds(lowerBound, upperBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            //the store can't have an upper bound lower than a product's lower bound
            productPurchasePolicy.checkConflictsAndSetQuantityBounds(
                    lowerBound, upperBound, this.getPurchasePolicy().getQuantityBounder()
            );
        }
    }

    //only the store founder can do this function
    public void setProductPurchasePriceUpperBound(int userId, int productId, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase price upper bound in the store: " + this.storeId);

        PurchasePolicy productPurchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(this.storeId, productId);
        synchronized (productPurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                productPurchasePolicy.setPriceUpperBound(upperBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            productPurchasePolicy.checkConflictsAndSetPriceUpperBound(
                    upperBound, this.getPurchasePolicy().getPriceBounder()
            );
        }
    }

    //only the store founder can do this function
    public void setProductPurchasePriceLowerBound(int userId, int productId, int lowerBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase price lower bound in the store: " + this.storeId);

        PurchasePolicy productPurchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(this.storeId, productId);
        synchronized (productPurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                productPurchasePolicy.setPriceLowerBound(lowerBound);
                return;
            }

            //the store can't have an upper bound lower than a product's lower bound
            productPurchasePolicy.checkConflictsAndSetPriceLowerBound(
                    lowerBound, this.getPurchasePolicy().getPriceBounder()
            );
        }
    }

    //only the store founder can do this function
    public void setProductPurchasePriceBounds(int userId, int productId, int lowerBound, int upperBound) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to set purchase price bounds in the store: " + this.storeId);

        PurchasePolicy productPurchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(this.storeId, productId);
        synchronized (productPurchasePolicy) {
            if(purchasePolicyConflictsAllowed){
                productPurchasePolicy.setPriceBounds(lowerBound, upperBound);
                return;
            }

            //the store can't have a lower bound higher than a product's upper bound
            //the store can't have an upper bound lower than a product's lower bound
            productPurchasePolicy.checkConflictsAndSetPriceBounds(
                    lowerBound, upperBound, this.getPurchasePolicy().getPriceBounder()
            );
        }
    }

    //only the store founder can do this function
    public int addStoreVisibleDiscount(int userId, double discountPercentage, LocalDateTime discountLastDate) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store visible discount in the store: " + this.storeId);

        return this.discountPolicy.addVisibleDiscount(discountPercentage, discountLastDate);
    }

    //only the store founder can do this function
    public int addStoreConditionalDiscount(int userId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store conditional discount in the store: " + this.storeId);

        return this.discountPolicy.addConditionalDiscount(discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
    }

    //only the store founder can do this function
    public int addStoreHiddenDiscount(int userId, double discountPercentage, LocalDateTime discountLastDate, String code) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add store hidden discount in the store: " + this.storeId);

        return this.discountPolicy.addHiddenDiscount(discountPercentage, discountLastDate, code);
    }

    //only the store founder can do this function
    public void removeStoreDiscount(int userId, int discountId) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove store discount in the store: " + this.storeId);

        this.discountPolicy.removeDiscount(discountId);
    }

    //only the store founder can do this function
    public int addProductVisibleDiscount(int userId, int productId, double discountPercentage, LocalDateTime discountLastDate) throws NoPermissionException{
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product visible discount in the store: " + this.storeId);

        return getStoreProduct(userId, productId).addVisibleDiscount(discountPercentage, discountLastDate);
    }

    //only the store founder can do this function
    public int addProductConditionalDiscount(int userId, int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) throws NoPermissionException{
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product conditional discount in the store: " + this.storeId);

        return getStoreProduct(userId, productId).addConditionalDiscount(discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
    }

    //only the store founder can do this function
    public int addProductHiddenDiscount(int userId, int productId, double discountPercentage, LocalDateTime discountLastDate, String code) throws NoPermissionException {
        if (!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to add product hidden discount in the store: " + this.storeId);

        return getStoreProduct(userId, productId).addHiddenDiscount(discountPercentage, discountLastDate, code);
    }

    //only the store founder can do this function
    public void removeProductDiscount(int userId, int productId, int discountId) throws NoPermissionException{
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to remove product discount in the store: " + this.storeId);

        getStoreProduct(userId, productId).removeDiscount(discountId);
    }

    //only the store founder can do this function
    public synchronized void hideStore(int userId) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to hide the store: " + this.storeId);

        if(this.hidden)
            throw new RuntimeException("The store " + this.storeName + " is already hidden");

        this.hidden = true;
        productRepository.hideAllStoreProducts(this.storeId);

        //notify all store owners and managers
        notifyAllWorkers(userId, "Store Closed", "The store " + this.storeName + " has been hidden");
    }

    //will send a message to all the store's workers except the user with the given id
    private void notifyAllWorkers(int userId, String msgHeader, String msgBody) {
        throw new NotImplementedException("TODO - notifyAllWorkers");
        /* //TODO. the parameters can be changed if needed. make sure to update the function call in the function above (hideStore)
        for(int workerId : this.storePermission.getWorkersInfo().stream().map(WorkerCard::userId).toList()){
            if(workerId != userId)
                sendMassage(workerId, msgHeader, msgBody);
        }
         */
    }

    //only the store founder can do this function
    public synchronized void unhideStore(int userId) throws NoPermissionException {
        if(!this.storePermission.checkPermission(userId, hidden))
            throw new NoPermissionException("User " + userId + " has no permission to unhide the store: " + this.storeId);

        if(!this.hidden)
            throw new RuntimeException("The store " + this.storeName + " isn't hidden");

        this.hidden = false;
        productRepository.unhideAllStoreProducts(this.storeId);

        //notify all store owners and managers
        notifyAllWorkers(userId, "Store Closed", "The store " + this.storeName + " has been hidden");
    }


    public boolean isHidden() {
        return hidden;
    }



    public int getStoreFounder() {
        return storePermission.getStoreFounder(storeId);
    }
}
