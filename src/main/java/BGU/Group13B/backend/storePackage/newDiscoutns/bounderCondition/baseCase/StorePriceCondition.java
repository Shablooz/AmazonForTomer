package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class StorePriceCondition extends Condition {
    private final Bounder<Double> priceBounder;


    public StorePriceCondition(int conditionId, double lowerBound, double upperBound) {
        super(conditionId);
        this.priceBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param lowerBound dirbalek call this with upperbound
     */
    public StorePriceCondition(int conditionId, double lowerBound) {
        super(conditionId);
        this.priceBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        //sum all products quantities
        double price = basketInfo.basketProducts().stream().
                map(BasketProduct::getPrice).reduce(0.0, Double::sum);

        return priceBounder.inBounds(price);
    }

    @Override
    public String toString() {
        return "total price: " + priceBounder;
    }
}
