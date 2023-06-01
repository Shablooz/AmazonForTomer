package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;

public class UserAgeConditionEntity extends ConditionEntity implements LeafConditionEntity{
    private final int lowerBound;
    private final int upperBound;
    public UserAgeConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
    }

    public UserAgeConditionEntity(LogicalConditionEntity parent, int lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = -1;
    }

    public UserAgeConditionEntity(LogicalConditionEntity parent, int lowerBound, int upperBound) {
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

    @Override
    public String toString() {
        boolean hasUpperBound = upperBound != -1;
        if(hasUpperBound)
            return "User age is between " + lowerBound + " and " + upperBound;
        else
            return "User age is at least " + lowerBound;
    }


    @Override
    public Response<Integer> addToBackend(Session session, int storeId, int userId) {
        if (upperBound == -1)
            return session.addUserAgeCondition(storeId, userId, lowerBound);

        return session.addUserAgeCondition(storeId, userId, lowerBound, upperBound);
    }
}
