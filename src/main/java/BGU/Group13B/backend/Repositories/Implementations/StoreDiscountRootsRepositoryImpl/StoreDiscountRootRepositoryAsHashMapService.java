package BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreDiscountRootRepositoryAsHashMapService {
    private StoreDiscountRootsRepositoryAsHashMapJPA StoreDiscountRootsRepositoryAsHashMapJPA;

    @Autowired
    public StoreDiscountRootRepositoryAsHashMapService(StoreDiscountRootsRepositoryAsHashMapJPA storeDiscountRootsRepositoryAsHashMapJPA) {
        this.StoreDiscountRootsRepositoryAsHashMapJPA = storeDiscountRootsRepositoryAsHashMapJPA;
    }

    public StoreDiscountRootRepositoryAsHashMapService(){

    }

    public void save(StoreDiscountRootsRepositoryAsHashMap storeDiscountRootsRepositoryAsHashMap){
        StoreDiscountRootsRepositoryAsHashMapJPA.save(storeDiscountRootsRepositoryAsHashMap);
    }

    public void delete(StoreDiscountRootsRepositoryAsHashMap storeDiscountRootsRepositoryAsHashMap){
        StoreDiscountRootsRepositoryAsHashMapJPA.delete(storeDiscountRootsRepositoryAsHashMap);
    }

    public StoreDiscountRootsRepositoryAsHashMap getStoreDiscountRepository() {
        return StoreDiscountRootsRepositoryAsHashMapJPA.findById(1).orElse(null);
    }
}
