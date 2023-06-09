package BGU.Group13B.backend.storePackage.newDiscoutns.bounders;

import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class DateBounder {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private final Date upperBound; //if upperBound == null then there is no upper bound
    @OneToOne
    private final Date lowerBound;


    public DateBounder(Date lowerBound, Date upperBound) {
        if (upperBound.compareTo(lowerBound) < 0)
            throw new RuntimeException("upper bound can't be less than lower bound");


        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public DateBounder(Date lowerBound) {
        this.upperBound = null;
        this.lowerBound = lowerBound;
    }

    //i just added this constructor for the sake of hibernate
    public DateBounder() {
        this.upperBound = null;
        this.lowerBound = null;
    }


    public boolean inBounds(Date n) {
        return !(n.compareTo(lowerBound) < 0 || (upperBound != null && n.compareTo(upperBound) > 0));
    }

    public Date getUpperBound() {
        return upperBound;
    }

    public Date getLowerBound() {
        return lowerBound;
    }

    public String toString() {
        if (upperBound == null)
            return lowerBound + "+";
        return lowerBound + "-" + upperBound;
    }


    public boolean hasConflicts(DateBounder otherBounder) {
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
