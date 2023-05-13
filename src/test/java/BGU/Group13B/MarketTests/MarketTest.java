package BGU.Group13B.MarketTests;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.*;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.info.ProductInfo;
import org.junit.jupiter.api.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MarketTest {

    private static Market market;
    private static String productName1;
    private static String productName2;
    private static String productName3;
    private static String productName4;
    private static String productCategory1;
    private static String productCategory2;
    private static String productKeywords;
    private static int minPrice;
    private static int maxPrice;
    private static IProductRepository productRepository;
    private static IStoreRepository storeRepository;
    private static int storeId1;
    private static int storeId2;
    private static int storeId3;
    private static int productId1;
    private static int productId2;
    private static int productId3;
    private static int productId4;
    private static int productId5;


    @BeforeEach
    void setUp() {

        productName1 = "Dell computer";
        productName2 = "computer";
        productName3 = "Gaming laptop";
        productName4 = "Mac laptop";
        productCategory1 = "electronics";
        productCategory2 = "gaming";
        productKeywords = "computer laptop pc";
        minPrice = 1000;
        maxPrice = 3000;
        market = new Market();
        productRepository = SingletonCollection.getProductRepository();
        storeRepository = SingletonCollection.getStoreRepository();

        try {
            storeId1 = storeRepository.addStore(2, "Electronics store", "electronics");
            productId1 = productRepository.addProduct(storeId1, "Dell computer", "electronics", 1000, 50, "Good and stable laptop.");
            storeId2 = storeRepository.addStore(2, "Electronics store", "electronics");
            productId2 = productRepository.addProduct(storeId2, "HP computer", "electronics", 6000, 50, "Good and stable pc.");
            productId3 = productRepository.addProduct(storeId2, "Dell computer A12", "electronics", 2000, 50, "Good and stable laptop.");
            storeId3 = storeRepository.addStore(2, "Electronics store", "electronics");
            productId4 = productRepository.addProduct(storeId3, "Mac laptop", "electronics devices", 10000, 50, "Good and stable .");
            productId5 = productRepository.addProduct(storeId3, "Gaming laptop", "gaming", 10000, 50, "Good and stable computer.");


        } catch (Exception e) {
            fail("Exception was thrown");
        }

    }

    public void deleteAllData() {
        productRepository.removeStoreProduct(productId1, storeId1);
        productRepository.removeStoreProduct(productId2, storeId2);
        productRepository.removeStoreProduct(productId3, storeId2);
        productRepository.removeStoreProduct(productId4, storeId3);
        productRepository.removeStoreProduct(productId5, storeId3);
        storeRepository.removeStore(storeId1);
        storeRepository.removeStore(storeId2);
        storeRepository.removeStore(storeId3);
    }

    @AfterEach
    void tearDown() {
        deleteAllData();
    }

    @Test
    void searchProductByName() {
        try {
            List<ProductInfo> products = market.searchProductByName(productName1);
            assertEquals(2, products.size());
            assertEquals(2, products.stream().filter(p -> p.name().contains(productName1)).count());

        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void searchProductByCategory() {
        try {
            List<ProductInfo> products = market.searchProductByCategory(productCategory1);
            assertEquals(4, products.size());
            assertEquals(4, products.stream().filter(p -> p.category().contains(productCategory1)).count());

        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    private boolean checkIfContainsSomeKeywords(String keywords, String description) {
        String[] keywordsArray = keywords.split(" ");
        List<String> keywordsAsList = Arrays.asList(keywordsArray);
        keywordsAsList.replaceAll(String::toLowerCase);
        String modifiedDescription = description.toLowerCase();
        for (String keyword : keywordsAsList) {
            if (modifiedDescription.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void searchProductByKeywords() {
        try {
            List<ProductInfo> products = market.searchProductByKeywords(productKeywords);
            assertEquals(4, products.size());
            assertEquals(4, products.stream().filter(p -> checkIfContainsSomeKeywords(productKeywords, p.description())).count());
        } catch (Exception e) {
        }
    }

    private boolean checkRange(double min, double max, double value) {
        return value >= min && value <= max;
    }

    @Test
    void filterByPriceRange() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByPriceRange(0, 2000);
            assertEquals(2, products.size());
            assertEquals(2, products.stream().filter(p -> checkRange(minPrice, maxPrice, p.getPrice())).count());

        } catch (IllegalArgumentException e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByProductRank_nonEmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByProductRank(0, 5);
            assertEquals(3, products.size());
            assertEquals(3, products.stream().filter(p -> checkRange(0, 5, p.getRank())).count());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByProductRank_EmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByProductRank(5, 5);
            assertEquals(0, products.size());
            assertEquals(0, products.stream().filter(p -> checkRange(5, 5, p.getRank())).count());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByCategory_fail() {
        try {
            market.searchProductByName(productName4);
            List<Product> products = market.filterByCategory(productCategory2);
            assertEquals(0, products.size());
            assertEquals(0, products.stream().filter(p -> p.getCategory().contains(productCategory2)).count());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void filterByCategory_success() {
        try {
            market.searchProductByName(productName3);
            List<Product> products = market.filterByCategory(productCategory2);
            assertEquals(1, products.size());
            assertEquals(1, products.stream().filter(p -> p.getCategory().contains(productCategory2)).count());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void filterByStoreRank_nonEmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByStoreRank(0, 5);
            assertEquals(3, products.size());
            assertEquals(3, products.stream().filter(p -> checkRange(0, 5, p.getRank())).count());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByStoreRank_EmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByStoreRank(5, 5);
            assertEquals(0, products.size());
            assertEquals(0, products.stream().filter(p -> checkRange(5, 5, p.getRank())).count());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }


}
