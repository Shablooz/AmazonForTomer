package BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMapJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountAccumulationRepositoryAsHashMapService {

    private DiscountAccumulationRepositoryAsHashMapJPA discountAccumulationRepositoryAsHashMapJPA;

    @Autowired
    public DiscountAccumulationRepositoryAsHashMapService(DiscountAccumulationRepositoryAsHashMapJPA discountAccumulationRepositoryAsHashMapJPA) {
        this.discountAccumulationRepositoryAsHashMapJPA = discountAccumulationRepositoryAsHashMapJPA;
    }

    public DiscountAccumulationRepositoryAsHashMapService(){

    }

    public void save(DiscountAccumulationRepositoryAsHashMap discountAccumulationRepositoryAsHashMap){
        discountAccumulationRepositoryAsHashMapJPA.save(discountAccumulationRepositoryAsHashMap);
    }

    public void delete(DiscountAccumulationRepositoryAsHashMap discountAccumulationRepositoryAsHashMap){
        discountAccumulationRepositoryAsHashMapJPA.delete(discountAccumulationRepositoryAsHashMap);
    }

    public DiscountAccumulationRepositoryAsHashMap getDiscountAccumulationAsHashMap() {
        return discountAccumulationRepositoryAsHashMapJPA.findById(1).orElse(null);
    }
}
