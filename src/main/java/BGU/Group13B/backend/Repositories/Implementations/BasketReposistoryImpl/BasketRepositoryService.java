package BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository.StoreScoreRepoJPA;
import BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository.StoreScoreSingle;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BasketRepositoryService {

    private BasketRepositoryJPA basketRepositoryJPA;

    @Autowired
    public BasketRepositoryService(BasketRepositoryJPA basketRepositoryJPA) {
        this.basketRepositoryJPA = basketRepositoryJPA;
    }

    public BasketRepositoryService(){

    }

    public void save(BasketRepositoryAsHashMap basketRepositoryAsHashMap){
        SingletonCollection.setBasketRepository( basketRepositoryJPA.save(basketRepositoryAsHashMap));
    }

    public void delete(BasketRepositoryAsHashMap basketRepositoryAsHashMap){
        basketRepositoryJPA.delete(basketRepositoryAsHashMap);
    }

    public BasketRepositoryAsHashMap getBasketRepository() {
        return basketRepositoryJPA.findById(1).orElse(null);
    }
}
