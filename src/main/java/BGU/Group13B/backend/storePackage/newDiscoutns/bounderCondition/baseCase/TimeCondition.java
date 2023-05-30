package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Time;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

import java.time.LocalDateTime;


public class TimeCondition extends Condition {

    private final Bounder<Time> timeBounder;

    public TimeCondition(int conditionId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        super(conditionId);
        this.timeBounder = new Bounder<>(Time.of(lowerBound), Time.of(upperBound));
    }

    public TimeCondition(int conditionId, LocalDateTime lowerBound) {
        super(conditionId);
        this.timeBounder = new Bounder<>(Time.of(lowerBound));
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        if(!timeBounder.inBounds(Time.of(LocalDateTime.now())))
            throw new PurchaseExceedsPolicyException("time must be " + timeBounder);
    }

    @Override
    public String toString() {
        return "time: " + timeBounder;
    }
}
