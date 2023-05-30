package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.PurchasePolicy;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

public class IMPLY extends LogicalCondition{
    public IMPLY(int conditionId, Condition operand1, Condition operand2) {
        super(conditionId, operand1, operand2);
    }

    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        boolean error = false;
        try{
            operand1.satisfied(basketInfo, user);
            error = true;
            operand2.satisfied(basketInfo, user);
        }
        catch (PurchaseExceedsPolicyException e){
            if(error)
                throw new PurchaseExceedsPolicyException("condition " + operand2 + " must be satisfied");
        }

    }

    @Override
    public String toString() {
        return "( " + operand1 + " ⟹ " + operand2 + " )";
    }
}