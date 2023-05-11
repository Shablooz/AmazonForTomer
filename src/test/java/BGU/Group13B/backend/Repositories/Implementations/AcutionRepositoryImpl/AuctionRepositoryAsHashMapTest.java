package BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IAuctionRepository;
import BGU.Group13B.service.callbacks.AddToUserCart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

class AuctionRepositoryAsHashMapTest {
    private static final IAuctionRepository auctionRepositoryAsHashMap;
    private static final AddToUserCart addToUserCart;
    private static final int storeId = 1;
    private static int productId = 1;
    private static final int userId = 1;
    private static final double startingPrice = 10;
    private static LocalDateTime endTime;

    static {
        addToUserCart = Mockito.mock(AddToUserCart.class);
        auctionRepositoryAsHashMap = new AuctionRepositoryAsHashMap(addToUserCart);
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }


    @Test
    void addNewAuctionForAProductSimpleUpdateAndAutomaticTimeout() {
       /* endTime = LocalDateTime.now().plusSeconds(3);
        int currentProductId = productId;
        Mockito.doNothing().when(addToUserCart).apply(userId, storeId, currentProductId);

        auctionRepositoryAsHashMap.addNewAuctionForAProduct(productId++, startingPrice, storeId, endTime);

        auctionRepositoryAsHashMap.updateAuction(currentProductId, storeId, 22, userId);

        //auctionRepositoryAsHashMap.endAuction(productId, storeId);


        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(addToUserCart,
                Mockito.times(1)).apply(userId, storeId, currentProductId);
*/
    }

    @Test
    void endAuctionBeforeTimeout() {
        /*try {
            endTime = LocalDateTime.now().plusSeconds(2);
            int currentProductId = productId;

            auctionRepositoryAsHashMap.addNewAuctionForAProduct(productId++, startingPrice, storeId, endTime);
            auctionRepositoryAsHashMap.endAuction(currentProductId, storeId);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(addToUserCart,
                Mockito.times(0)).apply(userId, storeId, productId);
    */}

    @Test
    void updateAuction() {
    }

    @Test
    void getAuctionInfo() {
    }
}