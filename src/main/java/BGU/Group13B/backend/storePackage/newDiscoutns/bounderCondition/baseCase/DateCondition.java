package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Date;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.DateConditionEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Entity
public class DateCondition extends Condition {
    @OneToOne
    private final Bounder<Date> bounder;

    public DateCondition(int conditionId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        super(conditionId);
        this.bounder = new Bounder<>(Date.of(lowerBound), Date.of(upperBound));
    }

    public DateCondition(int conditionId, LocalDateTime lowerBound) {
        super(conditionId);
        this.bounder = new Bounder<>(Date.of(lowerBound));
    }

    //added for hibernate
    public DateCondition() {
        super(0);
        this.bounder = null;
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        if(!bounder.inBounds(Date.of(LocalDateTime.now())))
            throw new PurchaseExceedsPolicyException("date must be " + bounder);
    }

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        if(bounder.hasUpperBound()){
            return new DateConditionEntity(parent, bounder.getLowerBound().getDate().toLocalDate(), bounder.getUpperBound().getDate().toLocalDate());
        }
        return new DateConditionEntity(parent, bounder.getLowerBound().getDate().toLocalDate());
    }

    public String toString(){
        return "date: " + bounder.toString();
    }


}
