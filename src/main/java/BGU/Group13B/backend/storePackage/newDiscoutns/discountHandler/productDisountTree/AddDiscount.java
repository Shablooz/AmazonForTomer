package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;
import BGU.Group13B.frontEnd.components.views.ManageDiscountsView;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;

import java.util.List;

public class AddDiscount extends DiscountAccumulationTree {

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        ProductDiscountMap result = left.computeProductDiscountMap(basketInfo, userInfo, coupons);
        result.computeAddDiscount(right.computeProductDiscountMap(basketInfo, userInfo, coupons));
        return result;
    }

    public AddDiscount(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        super(nodeId, left, right);
    }

    public AddDiscount(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        super(nodeId, left, right);
    }

}
