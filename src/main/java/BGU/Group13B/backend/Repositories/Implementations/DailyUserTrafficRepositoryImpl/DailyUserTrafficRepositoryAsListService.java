package BGU.Group13B.backend.Repositories.Implementations.DailyUserTrafficRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMapJPA;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyUserTrafficRepositoryAsListService {
    private DailyUserTrafficRepositoryAsListJPA dailyUserTrafficRepositoryAsListJPA;

    @Autowired
    public DailyUserTrafficRepositoryAsListService(DailyUserTrafficRepositoryAsListJPA dailyUserTrafficRepositoryAsListJPA) {
        this.dailyUserTrafficRepositoryAsListJPA = dailyUserTrafficRepositoryAsListJPA;
    }

    public DailyUserTrafficRepositoryAsListService(){

    }

    public void save(DailyUserTrafficRepositoryAsList dailyUserTrafficRepositoryAsList){
        SingletonCollection.setDailyUserTrafficRepository(this.dailyUserTrafficRepositoryAsListJPA.save(dailyUserTrafficRepositoryAsList));
    }

    public void delete(DailyUserTrafficRepositoryAsList dailyUserTrafficRepositoryAsList){
        dailyUserTrafficRepositoryAsListJPA.delete(dailyUserTrafficRepositoryAsList);
    }

    public DailyUserTrafficRepositoryAsList getDailyUserTrafficRepository() {
        return dailyUserTrafficRepositoryAsListJPA.findById(1).orElse(null);
    }
}
