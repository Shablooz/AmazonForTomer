package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;

public class CategoryPriceCondition extends Condition {

    private final String category;
    private final Bounder<Double> priceBounder;


    public CategoryPriceCondition(int conditionId, String category, double lowerBound, double upperBound) {
        super(conditionId);
        this.category = category;
        this.priceBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param category   product id
     * @param lowerBound dirbalek call this with upperbound
     */
    public CategoryPriceCondition(int conditionId, String category, double lowerBound) {
        super(conditionId);
        this.category = category;
        this.priceBounder = new Bounder<>(lowerBound);
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        //sum products quantity in category
        double price = basketInfo.basketProducts().stream().
                filter(p -> p.getCategory().equals(category)).
                map(b -> b.getPrice() * b.getQuantity()).reduce(0.0, Double::sum);

        if (!priceBounder.inBounds(price)) {
            throw new PurchaseExceedsPolicyException("category price must be " + priceBounder);
        }
    }

    public String toString() {
        return "category: " + category + ", price: " + priceBounder.toString();
    }
}
