package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;

public class CategoryPriceConditionEntity extends ConditionEntity implements LeafConditionEntity {
    private final String category;

    private final double lowerBound;
    private final double upperBound;

    public CategoryPriceConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
        this.category = null;
    }

    public CategoryPriceConditionEntity(String category, LogicalConditionEntity parent, double lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = -1;
        this.category = category;

    }

    public CategoryPriceConditionEntity(String category, LogicalConditionEntity parent, double lowerBound, double upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.category = category;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public String toString() {
        boolean hasUpperBound = upperBound != -1;
        if (hasUpperBound)
            return "Category " + category + " price is between " + lowerBound + " and " + upperBound;
        else
            return "Category " + category + " price is at least " + lowerBound;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public Response<Integer> addToBackend(Session session, int storeId, int userId) {
        if (upperBound == -1)
            return session.addCategoryPriceCondition(storeId, userId, category, lowerBound);

        return session.addCategoryPriceCondition(storeId, userId, category, lowerBound, upperBound);
    }
}
