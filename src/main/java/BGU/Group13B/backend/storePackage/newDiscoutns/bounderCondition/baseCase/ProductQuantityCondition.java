package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class ProductQuantityCondition extends Condition {
    private final int productId;
    private final Bounder<Integer> quantityBounder;


    public ProductQuantityCondition(int productId, int lowerBound, int upperBound){
        this.productId = productId;
        this.quantityBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param productId     product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public ProductQuantityCondition(int productId, int lowerBound){
        this.productId = productId;
        this.quantityBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        BasketProduct product = basketInfo.basketProducts().
                stream().filter(p -> p.getProductId() == productId).
                findFirst().orElse(null);

        if(product == null)
            return quantityBounder.getLowerBound() <= 0;

        return quantityBounder.inBounds(product.getQuantity());
    }
}
