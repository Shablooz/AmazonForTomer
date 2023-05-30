package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.HashMap;

class UserTest {

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    private IUserRepository userRepository = SingletonCollection.getUserRepository();
    private IPurchaseHistoryRepository purchaseHistoryRepository = SingletonCollection.getPurchaseHistoryRepository();

    private IStoreRepository storeRepository= SingletonCollection.getStoreRepository();

    private final String goodUsername1 = "goodUsername1";
    private final String goodUsername2 = "greatname";
    private final String goodUsername3 = "bestname";


    private final String badUsername1 = "goo)(*&^%$dU%ser#n!ame1";
    private final String badUsername2 = "-+!@#$helloimbad";
    private final String badUsername3 = "awful  name";


    private final String badPassword1 = "ihavenocapslock123";
    private final String badPassword2 = "HELLO";
    private final String badPassword3 = "123458797654321";

    private final String goodPassword1 = "goodPassword1";
    private final String goodPassword2 = "Ookodoo1234";
    private final String goodPassword3 = "ShtrudelEater420";


    private final String goodEmail1 = "eylalozof123@gmail.com";
    private final String goodEmail2 = "eyalthegever@gmail.com";
    private final String goodEmail3 = "eyalisthebest123@gmail.com";

    private final String badEmail1 = "tefsadgvnspoiseropgesrgpoe123542@gmail.com";
    private final String badEmail2 = "hello@gmail.lmao";
    private final String badEmail3 = "a@waaaail.com";
    private User user;


    @BeforeEach
    void setUp() {
        user1 = new User(1);
        user2 = new User(2);
        user3 = new User(3);
        user4 = new User(4);
        user5 = new User(5);
        userRepository.addUser(1, user1);
        userRepository.addUser(2, user2);
        userRepository.addUser(3, user3);
        userRepository.addUser(4, user4);
        userRepository.addUser(5, user5);
    }

    @AfterEach
    void tearDown() {
        userRepository.removeUser(1);
        userRepository.removeUser(2);
        userRepository.removeUser(3);
        userRepository.removeUser(4);
        userRepository.removeUser(5);
    }

