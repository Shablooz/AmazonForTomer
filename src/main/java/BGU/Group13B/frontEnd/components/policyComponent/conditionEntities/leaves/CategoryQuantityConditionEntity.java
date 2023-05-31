package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class CategoryQuantityConditionEntity extends ConditionEntity {
    private int lowerBound;
    private int upperBound;
    public CategoryQuantityConditionEntity(LogicalConditionEntity parent) {
        super(parent);
    }

    public CategoryQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound, int upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public CategoryQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public String toString(){
        boolean hasUpperBound = upperBound != -1;
        if(hasUpperBound)
            return "Category quantity is between " + lowerBound + " and " + upperBound;
        else
            return "Category quantity is at least " + lowerBound;
    }
}
