package BGU.Group13B.backend.Repositories.Implementations.PurchasePolicyRootsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Interfaces.IPurchasePolicyRootsRepository;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class PurchasePolicyRootsRepositoryAsHashMap implements IPurchasePolicyRootsRepository {
    @Transient
    private boolean saveMode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PurchasePolicyRootsRepositoryAsHashMap_conditionRootId",
            joinColumns = {@JoinColumn(name = "PurchasePolicyRootsRepositoryAsHashMap_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "storeId")
    @Column(name = "conditionRootId")
    private Map<Integer/*storeId*/, Integer/*conditionRootId*/> purchasePolicyRoots;

    public PurchasePolicyRootsRepositoryAsHashMap() {
        saveMode = true;
        this.purchasePolicyRoots = new ConcurrentHashMap<>();
    }

    public int addPurchasePolicyRoot(int storeId, int conditionRootId) {
        purchasePolicyRoots.put(storeId, conditionRootId);
        save();
        return conditionRootId;
    }

    public int getPurchasePolicyRoot(int storeId) {
        return purchasePolicyRoots.getOrDefault(storeId, -1);
    }

    public void removePurchasePolicyRoot(int storeId) {
        purchasePolicyRoots.remove(storeId);
        save();
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void save(){
        if(saveMode && SingletonCollection.databaseExists())
            SingletonCollection.getContext().getBean(PurchasePolicyRootsRepositoryAsHashMapService.class).save(this);
    }


    public Map<Integer, Integer> getPurchasePolicyRoots() {
        return purchasePolicyRoots;
    }

    public void setPurchasePolicyRoots(Map<Integer, Integer> purchasePolicyRoots) {
        this.purchasePolicyRoots = purchasePolicyRoots;
    }
}
