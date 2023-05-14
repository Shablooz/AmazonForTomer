package BGU.Group13B.service;

import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        assertEquals("No unread messages", session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()], message.getReceiverId(), message.getSenderId(), message.getMessageId()).getMessage());
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
        assertEquals("receiver Id not found", session.sendMassageAdmin(userIds[UsersIndex.ADMIN.ordinal()], "Not EXIST", "massage", "massage").getMessage());
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
        assertEquals("no complaint to answer", session.answerComplaint(userIds[UsersIndex.ADMIN.ordinal()], "answer").getMessage());
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
                storeIds[StoresIndex.STORE_1.ordinal()]);

        double payedPrice = session.purchaseProductCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                "12341234", "4", "2044", "shaun", "123", "12323", new HashMap<>(), "");
        assertEquals(products[ProductsIndex.PRODUCT_1.ordinal()][ProductIndexes.PRICE.ordinal()],
                payedPrice);
        int quantity_after = handleResponse(session.getStoreProductInfo(userIds[UsersIndex.STORE_OWNER_2.ordinal()], storeIds[StoresIndex.STORE_1.ordinal()], productIds[ProductsIndex.PRODUCT_1.ordinal()])).stockQuantity();
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
    public void purchaseCart_PayFail() {
        //expect payment fail
        session.addProductToCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                productIds[ProductsIndex.PRODUCT_1.ordinal()],
                storeIds[StoresIndex.STORE_1.ordinal()]);
        try {
            double payedPrice = session.purchaseProductCart(userIds[UsersIndex.STORE_OWNER_2.ordinal()],
                    "", "", "", "", "", "", new HashMap<>(), "");

        } catch (Exception e) {
            assertEquals("Payment failed", e.getMessage());
        }
    }

    @RepeatedTest(10)
    void twoThreadsTryToPurchaseTheLastProduct() {
        AtomicBoolean paymentFailed = new AtomicBoolean(false);
        final double[] pricePayed1 = {0.0};
        final double[] pricePayed2 = {0.0};
        int storeOwner = userIds[UsersIndex.STORE_OWNER_1.ordinal()];
        int newProductId = session.addProduct(storeOwner, storeIds[StoresIndex.STORE_1.ordinal()], "product1", "category1", 15.0, 1, "name").getData();

        int userId1 = userIds[UsersIndex.STORE_OWNER_2.ordinal()];
        int userId2 = userIds[UsersIndex.GUEST.ordinal()];

        session.addProductToCart(userId1, newProductId, storeIds[StoresIndex.STORE_1.ordinal()]);
        session.addProductToCart(userId2, newProductId, storeIds[StoresIndex.STORE_1.ordinal()]);

        Thread thread1 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(100);

                pricePayed1[0] = session.purchaseProductCart(userId1, "12344321123444321", "4", "2032", "Shaun Shuster", "321", "12323221", new HashMap<>(), "");
            } catch (InterruptedException e) {
                Assertions.fail(e);
            } catch (Exception e) {
                Assertions.assertEquals("Payment failed", e.getMessage());
                paymentFailed.set(true);

            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(100);
                pricePayed2[0] = session.purchaseProductCart(userId2, "12344321123444321", "4", "2032", "Shaun Shuster", "321", "12323221", new HashMap<>(), "");
            } catch (InterruptedException e) {
                Assertions.fail(e);
            } catch (Exception e) {
                Assertions.assertTrue(e.getMessage().equals("Payment failed") || e.getMessage().equals("Supply failed"));
                paymentFailed.set(true);
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (paymentFailed.get())
            return;
        int stock = session.getStoreProductInfo(storeOwner, storeIds[StoresIndex.STORE_1.ordinal()], newProductId).getData().stockQuantity();
        Assertions.assertEquals(0, stock);

        Assertions.assertEquals(15.0, pricePayed1[0] + pricePayed2[0]);
        List<Integer> failedProducts1 = session.getFailedProducts(userId1, storeIds[StoresIndex.STORE_1.ordinal()]);
        List<Integer> failedProducts2 = session.getFailedProducts(userId2, storeIds[StoresIndex.STORE_1.ordinal()]);
        if (failedProducts1.size() == 0 && failedProducts2.size() == 0)
            Assertions.fail();

        if (failedProducts1.size() == 0) {
            Assertions.assertEquals(15.0, pricePayed1[0]);
            Assertions.assertEquals(newProductId, failedProducts2.get(0));
        } else if (failedProducts2.size() == 0) {
            Assertions.assertEquals(15.0, pricePayed2[0]);
            Assertions.assertEquals(newProductId, failedProducts1.get(0));
        } else
            Assertions.fail("both failed");

    }

}