    @Test
    void testRegister() {
        //user1
        try {
            user1.register(goodUsername1, goodPassword1, goodEmail1, "", "", "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        try {
            user2.register(goodUsername2, goodPassword2, goodEmail2, "", "", "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        try {
            user3.register(goodUsername3, goodPassword3, goodEmail3, "", "", "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        try {
            user4.register(badUsername1, badPassword1, badEmail1, "", "", "");
            Assertions.fail();
        } catch (Exception e) {

        }

        try {
            user5.register(badUsername2, badPassword2, badEmail2, "", "", "");
            Assertions.fail();
        } catch (Exception e) {

        }

        //now lets check that we cant register twice!
        try {
            user1.register(goodUsername1, goodPassword1, goodEmail1, "", "", "");
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testLogin() {
        user1.register(goodUsername1, goodPassword1, goodEmail1, "yellow", "", "");
        user2.register(goodUsername2, goodPassword2, goodEmail2, "", "yak", "");
        user3.register(goodUsername3, goodPassword3, goodEmail3, "", "", "");

        user4.register("orangesLove", goodPassword1, "orangeslover@gmail.com", "yellow", "", "");

        try {
            user1.login(goodUsername1, goodPassword1, "yellow", "", "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        try {
            user2.login(goodUsername2, goodPassword2, "", "yak", "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        try {
            user3.login(goodUsername3, goodPassword3, "", "", "");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        //should fail because he didnt answer the questions
        try {
            user4.login("orangesLove", goodPassword1, "", "", "");
            Assertions.fail();
        } catch (Exception e) {
        }
    }

    @Test
    void testLogout() {
        user1.register(goodUsername1, goodPassword1, goodEmail1, "yellow", "", "");
        user2.register(goodUsername2, goodPassword2, goodEmail2, "", "yak", "");
        user3.register(goodUsername3, goodPassword3, goodEmail3, "", "", "");

        user1.login(goodUsername1, goodPassword1, "yellow", "", "");
        user2.login(goodUsername2, goodPassword2, "", "yak", "");
        user3.login(goodUsername3, goodPassword3, "", "", "");

        Assertions.assertTrue(user1.isLoggedIn());
        user1.logout();
        Assertions.assertFalse(user1.isLoggedIn());

        Assertions.assertTrue(user2.isLoggedIn());
        user2.logout();
        Assertions.assertFalse(user2.isLoggedIn());

        Assertions.assertTrue(user3.isLoggedIn());
        user3.logout();
        Assertions.assertFalse(user3.isLoggedIn());

        try {
            user3.logout();
            Assertions.fail("should have failed - cant log out twice!");
        } catch (Exception e) {

        }
    }

    @Test
    void passwordEncryption() {
        user1.register(goodUsername1, goodPassword1, goodEmail1, "yellow", "", "");
        user2.register(goodUsername2, goodPassword1, goodEmail2, "", "yak", "");
        Assertions.assertTrue(user1.SecurityAnswer1Exists());
        Assertions.assertFalse(user1.SecurityAnswer2Exists());
        Assertions.assertFalse(user1.SecurityAnswer3Exists());

        Assertions.assertFalse(user2.SecurityAnswer1Exists());
        Assertions.assertFalse(user2.SecurityAnswer3Exists());
        Assertions.assertTrue(user2.SecurityAnswer2Exists());
    }

    @Test
    void purchaseCartLoginLogoutSuccess() {
        //setup
        SingletonCollection.reset_system();
        int newUserId = SingletonCollection.getUserRepository().getNewUserId();
        User user = new User(newUserId);
        user.register(goodUsername1, goodPassword1, "email@gmail.com", "yellow", "", "");
        user.login(goodUsername1, goodPassword1, "yellow", "", "");
        SingletonCollection.getUserRepository().addUser(newUserId, user);
        int storeId = SingletonCollection.getStoreRepository().addStore(newUserId, "store1", "media");
        int productId = SingletonCollection.getProductRepository().addProduct(storeId, "product1", "media", 10.0, 10, "very nice product").getProductId();
        //action
        try {
            //user is logged in
            user.addProductToCart(productId, storeId);
            double pricePayed = user.purchaseCart("12345678", "12", "2033", "Shaun",
                    "123", "12345678", new HashMap<>(), "");
            Assertions.assertEquals(10.0, pricePayed);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void purchaseUserHistory(){
        user1.register(goodUsername1, goodPassword1, goodEmail1, "yellow", "", "");
        user1.login(goodUsername1, goodPassword1, "yellow", "", "");
        int storeId1 = SingletonCollection.getStoreRepository().addStore(user2.getUserId(), "Electronics store", "electronics");
        Product product1 = SingletonCollection.getProductRepository().addProduct(storeId1, "Dell computer", "electronics", 1000, 50, "Good and stable laptop.");
        Product product2 = SingletonCollection.getProductRepository().addProduct(storeId1, "HP computer", "electronics", 6000, 0, "Good and stable pc.");
        Product product3 = SingletonCollection.getProductRepository().addProduct(storeId1, "Dell computer A12", "electronics", 2000, 1, "Good and stable laptop.");
        BasketProduct basketProduct1= new BasketProduct(product1);
        basketProduct1.setQuantity(2);
        BasketProduct basketProduct2= new BasketProduct(product2);
        BasketProduct basketProduct3= new BasketProduct(product3);
        basketProduct3.setQuantity(3);
        ConcurrentLinkedQueue<BasketProduct> basketProducts1 = new ConcurrentLinkedQueue<>();
        basketProducts1.add(basketProduct1);
        basketProducts1.add(basketProduct2);
        ConcurrentLinkedQueue<BasketProduct> basketProducts2 = new ConcurrentLinkedQueue<>();
        basketProducts2.add(basketProduct2);
        basketProducts2.add(basketProduct3);
        PurchaseHistory purchaseHistory1 = purchaseHistoryRepository.addPurchase(user1.getUserId(), storeId1, basketProducts1, 8000);
        PurchaseHistory purchaseHistory2 = purchaseHistoryRepository.addPurchase(user1.getUserId(), storeId1, basketProducts2, 12000);
        Assertions.assertEquals(purchaseHistory1.toString()+'\n'+purchaseHistory2 +'\n',user1.getPurchaseHistory());
    }

    @Test
    void purchaseUserHistory_Empty() {
        user1.register(goodUsername1, goodPassword1, goodEmail1, "yellow", "", "");
        user1.login(goodUsername1, goodPassword1, "yellow", "", "");
        Assertions.assertEquals("", user1.getPurchaseHistory());
    }

//    @Test
//    void purchaseUserHistoryAsAdmin(){ //user history
//        Session session = SingletonCollection.getSession();
//        user2.register(goodUsername2, goodPassword2, goodEmail2, "", "yak", "");
//        user2.login(goodUsername2, goodPassword2, "", "yak", "");
//        int storeId1 = SingletonCollection.getStoreRepository().addStore(user2.getUserId(), "Electronics store", "electronics");
//        Product product1 = SingletonCollection.getProductRepository().addProduct(storeId1, "Dell computer", "electronics", 1000, 50, "Good and stable laptop.");
//        Product product2 = SingletonCollection.getProductRepository().addProduct(storeId1, "HP computer", "electronics", 6000, 0, "Good and stable pc.");
//        BasketProduct basketProduct1= new BasketProduct(product1);
//        basketProduct1.setQuantity(2);
//        BasketProduct basketProduct2= new BasketProduct(product2);
//        ConcurrentLinkedQueue<BasketProduct> basketProducts1 = new ConcurrentLinkedQueue<>();
//        basketProducts1.add(basketProduct1);
//        basketProducts1.add(basketProduct2);
//        PurchaseHistory purchaseHistory1 = purchaseHistoryRepository.addPurchase(user1.getUserId(), storeId1, basketProducts1, 8000);
//        user2.logout();
//        User admin= userRepository.getUser(1);
//        admin.login("kingOfTheSheep","SheePLover420","11","11","11");
//        session.getUserPurchaseHistoryAsAdmin(user1.getUserId(),admin.getUserId());
//        Assertions.assertEquals(purchaseHistory1.toString()+'\n',user1.getPurchaseHistory());
//        admin.logout();
//    }

   /* @Test
    void purchaseStoreHistoryAsAdmin(){ //storeHistory
        Session session = SingletonCollection.getSession();
        user2.register(goodUsername2, goodPassword2, goodEmail2, "", "yak", "");
        user2.login(goodUsername2, goodPassword2, "", "yak", "");
        int storeId1 = SingletonCollection.getStoreRepository().addStore(user2.getUserId(), "Electronics store", "electronics");
        Product product1 = SingletonCollection.getProductRepository().addProduct(storeId1, "Dell computer", "electronics", 1000, 50, "Good and stable laptop.");
        Product product2 = SingletonCollection.getProductRepository().addProduct(storeId1, "HP computer", "electronics", 6000, 0, "Good and stable pc.");
        BasketProduct basketProduct1= new BasketProduct(product1);
        basketProduct1.setQuantity(2);
        BasketProduct basketProduct2= new BasketProduct(product2);
        ConcurrentLinkedQueue<BasketProduct> basketProducts1 = new ConcurrentLinkedQueue<>();
        basketProducts1.add(basketProduct1);
        basketProducts1.add(basketProduct2);
        PurchaseHistory purchaseHistory1 = purchaseHistoryRepository.addPurchase(user1.getUserId(), storeId1, basketProducts1, 8000);
        user2.logout();
        User admin= userRepository.getUser(1);
        admin.login("kingOfTheSheep","SheePLover420","11","11","11");
        List<PurchaseHistory> history= session.getStorePurchaseHistoryAsAdmin(storeId1,admin.getUserId()).getData();
        Assertions.assertEquals(1,history.size());
        Assertions.assertEquals(purchaseHistory1,history.get(0));
        storeRepository.removeStore(storeId1);
        purchaseHistoryRepository.removePurchase(purchaseHistory1);
        admin.logout();

    }*/

    @Test
    void purchaseCartLoginLogoutFail() {
        //setup
        SingletonCollection.reset_system();
        int newUserId = SingletonCollection.getUserRepository().getNewUserId();
        user = new User(newUserId);
        user.register(goodUsername1, goodPassword1, "email@gmail.com", "yellow", "", "");
        user.login(goodUsername1, goodPassword1, "yellow", "", "");
        SingletonCollection.getUserRepository().addUser(newUserId, user);
        int storeId = SingletonCollection.getStoreRepository().addStore(newUserId, "store1", "media");
        int productId = SingletonCollection.getProductRepository().addProduct(storeId, "product1", "media", 10.0, 10, "very nice product").getProductId();
        try {
            //user is logged in
            user.addProductToCart(productId, storeId);
            user.logout();
            double pricePayed = user.purchaseCart("12345678", "12", "2033", "Shaun",
                    "123", "12345678", new HashMap<>(), "");
            Assertions.fail();//should not get here
        } catch (NoPermissionException e) {
            Assertions.assertEquals("Only logged in users can purchase cart", e.getMessage());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
    @Test
    void purchaseCartLoginLogoutSuccess2() {
        purchaseCartLoginLogoutFail();
        user.login(goodUsername1, goodPassword1, "yellow", "", "");
        try {
            //user is logged in
            double pricePayed = user.purchaseCart("12345678", "12", "2033", "Shaun",
                    "123", "12345678", new HashMap<>(), "");
            Assertions.assertEquals(10.0, pricePayed);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}