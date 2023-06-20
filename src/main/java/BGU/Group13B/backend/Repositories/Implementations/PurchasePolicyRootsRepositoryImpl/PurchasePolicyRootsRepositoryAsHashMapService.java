package BGU.Group13B.backend.Repositories.Implementations.PurchasePolicyRootsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootsRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootsRepositoryAsHashMapJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchasePolicyRootsRepositoryAsHashMapService {
    private PurchasePolicyRootsRepositoryAsHashMapJPA purchasePolicyRootsRepositoryAsHashMapJPA;

    @Autowired
    public PurchasePolicyRootsRepositoryAsHashMapService(PurchasePolicyRootsRepositoryAsHashMapJPA purchasePolicyRootsRepositoryAsHashMapJPA) {
        this.purchasePolicyRootsRepositoryAsHashMapJPA = purchasePolicyRootsRepositoryAsHashMapJPA;
    }

    public PurchasePolicyRootsRepositoryAsHashMapService(){

    }

    public void save(PurchasePolicyRootsRepositoryAsHashMap purchasePolicyRootsRepositoryAsHashMap){
        purchasePolicyRootsRepositoryAsHashMapJPA.save(purchasePolicyRootsRepositoryAsHashMap);
    }

    public void delete(PurchasePolicyRootsRepositoryAsHashMap purchasePolicyRootsRepositoryAsHashMap){
        purchasePolicyRootsRepositoryAsHashMapJPA.delete(purchasePolicyRootsRepositoryAsHashMap);
    }

    public PurchasePolicyRootsRepositoryAsHashMap getPurchesPolicyHashMap() {
        return purchasePolicyRootsRepositoryAsHashMapJPA.findById(1).orElse(null);
    }
}
