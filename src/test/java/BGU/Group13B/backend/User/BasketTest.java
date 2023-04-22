package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl.BasketProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Discounts.VisibleDiscount;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class BasketTest {
    private Basket basket;
    private static int userId;
    private static int storeId;
    private static IBasketProductRepository basketProductRepository;
    private PaymentAdapter paymentAdapter;
    private IProductHistoryRepository productHistoryRepository;
    private CalculatePriceOfBasket calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
    private static IProductRepository productRepository;
    private static int productId1;
    private static int productId2;

    @BeforeEach
    void setUp() {
        Market market = new Market();
        /*Mockito.when(SingletonCollection.getProductRepository()).
                thenReturn(Mockito.mock(IProductRepository.class));*/ //remember

        productRepository = SingletonCollection.getProductRepository();

        basketProductRepository = new BasketProductRepositoryAsHashMap();

        //initCalculatePriceOfBasket(market);
        userId = 1;
        storeId = SingletonCollection.getStoreRepository().addStore(userId, "store1", "category1");

        initProducts();
        //total price before 25.0
        SingletonCollection.getProductDiscountsRepository().addProductDiscount(productId1,
                new VisibleDiscount(0, 0.2, LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        SingletonCollection.getProductDiscountsRepository().addProductDiscount(productId2,
                new VisibleDiscount(0, 0.2, LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));

        //total price after product discount 20.0
        SingletonCollection.getStoreDiscountsRepository().addStoreDiscount(storeId,
                new VisibleDiscount(0, 0.1, LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        //total price after store discount 18.0

        paymentAdapter = Mockito.mock(PaymentAdapter.class);
        productHistoryRepository = Mockito.mock(IProductHistoryRepository.class);
        initBasket();

        paymentAndHistoryBehaviour();
    }

    private void paymentAndHistoryBehaviour() {
        Mockito.doNothing().when(productHistoryRepository).addProductToHistory(
                Mockito.any(BasketProduct.class), Mockito.anyInt());
    }

    private void payBehaviour(boolean success) {
        Mockito.when(paymentAdapter.pay(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyDouble())).thenReturn(success);
    }

    private void initBasket() {
        basket = new Basket(userId, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket);
    }

    private void initProducts() {
        productId1 = productRepository.addProduct(storeId, "product1", "category1", 10.0, 1, "description1");
        productId2 = productRepository.addProduct(storeId, "product2", "category2", 15.0, 1, "eyal was here");

        basketProductRepository.addNewProductToBasket(productId1, storeId, userId);//adding product 0 to basket
        basketProductRepository.addNewProductToBasket(productId2, storeId, userId);//adding product 1 to basket
    }

    private void initCalculatePriceOfBasket(Market market) {
        final Method method;//effective final
        try {
            method = Market.class.getDeclaredMethod("calculatePriceOfBasket", double.class, ConcurrentLinkedQueue.class, int.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        method.setAccessible(true);

        calculatePriceOfBasket = ((totalAmount, successfulProducts, storeId, storeCoupons) -> {
            try {
                return (double) method.invoke(market, totalAmount, successfulProducts, storeId, storeCoupons);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterEach
    void tearDown() {

    }

    @RepeatedTest(10)
    void twoThreadsTryToPurchaseTheLastProduct() {
        AtomicInteger firstThread = new AtomicInteger(0);
        AtomicReference<Double> pricePayed2 = new AtomicReference<>((double) 0);
        AtomicReference<Double> pricePayed3 = new AtomicReference<>((double) 0);
        int userId2 = 2;
        int userId3 = 3;
        Basket basket2 = new Basket(userId2, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket);
        Basket basket3 = new Basket(userId3, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket);
        int productId3 = productRepository.addProduct(storeId, "product3", "category3", 15.0, 1, "eyal was still here");
        basketProductRepository.addNewProductToBasket(productId3, storeId, userId3);//adding product 1 to basket
        basketProductRepository.addNewProductToBasket(productId3, storeId, userId2);//adding product 1 to basket2
        Thread thread1 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(100);
                payBehaviour(true);
                firstThread.compareAndSet(0, 1);
                pricePayed3.set(basket3.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), ""));
            } catch (PurchaseFailedException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(100);
                payBehaviour(true);
                firstThread.compareAndSet(0, 2);
                pricePayed2.set(basket2.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), ""));
            } catch (PurchaseFailedException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(0, productRepository.getStoreProductById(productId3, storeId).getStockQuantity());

        Assertions.assertEquals(13.5, pricePayed2.get() + pricePayed3.get());
        if (basket2.getFailedProducts().size() == 0 && basket3.getFailedProducts().size() == 0)
            Assertions.fail();

        if (basket2.getFailedProducts().size() == 0) {
            Assertions.assertEquals(13.5, pricePayed2.get());
            Assertions.assertEquals(productId3, basket3.getFailedProducts().peek().getProductId());
        } else if (basket3.getFailedProducts().size() == 0) {
            Assertions.assertEquals(13.5, pricePayed3.get());
            Assertions.assertEquals(productId3, basket2.getFailedProducts().peek().getProductId());
        } else
            Assertions.fail("both failed");

    }

    @Test
    void purchaseBasketSimpleTest_externalPaySuccess() {

        try {
            payBehaviour(true);
            Assertions.assertEquals(18, basket.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), ""));
        } catch (PurchaseFailedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void purchaseBasketSimpleTest_externalPayFail() {

        try {
            payBehaviour(false);
            basket.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("Payment failed", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_notInStockFail() {
        try {
            payBehaviour(true);
            basketProductRepository.changeProductQuantity(productId1, storeId, userId, 2);
            double price = basket.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), "");
            Assertions.assertEquals(15 * 0.9 * 0.8, price);
            BasketProduct failedProduct = basket.getFailedProducts().peek();
            BasketProduct successfulProduct = basket.getSuccessfulProductsList().peek();
            if (failedProduct == null)
                Assertions.fail("failed product is null");
            Assertions.assertNull(successfulProduct);
            Assertions.assertEquals(productId1, failedProduct.getProductId());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void cancelPurchase() {
        try {
            payBehaviour(false);
            basket.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            basket.cancelPurchase();
            Assertions.assertEquals(1, productRepository.getStoreProductById(productId1, storeId).getStockQuantity());
            Assertions.assertEquals(1, productRepository.getStoreProductById(productId2, storeId).getStockQuantity());

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void cancelPurchaseTimeout() {

        basket.setIdealTime(1);
        basket.setUnitsToRestore(TimeUnit.SECONDS);
        payBehaviour(false);
        Thread t1 = new Thread(() -> {
            try {
                basket.purchaseBasket("", "", "", "", "", "", "", "", "", new HashMap<>(), "");
                //failed products
                Assertions.assertEquals(2, basket.getFailedProducts().size());
            } catch (PurchaseFailedException ignore) {

            }
        });
        t1.start();

        try {
            t1.join();
            Thread.sleep(2000);
            //items restored
            Assertions.assertEquals(1, productRepository.getStoreProductById(productId1, storeId).getStockQuantity());
            Assertions.assertEquals(1, productRepository.getStoreProductById(productId2, storeId).getStockQuantity());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    void addProduct() {
    }
    @AfterAll
    static void afterAll() {
        try {
            productRepository.removeStoreProducts(storeId);
            basketProductRepository.removeBasketProducts(storeId, userId);
            SingletonCollection.getStoreRepository().removeStore(storeId);
            SingletonCollection.getProductDiscountsRepository().removeProductDiscount(productId1);
            SingletonCollection.getProductDiscountsRepository().removeProductDiscount(productId2);
            SingletonCollection.getStoreDiscountsRepository().removeStoreDiscounts(storeId);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}