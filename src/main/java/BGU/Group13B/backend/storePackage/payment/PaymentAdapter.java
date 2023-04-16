package BGU.Group13B.backend.storePackage.payment;

import BGU.Group13B.backend.User.BasketProduct;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface PaymentAdapter {

    boolean pay(String address, String creditCardNumber, String creditCardMonth, String creditCardYear, String creditCardHolderFirstName, String creditCardHolderLastName, String creditCardCVV, String id, String creditCardType, ConcurrentLinkedQueue<BasketProduct> successfulProducts, ConcurrentLinkedQueue<BasketProduct> failedProducts);
}