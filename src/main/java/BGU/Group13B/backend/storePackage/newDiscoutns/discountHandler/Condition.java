package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public abstract class Condition {
    private final int conditionId;

    protected Condition(int conditionId){
        this.conditionId = conditionId;
    }

    public abstract void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException;

    public abstract ConditionEntity convertToUiEntity(LogicalConditionEntity parent);

}
