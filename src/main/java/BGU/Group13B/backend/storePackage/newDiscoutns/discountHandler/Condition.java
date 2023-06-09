package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import jakarta.persistence.*;


@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Condition {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private final int conditionId;

    protected Condition(int conditionId){
        this.conditionId = conditionId;
    }

    //added for hibernate shouldnt use this!
    public Condition() {
        this.conditionId = -1;
    }

    public abstract void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException;

    public abstract ConditionEntity convertToUiEntity(LogicalConditionEntity parent);

}
