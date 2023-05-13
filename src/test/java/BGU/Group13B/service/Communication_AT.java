package BGU.Group13B.service;

import BGU.Group13B.backend.User.Message;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Communication_AT extends ProjectTest {

    protected final int[] storeIds = new int[stores.length];
    @Test
    public void openComplaint_Valid() {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_2.ordinal()], "complaint", "complaint");
        Message message = session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getData();
        assertEquals(message.getHeader(), "complaint");
        String name = session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(message.getSenderId(), name);
    }

    @Test
    public void openComplaint_NotValid() {
        assertEquals("Only registered users can open complaints", session.openComplaint(userIds[UsersIndex.GUEST.ordinal()], "complaint", "complaint").getMessage());
    }

    @Test
    public void getComplaint_Valid() {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_2.ordinal()], "complaint2", "complaint2");

        Message message = session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getData();
        assertEquals("complaint", message.getHeader());
        String name = session.getUserName(userIds[UsersIndex.STORE_OWNER_1.ordinal()]);
        assertEquals(message.getSenderId(), name);

        session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()], message.getReceiverId(), message.getSenderId(), message.getMessageId());

        message = session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getData();
        assertEquals("complaint2", message.getHeader());
        name = session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(message.getSenderId(), name);
    }

    @Test
    public void getComplaint_NotValid() {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        assertEquals("Only admin can read complaints", session.getComplaint(userIds[UsersIndex.STORE_OWNER_2.ordinal()]).getMessage());
    }

    @Test
    public void markMessageAsRead_Valid() {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        Message message = session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getData();
        session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()], message.getReceiverId(), message.getSenderId(), message.getMessageId());
        assertEquals("no unread messages", session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getMessage());
    }

    @Test
    public void markMessageAsRead_NotValid() {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        Message message = session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getData();
        session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()], message.getReceiverId(), message.getSenderId(), message.getMessageId());
        assertEquals("No unread messages",session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()], message.getReceiverId(), message.getSenderId(), message.getMessageId()).getMessage());
    }

    @Test
    public void sendMassageAdmin_Valid() {
        session.sendMassageAdmin(userIds[UsersIndex.ADMIN.ordinal()], session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]), "massage", "massage");
        Message message = session.readMessage(userIds[UsersIndex.STORE_OWNER_2.ordinal()]).getData();
        assertEquals("massage", message.getHeader());
        String name = session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(name, message.getReceiverId());
    }

    @Test
    public void sendMassageAdmin_NotValid() {
        assertEquals("receiver Id not found",session.sendMassageAdmin(userIds[UsersIndex.ADMIN.ordinal()], "Not EXIST", "massage", "massage").getMessage());
    }

    @Test
    public void answerComplaint_Valid() {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        Message message = session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]).getData();
        session.answerComplaint(userIds[UsersIndex.ADMIN.ordinal()], "answer");
        message = session.readMessage(userIds[UsersIndex.STORE_OWNER_1.ordinal()]).getData();
        assertEquals("answer", message.getMessage());
        String name = session.getUserName(userIds[UsersIndex.ADMIN.ordinal()]);
        assertEquals(name, message.getSenderId());
    }

    @Test
    public void answerComplaint_NotValid() {
        answerComplaint_Valid();
        assertEquals("no complaint to answer",session.answerComplaint(userIds[UsersIndex.ADMIN.ordinal()], "answer").getMessage());
    }

    @Test
    public void readMessage_Valid() {
        session.sendMassageAdmin(userIds[UsersIndex.ADMIN.ordinal()], session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]), "massage", "massage");
        Message message = session.readMessage(userIds[UsersIndex.STORE_OWNER_2.ordinal()]).getData();

        assertEquals("massage", message.getHeader());
        String name = session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(name, message.getReceiverId());
    }

    @Test
    public void readMessage_NotValid() {
        session.sendMassageAdmin(userIds[UsersIndex.ADMIN.ordinal()], session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]), "massage", "massage");
        assertEquals("no unread messages", session.readMessage(userIds[UsersIndex.STORE_OWNER_1.ordinal()]).getMessage());
    }

    @Test
    public void purchaseCart_Valid() {
        session.addProductToCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                productIds[ProductsIndex.PRODUCT_1.ordinal()],
                storeIds[StoresIndex.STORE_1.ordinal()] );

        double payedPrice = session.purchaseProductCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                "12341234", "4", "2044", "shaun", "123", "12323", new HashMap<>(), "");
        assertEquals(products[ProductsIndex.PRODUCT_1.ordinal()][ProductInfo.PRICE.ordinal()],
                payedPrice);
        int quantity_after = session.getStoreProductInfo(userIds[UsersIndex.STORE_OWNER_2.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()]).stockQuantity();
        assertEquals(9, quantity_after);

    }

    @Test
    public void purchaseCart_NotValid_try_purchase_deleteProduct() {
        session.addProductToCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                productIds[ProductsIndex.PRODUCT_1.ordinal()],
                storeIds[StoresIndex.STORE_1.ordinal()]);
        session.removeProduct(userIds[UsersIndex.STORE_OWNER_1.ordinal()],
                storeIds[StoresIndex.STORE_1.ordinal()],
                productIds[ProductsIndex.PRODUCT_1.ordinal()]);
        double totalPrice = session.purchaseProductCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()], "12341234", "4", "2044", "shaun", "123", "12323", new HashMap<>(), "");
        assertEquals(0, totalPrice);
        var failedProducts = session.getFailedProducts(
                userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                storeIds[StoresIndex.STORE_1.ordinal()]);
        assertEquals(1, failedProducts.size());
        assertEquals(productIds[ProductsIndex.PRODUCT_1.ordinal()], failedProducts.get(0));
    }

    @Test
    public void purchaseCart_PayFail(){//fixme its ok AT should not always pass
        //expect payment fail
        session.addProductToCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                productIds[ProductsIndex.PRODUCT_1.ordinal()],
                storeIds[StoresIndex.STORE_1.ordinal()] );
        double payedPrice = session.purchaseProductCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                "12341234", "4", "2044", "shaun", "123", "12323", new HashMap<>(), "");
        assertEquals(0, payedPrice);
    }
}
