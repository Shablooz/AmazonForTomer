package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexAT{

    private Session session;
    private final int adminId = 1;
    private final LocalDate today = LocalDate.now();

    private int guests;
    private int regularMembers;
    private int storeManagersThatAreNotOwners;
    private int storeOwners;
    private int admins;

    @BeforeEach
    public void setup() {
        SingletonCollection.reset_system();
        session = SingletonCollection.getSession();
        session.enterAsGuest();
        session.login(adminId, "kingOfTheSheep", "SheePLover420", "11", "11", "11");
        guests = 0;
        regularMembers = 0;
        storeManagersThatAreNotOwners = 0;
        storeOwners = 0;
        admins = 1;
    }

    private <T> T handleResponse(Response<T> response) {
        if(response.didntSucceed()) {
            fail();
        }
        return response.getData();
    }

    private void checkTraffic(int addedGuests, int addedRegularMembers, int addedStoreManagersThatAreNotOwners, int addedStoreOwners, int addedAdmins){
        var traffic = handleResponse(session.getUserTrafficOfRange(adminId, today, today));
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
        double[] historyIncome = handleResponse(session.getSystemHistoryIncome(adminId, today, today));
        assertEquals(1, historyIncome.length);
        assertEquals(income, historyIncome[0]);
    }

    private void checkStoreIncome(int storeId, double income){
        double[] historyIncome = handleResponse(session.getStoreHistoryIncome(adminId, storeId, today, today));
        assertEquals(1, historyIncome.length);
        assertEquals(income, historyIncome[0]);
    }

    private void reenterSystem(int... userIds){
        for(int userId : userIds){
            session.logout(userId);
            login(userId);
        }
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

        handleResponse(session.removeProduct(founderId, storeId, productId));

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

        checkTraffic(1, 4, 0, 0, 0);
        reenterSystem(memberId, managerId, ownerId, founderId);
        checkTraffic(0, 1, 1, 2, 0);

        checkSystemIncome(0);
    }




}
