package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.*;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.AND;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.OR;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.XOR;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.*;
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
        Product _product1 = SingletonCollection.getProductRepository().addProduct(1, "name", "category", 15.0, 3, "description");
        Product _product2 = SingletonCollection.getProductRepository().addProduct(2, "name", "category", 30.0, 3, "description");
        Product _product3 = SingletonCollection.getProductRepository().addProduct(3, "name", "category", 100.0, 3, "description");

        product1  = new BasketProduct(_product1);
        product2 = new BasketProduct(_product2);
        product3 = new BasketProduct(_product3);

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
    void satisfied_simple_product_quantity_success() {
        Condition condition = new ProductQuantityCondition(1, product1.getProductId(), 0, 2);
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }

    @Test
    void satisfied_simple_product_quantity_fail() {
        product1.setQuantity(3);
        product2.setQuantity(3);
        product3.setQuantity(3);
        Condition condition = new ProductQuantityCondition(1, product1.getProductId(), 8);
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
    void satisfied_simple_time_success() {
        Condition condition = new TimeCondition(1, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_simple_time_fail() {
        Condition condition = new TimeCondition(1, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
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

    @Test
    void satisfied_complex_xor_success() {
        Condition condition = new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_xor_fail() {
        Condition condition = new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }

    @Test
    void satisfied_complex_composition_success1() {
        Condition condition = new AND(1, new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100)), new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_composition_success2() {
        Condition condition = new AND(1, new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100)), new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_composition_fail2() {
        Condition condition = new AND(1, new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100)), new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_composition_fail3() {
        Condition condition = new AND(1, new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100)), new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }

    @Test
    void satisfied_complex_composition_fail1() {
        Condition condition = new AND(1, new XOR(1, new CategoryPriceCondition(1, "category", 0, 100), new UserAgeCondition(2, 18, 100)), new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_multiple_composition_success1(){
        Condition condition = new AND(1,
                new XOR(1,
                        new CategoryPriceCondition(1, "category", 0, 100),
                        new UserAgeCondition(2, 5, 18)),
                new OR(2,
                        new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                        new AND(4,
                                new TimeCondition(5, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                                new UserAgeCondition(6, 18, 100))));

        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_multiple_composition_fail1(){
        Condition condition = new AND(1,
                new XOR(1,
                        new CategoryPriceCondition(1, "category", 0, 100),
                        new UserAgeCondition(2, 5, 18)),
                new OR(2,
                        new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                        new AND(4,
                                new TimeCondition(5, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                                new UserAgeCondition(6, 18, 100))));

        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_multiple_composition_success2(){
        Condition condition = new AND(1,
                new XOR(1,
                        new CategoryPriceCondition(1, "category", 0, 100),
                        new UserAgeCondition(2, 18, 100)),
                new OR(2,
                        new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                        new AND(4,
                                new TimeCondition(5, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                                new UserAgeCondition(6, 18, 100))));

        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_multiple_composition_fail2(){
        Condition condition = new AND(1,
                new XOR(1,
                        new CategoryPriceCondition(1, "category", 0, 100),
                        new UserAgeCondition(2, 18, 100)),
                new OR(2,
                        new TimeCondition(3, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                        new AND(4,
                                new TimeCondition(5, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                                new UserAgeCondition(6, 18, 100))));

        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_multiple_composition_success3(){
        Condition condition = new AND(1,
                new XOR(1,
                        new CategoryPriceCondition(1, "category", 0, 100),
                        new UserAgeCondition(2, 18, 100)),
                new OR(2,
                        new TimeCondition(3, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                        new AND(4,
                                new TimeCondition(5, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                                new UserAgeCondition(6, 18, 25))));

        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(30));
        assertDoesNotThrow(() -> condition.satisfied(basketInfo, userinfo));
    }
    @Test
    void satisfied_complex_multiple_composition_fail3(){
        Condition condition = new AND(1,
                new XOR(1,
                        new CategoryPriceCondition(1, "category", 0, 100),
                        new UserAgeCondition(2, 18, 100)),
                new OR(2,
                        new TimeCondition(3, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                        new AND(4,
                                new TimeCondition(5, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                                new UserAgeCondition(6, 18, 25))));

        BasketInfo basketInfo = new BasketInfo(List.of(product1, product2, product3));
        UserInfo userinfo = new UserInfo(LocalDate.now().minusYears(10));
        assertThrows(PurchaseFailedException.class, () -> condition.satisfied(basketInfo, userinfo));
    }
}