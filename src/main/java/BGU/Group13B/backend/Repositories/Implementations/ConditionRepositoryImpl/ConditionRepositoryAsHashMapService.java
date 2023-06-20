package BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl.DiscountAccumulationRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl.DiscountAccumulationRepositoryAsHashMapJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConditionRepositoryAsHashMapService {
    private ConditionRepositoryAsHashMapJPA conditionRepositoryAsHashMapJPA;

    @Autowired
    public ConditionRepositoryAsHashMapService(ConditionRepositoryAsHashMapJPA conditionRepositoryAsHashMapJPA) {
        this.conditionRepositoryAsHashMapJPA = conditionRepositoryAsHashMapJPA;
    }

    public ConditionRepositoryAsHashMapService(){

    }

    public void save(ConditionRepositoryAsHashMap conditionRepositoryAsHashMap){
        this.conditionRepositoryAsHashMapJPA.save(conditionRepositoryAsHashMap);
    }

    public void delete(ConditionRepositoryAsHashMap conditionRepositoryAsHashMap){
        conditionRepositoryAsHashMapJPA.delete(conditionRepositoryAsHashMap);
    }

    public ConditionRepositoryAsHashMap getConditionRepositoryAsHashMap() {
        return conditionRepositoryAsHashMapJPA.findById(1).orElse(null);
    }
}
