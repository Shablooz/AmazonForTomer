package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

public class StoreQuantityCondition extends Condition {

    private final Bounder<Integer> quantityBounder;


    public StoreQuantityCondition(int conditionId, int lowerBound, int upperBound) {
        super(conditionId);
        this.quantityBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param lowerBound dirbalek call this with upperbound
     */
    public StoreQuantityCondition(int conditionId, int lowerBound) {
        super(conditionId);
        this.quantityBounder = new Bounder<>(lowerBound);
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        //sum all products quantities
        int quantity = basketInfo.basketProducts().stream().
                map(BasketProduct::getQuantity).reduce(0, Integer::sum);

        if(!quantityBounder.inBounds(quantity))
            throw new PurchaseExceedsPolicyException("total quantity must be " + quantityBounder);
    }

    @Override
    public String toString() {
        return "total quantity: " + quantityBounder;
    }
}
