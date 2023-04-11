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
    public Optional<Store> getStore(int storeId) {
        return Optional.empty();
    }
}
