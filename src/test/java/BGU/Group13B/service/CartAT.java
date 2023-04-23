/*
package BGU.Group13B.service;

import BGU.Group13B.backend.User.BasketProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartAT extends ProjectTest {

    */
/*@Test
    public void addToCart_Valid() {
        session.addToCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()],
                productIds[ProductsIndex.PRODUCT_1.ordinal()]);

        BasketProduct basketProduct =
                session.getBasketProduct(storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()]).getQuantity();
        Assertions.assertEquals(1, basketProduct.getQuantity());

        session.setBasketProductQuantity(storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()], 2);
        Assertions.assertEquals(2, basketProduct.getQuantity());
    }

    @Test
    public void addToCart_NotValid() {
        Exception exception = assertThrows(Exception.class, () -> session.addToCart(userIds[UsersIndex.GUEST.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()]));
        assertEquals("BGU.Group13B.backend.storePackage.permissions.NoPermissionException: Only registered users can add products to cart", exception.getMessage());
    }

    @Test
    public void removeFromCart_Valid() {
        session.addToCart(userIds[UsersIndex.GUEST.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()]);
        session.removeFromCart(userIds[UsersIndex.GUEST.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()], 1);
        assertEquals(session.getCart(userIds[UsersIndex.GUEST.ordinal()]).size(), 0);
    }

    @Test
    public void removeFromCart_NotValid() {
        Exception exception = assertThrows(Exception.class, () -> session.removeFromCart(userIds[UsersIndex.GUEST.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()]));
        assertEquals("BGU.Group13B.backend.storePackage.permissions.NoPermissionException: Only registered users can remove products from cart", exception.getMessage());
    }

    @Test
    public void getCart_Valid() {
        session.addToCart(userIds[UsersIndex.GUEST.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()]);
        assertEquals(session.getCart(userIds[UsersIndex.GUEST.ordinal()]).size(), 1);
    }

    @Test
    public void getCart_NotValid() {
        Exception exception = assertThrows(Exception.class, () -> session.getCart(userIds[UsersIndex.GUEST.ordinal()]));
        assertEquals("BGU.Group13B.backend.storePackage.permissions.NoPermissionException: Only registered users can get cart", exception.getMessage());
    }
*//*




}
*/
