package BGU.Group13B.service;

import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class StoreAT extends ProjectTest{


    @Test
    void addStore_member_success(){
        int storeId = addStore(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "my store", categories[0]);
        StoreInfo storeInfo = getStoreInfo(userIds[UsersIndex.STORE_OWNER_1.ordinal()], storeId);
        assertEquals("my store", storeInfo.storeName());
    }

    @Test
    void addStore_guest_fail(){
        assertTrue(session.addStore(userIds[UsersIndex.GUEST.ordinal()], "my store", categories[0]).didntSucceed());
    }

    @Test
    void addProduct_storeOwner_success(){
        int productId = addProduct(userIds[UsersIndex.STORE_OWNER_1.ordinal()],
                (int) stores[0][0], "my product", "my description",
                10, 10, "my category");

        ProductInfo productInfo = getStoreProductInfo(userIds[UsersIndex.STORE_OWNER_1.ordinal()], (int) stores[0][0], productId);
        assertEquals("my product", productInfo.name());
    }

    @Test
    void addProduct_guest_fail(){
            assertTrue(session.addProduct(userIds[UsersIndex.GUEST.ordinal()],
                    (int) stores[0][0], "my product", "my description",
                    10, 10, "my category").didntSucceed());
    }

    @Test
    void removeProduct_storeOwner_success(){
        removeProduct((int) products[0][2], (int) products[0][1], (int) products[0][0]);
        assertTrue(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).didntSucceed());
    }

    @Test
    void removeProduct_guest_fail(){
        assertTrue(session.removeProduct(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0]).didntSucceed());
    }

    @Test
    void setProductQuantity_storeOwner_success(){
        int stockQuantity = 42;
        setProductStockQuantity((int) products[0][2], (int) products[0][1], (int) products[0][0], stockQuantity);
        int quantityAfterSet = getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).stockQuantity();
        assertEquals(stockQuantity, quantityAfterSet);
    }

    @Test
    void setProductQuantity_guest_fail(){

        assertTrue(session.setProductStockQuantity(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], 42).didntSucceed());
    }

    @Test
    void setProductQuantity_negativeQuantity_fail(){
        assertTrue(session.setProductStockQuantity((int) products[0][2], (int) products[0][1], (int) products[0][0], -1).didntSucceed());
    }

    @Test
    void setProductPrice_storeOwner_success(){
        double price = 42;
        setProductPrice((int) products[0][2], (int) products[0][1], (int) products[0][0], price);
        double priceAfterSet = getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).price();
        assertEquals(price, priceAfterSet);
    }

    @Test
    void setProductPrice_guest_fail(){
        assertTrue(session.setProductPrice(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], 42).didntSucceed());
    }

    @Test
    void setProductPrice_negativePrice_fail(){
        assertTrue(session.setProductPrice((int) products[0][2], (int) products[0][1], (int) products[0][0], -1).didntSucceed());
    }

    @Test
    void setProductName_storeOwner_success(){
        String name = "new name";
        setProductName((int) products[0][2], (int) products[0][1], (int) products[0][0], name);
        String nameAfterSet = getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).name();
        assertEquals(name, nameAfterSet);
    }

    @Test
    void setProductName_guest_fail(){
        assertTrue(session.setProductName(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new name").didntSucceed());
    }

    @Test
    void setProductDescription_storeOwner_success(){
        String description = "new description";
        setProductDescription((int) products[0][2], (int) products[0][1], (int) products[0][0], description);
        String descriptionAfterSet = getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).description();
        assertEquals(description, descriptionAfterSet);
    }

    @Test
    void setProductDescription_guest_fail(){
        assertTrue(session.setProductDescription(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new description").didntSucceed());
    }

    @Test
    void setProductCategory_storeOwner_success(){
        String category = "new category";
        setProductCategory((int) products[0][2], (int) products[0][1], (int) products[0][0], category);
        String categoryAfterSet = getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).category();
        assertEquals(category, categoryAfterSet);
    }

    @Test
    void setProductCategory_guest_fail(){
        assertTrue(session.setProductCategory(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new category").didntSucceed());
    }


}
