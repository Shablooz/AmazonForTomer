package BGU.Group13B.backend.storePackage.payment;


@FunctionalInterface
public interface PaymentAdapter {

    boolean pay(String cardNumber, String month, String year, String holder, String ccv, String id);
}