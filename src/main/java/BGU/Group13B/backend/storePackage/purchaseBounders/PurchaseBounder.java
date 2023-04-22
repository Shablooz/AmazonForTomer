package BGU.Group13B.backend.storePackage.purchaseBounders;

public class PurchaseBounder {

    public enum BounderType {
        QUANTITY,
        PRICE
    }

    private int upperBound; //if upperBound == -1 then there is no upper bound
    private int lowerBound;


    public PurchaseBounder(int lowerBound, int upperBound) {
        if(upperBound < lowerBound)
            throw new RuntimeException("upper bound can't be less than lower bound");

        if(lowerBound < 0)
            throw new RuntimeException("lower bound can't be less than 0");

        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public PurchaseBounder() {
        this.upperBound = -1;
        this.lowerBound = 0;
    }


    public boolean isNotInBound(double n) {
        return n < lowerBound || (upperBound != -1 && n > upperBound);
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

    public void setBounds(int lowerBound, int upperBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public String toString() {
        if (upperBound == -1)
            return "[" + lowerBound + ", +inf)";
        return "[" + lowerBound + ", " + upperBound + "]";
    }

    public boolean isDefault() {
        return upperBound == -1 && lowerBound == 0;
    }

    public void reset(){
        upperBound = -1;
        lowerBound = 0;
    }

    /*
     * returns true if the two bounder don't have a common range
     * () [] = true
     * [] () = true
     * [( ]) = false
     * ([ )] = false
     * [( )] = false
     * ([ ]) = false
     */
    public boolean hasConflicts(PurchaseBounder otherBounder) {
        if (upperBound == -1 && otherBounder.upperBound == -1)
            return false;

        if (upperBound == -1)
            return otherBounder.upperBound < lowerBound;

        if (otherBounder.upperBound == -1)
            return upperBound < otherBounder.lowerBound;

        return upperBound < otherBounder.lowerBound || otherBounder.upperBound < lowerBound;
    }
}
