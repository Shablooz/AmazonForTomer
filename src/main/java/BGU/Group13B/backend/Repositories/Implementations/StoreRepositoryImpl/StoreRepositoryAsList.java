package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Store;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreRepositoryAsList implements IStoreRepository {
    private final Set<Store> stores;
    private final AtomicInteger storeIdCounter = new AtomicInteger(0);


    public StoreRepositoryAsList() {
        this.stores = new ConcurrentSkipListSet<>(Comparator.comparingInt(Store::getStoreId));
    }

    @Override
    public Store getStore(int storeId) {
        return null;
    }

    //(#24) open store - requirement 3.2
    @Override
    public void addStore(int founderId, String storeName, String category) {
        this.stores.add(new Store(storeIdCounter.getAndIncrement(), founderId, storeName, category));
    }
}
