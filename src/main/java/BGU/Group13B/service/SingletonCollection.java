package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl.AuctionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl.BasketProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository.StoreScoreImplNotPer;
import BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository.StoreScoreSingle;
import BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl.MessageRepositorySingle;
import BGU.Group13B.backend.Repositories.Implementations.ProductDiscountsRepositoryImpl.ProductDiscountsRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ProductHistoryRepositoryImpl.ProductHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ProductPurchasePolicyRepositoryImpl.ProductPurchasePolicyRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl.PurchaseHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountsRepositoryImpl.StoreDiscountsRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.StorePurchasePolicyRepositoryImpl.StorePurchasePolicyRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageSingle;
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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
    private static final IBasketProductRepository basketProductRepository;
    private static final IStoreDiscountsRepository storeDiscountsRepository;
    private static final IAuctionRepository auctionRepository;
    private static final IProductHistoryRepository productHistoryRepository;
    private static final IProductDiscountsRepository productDiscountsRepository;
    private static final IStoreScore storeScoreRepository;
    private static final IProductPurchasePolicyRepository productPurchasePolicyRepository;
    private static final IStorePurchasePolicyRepository storePurchasePolicyRepository;


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

    private static final String LOG_FILE_NAME = "logger_output.txt";

    //static initializer
    static {

        //repositories
        bidRepository = new BIDRepositoryAsList();
        productRepository = new ProductRepositoryAsHashMap();
        purchaseHistoryRepository = new PurchaseHistoryRepositoryAsList();
        storeRepository = new StoreRepositoryAsList();
        userRepository = new UserRepositoryAsHashmap();
        basketRepository = new BasketRepositoryAsHashMap();
        storeDiscountsRepository = new StoreDiscountsRepositoryAsHashMap();
        auctionRepository = new AuctionRepositoryAsHashMap();
        basketProductRepository = new BasketProductRepositoryAsHashMap();
        productHistoryRepository = new ProductHistoryRepositoryAsList();
        productDiscountsRepository = new ProductDiscountsRepositoryAsHashMap();
        productPurchasePolicyRepository = new ProductPurchasePolicyRepositoryAsHashMap();
        storePurchasePolicyRepository = new StorePurchasePolicyRepositoryAsList();
        storeMessagesRepository = new StoreMessageSingle();
        reviewRepository = new ReviewRepoSingle();
        messageRepository = new MessageRepositorySingle();
        storeScoreRepository = new StoreScoreSingle();


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

    public static IBasketProductRepository getBasketProductRepository() { return basketProductRepository; }

    public static IProductHistoryRepository getProductHistoryRepository() { return productHistoryRepository;}

    public static IStoreScore getStoreScoreRepository() { return storeScoreRepository; }

    public static IProductDiscountsRepository getProductDiscountsRepository() {
        return SingletonCollection.productDiscountsRepository;
    }

    public static IProductPurchasePolicyRepository getProductPurchasePolicyRepository() {
        return productPurchasePolicyRepository;
    }

    public static IStorePurchasePolicyRepository getStorePurchasePolicyRepository() {
        return storePurchasePolicyRepository;
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


    /**
     * <h1>LOGGER</h1>
     */
    public static void setFileHandler(Logger LOGGER) {
        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE_NAME);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.out.println("Error setting the logger handler: " + e.getMessage());
        }
    }


}

