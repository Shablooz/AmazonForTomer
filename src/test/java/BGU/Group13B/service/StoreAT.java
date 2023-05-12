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
        int storeId = handleResponse(session.addStore(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "my store", categories[0]));
        StoreInfo storeInfo = handleResponse(session.getStoreInfo(userIds[UsersIndex.STORE_OWNER_1.ordinal()], storeId));
        assertEquals("my store", storeInfo.storeName());
    }

    @Test
    void addStore_guest_fail(){
        assertTrue(session.addStore(userIds[UsersIndex.GUEST.ordinal()], "my store", categories[0]).didntSucceed());
    }

    @Test
    void addProduct_storeOwner_success(){
        int productId = handleResponse(session.addProduct(userIds[UsersIndex.STORE_OWNER_1.ordinal()],
                (int) stores[0][0], "my product", "my description",
                10, 10, "my category"));

        ProductInfo productInfo = handleResponse(session.getStoreProductInfo(userIds[UsersIndex.STORE_OWNER_1.ordinal()], (int) stores[0][0], productId));
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
        handleResponse(session.removeProduct((int) products[0][2], (int) products[0][1], (int) products[0][0]));
        assertTrue(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).didntSucceed());
    }

    @Test
    void removeProduct_guest_fail(){
        assertTrue(session.removeProduct(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0]).didntSucceed());
    }

    @Test
    void setProductQuantity_storeOwner_success(){
        int stockQuantity = 42;
        handleResponse(session.setProductStockQuantity((int) products[0][2], (int) products[0][1], (int) products[0][0], stockQuantity));
        int quantityAfterSet = handleResponse(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0])).stockQuantity();
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
        handleResponse(session.setProductPrice((int) products[0][2], (int) products[0][1], (int) products[0][0], price));
        double priceAfterSet = handleResponse(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0])).price();
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
        handleResponse(session.setProductName((int) products[0][2], (int) products[0][1], (int) products[0][0], name));
        String nameAfterSet = handleResponse(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0])).name();
        assertEquals(name, nameAfterSet);
    }

    @Test
    void setProductName_guest_fail(){
        assertTrue(session.setProductName(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new name").didntSucceed());
    }

    @Test
    void setProductDescription_storeOwner_success(){
        String description = "new description";
        handleResponse(session.setProductDescription((int) products[0][2], (int) products[0][1], (int) products[0][0], description));
        String descriptionAfterSet = handleResponse(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0])).description();
        assertEquals(description, descriptionAfterSet);
    }

    @Test
    void setProductDescription_guest_fail(){
        assertTrue(session.setProductDescription(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new description").didntSucceed());
    }

    @Test
    void setProductCategory_storeOwner_success(){
        String category = "new category";
        handleResponse(session.setProductCategory((int) products[0][2], (int) products[0][1], (int) products[0][0], category));
        String categoryAfterSet = handleResponse(session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0])).category();
        assertEquals(category, categoryAfterSet);
    }

    @Test
    void setProductCategory_guest_fail(){
        assertTrue(session.setProductCategory(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new category").didntSucceed());
    }


}
