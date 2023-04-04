package BGU.Group13B.backend.storePackage.payment;

public interface PaymentAdapter {
    boolean pay(String cardNumber, String holder, String expirationDate, String ccv, double amount);
}
