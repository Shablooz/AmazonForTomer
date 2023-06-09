package BGU.Group13B.backend.storePackage.newDiscoutns.Logical;


import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;


@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LogicalCondition extends Condition {
    @OneToOne
    protected final Condition operand1;
    @OneToOne
    protected final Condition operand2;

    public LogicalCondition(int conditionId, Condition operand1, Condition operand2){
        super(conditionId);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public LogicalCondition() {
        this.operand2 = null;
        this.operand1 = null;
    }

    public Condition[] getChildren(){
        return new Condition[]{operand1, operand2};
    }

    //noder neder
    public abstract LogicalConditionEntity convertToUiEntity(LogicalConditionEntity parent);
}