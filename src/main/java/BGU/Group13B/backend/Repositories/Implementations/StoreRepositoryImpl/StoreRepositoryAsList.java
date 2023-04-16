package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Store;

import java.util.List;
import java.util.Optional;

public class StoreRepositoryAsList implements IStoreRepository {
    private final List<Store> stores;

    public StoreRepositoryAsList(List<Store> stores) {
        this.stores = stores;
    }

    @Override
    public Store getStore(int storeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
