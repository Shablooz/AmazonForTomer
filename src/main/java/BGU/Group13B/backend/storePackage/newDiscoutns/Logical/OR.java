package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

public class OR extends LogicalCondition{
    public OR(int conditionId, Condition operand1, Condition operand2) {
        super(conditionId, operand1, operand2);
    }

    public void satisfied(BasketInfo basketInfo, UserInfo userinfo) throws PurchaseExceedsPolicyException {
        try{
            operand1.satisfied(basketInfo, userinfo);
        }
        catch (PurchaseExceedsPolicyException e){
            operand2.satisfied(basketInfo, userinfo);
        }

    }

    @Override
    public String toString() {
        return "( " + operand1 + " or " + operand2 + " )";
    }
}