package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.storePackage.*;
import BGU.Group13B.service.ISession;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    private static IProductRepository productRepository;
    private static IStoreRepository storeRepository;
    private static IUserRepository userRepository;
    private static IBasketRepository basketRepository;
    private static IBasketProductRepository basketProductRepository;
    private static int storeId1;
    private static int storeId2;
    private static int productId1;
    private static int productId2;
    private static int productId3;
    private static User user1;
    private static User user2;
    private static ISession session;

    @BeforeEach
    void setUp() {

        try {
            productRepository = SingletonCollection.getProductRepository();
            storeRepository = SingletonCollection.getStoreRepository();
            userRepository = SingletonCollection.getUserRepository();
            basketRepository = SingletonCollection.getBasketRepository();
            basketProductRepository = SingletonCollection.getBasketProductRepository();
            //session = new ProxySession();
            session = new Session(new Market());
            int userId1 = userRepository.getNewUserId();
            user1 = new User(userId1);
            userRepository.addUser(userId1, user1);
            int userId2 = userRepository.getNewUserId();
            user2 = new User(userId2);
            userRepository.addUser(userId2, user2);
            storeId1 = storeRepository.addStore(2, "Electronics store", "electronics");
            productId1 = productRepository.addProduct(storeId1, "Dell computer", "electronics", 1000, 50, "Good and stable laptop.");
            storeId2 = storeRepository.addStore(2, "Electronics store", "electronics");
            productId2 = productRepository.addProduct(storeId2, "HP computer", "electronics", 6000, 0, "Good and stable pc.");
            productId3 = productRepository.addProduct(storeId2, "Dell computer A12", "electronics", 2000, 1, "Good and stable laptop.");

        } catch (Exception e) {
            fail("Exception was thrown");
        }

    }

    public void deleteAllData() {

        productRepository.removeStoreProduct(productId1, storeId1); //removes the product from baskets as well
        productRepository.removeStoreProduct(productId2, storeId2);
        productRepository.removeStoreProduct(productId3, storeId2);
        storeRepository.removeStore(storeId1);
        storeRepository.removeStore(storeId2);
        //user1.removeBasket(storeId1);
        //user2.removeBasket(storeId2);
        //userRepository.removeUser(user2.getUserId());

    }

    @AfterEach
    void tearDown() {
        deleteAllData();
    }

    @Test
    void addProductToCart_normalCase() {
        try {
            session.addProductToCart(user1.getUserId(), productId1, storeId1);
            assertTrue(basketProductRepository.getBasketProduct(productId1, storeId1, user1.getUserId()) != null);
            assertEquals(1, user1.getCart().getCartContent().size());
            assertEquals(productId1, user1.getCart().getCartContent().get(0).getProductId());
            assertEquals(storeId1, user1.getCart().getCartContent().get(0).getStoreId());
            //check if the right basket contains the new product
            assertEquals(productId1, basketRepository.getUserBaskets(user1.getUserId()).stream()
                    .filter(b -> b.getStoreId() == storeId1).findFirst().get().getBasketContent().get(0).getProductId());
            userRepository.removeUser(user1.getUserId());

        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void addProductToCart_outOfStock() {
        try {
            session.addProductToCart(user1.getUserId(), productId2, storeId2);
        } catch (Exception e) {
            assertEquals(0, user1.getCart().getCartContent().size());
            assertEquals("The product is out of stock", e.getMessage());
            user1.removeBasket(storeId2);
            userRepository.removeUser(user1.getUserId());
        }
    }

    @Test
    void addProductToCart_multiUsers() {
        try {
            //a thread test
            Thread[] threads = new Thread[2];
            threads[0] = new Thread(() -> session.addProductToCart(user1.getUserId(), productId1, storeId1));
            threads[1] = new Thread(() -> session.addProductToCart(user2.getUserId(), productId1, storeId1));
            threads[0].start();
            threads[1].start();

            for (int i = 0; i < 2; i++) {
                threads[i].join();
            }
            assertEquals(1, user1.getCart().getCartContent().size());
            assertEquals(productId1, user1.getCart().getCartContent().get(0).getProductId());
            assertEquals(storeId1, user1.getCart().getCartContent().get(0).getStoreId());
            //check if the right basket contains the new product
            assertEquals(productId1, basketRepository.getUserBaskets(user1.getUserId()).stream()
                    .filter(b -> b.getStoreId() == storeId1).findFirst().get().getBasketContent().get(0).getProductId());
            assertEquals(true, basketProductRepository.getBasketProduct(productId1, storeId1, user1.getUserId()) != null);
            assertEquals(1, user2.getCart().getCartContent().size());
            assertEquals(productId1, user2.getCart().getCartContent().get(0).getProductId());
            assertEquals(storeId1, user2.getCart().getCartContent().get(0).getStoreId());
            //check if the right basket contains the new product
            assertEquals(productId1, basketRepository.getUserBaskets(user2.getUserId()).stream()
                    .filter(b -> b.getStoreId() == storeId1).findFirst().get().getBasketContent().get(0).getProductId());
            assertEquals(true, basketProductRepository.getBasketProduct(productId1, storeId1, user2.getUserId()) != null);
            userRepository.removeUser(user1.getUserId());
            userRepository.removeUser(user2.getUserId());

        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    void addProductToCart_multiUsers_buyAndAdd() {

        try {
            //a thread test
            Thread[] threads = new Thread[2];
            threads[0] = new Thread(() -> {
                session.addProductToCart(user1.getUserId(), productId1, storeId1);
                session.purchaseProductCart(
                        user1.getUserId(),
                        "address",
                        "creditCardNumber",
                        "creditCardMonth",
                        "creditCardYear",
                        "creditCardHolderFirstName",
                        "creditCardHolderLastName",
                        "creditCardCcv",
                        "id",
                        "creditCardType",
                        new HashMap<>(),
                        "couponCode");
            });
            threads[1] = new Thread(() -> session.addProductToCart(user2.getUserId(), productId1, storeId1));
            threads[0].start();
            threads[1].start();


            for (int i = 0; i < 2; i++) {
                threads[i].join();
            }
            assertEquals(0, user1.getCart().getCartContent().size());
            assertEquals(0, basketRepository.getUserBaskets(user1.getUserId()).stream()
                    .filter(b -> b.getStoreId() == storeId1).findFirst().get().getBasketContent().size());
            assertEquals(true, basketProductRepository.getBasketProduct(productId1, storeId1, user1.getUserId()) == null);
            assertEquals(1, user2.getCart().getCartContent().size());
            assertEquals(productId1, user2.getCart().getCartContent().get(0).getProductId());
            assertEquals(storeId1, user2.getCart().getCartContent().get(0).getStoreId());
            //check if the right basket contains the new product
            assertEquals(productId1, basketRepository.getUserBaskets(user2.getUserId()).stream()
                    .filter(b -> b.getStoreId() == storeId1).findFirst().get().getBasketContent().get(0).getProductId());
            assertEquals(true, basketProductRepository.getBasketProduct(productId1, storeId1, user2.getUserId()) != null);
            userRepository.removeUser(user1.getUserId());
            userRepository.removeUser(user2.getUserId());

        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }


    }

    @Test
    void addProductToCart_multiUsers_buyAndAdd_edgeCase() {

        //TODO: run when purchaseProductCart is ready
        try {
            //a thread test
            Thread[] threads = new Thread[2];
            threads[0] = new Thread(() -> {
                session.addProductToCart(user1.getUserId(), productId3, storeId2);
                session.purchaseProductCart(
                        user1.getUserId(),
                        "address",
                        "creditCardNumber",
                        "creditCardMonth",
                        "creditCardYear",
                        "creditCardHolderFirstName",
                        "creditCardHolderLastName",
                        "creditCardCcv",
                        "id",
                        "creditCardType",
                        new HashMap<>(),
                        "couponCode");
            });
            threads[1] = new Thread(() -> session.addProductToCart(user2.getUserId(), productId3, storeId1));
            threads[0].start();
            threads[1].start();


            for (int i = 0; i < 2; i++) {
                threads[i].join();
            }
            assertEquals(0, user1.getCart().getCartContent().size());
            assertEquals(0, basketRepository.getUserBaskets(user1.getUserId()).stream()
                    .filter(b -> b.getStoreId() == storeId1).findFirst().get().getBasketContent().size());
            assertEquals(true, basketProductRepository.getBasketProduct(productId3, storeId1, user1.getUserId()) == null);
            user1.removeBasket(storeId2);
            user2.removeBasket(storeId2);
            userRepository.removeUser(user1.getUserId());
            userRepository.removeUser(user2.getUserId());

        } catch (Exception e) {
            assertEquals(0, user2.getCart().getCartContent().size());
            assertEquals(true, basketProductRepository.getBasketProduct(productId1, storeId1, user2.getUserId()) == null);
            //assertEquals("The product is out of stock", e.getMessage());

        }
    }

}