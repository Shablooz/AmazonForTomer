package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.Bounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounders.DoubleBounder;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.CategoryPriceConditionEntity;

@Entity
public class CategoryPriceCondition extends Condition {


    private String category;

    @OneToOne(cascade = CascadeType.ALL)
    private DoubleBounder priceBounder;


    public CategoryPriceCondition(int conditionId, String category, double lowerBound, double upperBound) {
        super(conditionId);
        this.category = category;
        this.priceBounder = new DoubleBounder(lowerBound, upperBound);
    }

    /**
     * @param category   product id
     * @param lowerBound dirbalek call this with upperbound
     */
    public CategoryPriceCondition(int conditionId, String category, double lowerBound) {
        super(conditionId);
        this.category = category;
        this.priceBounder = new DoubleBounder(lowerBound);
    }
    //added for hibernate
    public CategoryPriceCondition() {
        super(0);
        this.category = null;
        this.priceBounder = null;
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

    @Override
    public ConditionEntity convertToUiEntity(LogicalConditionEntity parent) {
        if(priceBounder.hasUpperBound()){
            return new CategoryPriceConditionEntity(category, parent, priceBounder.getLowerBound(), priceBounder.getUpperBound());
        }
        return new CategoryPriceConditionEntity(category, parent, priceBounder.getLowerBound());
    }

    public String toString() {
        return "category: " + category + ", price: " + priceBounder.toString();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public DoubleBounder getPriceBounder() {
        return priceBounder;
    }

    public void setPriceBounder(DoubleBounder priceBounder) {
        this.priceBounder = priceBounder;
    }
}
