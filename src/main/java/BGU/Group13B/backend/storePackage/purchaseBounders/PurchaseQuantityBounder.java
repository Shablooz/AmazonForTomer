package BGU.Group13B.backend.storePackage.purchaseBounders;

public class PurchaseQuantityBounder extends PurchaseBounder{
    public PurchaseQuantityBounder(int parentId, int upperBound, int lowerBound) {
        super(parentId, upperBound, lowerBound);
    }

    public PurchaseQuantityBounder(int parentId) {
        super(parentId);
    }
}
