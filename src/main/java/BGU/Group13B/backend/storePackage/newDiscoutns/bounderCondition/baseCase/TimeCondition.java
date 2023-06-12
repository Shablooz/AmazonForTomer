package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounders.TimeBounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Time;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.TimeConditionEntity;

import java.time.LocalDateTime;

@Entity
public class TimeCondition extends Condition {
    @OneToOne(cascade = CascadeType.ALL)
    private TimeBounder timeBounder;

    public TimeCondition(int conditionId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        super(conditionId);
        this.timeBounder = new TimeBounder(Time.of(lowerBound), Time.of(upperBound));
    }

    public TimeCondition(int conditionId, LocalDateTime lowerBound) {
        super(conditionId);
        this.timeBounder = new TimeBounder(Time.of(lowerBound));
    }

    //added for hibernate
    public TimeCondition() {
        super(0);
        this.timeBounder = null;
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        if(!timeBounder.inBounds(Time.of(LocalDateTime.now())))
            throw new PurchaseExceedsPolicyException("time must be " + timeBounder);
    }

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        if(timeBounder.hasUpperBound()){
            return new TimeConditionEntity(parent, timeBounder.getLowerBound().getTime().toLocalTime(), timeBounder.getUpperBound().getTime().toLocalTime());
        }
        return new TimeConditionEntity(parent, timeBounder.getLowerBound().getTime().toLocalTime());
    }

    @Override
    public String toString() {
        return "time: " + timeBounder;
    }

    public TimeBounder getTimeBounder() {
        return timeBounder;
    }

    public void setTimeBounder(TimeBounder timeBounder) {
        this.timeBounder = timeBounder;
    }
}
