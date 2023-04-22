package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class StoreManagementTest {
    private static IStoreRepository storeRepository;
    private static IStorePurchasePolicyRepository storePurchasePolicyRepository;
    private static IStoreDiscountsRepository storeDiscountsRepository;
    private static IProductRepository productRepository;
    private static IProductPurchasePolicyRepository productPurchasePolicyRepository;
    private static IProductDiscountsRepository productDiscountsRepository;

    @BeforeAll
    static void setUp() {
        storeRepository = SingletonCollection.getStoreRepository();
        storePurchasePolicyRepository = SingletonCollection.getStorePurchasePolicyRepository();
        storeDiscountsRepository = SingletonCollection.getStoreDiscountsRepository();
        productRepository = SingletonCollection.getProductRepository();
        productPurchasePolicyRepository = SingletonCollection.getProductPurchasePolicyRepository();
        productDiscountsRepository = SingletonCollection.getProductDiscountsRepository();
    }

    @AfterEach
    void tearDown() {
        storeRepository.reset();
        storePurchasePolicyRepository.reset();
        storeDiscountsRepository.reset();
        productRepository.reset();
        productPurchasePolicyRepository.reset();
        productDiscountsRepository.reset();
    }

    @Test
    void addProductTest_simpleCase_success(){
        int founderId = 1;
        String storeName = "storeName";
        String category = "category";
        String productName = "productName";
        double price = 10;
        int stockQuantity = 50;
        String description = "description";
        int storeId = storeRepository.addStore(founderId, storeName, category);
        Store store = storeRepository.getStore(storeId);
        try{
            int productId = store.addProduct(founderId, productName, category, price, stockQuantity, description);
            Product result = productRepository.getStoreProductById(productId, storeId);
            assertEquals(storeId, result.getStoreId());
            assertEquals(productName, result.getName());
            assertEquals(category, result.getCategory());
            assertEquals(price, result.getPrice());
            assertEquals(stockQuantity, result.getStockQuantity());
            assertEquals(description, result.getDescription());

            PurchasePolicy purchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(storeId, productId);
            assertEquals(productId, purchasePolicy.getParentId());
            assertEquals(0, purchasePolicy.getPriceLowerBound());
            assertEquals(-1, purchasePolicy.getPriceUpperBound());
            assertEquals(0, purchasePolicy.getQuantityLowerBound());
            assertEquals(-1, purchasePolicy.getQuantityUpperBound());
        }
        catch (Exception e){
            fail();
        }
    }

    @RepeatedTest(10)
    void addProductThreadTest_success(){
        AtomicBoolean failed = new AtomicBoolean(false);

        int founderId = 1;
        String storeName = "storeName";
        String category = "category";
        String productNamePrefix = "productName";
        double price = 10;
        int stockQuantity = 50;
        String description = "description";
        int storeId = storeRepository.addStore(founderId, storeName, category);
        Store store = storeRepository.getStore(storeId);

        int numOfThreads = 100;
        Thread[] threads = new Thread[numOfThreads];
        int[] productIds = new int[numOfThreads];
        Product[] products = new Product[numOfThreads];
        PurchasePolicy[] purchasePolicies = new PurchasePolicy[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try{
                    int productId = store.addProduct(founderId, productNamePrefix + finalI, category, price, stockQuantity, description);
                    productIds[finalI] = productId;

                    Product result = productRepository.getStoreProductById(productId, storeId);
                    PurchasePolicy purchasePolicy = productPurchasePolicyRepository.getPurchasePolicy(storeId, productId);
                    products[finalI] = result;
                    purchasePolicies[finalI] = purchasePolicy;
                }
                catch (Exception e){
                    System.out.println("Exception in thread " + finalI + ": " + e.getMessage());
                    failed.set(true);
                }
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
        if(failed.get())
            fail();

        // check that all the product Ids are unique
        for (int i = 0; i < numOfThreads; i++) {
            for (int j = i + 1; j < numOfThreads; j++) {
                assertNotEquals(productIds[i], productIds[j]);
            }
        }

        // check that all the products are correct
        for (int i = 0; i < numOfThreads; i++) {
            assertEquals(storeId, products[i].getStoreId());
            assertEquals(productNamePrefix + i, products[i].getName());
            assertEquals(category, products[i].getCategory());
            assertEquals(price, products[i].getPrice());
            assertEquals(stockQuantity, products[i].getStockQuantity());
            assertEquals(description, products[i].getDescription());
        }

        // check that all the purchase policies are correct
        for (int i = 0; i < numOfThreads; i++) {
            assertEquals(productIds[i], purchasePolicies[i].getParentId());
            assertEquals(0, purchasePolicies[i].getPriceLowerBound());
            assertEquals(-1, purchasePolicies[i].getPriceUpperBound());
            assertEquals(0, purchasePolicies[i].getQuantityLowerBound());
            assertEquals(-1, purchasePolicies[i].getQuantityUpperBound());
        }
    }

    @Test
    void addProductTest_noPermission_fail(){
        int founderId = 1;
        String storeName = "storeName";
        String category = "category";
        String productName = "productName";
        double price = 10;
        int stockQuantity = 50;
        String description = "description";
        int storeId = storeRepository.addStore(founderId, storeName, category);
        Store store = storeRepository.getStore(storeId);
        try{
            store.addProduct(founderId + 1, productName, category, price, stockQuantity, description);
            fail();
        }
        catch (NoPermissionException ignored){}
    }


}
