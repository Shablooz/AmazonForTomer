package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscountMap;
import BGU.Group13B.service.info.DiscountAccumulationTreeInfo;
import jakarta.persistence.*;

import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DiscountAccumulationNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int nodeId;

    public DiscountAccumulationNode() {
        this.nodeId = 420;
    }

    public abstract ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons);

    public abstract DiscountAccumulationTreeInfo getInfo();

    public DiscountAccumulationNode(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getDiscountNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
}
