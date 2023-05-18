package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

public class ProductPriceCondition extends Condition {
    private final int productId;
    private final Bounder<Double> priceBounder;


    public ProductPriceCondition(int productId, double lowerBound, double upperBound){
        this.productId = productId;
        this.priceBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param productId     product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public ProductPriceCondition(int productId, double lowerBound){
        this.productId = productId;
        this.priceBounder = new Bounder<>(lowerBound);
    }

    @Override
    public boolean satisfied(BasketInfo basketInfo, UserInfo user) {
        BasketProduct product = basketInfo.basketProducts().
                stream().filter(p -> p.getProductId() == productId).
                findFirst().orElse(null);

        if(product == null)
            return priceBounder.getLowerBound() <= 0;

        return priceBounder.inBounds(product.getPrice());
    }
}
