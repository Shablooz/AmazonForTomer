package BGU.Group13B.backend.storePackage.newDiscoutns;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Time;

@Deprecated
public class Bounder<T extends Comparable<T>> {

    private int id;

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

    //i just added this constructor for the sake of hibernate
    public Bounder() {
        this.upperBound = null;
        this.lowerBound = null;
    }


    public boolean inBounds(T n) {
        if(upperBound instanceof Time && upperBound.compareTo(lowerBound) < 0){
            //in [lowerBound, 23:59:59] or [00:00:00, upperBound]
            return !(n.compareTo(lowerBound) < 0 && n.compareTo(upperBound) > 0);
        }
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
