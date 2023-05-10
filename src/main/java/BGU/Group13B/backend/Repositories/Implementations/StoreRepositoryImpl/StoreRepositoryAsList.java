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
        return this.stores.stream().filter(store -> store.getStoreId() == storeId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is not store with the id " + storeId));
    }

    //(#24) open store - requirement 3.2
    @Override
    public synchronized int addStore(int founderId, String storeName, String category) {
        int storeId = storeIdCounter.getAndIncrement();
        this.stores.add(new Store(storeId, founderId, storeName, category));
        return storeId;
    }

    @Override
    public synchronized void removeStore(int storeId ) {
        this.stores.remove(stores.stream().filter(store -> store.getStoreId() == storeId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is not store with the id " + storeId)));
    }

    @Override
    public void reset() {
        this.stores.clear();
        this.storeIdCounter.set(0);
    }

    @Override
    public void removeAllStores(int userId) {
        //remove stores products
        for (Store store : this.stores) {
            if (store.getStorePermission().getFounderId() == userId) {
                store.getStorePermission().emptyMaps();
                store.removeAllProducts();
            }
        }
        this.stores.removeIf(store -> store.getStorePermission().getFounderId() == userId);
    }


}
