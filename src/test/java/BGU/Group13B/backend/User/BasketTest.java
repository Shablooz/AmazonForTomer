package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import com.vaadin.flow.data.selection.SelectionModel;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
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
    private CalculatePriceOfBasket calculatePriceOfBasket;
    private static IProductRepository productRepository;
    private static int productId1;
    private static int productId2;

    private DeliveryAdapter deliveryAdapter;
    private final int quantityLowerBound = 1;
    private final int quantityUpperBound = 10;
    private final int priceLowerBound = 2;
    private final int priceUpperBound = 100;
    private int productId3;
    private int productId4;

    private Store store;


    @BeforeEach
    void setUp() throws NoPermissionException {

        /*Mockito.when(SingletonCollection.getProductRepository()).
                thenReturn(Mockito.mock(IProductRepository.class));*/ //remember
        //eyal was here
        SingletonCollection.reset_system();
        productRepository = SingletonCollection.getProductRepository();
        basketProductRepository = SingletonCollection.getBasketProductRepository();
        calculatePriceOfBasket = SingletonCollection.getCalculatePriceOfBasket();
        //initCalculatePriceOfBasket(market);
        userId = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(userId, new User(userId));
        storeId = SingletonCollection.getStoreRepository().addStore(userId, "store1", "category1");
        store = SingletonCollection.getStoreRepository().getStore(storeId);
        int c1 = this.store.addStorePriceCondition(userId, priceLowerBound, priceUpperBound);
        int c2 = this.store.addStoreQuantityCondition(userId, quantityLowerBound, quantityUpperBound);
        int c3 = this.store.addANDCondition(userId, c1, c2);
        this.store.setPurchasePolicyCondition(userId, c3);
        //SingletonCollection.getStorePurchasePolicyRepository().getPurchasePolicy(storeId).setPriceBounds(priceLowerBound, priceUpperBound);
        //SingletonCollection.getStorePurchasePolicyRepository().getPurchasePolicy(storeId).setQuantityBounds(quantityLowerBound, quantityUpperBound);

        initProducts();
        //total price before 25.0
        int discountId1 = this.store.addProductDiscount(userId, 0.2, LocalDate.now().plusDays(1), productId1);
        int discountId2 = this.store.addProductDiscount(userId, 0.2, LocalDate.now().plusDays(1), productId2);
        int discountId3 = this.store.addStoreDiscount(userId, 0.1, LocalDate.now().plusDays(1));

        this.store.addDiscountAsRoot(userId, discountId1);
        this.store.addDiscountToADDRoot(userId, discountId2);
        this.store.addDiscountToADDRoot(userId, discountId3);

        paymentAdapter = Mockito.mock(PaymentAdapter.class);
        deliveryAdapter = Mockito.mock(DeliveryAdapter.class);
        productHistoryRepository = Mockito.mock(IProductHistoryRepository.class);
        initBasket();

        paymentAndHistoryBehaviour();

    }

    private void paymentAndHistoryBehaviour() {
        Mockito.doNothing().when(productHistoryRepository).addProductToHistory(
                Mockito.any(BasketProduct.class), Mockito.anyInt());
    }

    private void payBehaviour(boolean success) {
        Mockito.when(paymentAdapter.pay(
                Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString())).thenReturn(success);
    }

    private void deliveryBehaviour(boolean success) {
        Mockito.when(deliveryAdapter.supply(
                Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString())).thenReturn(success);
    }

    private void initBasket() {
        basket = new Basket(userId, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket, deliveryAdapter);
        SingletonCollection.getBasketRepository().addUserBasket(basket);
    }

    private void initProducts() {
        productId1 = productRepository.addProduct(storeId, "product1", "category1", 10.0, 1, "description1").getProductId();
        productId2 = productRepository.addProduct(storeId, "product2", "category2", 15.0, 1, "eyal was here").getProductId();
        productId3 = productRepository.addProduct(storeId, "product3", "category3", 1.0, 1000, "description3").getProductId();
        productId4 = productRepository.addProduct(storeId, "product4", "category4", 15, 1000, "description4").getProductId();
        basketProductRepository.addNewProductToBasket(productId1, storeId, userId);//adding product 0 to basket
        basketProductRepository.addNewProductToBasket(productId2, storeId, userId);//adding product 1 to basket
    }

    @AfterEach
    void tearDown() {

    }

    @RepeatedTest(10)
    void twoThreadsTryToPurchaseTheLastProduct() {
        AtomicReference<Double> pricePayed2 = new AtomicReference<>(0.0);
        AtomicReference<Double> pricePayed3 = new AtomicReference<>(0.0);
        int userId2 = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(userId2, new User(userId2));
        int userId3 = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(userId3, new User(userId3));
        Basket basket2 = new Basket(userId2, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket, deliveryAdapter);
        Basket basket3 = new Basket(userId3, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket, deliveryAdapter);
        SingletonCollection.getBasketRepository().addUserBasket(basket2);
        SingletonCollection.getBasketRepository().addUserBasket(basket3);
        int productId3 = productRepository.addProduct(storeId, "product3", "category3", 15.0, 1, "eyal was still here").getProductId();
        basketProductRepository.addNewProductToBasket(productId3, storeId, userId3);//adding product3 to basket
        basketProductRepository.addNewProductToBasket(productId3, storeId, userId2);//adding product3 to basket2
        Thread thread1 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(100);
                payBehaviour(true);
                deliveryBehaviour(true);
                pricePayed3.set(basket3.purchaseBasket("", "", "",
                        "", "", "", new HashMap<>(), ""));
            } catch (PurchaseFailedException e) {
                Assertions.assertEquals("total price must be 2.0-100.0", e.getMessage());
            } catch (InterruptedException e) {
                Assertions.fail(e);
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(100);
                payBehaviour(true);
                deliveryBehaviour(true);

                pricePayed2.set(basket2.purchaseBasket("", "", "", "", "", "", new HashMap<>(), ""));
            } catch (PurchaseFailedException e) {
                Assertions.assertEquals("total price must be 2.0-100.0", e.getMessage());

            } catch (InterruptedException e) {
                Assertions.fail(e);
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
            deliveryBehaviour(true);

            Assertions.assertEquals(17.5, basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), ""));
        } catch (PurchaseFailedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void purchaseBasketSimpleTest_externalPayFail() {

        try {
            payBehaviour(false);
            deliveryBehaviour(true);

            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("Payment failed", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_externalDeliverySuccess() {

        try {
            payBehaviour(true);
            deliveryBehaviour(true);

            Assertions.assertEquals(17.5, basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), ""));
        } catch (PurchaseFailedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void purchaseBasketSimpleTest_externalDeliveryFail() {

        try {
            payBehaviour(true);
            deliveryBehaviour(false);

            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("Delivery failed", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_externalPaymentAndDeliveryFailed() {

        try {
            payBehaviour(false);
            deliveryBehaviour(false);

            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
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
            deliveryBehaviour(true);
            basketProductRepository.changeProductQuantity(productId1, storeId, userId, 3);
            double price = basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
            Assertions.assertEquals(10.5, price);
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
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("Payment failed", e.getMessage());
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
                basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
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

    //policy tests
    @Test
    void purchaseBasketSimpleTest_StorePolicyMinQuantityFail() {
        try {
            int c = store.addStoreQuantityCondition(userId, quantityLowerBound, quantityUpperBound);
            store.setPurchasePolicyCondition(userId, c);
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId3, storeId, userId);
            basketProductRepository.changeProductQuantity(productId3, storeId, userId, 0);
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("total quantity must be 1-10", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_StorePolicyMinQuantitySuccess() {
        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId3, storeId, userId);
            basketProductRepository.changeProductQuantity(productId3, storeId, userId, 5);
            basket.purchaseBasket(
                    "12341234", "4", "2033", "Shaun",
                    "123", "1232323", new HashMap<>(), "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_StorePolicyMaxQuantityFail() {
        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId3, storeId, userId);
            basketProductRepository.changeProductQuantity(productId3, storeId, userId, 13);
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("total quantity must be 1-10", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_StorePolicyMaxQuantitySuccess() {
        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId3, storeId, userId);
            basketProductRepository.changeProductQuantity(productId3, storeId, userId, 10);
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_StorePolicyMinBagPriceFail() {
        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId3, storeId, userId);
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("total price must be 2.0-100.0", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_StorePolicyMinBagPriceSuccess() {
        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId3, storeId, userId);
            basketProductRepository.changeProductQuantity(productId3, storeId, userId, 3);
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void purchaseBasketSimpleTest_StorePolicyMaxBagPriceFail() {
        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            basketProductRepository.removeBasketProducts(storeId, userId);
            basketProductRepository.addNewProductToBasket(productId4, storeId, userId);
            basketProductRepository.changeProductQuantity(productId4, storeId, userId, 10);
            basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
        } catch (PurchaseFailedException e) {
            Assertions.assertEquals("total price must be 2.0-100.0", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void purchaseBasketSimpleComplexDiscountTestSuccess1() {
        SingletonCollection.reset_system();
        int userId = SingletonCollection.getSession().enterAsGuest();
        SingletonCollection.getSession().register(userId, "userName", "Password1", "emao@gmail.com", "", "", "", LocalDate.now().minusYears(20));
        SingletonCollection.getSession().login(userId, "userName", "Password1", "", "", "");
        int storeId = SingletonCollection.getSession().addStore(userId, "store1", "category1").getData();


        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            /*
             * set up products
             * */
            int productId1 = SingletonCollection.getSession().addProduct(userId, storeId, "Yogurt", "milk", 30, 5, "description").getData();
            int productId2 = SingletonCollection.getSession().addProduct(userId, storeId, "pasta", "category1", 20, 4, "description").getData();

            /*set policy*/
            int c1 = SingletonCollection.getSession().addStorePriceCondition(storeId, userId, 100).getData();//basket is larger then 100
            int c2 = SingletonCollection.getSession().addProductQuantityCondition(storeId, userId, productId2, 3).getData();//at least 3 pasta
            int c3 = SingletonCollection.getSession().addANDCondition(storeId, userId, c1, c2).getData();//and between those 2
            //store.setPurchasePolicyCondition(userId, c3);

            int discountId1 = SingletonCollection.getSession().addCategoryDiscount(storeId, userId, c3, 0.05, LocalDate.now().plusDays(1), "milk").getData();

            SingletonCollection.getSession().addDiscountAsRoot(storeId, userId, discountId1);

            SingletonCollection.getSession().addProductToCart(userId,  productId1, storeId);
            SingletonCollection.getSession().changeProductQuantityInCart(userId, storeId, productId1, 4);
            SingletonCollection.getSession().addProductToCart(userId,  productId2, storeId);
            SingletonCollection.getSession().changeProductQuantityInCart(userId, storeId, productId2, 3);
            var basket = SingletonCollection.getBasketRepository().getUserBaskets(userId).stream().toList().get(0);
            double pricePayed = basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
            Assertions.assertEquals(174/*30*4*0.95 + 20*3*/, pricePayed);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
    @Test
    void purchaseBasketSimpleComplexDiscountTestSuccess2() {
        SingletonCollection.reset_system();
        int userId = SingletonCollection.getSession().enterAsGuest();
        SingletonCollection.getSession().register(userId, "userName", "Password1", "emao@gmail.com", "", "", "", LocalDate.now().minusYears(20));
        SingletonCollection.getSession().login(userId, "userName", "Password1", "", "", "");
        int storeId = SingletonCollection.getSession().addStore(userId, "store1", "category1").getData();


        try {
            payBehaviour(true);
            deliveryBehaviour(true);
            /*
             * set up products
             * */
            int productId1 = SingletonCollection.getSession().addProduct(userId, storeId, "Yogurt", "milk", 30, 5, "description").getData();
            int productId2 = SingletonCollection.getSession().addProduct(userId, storeId, "pasta", "category1", 20, 4, "description").getData();

            /*set policy*/
            int c1 = SingletonCollection.getSession().addStorePriceCondition(storeId, userId, 100).getData();//basket is larger then 100
            int c2 = SingletonCollection.getSession().addProductQuantityCondition(storeId, userId, productId2, 3).getData();//at least 3 pasta
            int c3 = SingletonCollection.getSession().addANDCondition(storeId, userId, c1, c2).getData();//and between those 2
            //store.setPurchasePolicyCondition(userId, c3);

            int discountId1 = SingletonCollection.getSession().addCategoryDiscount(storeId, userId, c3, 0.05, LocalDate.now().plusDays(1), "milk").getData();
            int discountId2 = SingletonCollection.getSession().addStoreDiscount(storeId, userId, 0.04, LocalDate.now().plusDays(1)).getData();

            SingletonCollection.getSession().addDiscountAsRoot(storeId, userId, discountId1);
            SingletonCollection.getSession().addDiscountToMAXRoot(storeId, userId, discountId2);//max(discount1,discount2)

            SingletonCollection.getSession().addProductToCart(userId,  productId1, storeId);
            SingletonCollection.getSession().changeProductQuantityInCart(userId, storeId, productId1, 4);
            SingletonCollection.getSession().addProductToCart(userId,  productId2, storeId);
            SingletonCollection.getSession().changeProductQuantityInCart(userId, storeId, productId2, 3);
            var basket = SingletonCollection.getBasketRepository().getUserBaskets(userId).stream().toList().get(0);
            double pricePayed = basket.purchaseBasket("", "", "", "", "", "", new HashMap<>(), "");
            Assertions.assertEquals(171.6/*min{min{30*4*0.95, 30*4*0.96} + min{20*3, 20*3*0.96}} = 172.8*/, pricePayed);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}