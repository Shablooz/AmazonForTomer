package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class StoreQuantityCondition extends Condition {

    private final Bounder<Integer> quantityBounder;


    public StoreQuantityCondition(int lowerBound, int upperBound) {
        this.quantityBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param lowerBound dirbalek call this with upperbound
     */
    public StoreQuantityCondition(int lowerBound) {
        this.quantityBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        //sum all products quantities
        int quantity = basketInfo.basketProducts().stream().
                map(BasketProduct::getQuantity).reduce(0, Integer::sum);

        return quantityBounder.inBounds(quantity);
    }
}
