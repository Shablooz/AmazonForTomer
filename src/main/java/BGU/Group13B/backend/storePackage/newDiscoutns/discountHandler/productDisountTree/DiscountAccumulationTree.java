package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DiscountAccumulationTree extends DiscountAccumulationNode {
    @OneToOne(cascade = CascadeType.ALL)
    protected DiscountAccumulationNode left;
    @OneToOne(cascade = CascadeType.ALL)
    protected DiscountAccumulationNode right;

    public DiscountAccumulationTree(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        super(nodeId);
        this.left = left;
        this.right = right;
    }

    public DiscountAccumulationTree() {
        this.left = null;
        this.right = null;
    }
}
