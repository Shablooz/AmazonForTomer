package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class CategoryPriceCondition extends Condition {

    private final String category;
    private final Bounder<Double> priceBounder;


    public CategoryPriceCondition(int conditionId, String category, double lowerBound, double upperBound){
        super(conditionId);
        this.category = category;
        this.priceBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param category      product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public CategoryPriceCondition(int conditionId, String category, double lowerBound){
        super(conditionId);
        this.category = category;
        this.priceBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        //sum products quantity in category
        double price = basketInfo.basketProducts().stream().
                filter(p -> p.getCategory().equals(category)).
                map(BasketProduct::getPrice).reduce(0.0, Double::sum);

        return priceBounder.inBounds(price);
    }
    public String toString(){
        return "category: " + category + ", price: " + priceBounder.toString();
    }
}
