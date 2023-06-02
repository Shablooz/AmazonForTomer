package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;

public class ProductPriceConditionEntity extends ConditionEntity implements LeafConditionEntity {
    private final int productId;
    private final String productName;

    private final double lowerBound;
    private final double upperBound;

    public ProductPriceConditionEntity(LogicalConditionEntity parent) {
        super(parent);
        this.lowerBound = -1;
        this.upperBound = -1;
        this.productId = -1;
        this.productName = null;
    }

    public ProductPriceConditionEntity(int productId, String productName, LogicalConditionEntity parent, double lowerBound, double upperBound) {
        super(parent);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.productId = productId;
        this.productName = productName;
    }

    public ProductPriceConditionEntity(int productId, String productName, LogicalConditionEntity parent, double lowerBound) {
        super(parent);
        this.lowerBound = lowerBound;
        upperBound = -1;
        this.productId = productId;
        this.productName = productName;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public String toString(){
        boolean hasUpperBound = upperBound != -1;
        if (hasUpperBound)
            return "Product " + productName + " price is between " + lowerBound + " and " + upperBound;
        else
            return "Product " + productName + " price is at least " + lowerBound;
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
