package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;


import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public abstract class LogicalCondition extends Condition {
    protected final Condition operand1;
    protected final Condition operand2;

    public LogicalCondition(int conditionId, Condition operand1, Condition operand2){
        super(conditionId);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public Condition[] getChildren(){
        return new Condition[]{operand1, operand2};
    }

    //noder neder
    public abstract LogicalConditionEntity convertToUiEntity(LogicalConditionEntity parent);
}