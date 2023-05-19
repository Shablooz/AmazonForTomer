package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class CategoryQuantityCondition extends Condition {
    private final String category;
    private final Bounder<Integer> quantityBounder;


    public CategoryQuantityCondition(int conditionId, String category, int lowerBound, int upperBound){
        super(conditionId);
        this.category = category;
        this.quantityBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param category      product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public CategoryQuantityCondition(int conditionId, String category, int lowerBound){
        super(conditionId);
        this.category = category;
        this.quantityBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        //sum products quantity in category
        int quantity = basketInfo.basketProducts().stream().
                filter(p -> p.getCategory().equals(category)).
                map(BasketProduct::getQuantity).reduce(0, Integer::sum);

        return quantityBounder.inBounds(quantity);
    }
    public String toString(){
        return "category: " + category + ", quantity: " + quantityBounder.toString();
    }
}
