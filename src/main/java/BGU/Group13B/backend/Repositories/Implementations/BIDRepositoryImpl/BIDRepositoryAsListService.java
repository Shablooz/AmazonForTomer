package BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.DailyUserTrafficRepositoryImpl.DailyUserTrafficRepositoryAsList;
import BGU.Group13B.backend.Repositories.Implementations.DailyUserTrafficRepositoryImpl.DailyUserTrafficRepositoryAsListJPA;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BIDRepositoryAsListService {
    private BIDRepositoryAsListJPA bIDRepositoryAsListJPA;

    @Autowired
    public BIDRepositoryAsListService(BIDRepositoryAsListJPA bIDRepositoryAsListJPA) {
        this.bIDRepositoryAsListJPA = bIDRepositoryAsListJPA;
    }

    public BIDRepositoryAsListService(){

    }

    public void save(BIDRepositoryAsList bIDRepositoryAsList){
        SingletonCollection.setBidRepository(this.bIDRepositoryAsListJPA.save(bIDRepositoryAsList));
    }

    public void delete(BIDRepositoryAsList bIDRepositoryAsList){
        bIDRepositoryAsListJPA.delete(bIDRepositoryAsList);
    }

    public BIDRepositoryAsList getBIDRepository() {
        return bIDRepositoryAsListJPA.findById(1).orElse(null);
    }
}
