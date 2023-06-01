package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class ProductQuantityConditionEntity extends ConditionEntity {
    private final int productId;
    private final String productName;

    private final int lowerBound;
    private final int upperBound;

    public ProductQuantityConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
        this.productId = -1;
        this.productName = null;
    }

    public ProductQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = -1;
    }

    public ProductQuantityConditionEntity(LogicalConditionEntity parent, int lowerBound, int upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String toString() {
        boolean hasUpperBound = upperBound != -1;
        if(hasUpperBound)
            return "Product quantity is between " + lowerBound + " and " + upperBound;
        else
            return "Product quantity is at least " + lowerBound;
    }
    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
}
