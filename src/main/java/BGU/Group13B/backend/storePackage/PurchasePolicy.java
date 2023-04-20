package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchasePriceBounder;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseQuantityBounder;

public class PurchasePolicy {

    private final int parentId; //the id of the store or the product that this policy belongs to
    private PurchaseQuantityBounder quantityBounder;
    private PurchasePriceBounder priceBounder;

    public PurchasePolicy(int parentId) {
        this.parentId = parentId;
        quantityBounder = new PurchaseQuantityBounder(parentId);
        priceBounder = new PurchasePriceBounder(parentId);
    }

    public void checkPolicy(int purchaseQuantity, int purchasePrice) throws PurchaseExceedsPolicyException {
        if(!quantityBounder.isInBound(purchaseQuantity))
            throw new PurchaseExceedsPolicyException("purchase quantity exceeds policy. must be in " + quantityBounder);

        if(!priceBounder.isInBound(purchasePrice))
            throw new PurchaseExceedsPolicyException("purchase price exceeds policy. must be in " + priceBounder);
    }

    public void resetPolicy() {
        quantityBounder = new PurchaseQuantityBounder(parentId);
        priceBounder = new PurchasePriceBounder(parentId);
    }

    public void resetQuantityBounder() {
        quantityBounder = new PurchaseQuantityBounder(parentId);
    }

    public void resetPriceBounder() {
        priceBounder = new PurchasePriceBounder(parentId);
    }

    public void setQuantityBounder(int lowerBound, int upperBound) {
        quantityBounder = new PurchaseQuantityBounder(parentId, lowerBound, upperBound);
    }

    public void setPriceBounder(int lowerBound, int upperBound) {
        priceBounder = new PurchasePriceBounder(parentId, lowerBound, upperBound);
    }

    public void setQuantityUpperBound(int upperBound) {
        quantityBounder.setUpperBound(upperBound);
    }

    public void setQuantityLowerBound(int lowerBound) {
        quantityBounder.setLowerBound(lowerBound);
    }

    public void setPriceUpperBound(int upperBound) {
        priceBounder.setUpperBound(upperBound);
    }

    public void setPriceLowerBound(int lowerBound) {
        priceBounder.setLowerBound(lowerBound);
    }
}
