package BGU.Group13B.backend.storePackage.payment;

public interface PaymentAdapter {
    boolean pay(String cardNumber, String firstName, String lastName,
                String expirationDate,String creditCardMonth, String creditCardYear,
                String cvv, String creditCardType,String id, double amount);
}