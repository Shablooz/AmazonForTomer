package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounders.IntBounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.CategoryQuantityConditionEntity;

@Entity
public class CategoryQuantityCondition extends Condition {
    private String category;
    @OneToOne(cascade = CascadeType.ALL)
    private final IntBounder quantityBounder;


    public CategoryQuantityCondition(int conditionId, String category, int lowerBound, int upperBound){
        super(conditionId);
        this.category = category;
        this.quantityBounder = new IntBounder(lowerBound, upperBound);
    }

    /**
     * @param category      product id
     * @param lowerBound    dirbalek call this with upperbound
     */
    public CategoryQuantityCondition(int conditionId, String category, int lowerBound){
        super(conditionId);
        this.category = category;
        this.quantityBounder = new IntBounder(lowerBound);
    }

    //added for hibernate
    public CategoryQuantityCondition() {
        super(0);
        this.category = null;
        this.quantityBounder = null;
    }

    @Override
    public void satisfied(BasketInfo basketInfo, UserInfo user) throws PurchaseExceedsPolicyException {
        //sum products quantity in category
        int quantity = basketInfo.basketProducts().stream().
                filter(p -> p.getCategory().equals(category)).
                map(BasketProduct::getQuantity).reduce(0, Integer::sum);

        if(!quantityBounder.inBounds(quantity)){
            throw new PurchaseExceedsPolicyException("quantity of category must be " + quantityBounder);
        }
    }

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        if(quantityBounder.hasUpperBound()){
            return new CategoryQuantityConditionEntity(category, parent, quantityBounder.getLowerBound(), quantityBounder.getUpperBound());
        }
        return new CategoryQuantityConditionEntity(category, parent, quantityBounder.getLowerBound());
    }

    public String toString(){
        return "category: " + category + ", quantity: " + quantityBounder.toString();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public IntBounder getQuantityBounder() {
        return quantityBounder;
    }
}
