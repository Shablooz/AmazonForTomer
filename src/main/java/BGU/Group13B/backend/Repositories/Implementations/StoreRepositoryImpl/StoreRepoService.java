package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingleJPA;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreRepoService {

    private StoreRepoJPA storeRepoJPA;

    @Autowired
    public StoreRepoService(StoreRepoJPA storeRepoJPA) {
        this.storeRepoJPA = storeRepoJPA;
    }

    public StoreRepoService(){

    }

    public void save(StoreRepositoryAsList storeRepositoryAsList){
        SingletonCollection.setStoreRepository(storeRepoJPA.save(storeRepositoryAsList));
        //storeRepoJPA.save(storeRepositoryAsList);
    }

    public void delete(StoreRepositoryAsList storeRepositoryAsList){
        storeRepoJPA.delete(storeRepositoryAsList);
    }

    public StoreRepositoryAsList getStoreRepoJPA() {
        return storeRepoJPA.findById(1).orElse(null);
    }
}
