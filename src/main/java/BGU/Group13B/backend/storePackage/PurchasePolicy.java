package BGU.Group13B.backend.storePackage;

public class PurchasePolicy {

    public void checkPolicy(Product product, int productQuantity) {
        if (productQuantity > product.getMaxAmount())
            throw new RuntimeException("Cant buy more than the amount: " + productQuantity);
    }
}
