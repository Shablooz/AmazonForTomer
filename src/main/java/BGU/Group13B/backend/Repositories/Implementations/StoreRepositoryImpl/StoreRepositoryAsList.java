package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingleService;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class StoreRepositoryAsList implements IStoreRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    private boolean saveMode;


    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "stores_set",
            joinColumns = {@JoinColumn(name = "StoreRepositoryAsList_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "store_id", referencedColumnName = "storeId")})
    private  Set<Store> stores;
    private  AtomicInteger storeIdCounter;


    public StoreRepositoryAsList() {
        this.stores = new ConcurrentSkipListSet<>(Comparator.comparingInt(Store::getStoreId));
        this.saveMode = true;
        this.storeIdCounter=new AtomicInteger(0);
    }
    public StoreRepositoryAsList(boolean saveMode) {
        this.stores = new ConcurrentSkipListSet<>(Comparator.comparingInt(Store::getStoreId));
        this.saveMode = saveMode;
        this.storeIdCounter=new AtomicInteger(0);
    }

    @Override
    public Store getStore(int storeId) {
        return this.stores.stream().filter(store -> store.getStoreId() == storeId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is not store with the id " + storeId));
    }

    public Set<Integer> getAllStoresId(){
        Set<Integer> idSet = new HashSet<>();
        for(Store store: stores){
            idSet.add(store.getStoreId());
        }
        return idSet;
    }

    //(#24) open store - requirement 3.2
    @Override
    public synchronized int addStore(int founderId, String storeName, String category) {
        int storeId = storeIdCounter.getAndIncrement();
        this.stores.add(new Store(storeId, founderId, storeName, category));
        save();
        return storeId;
    }

    @Override
    public synchronized void removeStore(int storeId ) {
        this.stores.remove(stores.stream().filter(store -> store.getStoreId() == storeId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is not store with the id " + storeId)));
        save();
    }

    @Override
    public void reset() {
        this.stores.clear();
        this.storeIdCounter.set(0);
        save();
    }
    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(StoreRepoService.class).save(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getSaveMode() {
        return saveMode;
    }

    public void setSaveMode(boolean saved) {
        saveMode = saved;
    }

    public Set<Store> getStores() {
        return stores;
    }

    public void setStores(Set<Store> stores) {
        this.stores = stores;
    }

    public AtomicInteger getStoreIdCounter() {
        return storeIdCounter;
    }

    public void setStoreIdCounter(AtomicInteger storeIdCounter) {
        this.storeIdCounter = storeIdCounter;
    }
}
