package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.SingletonCollection;

public class ProductPriceCondition extends Condition {
    private final int productId;
    private final Bounder<Double> priceBounder;


    public ProductPriceCondition(int conditionId, int productId, double lowerBound, double upperBound){
        super(conditionId);
        this.productId = productId;
        this.priceBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param productId     product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public ProductPriceCondition(int conditionId, int productId, double lowerBound){
        super(conditionId);
        this.productId = productId;
        this.priceBounder = new Bounder<>(lowerBound);
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        BasketProduct product = basketInfo.basketProducts().
                stream().filter(p -> p.getProductId() == productId).
                findFirst().orElse(null);

        String productName = SingletonCollection.getProductRepository().getProductById(productId).getName();
        if((product == null && priceBounder.getLowerBound() > 0)
                || !priceBounder.inBounds(product.getPrice()))
            throw new PurchaseExceedsPolicyException(productName + " price must be " + priceBounder);
    }
    public String toString(){
        //mafhhhhhheeeeid
        return "product: " + SingletonCollection.getProductRepository().getProductById(productId).getName() + ", price: " + priceBounder.toString();
    }
}
