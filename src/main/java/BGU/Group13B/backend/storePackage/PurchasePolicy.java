package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseBounder;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

public class PurchasePolicy {

    private final int parentId; // storeId or productId
    private final PurchaseBounder quantityBounder;
    private final PurchaseBounder priceBounder;

    public PurchasePolicy(int parentId) {
        this.parentId = parentId;
        quantityBounder = new PurchaseBounder();
        priceBounder = new PurchaseBounder();
    }

    //policy that does not belong to any particular product or store
    public PurchasePolicy(int quantityLowerBound, int quantityUpperBound, int priceLowerBound, int priceUpperBound) {
        this.parentId = -1;
        quantityBounder = new PurchaseBounder(quantityLowerBound, quantityUpperBound);
        priceBounder = new PurchaseBounder(priceLowerBound, priceUpperBound);
    }

    public void checkPolicy(int purchaseQuantity, double totalPurchasePrice) throws PurchaseExceedsPolicyException {
        if(quantityBounder.isNotInBound(purchaseQuantity))
            throw new PurchaseExceedsPolicyException("purchase quantity exceeds policy. must be in " + quantityBounder);

        if(priceBounder.isNotInBound(totalPurchasePrice))
            throw new PurchaseExceedsPolicyException("purchase price exceeds policy. must be in " + priceBounder);
    }

    public void resetPolicy(){
        quantityBounder.reset();
        priceBounder.reset();
    }

    public void resetQuantityBounder(){
        quantityBounder.reset();
    }

    public void resetPriceBounder(){
        priceBounder.reset();
    }

    public void setQuantityBounds(int lowerBound, int upperBound){
        quantityBounder.setBounds(lowerBound, upperBound);
    }

    public void setQuantityUpperBound(int upperBound){
        quantityBounder.setUpperBound(upperBound);
    }

    public void setQuantityLowerBound(int lowerBound){
        quantityBounder.setLowerBound(lowerBound);
    }

    public void setPriceBounds(int lowerBound, int upperBound){
        priceBounder.setBounds(lowerBound, upperBound);
    }

    public void setPriceUpperBound(int upperBound){
        priceBounder.setUpperBound(upperBound);
    }

    public void setPriceLowerBound(int lowerBound){
        priceBounder.setLowerBound(lowerBound);
    }

    public int getParentId() {
        return parentId;
    }

    public int getQuantityLowerBound() {
        return quantityBounder.getLowerBound();
    }

    public int getQuantityUpperBound() {
        return quantityBounder.getUpperBound();
    }

    public int getPriceLowerBound() {
        return priceBounder.getLowerBound();
    }

    public int getPriceUpperBound() {
        return priceBounder.getUpperBound();
    }

    public boolean hasConflicts(PurchasePolicy other){
        return quantityBounder.hasConflicts(other.quantityBounder) || priceBounder.hasConflicts(other.priceBounder);
    }

    public void checkConflictsAndSetQuantityBounds(int lowerBound, int upperBound, PurchaseBounder other){
        if(other.hasConflicts(new PurchaseBounder(lowerBound, upperBound)))
            throw new IllegalArgumentException("could not set quantity bounds. conflicts with other policy");

        quantityBounder.setBounds(lowerBound, upperBound);
    }

    public void checkConflictsAndSetPriceBounds(int lowerBound, int upperBound, PurchaseBounder other){
        if(other.hasConflicts(new PurchaseBounder(lowerBound, upperBound)))
            throw new IllegalArgumentException("could not set price bounds. conflicts with other policy");

        priceBounder.setBounds(lowerBound, upperBound);
    }

    public void checkConflictsAndSetQuantityUpperBound(int upperBound, PurchaseBounder other){
        if(other.hasConflicts(new PurchaseBounder(this.getQuantityLowerBound(), upperBound)))
            throw new IllegalArgumentException("could not set quantity upper bound. conflicts with other policy");

        quantityBounder.setUpperBound(upperBound);
    }

    public void checkConflictsAndSetPriceUpperBound(int upperBound, PurchaseBounder other){
        if(other.hasConflicts(new PurchaseBounder(this.getPriceLowerBound(), upperBound)))
            throw new IllegalArgumentException("could not set price upper bound. conflicts with other policy");

        priceBounder.setUpperBound(upperBound);
    }

    public void checkConflictsAndSetQuantityLowerBound(int lowerBound, PurchaseBounder other){
        if(other.hasConflicts(new PurchaseBounder(lowerBound, this.getQuantityUpperBound())))
            throw new IllegalArgumentException("could not set quantity lower bound. conflicts with other policy");

        quantityBounder.setLowerBound(lowerBound);
    }

    public void checkConflictsAndSetPriceLowerBound(int lowerBound, PurchaseBounder other){
        if(other.hasConflicts(new PurchaseBounder(lowerBound, this.getPriceUpperBound())))
            throw new IllegalArgumentException("could not set price lower bound. conflicts with other policy");

        priceBounder.setLowerBound(lowerBound);
    }

    public PurchaseBounder getPriceBounder() {
        return priceBounder;
    }

    public PurchaseBounder getQuantityBounder() {
        return quantityBounder;
    }
}
