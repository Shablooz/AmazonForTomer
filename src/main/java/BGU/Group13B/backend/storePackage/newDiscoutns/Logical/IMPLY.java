package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class IMPLY extends LogicalCondition{
    public IMPLY(Condition operand1, Condition operand2) {
        super(operand1, operand2);
    }

    public boolean satisfied(BasketInfo basketInfo, UserInfo user){
        return !operand1.satisfied(basketInfo, user) ||
                operand2.satisfied(basketInfo, user);
    }
}