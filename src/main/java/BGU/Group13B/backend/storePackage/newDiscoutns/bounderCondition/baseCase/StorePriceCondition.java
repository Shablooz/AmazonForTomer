package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.StorePriceConditionEntity;

public class StorePriceCondition extends Condition {
    private final Bounder<Double> priceBounder;


    public StorePriceCondition(int conditionId, double lowerBound, double upperBound) {
        super(conditionId);
        this.priceBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param lowerBound dirbalek call this with upperbound
     */
    public StorePriceCondition(int conditionId, double lowerBound) {
        super(conditionId);
        this.priceBounder = new Bounder<>(lowerBound);
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        //sum all products quantities
        double price = basketInfo.basketProducts().stream().
                map(b -> b.getPrice() * b.getQuantity()).reduce(0.0, Double::sum);

        if (!priceBounder.inBounds(price))
            throw new PurchaseExceedsPolicyException("total price must be " + priceBounder);
    }

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        if(priceBounder.hasUpperBound()){
            return new StorePriceConditionEntity(parent, priceBounder.getLowerBound(), priceBounder.getUpperBound());
        }
        return new StorePriceConditionEntity(parent, priceBounder.getLowerBound());
    }

    @Override
    public String toString() {
        return "total price: " + priceBounder;
    }
}
