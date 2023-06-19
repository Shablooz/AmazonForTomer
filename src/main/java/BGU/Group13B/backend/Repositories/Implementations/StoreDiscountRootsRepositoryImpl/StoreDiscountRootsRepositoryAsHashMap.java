package BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountRootsRepository;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class StoreDiscountRootsRepositoryAsHashMap implements IStoreDiscountRootsRepository {
    @Transient
    private boolean saveMode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "StoreDiscountRootsRepositoryAsHashMap_roots",
            joinColumns = {@JoinColumn(name = "StoreDiscountRootsRepositoryAsHashMap_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "store_Id")
    @Column(name = "discount_node_id")
    private Map<Integer, Integer> roots;


    public StoreDiscountRootsRepositoryAsHashMap() {
        this.saveMode = true;
        roots = new ConcurrentHashMap<>();
    }

    @Override
    public void setStoreDiscountRoot(int storeId, int discountNodeId) {
        roots.put(storeId, discountNodeId);
        save();
    }

    @Override
    public int getStoreDiscountRoot(int storeId) {
        return roots.getOrDefault(storeId, -1);
    }

    @Override
    public void removeStoreDiscountRoot(int storeId) {
        roots.remove(storeId);
        save();
    }

    @Override
    public void reset() {
        roots.clear();
        save();
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getRoots() {
        return roots;
    }

    public void setRoots(Map<Integer, Integer> roots) {
        this.roots = roots;
    }

    private void save(){
        if(saveMode && SingletonCollection.databaseExists())
            SingletonCollection.getContext().getBean(StoreDiscountRootRepositoryAsHashMapService.class).save(this);
    }


    public boolean isSaveMode() {
        return saveMode;
    }
}
