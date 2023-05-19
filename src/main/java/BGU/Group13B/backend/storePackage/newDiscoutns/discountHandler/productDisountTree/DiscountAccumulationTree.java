package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree;


public abstract class DiscountAccumulationTree implements DiscountAccumulationNode {
    private int nodeId;
    protected DiscountAccumulationNode left;
    protected DiscountAccumulationNode right;

    public DiscountAccumulationTree(int nodeId, DiscountAccumulationNode left, DiscountAccumulationNode right) {
        this.nodeId = nodeId;
        this.left = left;
        this.right = right;
    }

    @Override
    public int getDiscountNodeId() {
        return nodeId;
    }
}
