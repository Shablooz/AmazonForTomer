package BGU.Group13B.backend.Repositories.Implementations.PurchasePolicyRootsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchasePolicyRootsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PurchasePolicyRootsRepositoryAsHashMap implements IPurchasePolicyRootsRepository {
    private final Map<Integer/*storeId*/, Integer/*conditionRootId*/> purchasePolicyRoots;

    public PurchasePolicyRootsRepositoryAsHashMap() {
        this.purchasePolicyRoots = new ConcurrentHashMap<>();
    }

    public int addPurchasePolicyRoot(int storeId, int conditionRootId) {
        purchasePolicyRoots.put(storeId, conditionRootId);
        return conditionRootId;
    }

    public int getPurchasePolicyRoot(int storeId) {
        return purchasePolicyRoots.getOrDefault(storeId, -1);
    }

    public void removePurchasePolicyRoot(int storeId) {
        if(!purchasePolicyRoots.containsKey(storeId))
            throw new IllegalArgumentException("store id does not exist in purchase policy roots repository");
        purchasePolicyRoots.remove(storeId);
    }
}
