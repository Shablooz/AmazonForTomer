package BGU.Group13B.backend.storePackage.newDiscoutns;


import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Time;

public class Bounder<T extends Comparable<T>> {


    private final T upperBound; //if upperBound == null then there is no upper bound
    private final T lowerBound;


    public Bounder(T lowerBound, T upperBound) {
        if (!(upperBound instanceof Time) && upperBound.compareTo(lowerBound) < 0)
            throw new RuntimeException("upper bound can't be less than lower bound");


        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public Bounder(T lowerBound) {
        this.upperBound = null;
        this.lowerBound = lowerBound;
    }


    public boolean inBounds(T n) {
        return !(n.compareTo(lowerBound) < 0 || (upperBound != null && n.compareTo(upperBound) > 0));
    }

    public T getUpperBound() {
        return upperBound;
    }

    public T getLowerBound() {
        return lowerBound;
    }

    public String toString() {
        if (upperBound == null)
            return lowerBound + "+";
        return lowerBound + "-" + upperBound;
    }


    public boolean hasConflicts(Bounder<T> otherBounder) {
        if (upperBound == null && otherBounder.getUpperBound() == null)
            return false;

        if (upperBound == null)
            return otherBounder.getUpperBound().compareTo(lowerBound) < 0;

        if (otherBounder.getUpperBound() == null)
            return upperBound.compareTo(otherBounder.getLowerBound()) < 0;

        return upperBound.compareTo(otherBounder.getLowerBound()) < 0 || otherBounder.getUpperBound().compareTo(lowerBound) < 0;
    }

    public boolean hasUpperBound(){
        return upperBound != null;
    }
}
