package BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountRootsRepository;

import java.util.concurrent.ConcurrentHashMap;

public class StoreDiscountRootsRepositoryAsHashMap implements IStoreDiscountRootsRepository {
    private final ConcurrentHashMap<Integer, Integer> roots;

    public StoreDiscountRootsRepositoryAsHashMap() {
        roots = new ConcurrentHashMap<>();
    }

    @Override
    public void setStoreDiscountRoot(int storeId, int discountNodeId) {
        roots.put(storeId, discountNodeId);
    }

    @Override
    public int getStoreDiscountRoot(int storeId) {
        return roots.get(storeId);
    }

    @Override
    public void removeStoreDiscountRoot(int storeId) {
        roots.remove(storeId);
    }

    @Override
    public void reset() {
        roots.clear();
    }
}
