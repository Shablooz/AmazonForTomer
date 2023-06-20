package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;
import BGU.Group13B.frontEnd.components.views.ManageDiscountsView;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.List;
@Entity
public class AddDiscount extends DiscountAccumulationTree {

    public AddDiscount() {
        super();
    }

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        ProductDiscountMap result = left.computeProductDiscountMap(basketInfo, userInfo, coupons);
        result.computeAddDiscount(right.computeProductDiscountMap(basketInfo, userInfo, coupons));
        return result;
    }

    public AddDiscount(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        super(nodeId, left, right);
    }

    //SUS
    @Transient
    @Override
    public DiscountAccumulationTreeInfo getInfo() {
        //left - has the rest of the tree
        //right - has the current discount node

        DiscountAccumulationTreeInfo leftInfo = left.getInfo();
        DiscountAccumulationTreeInfo rightInfo = right.getInfo();
        leftInfo.operators().add(ManageDiscountsView.Operator.ADD);
        leftInfo.discounts().add(rightInfo.discounts().get(0) /*should only be of size 1*/);
        return leftInfo;
    }

}
