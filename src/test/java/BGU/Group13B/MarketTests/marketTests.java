package BGU.Group13B.MarketTests;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.*;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class marketTests {

    private static Market market;
    private static String productName1;
    private static String productName2;
    private static String productName3;
    private static String productName4;
    private static String productCategory1;
    private static String productCategory2;
    private static List<String> productKeywords;
    private static int minPrice;
    private static int maxPrice;
    private static IProductRepository productRepository;
    private static IStoreRepository storeRepository;

    @BeforeEach
    void setUp() {

       productName1 = "Dell computer";
       productName2 = "computer";
       productName3 = "Gaming laptop";
       productName4 = "Mac laptop";
       productCategory1= "electronics";
       productCategory2= "gaming";
       productKeywords = List.of("computer", "laptop", "pc");
       minPrice = 1000;
       maxPrice = 3000;
       market = new Market();
       productRepository = SingletonCollection.getProductRepository();
       storeRepository = SingletonCollection.getStoreRepository();

       try {
           int storeId1= storeRepository.addStore(2,"Electronics store", "electronics");
           productRepository.addProduct(storeId1,"Dell computer","electronics",1000,50, "Good and stable laptop.");
           int storeId2= storeRepository.addStore(2,"Electronics store", "electronics");
           productRepository.addProduct(storeId2,"HP computer","electronics",6000,50, "Good and stable pc.");
           productRepository.addProduct(storeId2,"Dell computer A12","electronics",2000,50, "Good and stable laptop.");
           int storeId3= storeRepository.addStore(2,"Electronics store", "electronics");
           productRepository.addProduct(storeId3,"Mac laptop","electronics devices",10000,50, "Good and stable .");
           productRepository.addProduct(storeId3,"Gaming laptop","gaming",10000,50, "Good and stable computer.");


       }catch (Exception e) {
           fail("Exception was thrown");
       }
//        Mockito.when(market.searchProductByName(productName)).thenReturn(List.of(productId));
//        Mockito.when(market.searchProductByCategory(productCategory)).thenReturn(List.of(productId));
//        Mockito.when(market.searchProductByKeywords(productKeywords)).thenReturn(List.of(productId));
//        Mockito.when(market.filterByProductRank(minProductRank, maxProductRank)).thenReturn(List.of(productId));
//        Mockito.when(market.filterByStoreRank(minStoreRank, maxStoreRank)).thenReturn(List.of(productId));
//        Mockito.when(market.filterByPriceRange(minPrice, maxPrice)).thenReturn(List.of(productId));
//        Mockito.when(market.filterByCategory(productCategory)).thenReturn(List.of(productId));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void searchProductByName() {
        try {
            List<Product> products=market.searchProductByName(productName1);
            assertEquals(products.size(),2);
            assertEquals(products.stream().filter(p->p.getName().contains(productName1)).count(),2);
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void searchProductByCategory() {
        try {
            List<Product> products=market.searchProductByCategory(productCategory1);
            assertEquals(products.size(),4);
            assertEquals(products.stream().filter(p->p.getCategory().equals(productCategory1)).count(),4);
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    private boolean checkIfContainsSomeKeywords(List<String> keywords, String description) {
        List<String> modifiedKeywords = keywords.stream().map(String::toLowerCase).toList();
        String modifiedDescription = description.toLowerCase();
        for (String keyword : modifiedKeywords) {
            if (modifiedDescription.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void searchProductByKeywords() {
        try {
            List<Product> products=market.searchProductByKeywords(productKeywords);
            assertEquals(products.size(),4);
            assertEquals(products.stream().filter(p->checkIfContainsSomeKeywords(productKeywords,p.getDescription())).count(),4);
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
            assertEquals(products.size(),2);
            assertEquals(products.stream().filter(p->checkRange(minPrice,maxPrice,p.getPrice())).count(),2);

        } catch (IllegalArgumentException e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByProductRank_nonEmptyList() {
         try {
             market.searchProductByName(productName2);
             List<Product> products = market.filterByProductRank(0, 5);
             assertEquals(products.size(),3);
             assertEquals(products.stream().filter(p->checkRange(0,5,p.getRank())).count(),3);
          } catch (Exception e) {
             fail("Exception was thrown");
         }
    }

    @Test
    void filterByProductRank_EmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByProductRank(5, 5);
            assertEquals(products.size(),0);
            assertEquals(products.stream().filter(p->checkRange(5,5,p.getRank())).count(),0);
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByCategory_fail() {
        try {
            market.searchProductByName(productName4);
            List<Product> products = market.filterByCategory(productCategory2);
            assertEquals(products.size(),0);
            assertEquals(products.stream().filter(p->p.getCategory().equals(productCategory2)).count(),0);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void filterByCategory_success() {
        try {
            market.searchProductByName(productName3);
            List<Product> products = market.filterByCategory(productCategory2);
            assertEquals(products.size(),1);
            assertEquals(products.stream().filter(p->p.getCategory().equals(productCategory2)).count(),1);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void filterByStoreRank_nonEmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByStoreRank(0, 5);
            assertEquals(products.size(),3);
            assertEquals(products.stream().filter(p->checkRange(0,5,p.getRank())).count(),3);
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void filterByStoreRank_EmptyList() {
        try {
            market.searchProductByName(productName2);
            List<Product> products = market.filterByStoreRank(5, 5);
            assertEquals(products.size(),0);
            assertEquals(products.stream().filter(p->checkRange(5,5,p.getRank())).count(),0);
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }


}
