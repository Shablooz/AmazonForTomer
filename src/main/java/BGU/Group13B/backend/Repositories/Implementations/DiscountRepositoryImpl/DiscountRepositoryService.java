package BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMapJPA;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountRepositoryService {
    private DiscountRepositoryJPA discountRepositoryJPA;

    @Autowired
    public DiscountRepositoryService(DiscountRepositoryJPA discountRepositoryJPA) {
        this.discountRepositoryJPA = discountRepositoryJPA;
    }

    public DiscountRepositoryService(){

    }

    public void save(DiscountRepositoryAsHashMap discountRepositoryAsHashMap){
        SingletonCollection.setDiscountRepository(this.discountRepositoryJPA.save(discountRepositoryAsHashMap));
    }

    public void delete(DiscountRepositoryAsHashMap discountRepositoryAsHashMap){
        discountRepositoryJPA.delete(discountRepositoryAsHashMap);
    }

    public DiscountRepositoryAsHashMap getDiscountRepositoryAsHashmap() {
        return discountRepositoryJPA.findById(1).orElse(null);
    }


}
