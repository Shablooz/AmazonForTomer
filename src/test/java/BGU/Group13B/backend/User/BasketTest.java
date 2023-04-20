package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl.BasketProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ProductHistoryRepositoryImpl.ProductHistoryRepositoryAsList;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductHistoryRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.payment.PaymentAdapter;
import BGU.Group13B.service.callbacks.CalculatePriceOfBasket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class BasketTest {

    private static Basket basket;
    private static int userId;
    private static IBasketProductRepository basketProductRepository;
    private static PaymentAdapter paymentAdapter;
    private IProductHistoryRepository productHistoryRepository;
    private CalculatePriceOfBasket calculatePriceOfBasket;

    @BeforeEach
    void setUp() {
        initCalculatePriceOfBasket();
        userId = 1;
        basketProductRepository = new BasketProductRepositoryAsHashMap();
        paymentAdapter = Mockito.mock(PaymentAdapter.class);
        productHistoryRepository = new ProductHistoryRepositoryAsList();
        //get calculatePriceOfBasket from Market class
        /*public Basket(int userId, int storeId, IBasketProductRepository productRepository,
                  PaymentAdapter paymentAdapter, IProductHistoryRepository productHistoryRepository,
                  CalculatePriceOfBasket calculatePriceOfBasket)*/
        basket = new Basket()
    }

    private void initCalculatePriceOfBasket() {
        final Method method;//effective final
        try {
            method = Market.class.getDeclaredMethod("calculatePriceOfBasket", double.class, ConcurrentLinkedQueue.class, int.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        method.setAccessible(true);

        calculatePriceOfBasket = ((totalAmount, successfulProducts, storeId, storeCoupons) -> {
            try {
                return (double) method.invoke(null, totalAmount, successfulProducts, storeId, storeCoupons);
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
    }

    @Test
    void cancelPurchase() {
    }

    @Test
    void addProduct() {
    }
}