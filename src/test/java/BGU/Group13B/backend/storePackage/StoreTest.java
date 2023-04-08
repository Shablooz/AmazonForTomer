package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import BGU.Group13B.service.callbacks.AddToUserCart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {


    private static Store store;
    private static int managerWithPermission;
    private static int managerWithoutPermission;
    private static int storeId;
    private static int productId;
    private static int amount;
    private static double proposedPrice;
    private static AddToUserCart addToUserCart;
    private static BIDRepositoryAsList bidRepository;

    @BeforeEach
    void setUp() {
        /*initializing data*/

        var storePermission = Mockito.mock(StorePermission.class);
        bidRepository = new BIDRepositoryAsList();
        addToUserCart = Mockito.mock(AddToUserCart.class);
        String functionName = "purchaseProposalSubmit";
        managerWithPermission = 1;
        managerWithoutPermission = 2;
        storeId = 1;
        productId = 1;
        amount = 1;
        proposedPrice = 10.0;

        /*Defining behaviour */

        Mockito.when(storePermission.checkPermission(managerWithPermission)).thenReturn(true);
        Mockito.when(storePermission.checkPermission(managerWithoutPermission)).thenReturn(false);
        Mockito.when(storePermission.getAllUsersWithPermission(functionName)).thenReturn(Set.of(managerWithPermission));
        /*verify the method invocation*/
        Mockito.doNothing().when(addToUserCart).apply(managerWithPermission, storeId, productId, amount);

        store = new Store(null, null, null, null, null,
                null, storePermission, addToUserCart, bidRepository, storeId);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void purchaseProposalSubmitWithPermissions() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);
            Assertions.assertEquals(bidRepository.getBID(0),
                    Optional.of(new BID(0, managerWithPermission, productId, proposedPrice, amount)));
            assertFalse(bidRepository.getBID(0).orElseThrow(() ->
                    new NoSuchElementException("No such bid with id 0")).isRejected());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void purchaseProposalSubmitWithOutPermissions() {
        try {
            store.purchaseProposalSubmit(managerWithoutPermission, productId, proposedPrice, amount);
        } catch (NoPermissionException e) {
            Assertions.assertEquals("User " + managerWithoutPermission + " has no permission to add product to store " + storeId, e.getMessage());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void purchaseProposalApprove() {
        //write a test for this function
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);

            store.purchaseProposalApprove(managerWithPermission, 0);
            Mockito.verify(addToUserCart,
                    Mockito.times(1)).apply(managerWithPermission, storeId, productId, amount);
            Assertions.assertFalse(bidRepository.getBID(0).orElseThrow().isRejected());

        } catch (NoPermissionException | NoSuchElementException e) {
            fail();
        }
    }

    @Test
    void purchaseProposalReject() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);

            store.purchaseProposalReject(managerWithPermission, 0);
            Mockito.verify(addToUserCart,
                    Mockito.times(0)).apply(managerWithPermission, storeId, productId, amount);

            Assertions.assertEquals(bidRepository.getBID(0), Optional.empty());

        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }
}