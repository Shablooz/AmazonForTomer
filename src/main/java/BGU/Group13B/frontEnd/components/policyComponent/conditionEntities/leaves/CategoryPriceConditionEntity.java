package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class CategoryPriceConditionEntity extends ConditionEntity {
    private final double lowerBound;
    private final double upperBound;
    public CategoryPriceConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
    }
    public CategoryPriceConditionEntity(LogicalConditionEntity parent, double lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = -1;
    }
    public CategoryPriceConditionEntity(LogicalConditionEntity parent, double lowerBound, double upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    public double getLowerBound() {
        return lowerBound;
    }
    public double getUpperBound() {
        return upperBound;
    }

    public String toString(){
        boolean hasUpperBound = upperBound != -1;
        if(hasUpperBound)
            return "Category price is between " + lowerBound + " and " + upperBound;
        else
            return "Category price is at least " + lowerBound;
    }
}
