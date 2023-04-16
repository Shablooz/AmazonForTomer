package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl.AuctionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl.MessageRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl.PurchaseHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountsRepositoryImpl.StoreDiscountsRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageRepositoryNonPersist;
import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.storePackage.AlertManager;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.callbacks.AddToUserCart;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;

//(#61)
public class SingletonCollection {

    /**
     * <h1>repositories</h1>
     */
    private static final IBIDRepository bidRepository;
    private static final IMessageRepository messageRepository;
    private static final IProductRepository productRepository;
    private static final IPurchaseHistoryRepository purchaseHistoryRepository;
    private static final IRepositoryReview reviewRepository;
    private static final IStoreMessagesRepository storeMessagesRepository;
    private static final IStoreRepository storeRepository;
    private static final IUserRepository userRepository;
    private static final IBasketRepository basketRepository;
    private static final IStoreDiscountsRepository storeDiscountsRepository;
    private static final IAuctionRepository auctionRepository;


    /**
     * <h1>callbacks</h1>
     */
    private static AddToUserCart addToUserCart;
    private static CalculatePriceOfBasket calculatePriceOfBasket;


    /**
     * <h1>adapters</h1>
     */
    private static final DeliveryAdapter deliveryAdapter;
    private static final PaymentAdapter paymentAdapter;


    /**
     * <h1>additional classes</h1>
     */
    private static final AlertManager alertManager;
    private static final Searcher searcher;
    private static final Market market;


    //static initializer
    static {
        //repositories
        bidRepository = new BIDRepositoryAsList();
        messageRepository = new MessageRepositoryAsList();
        productRepository = new ProductRepositoryAsHashMap();
        purchaseHistoryRepository = new PurchaseHistoryRepositoryAsList();
        reviewRepository = new ReviewRepositoryAsList();
        storeMessagesRepository = new StoreMessageRepositoryNonPersist();
        storeRepository = new StoreRepositoryAsList();
        userRepository = new UserRepositoryAsHashmap();
        basketRepository = new BasketRepositoryAsHashMap();
        storeDiscountsRepository = new StoreDiscountsRepositoryAsHashMap();
        auctionRepository = new AuctionRepositoryAsHashMap();


        //adapters
        deliveryAdapter = null; //TODO
        paymentAdapter = null;  //TODO


        //additional classes
        alertManager = new AlertManager(userRepository);
        searcher = new Searcher(productRepository, storeRepository);
        market = new Market();
    }

    private SingletonCollection() {
    }


    /**
     * <h1>getters</h1>
     */
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

    public static Searcher getSearcher() {
        return searcher;
    }

    public static CalculatePriceOfBasket getCalculatePriceOfBasket() {
        return calculatePriceOfBasket;
    }

    public static IBasketRepository getBasketRepository() {
        return basketRepository;
    }

    public static Market getMarket() {
        return market;
    }

    public static IStoreDiscountsRepository getStoreDiscountsRepository() {
        return storeDiscountsRepository;
    }

    public static IAuctionRepository getAuctionRepository() {
        return auctionRepository;
    }

    /**
     * <h1>setters (for callbacks)</h1>
     */
    public static void setAddToUserCart(AddToUserCart addToUserCart) {
        SingletonCollection.addToUserCart = addToUserCart;
    }

    public static void setCalculatePriceOfBasket(CalculatePriceOfBasket calculatePriceOfBasket) {
        SingletonCollection.calculatePriceOfBasket = calculatePriceOfBasket;
    }


}

