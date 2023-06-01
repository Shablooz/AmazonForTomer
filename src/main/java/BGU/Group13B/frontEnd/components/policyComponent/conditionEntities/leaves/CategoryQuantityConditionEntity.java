package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class CategoryQuantityConditionEntity extends ConditionEntity implements LeafConditionEntity{
    private final int lowerBound;
    private final int upperBound;
    private final String category;
    public CategoryQuantityConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
        this.category = null;
    }

    public CategoryQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound, int upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.category = category;

    }

    public CategoryQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = -1;
        this.category = category;
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

    public String getCategoryName() {
        return category;
    }

    @Override
    public Response<Integer> addToBackend(Session session, int storeId, int userId) {
        if (upperBound == -1)
            return session.addCategoryQuantityCondition(storeId, userId, category, lowerBound);

        return session.addCategoryQuantityCondition(storeId, userId, category, lowerBound, upperBound);
    }
}
