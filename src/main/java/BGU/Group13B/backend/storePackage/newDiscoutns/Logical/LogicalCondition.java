package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;


import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public abstract class LogicalCondition extends Condition {
    protected final Condition operand1;
    protected final Condition operand2;

    public LogicalCondition(int conditionId, Condition operand1, Condition operand2){
        super(conditionId);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }







}