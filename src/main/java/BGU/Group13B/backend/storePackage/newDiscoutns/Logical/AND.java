package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class AND extends LogicalCondition{
    public AND(int conditionId, Condition operand1, Condition operand2) {
        super(conditionId, operand1, operand2);
    }

    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        operand1.satisfied(basketInfo, user);
        operand2.satisfied(basketInfo, user);
    }

    @Override
    public String toString() {
        return "( " + operand1 + " and " + operand2 + " )";
    }

    @Override
    public LogicalConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        return new LogicalConditionEntity(parent, LogicalConditionEntity.LogicalOperator.AND);
    }
}