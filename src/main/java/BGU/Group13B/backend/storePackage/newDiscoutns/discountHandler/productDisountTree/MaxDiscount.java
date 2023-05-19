package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;

import java.util.List;

public class MaxDiscount extends DiscountAccumulationTree {

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        var result = left.computeProductDiscountMap(basketInfo, userInfo, coupons);
        result.computeMaxDiscount(right.computeProductDiscountMap(basketInfo, userInfo, coupons));
        return result;
    }

    public MaxDiscount(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        super(nodeId, left, right);
    }
}
