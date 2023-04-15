package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.CartRepositoryImpl.CartRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl.MessageRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl.PurchaseHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.storePackage.AlertManager;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.callbacks.AddToUserCart;

//(#61)
public class SingletonCollection {

    //repositories
    private static final IBIDRepository bidRepository;
    private static final ICartRepository cartRepository;
    private static final IMessageRepository messageRepository;
    private static final IProductRepository productRepository;
    private static final IPurchaseHistoryRepository purchaseHistoryRepository;
    private static final IRepositoryReview reviewRepository;
    private static final IStoreMessagesRepository storeMessagesRepository;
    private static final IStoreRepository storeRepository;
    private static final IUserRepository userRepository;


    //callbacks
    private static final AddToUserCart addToUserCart;


    //adapters
    private static final DeliveryAdapter deliveryAdapter;
    private static final PaymentAdapter paymentAdapter;


    //additional classes
    private static final AlertManager alertManager;


    //static initializer
    static {
        //repositories
        bidRepository = new BIDRepositoryAsList();
        cartRepository = new CartRepositoryAsList();                        //TODO
        messageRepository = null;                                           //TODO
        productRepository = new ProductRepositoryAsList();                  //TODO
        purchaseHistoryRepository = new PurchaseHistoryRepositoryAsList();  //TODO
        reviewRepository = new ReviewRepositoryAsList();
        storeMessagesRepository = null;                                     //TODO
        storeRepository = new StoreRepositoryAsList();                      //TODO
        userRepository = new UserRepositoryAsHashmap();


        //callbacks
        addToUserCart = null;   //TODO


        //adapters
        deliveryAdapter = null; //TODO
        paymentAdapter = null;  //TODO


        //additional classes
        alertManager = new AlertManager(userRepository);
    }

    private SingletonCollection() {
    }


    //getters
    public static AddToUserCart getAddToUserCart() {
        return addToUserCart;
    }

    public static AlertManager getAlertManager() {
        return alertManager;
    }

    public static DeliveryAdapter getDeliveryAdapter() {
        return deliveryAdapter;
    }

    public static IBIDRepository getBidRepository() {
        return bidRepository;
    }

    public static ICartRepository getCartRepository() {
        return cartRepository;
    }

    public static IMessageRepository getMessageRepository() {
        return messageRepository;
    }

    public static IProductRepository getProductRepository() {
        return productRepository;
    }

    public static IPurchaseHistoryRepository getPurchaseHistoryRepository() {
        return purchaseHistoryRepository;
    }

    public static IRepositoryReview getReviewRepository() {
        return reviewRepository;
    }

    public static IStoreMessagesRepository getStoreMessagesRepository() {
        return storeMessagesRepository;
    }

    public static IStoreRepository getStoreRepository() {
        return storeRepository;
    }

    public static IUserRepository getUserRepository() {
        return userRepository;
    }

    public static PaymentAdapter getPaymentAdapter() {
        return paymentAdapter;
    }
}

