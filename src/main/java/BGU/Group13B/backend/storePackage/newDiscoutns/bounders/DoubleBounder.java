package BGU.Group13B.backend.storePackage.newDiscoutns.bounders;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
@Entity
public class DoubleBounder {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    private Double upperBound; //if upperBound == null then there is no upper bound

    private Double lowerBound;


    public DoubleBounder(Double lowerBound, Double upperBound) {
        if (upperBound.compareTo(lowerBound) < 0)
            throw new RuntimeException("upper bound can't be less than lower bound");


        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

        public DoubleBounder(Double lowerBound) {
        this.upperBound = null;
        this.lowerBound = lowerBound;
    }

    //i just added this constructor for the sake of hibernate
    public DoubleBounder() {
        this.upperBound = null;
        this.lowerBound = null;
    }


    public boolean inBounds(Double n) {
        return !(n.compareTo(lowerBound) < 0 || (upperBound != null && n.compareTo(upperBound) > 0));
    }

    public Double getUpperBound() {
        return upperBound;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    public String toString() {
        if (upperBound == null)
            return lowerBound + "+";
        return lowerBound + "-" + upperBound;
    }


    public boolean hasConflicts(DoubleBounder otherBounder) {
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
