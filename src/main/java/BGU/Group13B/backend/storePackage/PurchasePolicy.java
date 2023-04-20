package BGU.Group13B.backend.storePackage;

public class PurchasePolicy {
    private int maxAmount = 0;
    public void checkPolicy(int productQuantity) {
        if (productQuantity > maxAmount)
            throw new RuntimeException("Cant buy more than the amount: " + productQuantity);
    }
}
