package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.service.SingletonCollection;

public class ProductQuantityCondition extends Condition {
    private final int productId;
    private final Bounder<Integer> quantityBounder;


    public ProductQuantityCondition(int conditionId, int productId, int lowerBound, int upperBound){
        super(conditionId);
        this.productId = productId;
        this.quantityBounder = new Bounder<>(lowerBound, upperBound);
    }

    /**
     * @param productId     product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public ProductQuantityCondition(int conditionId, int productId, int lowerBound){
        super(conditionId);
        this.productId = productId;
        this.quantityBounder = new Bounder<>(lowerBound);
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        BasketProduct product = basketInfo.basketProducts().
                stream().filter(p -> p.getProductId() == productId).
                findFirst().orElse(null);

        String productName = SingletonCollection.getProductRepository().getProductById(productId).getName();
        if((product == null && quantityBounder.getLowerBound() > 0)
                || !quantityBounder.inBounds(product.getQuantity()))
            throw new PurchaseExceedsPolicyException(productName + " quantity must be " + quantityBounder);
    }

    public String toString(){
        //mafhhhhhheeeeid
        return "product: " + SingletonCollection.getProductRepository().getProductById(productId).getName() + ", quantity: " + quantityBounder.toString();
    }
}
