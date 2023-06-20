package BGU.Group13B.backend.storePackage.newDiscoutns.bounders;

import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Time;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
@Entity
public class IntBounder {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    private Integer upperBound; //if upperBound == null then there is no upper bound

    private Integer lowerBound;


    public IntBounder(Integer lowerBound, Integer upperBound) {
        if (upperBound.compareTo(lowerBound) < 0)
            throw new RuntimeException("upper bound can't be less than lower bound");


        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public IntBounder(Integer lowerBound) {
        this.upperBound = null;
        this.lowerBound = lowerBound;
    }

    //i just added this constructor for the sake of hibernate
    public IntBounder() {
        this.upperBound = null;
        this.lowerBound = null;
    }


    public boolean inBounds(Integer n) {
        return !(n.compareTo(lowerBound) < 0 || (upperBound != null && n.compareTo(upperBound) > 0));
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public String toString() {
        if (upperBound == null)
            return lowerBound + "+";
        return lowerBound + "-" + upperBound;
    }


    public boolean hasConflicts(IntBounder otherBounder) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }
}


