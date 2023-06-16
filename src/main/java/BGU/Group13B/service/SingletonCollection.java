package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl.AuctionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl.BasketProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl.DiscountAccumulationRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl.DiscountAccumulationRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl.DiscountRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl.DiscountRepositoryService;
import BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository.StoreScoreSingle;
import BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl.MessageRepositorySingle;
import BGU.Group13B.backend.Repositories.Implementations.ProductHistoryRepositoryImpl.ProductHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl.PurchaseHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.PurchasePolicyRootsRepositoryImpl.PurchasePolicyRootsRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingleService;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootsRepositoryAsHashMap;
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
import org.springframework.context.ConfigurableApplicationContext;

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
    private static IAuctionRepository auctionRepository;
    private static IProductHistoryRepository productHistoryRepository;
    private static IStoreScore storeScoreRepository;
    private static IUserPermissionRepository userPermissionRepository;
    private static IStorePermissionsRepository storePermissionRepository;
    private static IDiscountRepository discountRepository;
    private static IDiscountAccumulationRepository discountAccumulationRepository;
    private static IConditionRepository conditionRepository;
    private static IStoreDiscountRootsRepository storeDiscountRootsRepository;
    private static IPurchasePolicyRootsRepository purchasePolicyRootsRepository;
    private static ConfigurableApplicationContext context;
    // set context
    public static void setContext(ConfigurableApplicationContext updated)
    {
        context=updated;
    }
    public static ConfigurableApplicationContext getContext()
    {
        return context;
    }

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
    private static Session session;

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
        auctionRepository = new AuctionRepositoryAsHashMap();
        basketProductRepository = new BasketProductRepositoryAsHashMap();
        productHistoryRepository = new ProductHistoryRepositoryAsList();
        storeMessagesRepository = new StoreMessageSingle();
        reviewRepository = new ReviewRepoSingle();
        messageRepository = new MessageRepositorySingle();
        storeScoreRepository = new StoreScoreSingle();
        userPermissionRepository = new UserPermissionRepositoryAsHashmap();
        storePermissionRepository = new StorePermissionsRepositoryAsHashmap();
        discountRepository = new DiscountRepositoryAsHashMap();
        discountAccumulationRepository = new DiscountAccumulationRepositoryAsHashMap();
        conditionRepository = new ConditionRepositoryAsHashMap();
        storeDiscountRootsRepository = new StoreDiscountRootsRepositoryAsHashMap();
        purchasePolicyRootsRepository = new PurchasePolicyRootsRepositoryAsHashMap();


        //adapters
        deliveryAdapter = new PaymentAPI();
        paymentAdapter = new PaymentAPI();


        //additional classes
        alertManager = new AlertManager(userRepository);
        searcher = new Searcher(productRepository, storeRepository);
        market = new Market();
        //session = new Session(market);//super bug sad shaun at 3:38 am

    }

    public static void reset_system() {
        //repositories
        bidRepository = new BIDRepositoryAsList();
        productRepository = new ProductRepositoryAsHashMap();
        purchaseHistoryRepository = new PurchaseHistoryRepositoryAsList();
        storeRepository = new StoreRepositoryAsList();
        userRepository = new UserRepositoryAsHashmap();
        basketRepository = new BasketRepositoryAsHashMap();
        auctionRepository = new AuctionRepositoryAsHashMap();
        basketProductRepository = new BasketProductRepositoryAsHashMap();
        productHistoryRepository = new ProductHistoryRepositoryAsList();
        storeMessagesRepository = new StoreMessageSingle();
        reviewRepository = new ReviewRepoSingle();
        messageRepository = new MessageRepositorySingle();
        storeScoreRepository = new StoreScoreSingle();
        userPermissionRepository = new UserPermissionRepositoryAsHashmap();
        storePermissionRepository = new StorePermissionsRepositoryAsHashmap();
        discountRepository = new DiscountRepositoryAsHashMap();
        discountAccumulationRepository = new DiscountAccumulationRepositoryAsHashMap();
        conditionRepository = new ConditionRepositoryAsHashMap();
        storeDiscountRootsRepository = new StoreDiscountRootsRepositoryAsHashMap();
        purchasePolicyRootsRepository = new PurchasePolicyRootsRepositoryAsHashMap();
        //adapters


        //additional classes
        alertManager = new AlertManager(userRepository);
        searcher = new Searcher(productRepository, storeRepository);
        market = new Market();
        session = new Session(market);
    }

    private SingletonCollection() {
    }

    public static void setProductRepository() {
        ProductRepositoryAsHashMap productRepositoryAsHashMap = SingletonCollection.getContext().getBean(ProductRepositoryAsHashMapService.class).getProductRepositoryAsHashMapJPA();
        if(productRepositoryAsHashMap == null){
            ProductRepositoryAsHashMap repo=(ProductRepositoryAsHashMap) SingletonCollection.getProductRepository();
            SingletonCollection.getContext().getBean(ProductRepositoryAsHashMapService.class).save(repo);
        }else{
            SingletonCollection.productRepository = SingletonCollection.getContext().getBean(ProductRepositoryAsHashMapService.class).getProductRepositoryAsHashMapJPA();
        }
    }


    public static void setReviewRepository() {
        ReviewRepoSingle reviewRepoSingle = SingletonCollection.getContext().getBean(ReviewRepoSingleService.class).getReviewRepoSingleJPA();
        if (reviewRepoSingle == null) {
            ReviewRepoSingle repo = (ReviewRepoSingle) SingletonCollection.getReviewRepository();
            SingletonCollection.getContext().getBean(ReviewRepoSingleService.class).save(repo);
        } else {
            SingletonCollection.reviewRepository = SingletonCollection.getContext().getBean(ReviewRepoSingleService.class).getReviewRepoSingleJPA();
        }
    }

    public static void setConditionRepository() {
        ConditionRepositoryAsHashMap conditionRepositoryAsHashMap = SingletonCollection.getContext().getBean(ConditionRepositoryAsHashMapService.class).getConditionRepositoryAsHashMap();
        if(conditionRepositoryAsHashMap == null){
            ConditionRepositoryAsHashMap repo=(ConditionRepositoryAsHashMap) SingletonCollection.getConditionRepository();
            SingletonCollection.getContext().getBean(ConditionRepositoryAsHashMapService.class).save(repo);
        }else{
            SingletonCollection.conditionRepository = SingletonCollection.getContext().getBean(ConditionRepositoryAsHashMapService.class).getConditionRepositoryAsHashMap();
        }
    }

    public static void setDiscountaccuRepository() {
        DiscountAccumulationRepositoryAsHashMap discountAccumulationRepositoryAsHashMap = SingletonCollection.getContext().getBean(DiscountAccumulationRepositoryAsHashMapService.class).getDiscountAccumulationAsHashMap();
        if(discountAccumulationRepositoryAsHashMap == null){
            DiscountAccumulationRepositoryAsHashMap repo=(DiscountAccumulationRepositoryAsHashMap) SingletonCollection.getDiscountAccumulationRepository();
            SingletonCollection.getContext().getBean(DiscountAccumulationRepositoryAsHashMapService.class).save(repo);
        }else{
            SingletonCollection.discountAccumulationRepository = SingletonCollection.getContext().getBean(DiscountAccumulationRepositoryAsHashMapService.class).getDiscountAccumulationAsHashMap();
        }
    }

    public static void setDiscountRepository() {
        DiscountRepositoryAsHashMap discountRepositoryAsHashMap = SingletonCollection.getContext().getBean(DiscountRepositoryService.class).getDiscountRepositoryAsHashmap();
        if(discountRepositoryAsHashMap == null){
            DiscountRepositoryAsHashMap repo=(DiscountRepositoryAsHashMap) SingletonCollection.getDiscountRepository();
            SingletonCollection.getContext().getBean(DiscountRepositoryService.class).save(repo);
        }else{
            SingletonCollection.discountRepository = SingletonCollection.getContext().getBean(DiscountRepositoryService.class).getDiscountRepositoryAsHashmap();
        }
    }


    public static void setStoreDiscountRootsRepository() {
        StoreDiscountRootsRepositoryAsHashMap storeDiscountRootsRepositoryAsHashMap = SingletonCollection.getContext().getBean(StoreDiscountRootRepositoryAsHashMapService.class).getStoreDiscountRepository();
        if(storeDiscountRootsRepositoryAsHashMap == null){
            StoreDiscountRootsRepositoryAsHashMap repo=(StoreDiscountRootsRepositoryAsHashMap) SingletonCollection.getStoreDiscountRootsRepository();
            SingletonCollection.getContext().getBean(StoreDiscountRootRepositoryAsHashMapService.class).save(repo);
        }else{
            SingletonCollection.storeDiscountRootsRepository = SingletonCollection.getContext().getBean(StoreDiscountRootRepositoryAsHashMapService.class).getStoreDiscountRepository();
        }
    }

    //lines below might need to be replaced with a field
    public static ProductRepositoryAsHashMapService getProductRepositoryAsHashMapService() {
        return SingletonCollection.getContext().getBean(ProductRepositoryAsHashMapService.class);
    }

    public static ReviewRepoSingleService getReviewRepoSingleService() {
        return SingletonCollection.getContext().getBean(ReviewRepoSingleService.class);
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


    public static IAuctionRepository getAuctionRepository() {
        return auctionRepository;
    }

    public static IBasketProductRepository getBasketProductRepository() {
        return basketProductRepository;
    }

    public static IProductHistoryRepository getProductHistoryRepository() {
        return productHistoryRepository;
    }

    public static IStoreScore getStoreScoreRepository() {
        return storeScoreRepository;
    }


    public static IStorePermissionsRepository getStorePermissionRepository() {
        return storePermissionRepository;
    }

    public static IUserPermissionRepository getUserPermissionRepository() {
        return userPermissionRepository;
    }

    public static IDiscountRepository getDiscountRepository() {
        return discountRepository;
    }

    public static IDiscountAccumulationRepository getDiscountAccumulationRepository() {

        return discountAccumulationRepository;
    }

    public static IConditionRepository getConditionRepository() {
        return conditionRepository;
    }

    public static IStoreDiscountRootsRepository getStoreDiscountRootsRepository() {
        return storeDiscountRootsRepository;
    }

    public static Session getSession() {
        if (session == null)
            session = new Session(market == null ? new Market() : market);
        return session;
    }

    public static IPurchasePolicyRootsRepository getPurchasePolicyRootsRepository() {
        return purchasePolicyRootsRepository;
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
     */
    public static void resetAll() {
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

    public static void setSaveMode(boolean saveMode){
        productRepository.setSaveMode(saveMode);
        discountAccumulationRepository.setSaveMode(saveMode);
        conditionRepository.setSaveMode(saveMode);
        discountRepository.setSaveMode(saveMode);
        storeDiscountRootsRepository.setSaveMode(saveMode);
    }
    public static void setPaymentFail() {
        paymentAdapter = null;
    }

    public static void setDiscountRepository(DiscountRepositoryAsHashMap discountRepositoryAsHashMap){
        SingletonCollection.discountRepository = discountRepositoryAsHashMap;
    }


}

