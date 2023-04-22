package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepositoryAsList;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IStoreRepositoryTest {
    private final IStoreRepository storeRepository = SingletonCollection.getStoreRepository();

    @BeforeEach
    void setUp() {
        storeRepository.reset();
        SingletonCollection.getStorePurchasePolicyRepository().reset();
        SingletonCollection.getStorePermissionRepository().reset();
    }

    @AfterEach
    void tearDown() {
        storeRepository.reset();
        SingletonCollection.getStorePurchasePolicyRepository().reset();
        SingletonCollection.getStorePermissionRepository().reset();
    }

    @Test
    void addStoreTest_simpleCase_success(){
        int founderId = 1;
        String storeName = "storeName";
        String category = "category";

        int storeId = storeRepository.addStore(founderId, storeName, category);
        assertEquals(0, storeId);

        Store result = storeRepository.getStore(storeId);
        assertEquals(storeName, result.getStoreName());
        assertEquals(category, result.getCategory());
    }

    @RepeatedTest(10)
    void addStoreThreadTest_success(){
        int founderId = 1;
        String storeNamePrefix = "storeName";
        String category = "category";
        int numOfThreads = 100;

        Thread[] threads = new Thread[numOfThreads];
        int[] storeIds = new int[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                int storeId = storeRepository.addStore(founderId, storeNamePrefix + finalI, category);
                storeIds[finalI] = storeId;

                Store result = storeRepository.getStore(storeId);
                assertEquals(storeNamePrefix + finalI, result.getStoreName());
            });
        }
        for (int i = 0; i < numOfThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < numOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //checking all the store ids are unique
        for (int i = 0; i < numOfThreads; i++) {
            for (int j = i + 1; j < numOfThreads; j++) {
                assertNotEquals(storeIds[i], storeIds[j]);
            }
        }
    }
}