package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.AND;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.OR;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.CategoryPriceCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.CategoryQuantityCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.DateCondition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.UserAgeCondition;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {
    private BasketProduct product1;
    private BasketProduct product2;
    private BasketProduct product3;
    @BeforeEach
    void setUp() {
        SingletonCollection.reset_system();
        product1  = new BasketProduct(new Product(1,1, "name1","category", 15.0, 3, "description1"));
        product2 = new BasketProduct(new Product(2,1, "name2","category", 30.0, 3, "description2"));
        product3 = new BasketProduct(new Product(3,1, "name3","category", 100.0, 3, "description3"));
    }
    @Test
    void satisfied_simple_category_price_success() {

        Condition condition = new CategoryPriceCondition(1, "category", 0, 100);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_category_price_fail() {
        product1.setQuantity(3);
        product2.setQuantity(3);
        Condition condition = new CategoryPriceCondition(1, "category", 0, 100);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_category_quantity_success() {

        Condition condition = new CategoryQuantityCondition(1, "category", 0, 10);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_category_quantity_fail() {
        product1.setQuantity(3);
        product2.setQuantity(3);
        product3.setQuantity(3);
        Condition condition = new CategoryQuantityCondition(1, "category", 0, 8);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_date_success() {
        Condition condition = new DateCondition(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_date_fail() {
        Condition condition = new DateCondition(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_age_success() {
        Condition condition = new UserAgeCondition(1, 18, 100);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_age_fail() {
        Condition condition = new UserAgeCondition(1, 18, 100);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_or_success() {
        Condition condition = new OR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_or_fail() {
        Condition condition = new OR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_or_fail2() {
        Condition condition = new OR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 2, 18));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_and_success() {
        Condition condition = new AND(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_and_fail1() {
        Condition condition = new AND(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_and_fail2() {
        Condition condition = new AND(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }

}