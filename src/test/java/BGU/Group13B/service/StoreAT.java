package BGU.Group13B.service;

import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class StoreAT extends ProjectTest{


    @Test
    void addStore_member_success(){
        int storeId = session.addStore(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "my store", categories[0]);
        StoreInfo storeInfo = session.getStoreInfo(userIds[UsersIndex.STORE_OWNER_1.ordinal()], storeId);
        assertEquals("my store", storeInfo.storeName());
    }

    @Test
    void addStore_guest_fail(){
        try{
            session.addStore(userIds[UsersIndex.GUEST.ordinal()], "my store", categories[0]);
            fail();
        }
        catch (Exception ignore){}

    }

    @Test
    void addProduct_storeOwner_success(){
        int productId = session.addProduct(userIds[UsersIndex.STORE_OWNER_1.ordinal()],
                (int) stores[0][0], "my product", "my description",
                10, 10, "my category");

        BGU.Group13B.service.info.ProductInfo productInfo = session.getStoreProductInfo(userIds[UsersIndex.STORE_OWNER_1.ordinal()], (int) stores[0][0], productId);
        assertEquals("my product", productInfo.name());
    }

    @Test
    void addProduct_guest_fail(){
        try{
            session.addProduct(userIds[UsersIndex.GUEST.ordinal()],
                    (int) stores[0][0], "my product", "my description",
                    10, 10, "my category");
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void removeProduct_storeOwner_success(){
        session.removeProduct((int) products[0][2], (int) products[0][1], (int) products[0][0]);
        try{
            session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]);
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void removeProduct_guest_fail(){
        try{
            session.removeProduct(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0]);
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductQuantity_storeOwner_success(){
        int stockQuantity = 42;
        session.setProductStockQuantity((int) products[0][2], (int) products[0][1], (int) products[0][0], stockQuantity);
        int quantityAfterSet = session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).stockQuantity();
        assertEquals(stockQuantity, quantityAfterSet);
    }

    @Test
    void setProductQuantity_guest_fail(){
        try{
            session.setProductStockQuantity(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], 42);
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductQuantity_negativeQuantity_fail(){
        try{
            session.setProductStockQuantity((int) products[0][2], (int) products[0][1], (int) products[0][0], -1);
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductPrice_storeOwner_success(){
        double price = 42;
        session.setProductPrice((int) products[0][2], (int) products[0][1], (int) products[0][0], price);
        double priceAfterSet = session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).price();
        assertEquals(price, priceAfterSet);
    }

    @Test
    void setProductPrice_guest_fail(){
        try{
            session.setProductPrice(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], 42);
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductPrice_negativePrice_fail(){
        try{
            session.setProductPrice((int) products[0][2], (int) products[0][1], (int) products[0][0], -1);
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductName_storeOwner_success(){
        String name = "new name";
        session.setProductName((int) products[0][2], (int) products[0][1], (int) products[0][0], name);
        String nameAfterSet = session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).name();
        assertEquals(name, nameAfterSet);
    }

    @Test
    void setProductName_guest_fail(){
        try{
            session.setProductName(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new name");
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductDescription_storeOwner_success(){
        String description = "new description";
        session.setProductDescription((int) products[0][2], (int) products[0][1], (int) products[0][0], description);
        String descriptionAfterSet = session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).description();
        assertEquals(description, descriptionAfterSet);
    }

    @Test
    void setProductDescription_guest_fail(){
        try{
            session.setProductDescription(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new description");
            fail();
        }
        catch (Exception ignore){}
    }

    @Test
    void setProductCategory_storeOwner_success(){
        String category = "new category";
        session.setProductCategory((int) products[0][2], (int) products[0][1], (int) products[0][0], category);
        String categoryAfterSet = session.getStoreProductInfo((int) products[0][2], (int) products[0][1], (int) products[0][0]).category();
    }

    @Test
    void setProductCategory_guest_fail(){
        try{
            session.setProductCategory(userIds[UsersIndex.GUEST.ordinal()], (int) products[0][1], (int) products[0][0], "new category");
            fail();
        }
        catch (Exception ignore){}
    }


}
