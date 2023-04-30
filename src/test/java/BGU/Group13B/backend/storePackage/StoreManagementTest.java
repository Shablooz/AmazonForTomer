package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Interfaces.*;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.frontEnd.service.SingletonCollection;
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
    private static IStorePermissionsRepository storePermissionsRepository;

    private final int founderId = 1;
    private final String storeName = "storeName";
    private final String category = "category";
    private final String productName = "productName";
    private final double price = 10;
    private final int stockQuantity = 50;
    private final String description = "description";

    private int storeId;
    private Store store;

    @BeforeAll
    static void setUp() {
        storeRepository = SingletonCollection.getStoreRepository();
        storePurchasePolicyRepository = SingletonCollection.getStorePurchasePolicyRepository();
        storeDiscountsRepository = SingletonCollection.getStoreDiscountsRepository();
        productRepository = SingletonCollection.getProductRepository();
        productPurchasePolicyRepository = SingletonCollection.getProductPurchasePolicyRepository();
        productDiscountsRepository = SingletonCollection.getProductDiscountsRepository();
        storePermissionsRepository = SingletonCollection.getStorePermissionRepository();
    }

    @BeforeEach
    void setUpEach() {
        storeId = storeRepository.addStore(founderId, storeName, category);
        store = storeRepository.getStore(storeId);
    }


    @AfterEach
    void tearDown() {
        storeRepository.reset();
        storePurchasePolicyRepository.reset();
        storeDiscountsRepository.reset();
        productRepository.reset();
        productPurchasePolicyRepository.reset();
        productDiscountsRepository.reset();
        storePermissionsRepository.reset();
    }

    private int addProduct1() throws NoPermissionException {
        return store.addProduct(founderId, productName, category, price, stockQuantity, description);
    }

    @Test
    void addProductTest_simpleCase_success(){
        try{
            int productId = addProduct1();
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

        int numOfThreads = 100;
        Thread[] threads = new Thread[numOfThreads];
        int[] productIds = new int[numOfThreads];
        Product[] products = new Product[numOfThreads];
        PurchasePolicy[] purchasePolicies = new PurchasePolicy[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try{
                    int productId = store.addProduct(founderId, productName + finalI, category, price, stockQuantity, description);
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
            assertEquals(productName + i, products[i].getName());
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
        try{
            store.addProduct(founderId + 1, productName, category, price, stockQuantity, description);
            fail();
        }
        catch (NoPermissionException ignored){}
    }

    @Test
    void addProductTest_invalidPrice_fail(){
        double price = -10;
        try{
            store.addProduct(founderId, productName, category, price, stockQuantity, description);
            fail();
        }
        catch (Exception ignored){}
    }

    @Test
    void addProductTest_invalidStockQuantity_fail(){
        int stockQuantity = -50;
        try{
            store.addProduct(founderId, productName, category, price, stockQuantity, description);
            fail();
        }
        catch (Exception ignored){}
    }

    @Test
    void deleteProductTest_simpleCase_success(){
        int productId = -1;
        try{
            productId = addProduct1();
            Product product = productRepository.getStoreProductById(productId, storeId);
            store.removeProduct(founderId, productId);
            assertTrue(product.isDeleted());
        }
        catch (Exception e){
            fail();
        }
        try{
            store.removeProduct(founderId, productId);
            fail();
        }
        catch (Exception ignored){}
    }

    @RepeatedTest(10)
    void deleteProductThreadTest_success(){
        AtomicBoolean failed = new AtomicBoolean(false);

        int numOfThreads = 100;
        Thread[] threads = new Thread[numOfThreads];
        int[] productIds = new int[numOfThreads];
        Product[] products = new Product[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try{
                    int productId = store.addProduct(founderId, productName + finalI, category, price, stockQuantity, description);
                    productIds[finalI] = productId;

                    Product result = productRepository.getStoreProductById(productId, storeId);
                    products[finalI] = result;
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

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try{
                    store.removeProduct(founderId, productIds[finalI]);
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

        // check that all the products are deleted
        for (int i = 0; i < numOfThreads; i++) {
            assertTrue(products[i].isDeleted());
        }
    }

    @Test
    void deleteProductTest_noPermission_fail(){
        try{
            int productId = addProduct1();
            store.removeProduct(founderId + 1, productId);
            fail();
        }
        catch (NoPermissionException ignored){}
    }

    @Test
    void deleteProductTest_invalidProductId_fail(){
        try{
            int productId = addProduct1();
            store.removeProduct(founderId, productId + 1);
            fail();
        }
        catch (Exception ignored){}
    }
    
    @Test
    void editProductTest_simpleCase_success(){
        try{
            int productId = addProduct1();
            String newName = "new name";
            String newCategory = "new category";
            double newPrice = 100;
            int newStockQuantity = 100;
            String newDescription = "new description";
            
            store.setProductName(founderId, productId, newName);
            store.setProductCategory(founderId, productId, newCategory);
            store.setProductPrice(founderId, productId, newPrice);
            store.setProductStockQuantity(founderId, productId, newStockQuantity);
            store.setProductDescription(founderId, productId, newDescription);
            
            Product result = productRepository.getStoreProductById(productId, storeId);
            assertEquals(newName, result.getName());
            assertEquals(newCategory, result.getCategory());
            assertEquals(newPrice, result.getPrice());
            assertEquals(newStockQuantity, result.getStockQuantity());
            assertEquals(newDescription, result.getDescription());
        }
        catch (Exception e){
            fail();
        }
    }

    @RepeatedTest(10)
    void editProductThreadTest_success(){
        AtomicBoolean failed = new AtomicBoolean(false);

        int numOfThreads = 100;
        Thread[] threads = new Thread[numOfThreads];
        int[] productIds = new int[numOfThreads];
        Product[] products = new Product[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try{
                    int productId = store.addProduct(founderId, productName + finalI, category, price, stockQuantity, description);
                    productIds[finalI] = productId;

                    Product result = productRepository.getStoreProductById(productId, storeId);
                    products[finalI] = result;
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

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try{
                    String newName = "new name" + finalI;
                    String newCategory = "new category" + finalI;
                    double newPrice = 100 + finalI;
                    int newStockQuantity = 100 + finalI;
                    String newDescription = "new description" + finalI;

                    store.setProductName(founderId, productIds[finalI], newName);
                    store.setProductCategory(founderId, productIds[finalI], newCategory);
                    store.setProductPrice(founderId, productIds[finalI], newPrice);
                    store.setProductStockQuantity(founderId, productIds[finalI], newStockQuantity);
                    store.setProductDescription(founderId, productIds[finalI], newDescription);
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

        //check that all the products are edited correctly
        for (int i = 0; i < numOfThreads; i++) {
            String newName = "new name" + i;
            String newCategory = "new category" + i;
            double newPrice = 100 + i;
            int newStockQuantity = 100 + i;
            String newDescription = "new description" + i;

            assertEquals(newName, products[i].getName());
            assertEquals(newCategory, products[i].getCategory());
            assertEquals(newPrice, products[i].getPrice());
            assertEquals(newStockQuantity, products[i].getStockQuantity());
            assertEquals(newDescription, products[i].getDescription());
        }
    }
    
    @Test
    void editProductTest_noPermission_fail(){
        try{
            int productId = addProduct1();
            String newName = "new name";
            store.setProductName(founderId + 1, productId, newName);
            fail();
        }
        catch (NoPermissionException ignored){}
    }
    
    @Test
    void editProductTest_invalidPrice_fail(){
        try{
            int productId = addProduct1();
            double newPrice = -10;
            store.setProductPrice(founderId, productId, newPrice);
            fail();
        }
        catch (Exception ignored){}
    }

    @Test
    void editProductTest_invalidStockQuantity_fail(){
        try{
            int productId = addProduct1();
            int newStockQuantity = -10;
            store.setProductStockQuantity(founderId, productId, newStockQuantity);
            fail();
        }
        catch (Exception ignored){}
    }

    @Test
    void editProductThreadTest_editingTheSameProduct(){
        AtomicBoolean failed = new AtomicBoolean(false);
        int productId = -1;

        try {
            productId = store.addProduct(founderId, productName, category, price, stockQuantity, description);
        } catch (NoPermissionException e) {
            fail();
        }

        int numOfThreads = 100;
        Thread[] threads = new Thread[numOfThreads];
        String[] newNames = new String[numOfThreads];
        String[] newCategories = new String[numOfThreads];
        double[] newPrices = new double[numOfThreads];
        int[] newStockQuantities = new int[numOfThreads];
        String[] newDescriptions = new String[numOfThreads];

        for (int i = 0; i < numOfThreads; i++) {
            int finalI = i;
            int finalProductId = productId;
            threads[i] = new Thread(() -> {
                try{
                    String newName = "new name" + finalI;
                    String newCategory = "new category" + finalI;
                    double newPrice = 100 + finalI;
                    int newStockQuantity = 100 + finalI;
                    String newDescription = "new description" + finalI;

                    newNames[finalI] = newName;
                    newCategories[finalI] = newCategory;
                    newPrices[finalI] = newPrice;
                    newStockQuantities[finalI] = newStockQuantity;
                    newDescriptions[finalI] = newDescription;

                    store.setProductName(founderId, finalProductId, newName);
                    store.setProductCategory(founderId, finalProductId, newCategory);
                    store.setProductPrice(founderId, finalProductId, newPrice);
                    store.setProductStockQuantity(founderId, finalProductId, newStockQuantity);
                    store.setProductDescription(founderId, finalProductId, newDescription);
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


        boolean foundName = false;
        boolean foundCategory = false;
        boolean foundPrice = false;
        boolean foundStockQuantity = false;
        boolean foundDescription = false;

        //check that the product has been edited correctly
        Product product = productRepository.getStoreProductById(productId, storeId);
        for(int i = 0; i < numOfThreads; i++){
            if(product.getName().equals(newNames[i]))
                foundName = true;
            if(product.getCategory().equals(newCategories[i]))
                foundCategory = true;
            if(product.getPrice() == newPrices[i])
                foundPrice = true;
            if(product.getStockQuantity() == newStockQuantities[i])
                foundStockQuantity = true;
            if(product.getDescription().equals(newDescriptions[i]))
                foundDescription = true;
        }

        assertTrue(foundName);
        assertTrue(foundCategory);
        assertTrue(foundPrice);
        assertTrue(foundStockQuantity);
        assertTrue(foundDescription);

    }




}
