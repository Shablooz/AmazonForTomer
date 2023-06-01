package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;

import java.time.LocalDate;

public class DateConditionEntity extends ConditionEntity {
    private final LocalDate lowerBound;
    private final LocalDate upperBound;
    public DateConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        lowerBound = null;
        upperBound = null;
    }

    public DateConditionEntity(LogicalConditionEntity parent, LocalDate lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = null;
    }

    public DateConditionEntity(LogicalConditionEntity parent, LocalDate lowerBound, LocalDate upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public LocalDate getLowerBound() {
        return lowerBound;
    }

    public LocalDate getUpperBound() {
        return upperBound;
    }

    public String toString(){
        boolean hasUpperBound = upperBound != null;
        if(hasUpperBound)
            return "Date is between " + lowerBound + " and " + upperBound;
        else
            return "Date is at least " + lowerBound;
    }

    @Override
    public Response<Integer> addToBackend(Session session, int storeId, int userId) {
        if (upperBound == null)
            return session.addDateCondition(storeId, userId, lowerBound.atStartOfDay());

        return session.addDateCondition(storeId, userId, lowerBound.atStartOfDay(), upperBound.atStartOfDay());
    }
}
