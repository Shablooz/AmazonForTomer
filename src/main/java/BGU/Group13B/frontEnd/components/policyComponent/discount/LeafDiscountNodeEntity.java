package BGU.Group13B.frontEnd.components.policyComponent.discount;

import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;

public class LeafDiscountNodeEntity implements DiscountNodeEntity {

    private int discountId;
    private String description;

    public LeafDiscountNodeEntity(int discountId, String description) {
        this.discountId = discountId;
        this.description = description;
    }

    public LeafDiscountNodeEntity(DiscountInfo discountInfo){
        this.discountId = discountInfo.discountId();
        this.description = discountInfo.stringRepresentation();
    }

    public int getDiscountId() {
        return discountId;
    }

    @Override
    public String toString(){
        return description;
    }
}
