package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class CategoryPriceCondition extends Condition {

    private final String category;
    private final Bounder<Double> quantityBounder;


    public CategoryPriceCondition(String category, double lowerBound, double upperBound){
        this.category = category;
        this.quantityBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param category      product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public CategoryPriceCondition(String category, double lowerBound){
        this.category = category;
        this.quantityBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        //sum products quantity in category
        double price = basketInfo.basketProducts().stream().
                filter(p -> p.getCategory().equals(category)).
                map(BasketProduct::getPrice).reduce(0.0, Double::sum);

        return quantityBounder.inBounds(price);
    }
}
