package BGU.Group13B.backend.storePackage.purchaseBounders;

public class PurchasePriceBounder extends PurchaseBounder{
    public PurchasePriceBounder(int parentId, int upperBound, int lowerBound) {
        super(parentId, upperBound, lowerBound);
    }

    public PurchasePriceBounder(int parentId) {
        super(parentId);
    }
}
