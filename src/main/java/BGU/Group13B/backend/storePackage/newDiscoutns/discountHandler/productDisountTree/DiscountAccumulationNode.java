package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;

import java.util.List;

public interface DiscountAccumulationNode {

    ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons);
    int getDiscountNodeId();
}
