package BGU.Group13B.service;

import BGU.Group13B.backend.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexAT{

    private Session session;
    private final int adminId = 1;

    @BeforeEach
    public void setup() {
        SingletonCollection.reset_system();
        session = SingletonCollection.getSession();
    }

    private <T> T handleResponse(Response<T> response) {
        if(response.didntSucceed()) {
            fail();
        }
        return response.getData();
    }

    private void checkTraffic(int addedGuests, int addedRegularMembers, int addedStoreManagersThatAreNotOwners, int addedStoreOwners, int addedAdmins){
        var traffic = handleResponse(session.getUserTrafficOfRange(adminId, LocalDate.now(), LocalDate.now()));
        assertEquals(addedGuests, traffic.numOfGuests());
        assertEquals(addedRegularMembers, traffic.numOfRegularMembers());
        assertEquals(addedStoreManagersThatAreNotOwners, traffic.numOfStoreManagersThatAreNotOwners());
        assertEquals(addedStoreOwners, traffic.numOfStoreOwners());
        assertEquals(addedAdmins, traffic.numOfAdmins());
    }

    private void checkSystemIncome(double income){
        //TODO
    }

    private void checkStoreIncome(int storeId, double income){
        //TODO
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

    private int registerAndLoginAsRegularMember(String username, String password){
        try{
            int id = session.enterAsGuest();
            session.register(id, username, password, username + "@gmail.com", "", "", "", LocalDate.now());
            return session.login(id, username, password, "", "", "");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            fail();
            return -1;
        }
    }

    private Pair<Integer /*userId*/, Integer /*storeId*/> registerAndLoginAsStoreFounder(String username, String password){
        int userId = registerAndLoginAsRegularMember(username, password);
        int storeId = handleResponse(session.addStore(userId, "storeName", "storeCategory"));
        return Pair.of(userId, storeId);
    }

    private int registerAndLoginAsStoreOwner(String username, String password, int founderId, int storeId){
        int userId = registerAndLoginAsRegularMember(username, password);
        handleResponse(session.addOwner(founderId, userId, storeId));
        return userId;
    }

    private int registerAndLoginAsStoreManager(String username, String password, int founderId, int storeId){
        int userId = registerAndLoginAsRegularMember(username, password);
        handleResponse(session.addManager(founderId, userId, storeId));
        return userId;
    }

    @Test
    public void AccessHiddenProduct(){
        Pair<Integer, Integer> userAndStoreIds = registerAndLoginAsStoreFounder("Username1", "Password1");
        int founderId = userAndStoreIds.getFirst();
        int storeId = userAndStoreIds.getSecond();
        int productId = handleResponse(session.addProduct(founderId, storeId, "productName", "productCategory", 10.0, 10, "description"));
        handleResponse(session.hideStore(founderId, storeId));

        int guestId = enterAsGuest();
        int memberId = registerAndLoginAsRegularMember("Username2", "Password2");
        int managerId = registerAndLoginAsStoreManager("Username4", "Password4", founderId, storeId);
        int ownerId = registerAndLoginAsStoreOwner("Username3", "Password3", founderId, storeId);

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
    }




}
