package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Store;

import java.util.List;

public class StoreRepositoryAsList implements IStoreRepository {
    private final List<Store> stores;

    public StoreRepositoryAsList(List<Store> stores) {
        this.stores = stores;
    }

    @Override
    public Store getStore(int storeId) {
        return null;
    }
}
