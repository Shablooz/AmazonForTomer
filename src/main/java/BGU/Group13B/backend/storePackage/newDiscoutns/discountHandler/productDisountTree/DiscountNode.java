package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.DiscountAccumulationNode;
import BGU.Group13B.frontEnd.components.views.ManageDiscountsView;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.util.LinkedList;
import java.util.List;
@Entity
public class DiscountNode extends DiscountAccumulationNode {
    private int discountNodeId;
    @ManyToOne
    private StoreDiscount discount;


    public DiscountNode(int discountNodeId, StoreDiscount discount) {
        this.discountNodeId = discountNodeId;
        this.discount = discount;
    }

    public DiscountNode() {
        super();
        this.discount = null;
        this.discountNodeId = 420;
    }

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        return discount.computeProductDiscountMap(basketInfo, userInfo, coupons);
    }
    @Override
    public int getDiscountNodeId() {
        return discountNodeId;
    }

    @Override
    public DiscountAccumulationTreeInfo getInfo() {
        LinkedList<DiscountInfo> discountInfos = new LinkedList<>();
        discountInfos.add(new DiscountInfo(discount.getDiscountId(), discount.toString()));
        return new DiscountAccumulationTreeInfo(new LinkedList<>(), discountInfos);
    }
}
