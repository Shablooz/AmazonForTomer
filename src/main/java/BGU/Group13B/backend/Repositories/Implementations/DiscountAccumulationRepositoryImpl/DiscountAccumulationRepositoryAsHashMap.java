package BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IDiscountAccumulationRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.*;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscountAccumulationRepositoryAsHashMap implements IDiscountAccumulationRepository {
    private final ConcurrentHashMap<Integer/*discountNodeId*/, DiscountAccumulationNode> noders;

    private final AtomicInteger nextId;

    public DiscountAccumulationRepositoryAsHashMap() {
        noders = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
    }


    @Override
    public DiscountAccumulationNode addDiscountAsRoot(StoreDiscount discount) {
        int rootId = nextId.getAndIncrement();
        DiscountAccumulationNode discountNode = new DiscountNode(rootId, discount);
        noders.put(rootId, discountNode);
        return discountNode;
    }

    @Override
    public DiscountAccumulationNode addDiscountToMAXRoot(int rootId ,StoreDiscount discount) {
        DiscountAccumulationNode discountNode = addDiscountAsRoot(discount);
        int newRootId = nextId.getAndIncrement();
        DiscountAccumulationNode root = getDiscountAccumulationNode(rootId);
        DiscountAccumulationTree newRoot = new MaxDiscount(newRootId, root, discountNode);
        noders.put(newRootId, newRoot);
        return newRoot;
    }


    @Override
    public DiscountAccumulationNode addDiscountToXORRoot(int rootId ,StoreDiscount discount) {
        DiscountAccumulationNode discountNode = addDiscountAsRoot(discount);
        int newRootId = nextId.getAndIncrement();
        DiscountAccumulationNode root = getDiscountAccumulationNode(rootId);
        DiscountAccumulationTree newRoot = new XorDiscount(newRootId, root, discountNode);
        noders.put(newRootId, newRoot);
        return newRoot;
    }

    @Override
    public DiscountAccumulationNode addDiscountToADDRoot(int rootId ,StoreDiscount discount) {
        DiscountAccumulationNode discountNode = addDiscountAsRoot(discount);
        int newRootId = nextId.getAndIncrement();
        DiscountAccumulationNode root = getDiscountAccumulationNode(rootId);
        DiscountAccumulationTree newRoot = new AddDiscount(newRootId, root, discountNode);
        noders.put(newRootId, newRoot);
        return newRoot;
    }

    @Override
    public DiscountAccumulationNode getDiscountAccumulationNode(int nodeId) {
        if(!noders.containsKey(nodeId))
            throw new IllegalArgumentException("the node with node id: " + nodeId + " does not exists");

        return noders.get(nodeId);
    }

    @Override
    public void removeDiscountAccumulationNode(int nodeId) {
        noders.remove(nodeId);
    }

    @Override
    public void reset() {
        noders.clear();
        nextId.set(0);
    }
}
