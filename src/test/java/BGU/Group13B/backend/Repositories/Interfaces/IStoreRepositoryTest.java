package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class IStoreRepositoryTest {
    private  IStoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        SingletonCollection.reset_system(false);
        SingletonCollection.setSaveMode(false);
        storeRepository= SingletonCollection.getStoreRepository();
    }

    @AfterEach
    void tearDown() {
        SingletonCollection.reset_system(false);
        SingletonCollection.setSaveMode(false);
    }

    @Test
    void addStoreTest_simpleCase_success(){
        SingletonCollection.setSaveMode(false);
        int founderId = getFounderId();
        String storeName = "storeName";
        String category = "category";

        int storeId = storeRepository.addStore(founderId, storeName, category);
        assertEquals(0, storeId);

        Store result = storeRepository.getStore(storeId);
        assertEquals(storeName, result.getStoreName());
        assertEquals(category, result.getCategory());
    }

    public static int getFounderId() {
        int founderId = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(founderId, new User(founderId));
        return founderId;
    }

    @RepeatedTest(10)
    void addStoreThreadTest_success(){
        int founderId = getFounderId();
        String storeNamePrefix = "storeName";
        String category = "category";
        int numOfThreads = 100;

        Thread[] threads = new Thread[numOfThreads];
        int[] storeIds = new int[numOfThreads];
        String[] storeNames = new String[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                int storeId = storeRepository.addStore(founderId, storeNamePrefix + finalI, category);
                storeIds[finalI] = storeId;

                Store result = storeRepository.getStore(storeId);
                storeNames[finalI] = result.getStoreName();
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

        //checking all the store names are correct
        for (int i = 0; i < numOfThreads; i++) {
            assertEquals(storeNamePrefix + i, storeNames[i]);
        }
    }
}