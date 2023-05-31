package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class StoreQuantityConditionEntity extends ConditionEntity {
    private final int lowerBound;
    private final int upperBound;
    public StoreQuantityConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        lowerBound = -1;
        upperBound = -1;
    }

    public StoreQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        upperBound = -1;
    }

    public StoreQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound, int upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }
    public String toString(){
        boolean hasUpperBound = upperBound != 0;
        if(hasUpperBound)
            return "Store quantity is between " + lowerBound + " and " + upperBound;
        else
            return "Store quantity is at least " + lowerBound;
    }
}
