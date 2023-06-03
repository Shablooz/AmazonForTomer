package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.DiscountAccumulationNode;

import java.util.List;

public class DiscountNode implements DiscountAccumulationNode {
    private final int discountNodeId;
    private final StoreDiscount discount;

    public DiscountNode(int discountNodeId, StoreDiscount discount) {
        this.discountNodeId = discountNodeId;
        this.discount = discount;
    }

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        return discount.computeProductDiscountMap(basketInfo, userInfo, coupons);
    }
    @Override
    public int getDiscountNodeId() {
        return discountNodeId;
    }
}
