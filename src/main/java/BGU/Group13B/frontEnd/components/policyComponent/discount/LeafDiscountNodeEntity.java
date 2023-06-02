package BGU.Group13B.frontEnd.components.policyComponent.discount;

public class LeafDiscountNodeEntity implements DiscountNodeEntity {

    private int discountId;
    private String description;

    public LeafDiscountNodeEntity(int discountId, String description) {
        this.discountId = discountId;
        this.description = description;
    }

    public int getDiscountId() {
        return discountId;
    }

    @Override
    public String toString(){
        return description;
    }
}
