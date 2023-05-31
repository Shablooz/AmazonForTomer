package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class StorePriceConditionEntity extends ConditionEntity {

    private final double upperBound;
    private final double lowerBound;

    public StorePriceConditionEntity(LogicalConditionEntity parent) {

        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
    }

    public StorePriceConditionEntity(LogicalConditionEntity parent, double lowerBound, double upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    public StorePriceConditionEntity(LogicalConditionEntity parent, double lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = -1;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }
    public String toString(){
        boolean hasUpperBound = upperBound != -1;
        if(hasUpperBound)
            return "Store price is between " + lowerBound + " and " + upperBound;
        else
            return "Store price is at least " + lowerBound;
    }
}
