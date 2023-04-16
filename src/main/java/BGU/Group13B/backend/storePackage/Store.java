package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageRepositoryNonPersist;
import BGU.Group13B.backend.Repositories.Interfaces.IBIDRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreMessagesRepository;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.permissions.*;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.util.Set;

import java.util.List;

public class Store implements Comparable<Store> {
    private final IProductRepository productRepository;
    private final PurchasePolicy purchasePolicy;
    private final DiscountPolicy discountPolicy;
    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final AlertManager alertManager;
    private final StorePermission storePermission;
    private final IStoreMessagesRepository storeMessagesRepository;
    private final AddToUserCart addToUserCart;
    private final IBIDRepository bidRepository;

    private int rank;
    private final int storeId;
    private String storeName;
    private String category;

    public Store(int storeId, int founderId, String storeName, String category) {
        this.productRepository = SingletonCollection.getProductRepository();
        this.bidRepository = SingletonCollection.getBidRepository();
        this.deliveryAdapter = SingletonCollection.getDeliveryAdapter();
        this.paymentAdapter = SingletonCollection.getPaymentAdapter();
        this.alertManager = SingletonCollection.getAlertManager();
        this.addToUserCart = SingletonCollection.getAddToUserCart();
        this.storeMessagesRepository = SingletonCollection.getStoreMessagesRepository();
        this.discountPolicy = new DiscountPolicy();
        this.purchasePolicy = new PurchasePolicy();
        this.storeId = storeId;
        this.storeName = storeName;
        this.category = category;
        this.storePermission = new StorePermission(founderId);
        this.rank = 0;
    }

    //used only for testing
    public Store(int storeId, String storeName, String category, StorePermission storePermission,
                 IProductRepository productRepository, IBIDRepository bidRepository, DeliveryAdapter deliveryAdapter,
                 PaymentAdapter paymentAdapter, AlertManager alertManager, AddToUserCart addToUserCart,
                 DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy, StoreMessageRepositoryNonPersist storeMessagesRepository){
        this.productRepository = productRepository;
        this.purchasePolicy = purchasePolicy;
        this.discountPolicy = discountPolicy;
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.alertManager = alertManager;
        this.storePermission = storePermission;
        this.addToUserCart = addToUserCart;
        this.bidRepository = bidRepository;
        this.storeMessagesRepository = storeMessagesRepository;
        this.storeId = storeId;
        this.storeName = storeName;
        this.category = category;
        this.rank = 0;
        this.rank=0;
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

        Product product = new Product(productName, -1/*todo*/, price, quantity);
        productRepository.add(product);
    }


    public void sendMassage(Message message,String userName) { //need to check how to send message back to the user
        //TODO: need to check permission only registered user can send massage
        storeMessagesRepository.sendMassage(message,this.storeId,userName);
    }
    public Message getUnreadMessages(String userName) {
        //TODO: need to check permission only store owner can read massage
        return storeMessagesRepository.readUnreadMassage(this.storeId,userName);
    }
    public void markAsCompleted(String senderId,int messageId,String userName) {
        //TODO: need to check permission
        storeMessagesRepository.markAsRead(senderId,messageId,userName);
    }

    public void refreshMessages(String userName) {
        storeMessagesRepository.refreshOldMassage(this.storeId,userName);
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
            //throw new IllegalArgumentException("The bid for product " + currentBid.getProductId() + " in store " + this.storeId + " has been rejected already");
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
}
