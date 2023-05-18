package BGU.Group13B.backend.storePackage.newDiscoutns;

import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class PurchasePolicy {
    private final Condition condition;

    public PurchasePolicy(Condition condition) {
        this.condition = condition;
    }
}
