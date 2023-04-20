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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class BasketTest {
    private static Basket basket;
    private static int userId;
    private static int storeId;
    private static IBasketProductRepository basketProductRepository;
    private static PaymentAdapter paymentAdapter;
    private static IProductHistoryRepository productHistoryRepository;
    private static CalculatePriceOfBasket calculatePriceOfBasket;
    private static IProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Market market = new Market();
        productRepository = SingletonCollection.getProductRepository();

        //initCalculatePriceOfBasket(market);
        userId = 1;
        SingletonCollection.getStoreRepository().addStore(userId, "store1", "category1");
        storeId = 0;
        initProducts();
        //total price before 25.0
        SingletonCollection.getProductDiscountsRepository().addProductDiscount(0,
                new VisibleDiscount(0, 0.2, LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        //total price after product discount 20.0
        SingletonCollection.getStoreDiscountsRepository().addStoreDiscount(0,
                new VisibleDiscount(0, 0.1, LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        //total price after store discount 18.0
        basketProductRepository = new BasketProductRepositoryAsHashMap();

        paymentAdapter = Mockito.mock(PaymentAdapter.class);
        productHistoryRepository = Mockito.mock(IProductHistoryRepository.class);
        initBasket();

        paymentAndHistoryBehaviour();
    }

    private static void paymentAndHistoryBehaviour() {
        Mockito.when(paymentAdapter.pay(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyDouble())).thenReturn(true);
        Mockito.doNothing().when(productHistoryRepository).addProductToHistory(
                Mockito.any(BasketProduct.class), Mockito.anyInt());
    }

    private static void initBasket() {
        basket = new Basket(userId, storeId, basketProductRepository, paymentAdapter,
                productHistoryRepository, calculatePriceOfBasket);
    }

    private static void initProducts() {
        productRepository.addProduct(storeId, "product1", "category1", 10.0, 1);
        productRepository.addProduct(storeId, "product2", "category2", 15.0, 1);

        basketProductRepository.addNewProductToBasket(0, storeId, userId);//adding product 0 to basket
        basketProductRepository.addNewProductToBasket(1, storeId, userId);//adding product 1 to basket
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

    @Test
    void purchaseBasket() {
        try {
            Assertions.assertEquals(basket.purchaseBasket("","","","","","","","","",new HashMap<>(),""), 18.0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void cancelPurchase() {
    }

    @Test
    void addProduct() {
    }
}