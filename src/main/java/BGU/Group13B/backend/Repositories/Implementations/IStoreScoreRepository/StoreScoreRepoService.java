package BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingleJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreScoreRepoService {

    private StoreScoreRepoJPA storeScoreRepoJPA;

    @Autowired
    public StoreScoreRepoService(StoreScoreRepoJPA storeScoreRepoJPA) {
        this.storeScoreRepoJPA = storeScoreRepoJPA;
    }

    public StoreScoreRepoService(){

    }

    public void save(StoreScoreSingle storeScoreSingle){
        storeScoreRepoJPA.save(storeScoreSingle);
    }

    public void delete(StoreScoreSingle storeScoreSingle){
        storeScoreRepoJPA.delete(storeScoreSingle);
    }

    public StoreScoreSingle getStoreScoreSingle() {
        return storeScoreRepoJPA.findById(1).orElse(null);
    }
}
