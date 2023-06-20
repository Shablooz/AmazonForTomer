package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.User.UserPermissions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexAT{

    private Session session;
    private final int adminId = 1;
    private final LocalDateTime today = LocalDateTime.now();

    private int guests;
    private int regularMembers;
    private int storeManagersThatAreNotOwners;
    private int storeOwners;
    private int admins;

    @BeforeEach
    public void setup() {
        SingletonCollection.reset_system(false);
        SingletonCollection.setSaveMode(false);
        session = SingletonCollection.getSession();
        session.enterAsGuest();
        session.login(adminId, "kingOfTheSheep", "SheePLover420", "11", "11", "11");
        guests = 0;
        regularMembers = 0;
        storeManagersThatAreNotOwners = 0;
        storeOwners = 0;
        admins = 1;
    }

    @AfterEach
    public void tearDown() {
        SingletonCollection.reset_system(false);
        SingletonCollection.setSaveMode(false);
    }

    private <T> T handleResponse(Response<T> response) {
        if(response.didntSucceed()) {
            fail();
        }
        return response.getData();
    }

    private void checkTraffic(int addedGuests, int addedRegularMembers, int addedStoreManagersThatAreNotOwners, int addedStoreOwners, int addedAdmins){
        var traffic = handleResponse(session.getUserTrafficOfRange(adminId, today.toLocalDate(), today.toLocalDate()));
        assertEquals(addedGuests, traffic.numOfGuests() - guests);
        assertEquals(addedRegularMembers, traffic.numOfRegularMembers() - regularMembers);
        assertEquals(addedStoreManagersThatAreNotOwners, traffic.numOfStoreManagersThatAreNotOwners() - storeManagersThatAreNotOwners);
        assertEquals(addedStoreOwners, traffic.numOfStoreOwners() - storeOwners);
        assertEquals(addedAdmins, traffic.numOfAdmins() - admins);
        guests = traffic.numOfGuests();
        regularMembers = traffic.numOfRegularMembers();
        storeManagersThatAreNotOwners = traffic.numOfStoreManagersThatAreNotOwners();
        storeOwners = traffic.numOfStoreOwners();
        admins = traffic.numOfAdmins();
    }

    private void checkSystemIncome(double income){
        double[] historyIncome = handleResponse(session.getSystemHistoryIncome(adminId, today.toLocalDate(), today.toLocalDate()));
        assertEquals(1, historyIncome.length);
        assertEquals(income, historyIncome[0]);
    }

    private void checkStoreIncome(int storeId, double income){
        double[] historyIncome = handleResponse(session.getStoreHistoryIncome(adminId, storeId, today.toLocalDate(), today.toLocalDate()));
        assertEquals(1, historyIncome.length);
        assertEquals(income, historyIncome[0]);
    }

    private void reenterSystem(int... userIds){
        logout(userIds);
        login(userIds);
    }

    private void logout(int... userIds){
        try{
            for(int userId : userIds)
                session.logout(userId);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    private void login(int... userIds){
        try{
            for(int userId : userIds){
                int id = session.enterAsGuest();
                session.login(id, "Username" + userId, "Password" + userId, "", "", "");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    private int enterAsGuest(){
        try{
            return session.enterAsGuest();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            fail();
            return -1;
        }
    }

    private int registerAndLoginAsRegularMember(){
        try{
            int id = session.enterAsGuest();
            session.register(id, "Username" + id, "Password" + id,  "Username" + id + "@gmail.com", "", "", "", today);
            return session.login(id, "Username" + id, "Password" + id, "", "", "");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            fail();
            return -1;
        }
    }

    private Pair<Integer /*userId*/, Integer /*storeId*/> registerAndLoginAsStoreFounder(){
        int userId = registerAndLoginAsRegularMember();
        int storeId = handleResponse(session.addStore(userId, "storeName", "storeCategory"));
        return Pair.of(userId, storeId);
    }

    private int registerAndLoginAsStoreOwner(int founderId, int storeId){
        int userId = registerAndLoginAsRegularMember();
        handleResponse(session.addOwner(founderId, userId, storeId));
        return userId;
    }

    private int registerAndLoginAsStoreManager(int founderId, int storeId){
        int userId = registerAndLoginAsRegularMember();
        handleResponse(session.addManager(founderId, userId, storeId));
        return userId;
    }

    private Response<VoidResponse> purchase(int userId){
        return session.purchaseProductCart(userId, "1234234534564567", "11",
                "2024", "noder", "123", "244545654",
                "some address", "some city", "israel", "71000232");
    }

    private void changeProductQuantityInCart(int userId, int storeId, int productId, int quantity){
        try{
            session.changeProductQuantityInCart(userId, storeId, productId, quantity);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void AccessHiddenProduct(){
        Pair<Integer, Integer> userAndStoreIds = registerAndLoginAsStoreFounder();
        int founderId = userAndStoreIds.getFirst();
        int storeId = userAndStoreIds.getSecond();
        int productId = handleResponse(session.addProduct(founderId, storeId, "productName", "productCategory", 10.0, 10, "description"));
        handleResponse(session.hideStore(founderId, storeId));

        int guestId = enterAsGuest();
        int memberId = registerAndLoginAsRegularMember();
        int managerId = registerAndLoginAsStoreManager(founderId, storeId);
        int ownerId = registerAndLoginAsStoreOwner(founderId, storeId);

        assertEquals(0, handleResponse(session.search("productName")).size());
        assertTrue(session.getStoreProductInfo(guestId, storeId, productId).didntSucceed());
        assertTrue(session.getStoreInfo(guestId, storeId).didntSucceed());
        assertTrue(session.getStoreProductInfo(memberId, storeId, productId).didntSucceed());
        assertTrue(session.getStoreInfo(memberId, storeId).didntSucceed());
        assertTrue(session.getStoreProductInfo(managerId, storeId, productId).didntSucceed());
        assertTrue(session.getStoreInfo(managerId, storeId).didntSucceed());
        assertFalse(session.getStoreProductInfo(ownerId, storeId, productId).didntSucceed());
        assertFalse(session.getStoreInfo(ownerId, storeId).didntSucceed());
        assertFalse(session.getStoreProductInfo(founderId, storeId, productId).didntSucceed());
        assertFalse(session.getStoreInfo(founderId, storeId).didntSucceed());

        checkTraffic(1, 4, 0, 0, 0);
        reenterSystem(memberId, managerId, ownerId, founderId);
        checkTraffic(0, 1, 1, 2, 0);

        checkSystemIncome(0);
    }

    @Test
    public void purchaseHiddenOrDeletedProduct(){
        Pair<Integer, Integer> userAndStoreIds = registerAndLoginAsStoreFounder();
        int founderId = userAndStoreIds.getFirst();
        int storeId = userAndStoreIds.getSecond();
        int productId = handleResponse(session.addProduct(founderId, storeId, "productName", "productCategory", 10.0, 10, "description"));

        int guestId = enterAsGuest();
        int memberId = registerAndLoginAsRegularMember();
        int managerId = registerAndLoginAsStoreManager(founderId, storeId);
        int ownerId = registerAndLoginAsStoreOwner(founderId, storeId);

        handleResponse(session.hideStore(founderId, storeId));
        assertTrue(session.addProductToCart(guestId, productId, storeId).didntSucceed());
        assertTrue(session.addProductToCart(memberId, productId, storeId).didntSucceed());
        assertTrue(session.addProductToCart(managerId, productId, storeId).didntSucceed());
        assertTrue(session.addProductToCart(ownerId, productId, storeId).didntSucceed());
        assertTrue(session.addProductToCart(founderId, productId, storeId).didntSucceed());

        handleResponse(session.unhideStore(founderId, storeId));
        handleResponse(session.addProductToCart(guestId, productId, storeId));
        handleResponse(session.addProductToCart(memberId, productId, storeId));
        handleResponse(session.addProductToCart(managerId, productId, storeId));
        handleResponse(session.addProductToCart(ownerId, productId, storeId));
        handleResponse(session.addProductToCart(founderId, productId, storeId));

        handleResponse(session.hideStore(founderId, storeId));
        session.startPurchaseBasketTransaction(guestId, List.of());
        session.startPurchaseBasketTransaction(memberId, List.of());
        session.startPurchaseBasketTransaction(managerId, List.of());
        session.startPurchaseBasketTransaction(ownerId, List.of());
        session.startPurchaseBasketTransaction(founderId, List.of());
        assertTrue(purchase(guestId).didntSucceed());
        assertTrue(purchase(memberId).didntSucceed());
        assertTrue(purchase(managerId).didntSucceed());
        assertTrue(purchase(ownerId).didntSucceed());
        assertTrue(purchase(founderId).didntSucceed());

        handleResponse(session.unhideStore(founderId, storeId));

        assertEquals(0, handleResponse(session.getCartContent(guestId)).size());
        assertEquals(0, handleResponse(session.getCartContent(memberId)).size());
        assertEquals(0, handleResponse(session.getCartContent(managerId)).size());
        assertEquals(0, handleResponse(session.getCartContent(ownerId)).size());
        assertEquals(0, handleResponse(session.getCartContent(founderId)).size());

        handleResponse(session.addProductToCart(guestId, productId, storeId));
        handleResponse(session.addProductToCart(memberId, productId, storeId));
        handleResponse(session.addProductToCart(managerId, productId, storeId));
        handleResponse(session.addProductToCart(ownerId, productId, storeId));
        handleResponse(session.addProductToCart(founderId, productId, storeId));

        handleResponse(session.startPurchaseBasketTransaction(guestId, List.of()));
        handleResponse(session.startPurchaseBasketTransaction(memberId, List.of()));
        handleResponse(session.startPurchaseBasketTransaction(managerId, List.of()));
        handleResponse(session.startPurchaseBasketTransaction(ownerId, List.of()));
        handleResponse(session.startPurchaseBasketTransaction(founderId, List.of()));

        handleResponse(session.removeProduct(founderId, storeId, productId));

//        assertTrue(purchase(guestId).didntSucceed());
//        assertTrue(purchase(memberId).didntSucceed());
//        assertTrue(purchase(managerId).didntSucceed());
//        assertTrue(purchase(ownerId).didntSucceed());
//        assertTrue(purchase(founderId).didntSucceed());

        checkTraffic(1, 4, 0, 0, 0);
        reenterSystem(memberId, managerId, ownerId, founderId);
        checkTraffic(0, 1, 1, 2, 0);

        checkSystemIncome(0);
    }

    @Test
    public void purchaseDeletedStore() {
        Pair<Integer, Integer> userAndStoreIds = registerAndLoginAsStoreFounder();
        int founderId = userAndStoreIds.getFirst();
        int storeId = userAndStoreIds.getSecond();
        int productId = handleResponse(session.addProduct(founderId, storeId, "productName", "productCategory", 10.0, 10, "description"));

        int guestId = enterAsGuest();
        int memberId = registerAndLoginAsRegularMember();
        int managerId = registerAndLoginAsStoreManager(founderId, storeId);
        int ownerId = registerAndLoginAsStoreOwner(founderId, storeId);

        handleResponse(session.addProductToCart(guestId, productId, storeId));
        handleResponse(session.addProductToCart(memberId, productId, storeId));
        handleResponse(session.addProductToCart(managerId, productId, storeId));
        handleResponse(session.addProductToCart(ownerId, productId, storeId));
        handleResponse(session.addProductToCart(founderId, productId, storeId));

        handleResponse(session.deleteStore(adminId, storeId));

        assertTrue(purchase(guestId).didntSucceed());
        assertTrue(purchase(memberId).didntSucceed());
        assertTrue(purchase(managerId).didntSucceed());
        assertTrue(purchase(ownerId).didntSucceed());
        assertTrue(purchase(founderId).didntSucceed());

        checkTraffic(1, 4, 0, 0, 0);
        reenterSystem(memberId, managerId, ownerId, founderId);
        checkTraffic(0, 4, 0, 0, 0);

        checkSystemIncome(0);

    }

    @Test
    public void managingWorkersInDeletedStore(){
        Pair<Integer, Integer> userAndStoreIds = registerAndLoginAsStoreFounder();
        int founderId = userAndStoreIds.getFirst();
        int storeId = userAndStoreIds.getSecond();
        int managerId = registerAndLoginAsStoreManager(founderId, storeId);

        int futureManagerId = registerAndLoginAsRegularMember();
        int futureOwnerId = registerAndLoginAsRegularMember();
        handleResponse(session.deleteStore(adminId, storeId));
        assertTrue(session.addManager(founderId, futureManagerId, storeId).didntSucceed());
        assertTrue(session.addOwner(founderId, futureOwnerId, storeId).didntSucceed());
        assertTrue(session.removeManager(founderId, managerId, storeId).didntSucceed());
        assertTrue(session.addIndividualPermission(founderId, managerId, storeId, UserPermissions.IndividualPermission.STOCK).didntSucceed());

        checkTraffic(0, 4, 0, 0, 0);
        reenterSystem(managerId, futureManagerId, futureOwnerId, founderId);
        checkTraffic(0, 4, 0, 0, 0);

        checkSystemIncome(0);
    }

    @Test
    public void purchaseProductWithConditionalDiscountWithCoupon(){
        Pair<Integer, Integer> userAndStoreIds = registerAndLoginAsStoreFounder();
        int founderId = userAndStoreIds.getFirst();
        int storeId = userAndStoreIds.getSecond();
        int productId = handleResponse(session.addProduct(founderId, storeId, "productName", "productCategory", 123, 100, "description"));

        int guestId = enterAsGuest();
        int memberId = registerAndLoginAsRegularMember();

        handleResponse(session.addProductToCart(guestId, productId, storeId));
        handleResponse(session.addProductToCart(memberId, productId, storeId));
        handleResponse(session.addProductToCart(founderId, productId, storeId));

        changeProductQuantityInCart(guestId, storeId, productId, 2);
        changeProductQuantityInCart(memberId, storeId, productId, 2);

        int conditionId = handleResponse(session.addStoreQuantityCondition(storeId, founderId, 2,2));
        int discountId = handleResponse(session.addStoreDiscount(storeId, founderId, conditionId, 0.3, today.plusYears(1).toLocalDate(), "noder"));
        handleResponse(session.addDiscountAsRoot(storeId, founderId, discountId));

        handleResponse(session.startPurchaseBasketTransaction(guestId, List.of("noder")));
        handleResponse(session.startPurchaseBasketTransaction(memberId, List.of()));
        handleResponse(session.startPurchaseBasketTransaction(founderId, List.of("noder")));

        handleResponse(purchase(guestId));
        handleResponse(purchase(memberId));
        handleResponse(purchase(founderId));

        assertTrue(handleResponse(session.getCartContent(guestId)).isEmpty());
        assertTrue(handleResponse(session.getCartContent(memberId)).isEmpty());
        assertTrue(handleResponse(session.getCartContent(founderId)).isEmpty());

        checkTraffic(1, 2, 0, 0, 0);
        reenterSystem(memberId, founderId);
        checkTraffic(0, 1, 0, 1, 0);

        checkSystemIncome(123*2*0.7 + 123*3);


    }




}
