package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;

public abstract class Condition {
    public abstract boolean satisfied(BasketInfo basketInfo, UserInfo user);

}
