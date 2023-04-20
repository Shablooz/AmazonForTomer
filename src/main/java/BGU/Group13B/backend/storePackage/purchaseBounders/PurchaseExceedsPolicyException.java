package BGU.Group13B.backend.storePackage.purchaseBounders;

import BGU.Group13B.backend.User.PurchaseFailedException;

public class PurchaseExceedsPolicyException extends PurchaseFailedException {

    public PurchaseExceedsPolicyException(String message) {
        super(message);
    }
}
