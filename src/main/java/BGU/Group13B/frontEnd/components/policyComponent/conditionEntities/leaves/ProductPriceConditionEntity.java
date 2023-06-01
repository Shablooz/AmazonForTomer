package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class ProductPriceConditionEntity extends ConditionEntity {
    private final double lowerBound;
    private final double upperBound;

    public ProductPriceConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
    }

    public ProductPriceConditionEntity(LogicalConditionEntity parent, double lowerBound, double upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public ProductPriceConditionEntity(LogicalConditionEntity parent, double lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        upperBound = -1;
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
            return "Product price is between " + lowerBound + " and " + upperBound;
        else
            return "Product price is at least " + lowerBound;
    }
    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }

    @Override
    public Response<Integer> addToBackend(Session session, int storeId, int userId) {
        if (upperBound == -1)
            return session.addProductPriceCondition(storeId, userId, productId, lowerBound);

        return session.addProductPriceCondition(storeId, userId, productId, lowerBound, upperBound);
    }
}
