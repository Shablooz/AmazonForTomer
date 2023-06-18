package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounders.IntBounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.UserAgeConditionEntity;

import java.time.LocalDate;
@Entity
public class UserAgeCondition extends Condition {
    @OneToOne(cascade = CascadeType.ALL)
    private IntBounder ageBounder;

    public UserAgeCondition(int conditionId, int lowerBound, int upperBound) {
        super(conditionId);
        this.ageBounder = new IntBounder(lowerBound, upperBound);
    }

    /**
     * @param lowerBound dirbalek call this with upperbound
     */
    public UserAgeCondition(int conditionId, int lowerBound) {
        super(conditionId);
        this.ageBounder = new IntBounder(lowerBound);
    }
    //added for hibernate
    public UserAgeCondition() {
        super(0);
        this.ageBounder = null;
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        int userAge = user.dateOfBirth().until(LocalDate.now()).getYears();
        if(!ageBounder.inBounds(userAge))
            throw new PurchaseExceedsPolicyException("user age must be " + ageBounder);
    }

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        if(ageBounder.hasUpperBound()){
            return new UserAgeConditionEntity(parent, ageBounder.getLowerBound(), ageBounder.getUpperBound());
        }
        return new UserAgeConditionEntity(parent, ageBounder.getLowerBound());
    }

    @Override
    public String toString() {
        return "user age: " + ageBounder;
    }

    public IntBounder getAgeBounder() {
        return ageBounder;
    }

    public void setAgeBounder(IntBounder ageBounder) {
        this.ageBounder = ageBounder;
    }
}
