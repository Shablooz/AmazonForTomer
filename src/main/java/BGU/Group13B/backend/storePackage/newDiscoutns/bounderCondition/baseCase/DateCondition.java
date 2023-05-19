package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time.Date;

import java.time.LocalDateTime;


public class DateCondition extends Condition {

    private final Bounder<Date> bounder;

    public DateCondition(int conditionId, LocalDateTime lowerBound, LocalDateTime upperBound) {
        super(conditionId);
        this.bounder = new Bounder<>(Date.of(lowerBound), Date.of(upperBound));
    }

    public DateCondition(int conditionId, LocalDateTime lowerBound) {
        super(conditionId);
        this.bounder = new Bounder<>(Date.of(lowerBound));
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        return bounder.inBounds(Date.of(LocalDateTime.now()));
    }

    public String toString(){
        return "date: " + bounder.toString();
    }


}
