package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounders.IntBounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.ProductQuantityConditionEntity;

@Entity
public class ProductQuantityCondition extends Condition {
    private int productId;
    @OneToOne(cascade = CascadeType.ALL)
    private final IntBounder quantityBounder;


    public ProductQuantityCondition(int conditionId, int productId, int lowerBound, int upperBound){
        super(conditionId);
        this.productId = productId;
        this.quantityBounder = new IntBounder(lowerBound, upperBound);
    }

    /**
     * @param productId     product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public ProductQuantityCondition(int conditionId, int productId, int lowerBound){
        super(conditionId);
        this.productId = productId;
        this.quantityBounder = new IntBounder(lowerBound);
    }
    //added for hibernate
    public ProductQuantityCondition() {
        super(0);
        this.productId = 0;
        this.quantityBounder = null;
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

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        String productName = SingletonCollection.getProductRepository().getProductById(productId).getName();
        if(quantityBounder.hasUpperBound()){
            return new ProductQuantityConditionEntity(productId, productName, parent, quantityBounder.getLowerBound(), quantityBounder.getUpperBound());
        }
        return new ProductQuantityConditionEntity(productId, productName, parent, quantityBounder.getLowerBound());
    }

    public String toString(){
        //mafhhhhhheeeeid
        return "product: " + SingletonCollection.getProductRepository().getProductById(productId).getName() + ", quantity: " + quantityBounder.toString();
    }
}
