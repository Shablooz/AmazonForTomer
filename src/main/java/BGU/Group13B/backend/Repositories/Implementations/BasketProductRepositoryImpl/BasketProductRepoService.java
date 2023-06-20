package BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryJPA;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BasketProductRepoService {

    private BasketProductRepoJPA basketProductRepoJPA;

    @Autowired
    public BasketProductRepoService(BasketProductRepoJPA basketProductRepoJPA) {
        this.basketProductRepoJPA = basketProductRepoJPA;
    }

    public BasketProductRepoService(){

    }

    public void save(BasketProductRepositoryAsHashMap basketProductRepositoryAsHashMap){
        SingletonCollection.setBasketProductRepository( basketProductRepoJPA.save(basketProductRepositoryAsHashMap));
    }

    public void delete(BasketProductRepositoryAsHashMap basketProductRepositoryAsHashMap){
        basketProductRepoJPA.delete(basketProductRepositoryAsHashMap);
    }

    public BasketProductRepositoryAsHashMap getBasketProductRepository() {
        return basketProductRepoJPA.findById(1).orElse(null);
    }
}
