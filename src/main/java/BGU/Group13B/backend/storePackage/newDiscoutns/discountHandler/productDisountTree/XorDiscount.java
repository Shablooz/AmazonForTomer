package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;

import java.util.List;

public class XorDiscount extends DiscountAccumulationTree{
    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        ProductDiscountMap result = left.computeProductDiscountMap(basketInfo, userInfo, coupons);
        result.computeXorDiscount(right.computeProductDiscountMap(basketInfo, userInfo, coupons));
        return result;
    }

    public XorDiscount(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        super(nodeId, left, right);
    }
}
