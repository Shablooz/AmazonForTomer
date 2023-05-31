package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeConditionEntity extends ConditionEntity {
    private final LocalTime lowerBound;
    private final LocalTime upperBound;
    public TimeConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = null;
        this.upperBound = null;
    }

    public TimeConditionEntity(LogicalConditionEntity parent, LocalTime lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = null;
    }

    public TimeConditionEntity(LogicalConditionEntity parent, LocalTime lowerBound, LocalTime upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public LocalTime getLowerBound() {
        return lowerBound;
    }

    public LocalTime getUpperBound() {
        return upperBound;
    }
    public String toString(){
        boolean hasUpperBound = upperBound != null;
        if(hasUpperBound)
            return "Time is between " + lowerBound + " and " + upperBound;
        else
            return "Time is at least " + lowerBound;
    }
}
