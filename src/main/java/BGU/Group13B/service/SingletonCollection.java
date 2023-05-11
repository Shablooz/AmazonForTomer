package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl.AuctionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl.BasketProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryAsHashMap;
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
import BGU.Group13B.backend.Repositories.Implementations.StorePermissionsRepositoryImpl.StorePermissionsRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.UserPemissionRepositoryImpl.UserPermissionRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Implementations.UserRepositoryImpl.UserRepositoryAsHashmap;
import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.storePackage.AlertManager;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAPI;
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
    private static IBIDRepository bidRepository;
    private static IMessageRepository messageRepository;
    private static IProductRepository productRepository;
    private static IPurchaseHistoryRepository purchaseHistoryRepository;
    private static IRepositoryReview reviewRepository;
    private static IStoreMessagesRepository storeMessagesRepository;
    private static IStoreRepository storeRepository;
    private static IUserRepository userRepository;
    private static IBasketRepository basketRepository;
    private static IBasketProductRepository basketProductRepository;
    private static IStoreDiscountsRepository storeDiscountsRepository;
    private static IAuctionRepository auctionRepository;
    private static IProductHistoryRepository productHistoryRepository;
    private static IProductDiscountsRepository productDiscountsRepository;
    private static IStoreScore storeScoreRepository;
    private static IProductPurchasePolicyRepository productPurchasePolicyRepository;
    private static IStorePurchasePolicyRepository storePurchasePolicyRepository;
    private static IUserPermissionRepository userPermissionRepository;
    private static IStorePermissionsRepository storePermissionRepository;


    /**
     * <h1>callbacks</h1>
     */
    private static AddToUserCart addToUserCart;
    private static CalculatePriceOfBasket calculatePriceOfBasket;


    /**
     * <h1>adapters</h1>
     */
    private static DeliveryAdapter deliveryAdapter;
    private static PaymentAdapter paymentAdapter;


    /**
     * <h1>additional classes</h1>
     */
    private static AlertManager alertManager;
    private static Searcher searcher;
    private static Market market;

    private static final String LOG_FILE_NAME = "info_logger.txt";
    private static final String ERROR_LOG_FILE_NAME = "error_logger.txt";

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

        userPermissionRepository = new UserPermissionRepositoryAsHashmap();
        storePermissionRepository = new StorePermissionsRepositoryAsHashmap();

        //adapters
        deliveryAdapter = new PaymentAPI();
        paymentAdapter = new PaymentAPI();



        //additional classes
        alertManager = new AlertManager(userRepository);
        searcher = new Searcher(productRepository, storeRepository);
        market = new Market();
    }

    public static void reset_system()
    {
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
        userPermissionRepository = new UserPermissionRepositoryAsHashmap();
        storePermissionRepository = new StorePermissionsRepositoryAsHashmap();


        //adapters
        deliveryAdapter = null; //TODO


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

    public static IStorePermissionsRepository getStorePermissionRepository(){
        return storePermissionRepository;
    }

    public static IUserPermissionRepository getUserPermissionRepository(){
        return userPermissionRepository;
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
    public static void setFileHandler(Logger LOGGER, boolean info) {
        try {
            FileHandler fileHandler = new FileHandler(info ? LOG_FILE_NAME : ERROR_LOG_FILE_NAME);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.out.println("Error setting the logger handler: " + e.getMessage());
        }
    }


    /**
     * <h1>reset</h1>
     *
     * */
    public static void resetAll(){
        /*bidRepository.reset();
        productRepository.reset();
        purchaseHistoryRepository.reset();
        storeRepository.reset();
        userRepository.reset();
        basketRepository.reset();
        storeDiscountsRepository.reset();
        auctionRepository.reset();
        basketProductRepository.reset();
        productHistoryRepository.reset();
        productDiscountsRepository.reset();
        productPurchasePolicyRepository.reset();
        storePurchasePolicyRepository.reset();
        storeMessagesRepository.reset();
        reviewRepository.reset();
        messageRepository.reset();
        storeScoreRepository.reset();
        userPermissionRepository.reset();
        storePermissionRepository.reset();*/
    }

    public static void setPaymentFail() {
        paymentAdapter = null;
    }
}

