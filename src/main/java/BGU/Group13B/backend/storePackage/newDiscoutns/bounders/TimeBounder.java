package BGU.Group13B.backend.storePackage.newDiscoutns.bounders;

import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Time;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

public class TimeBounder {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private final Time upperBound; //if upperBound == null then there is no upper bound
    @OneToOne
    private final Time lowerBound;


    public TimeBounder(Time lowerBound, Time upperBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public TimeBounder(Time lowerBound) {
        this.upperBound = null;
        this.lowerBound = lowerBound;
    }

    //i just added this constructor for the sake of hibernate
    public TimeBounder() {
        this.upperBound = null;
        this.lowerBound = null;
    }


    public boolean inBounds(Time n) {
        if (upperBound != null && upperBound.compareTo(lowerBound) < 0) {
            //in [lowerBound, 23:59:59] or [00:00:00, upperBound]
            return !(n.compareTo(lowerBound) < 0 && n.compareTo(upperBound) > 0);
        }
        return !(n.compareTo(lowerBound) < 0 || (upperBound != null && n.compareTo(upperBound) > 0));
    }

    public Time getUpperBound() {
        return upperBound;
    }

    public Time getLowerBound() {
        return lowerBound;
    }

    public String toString() {
        if (upperBound == null)
            return lowerBound + "+";
        return lowerBound + "-" + upperBound;
    }


    public boolean hasConflicts(TimeBounder otherBounder) {
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
