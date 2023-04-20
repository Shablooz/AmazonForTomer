package BGU.Group13B.backend.storePackage.purchaseBounders;

public abstract class PurchaseBounder {

    private final int parentId; //the id of the store or the product that this bounder belongs to
    protected int upperBound; //if upperBound == -1 then there is no upper bound
    protected int lowerBound;

    public PurchaseBounder(int parentId, int upperBound, int lowerBound) {
        if(upperBound < lowerBound)
            throw new RuntimeException("upper bound can't be less than lower bound");

        if(lowerBound < 0)
            throw new RuntimeException("lower bound can't be less than 0");

        this.parentId = parentId;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public PurchaseBounder(int parentId) {
        this.parentId = parentId;
        this.upperBound = -1;
        this.lowerBound = 0;
    }

    public boolean isInBound(int productPurchaseQuantity) {
        return productPurchaseQuantity >= lowerBound && productPurchaseQuantity <= upperBound;
    }

    public int getParentId() {
        return parentId;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String toString() {
        if (upperBound == -1)
            return "[" + lowerBound + ", +inf)";
        return "[" + lowerBound + ", " + upperBound + "]";
    }
}
