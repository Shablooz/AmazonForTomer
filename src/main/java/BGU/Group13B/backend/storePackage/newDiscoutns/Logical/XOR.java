package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;


import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public class XOR extends LogicalCondition {
    public XOR(int conditionId, Condition operand1, Condition operand2) {
        super(conditionId, operand1, operand2);
    }

    public void satisfied(BasketInfo basketInfo, UserInfo userInfo) throws PurchaseExceedsPolicyException {
        String error1 = "";
        String error2 = "";
        int count = 0;
        try{
            operand1.satisfied(basketInfo, userInfo);
            count++;
        }catch (PurchaseExceedsPolicyException e){
            error1 = e.getMessage();
        }

        try{
            operand2.satisfied(basketInfo, userInfo);
            count++;
        }catch (PurchaseExceedsPolicyException e){
            error2 = e.getMessage();
        }

        if(count != 1){
            throw new PurchaseExceedsPolicyException("exactly one of '" + operand1 + "' , '" + operand2 + "' must be met");
        }


    }

    @Override
    public String toString() {
        return "( " + operand1 + " xor " + operand2 + " )";
    }

    @Override
    public LogicalConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        return new LogicalConditionEntity(parent, LogicalConditionEntity.LogicalOperator.XOR);
    }
}