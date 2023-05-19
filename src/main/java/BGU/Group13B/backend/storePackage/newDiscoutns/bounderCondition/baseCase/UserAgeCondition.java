package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

import java.time.LocalDate;

public class UserAgeCondition extends Condition {
    private final Bounder<Integer> ageBounder;

    public UserAgeCondition(int conditionId, int lowerBound, int upperBound) {
        super(conditionId);
        this.ageBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param lowerBound dirbalek call this with upperbound
     */
    public UserAgeCondition(int conditionId, int lowerBound) {
        super(conditionId);
        this.ageBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        int userAge = user.dateOfBirth().until(LocalDate.now()).getYears();
        return ageBounder.inBounds(userAge);
    }

    @Override
    public String toString() {
        return "user age: " + ageBounder;
    }
}
