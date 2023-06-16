package BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PairForDiscountService {
    private PairForDiscountJPA pairForDiscountJPA;

    @Autowired
    public PairForDiscountService(PairForDiscountJPA pairForDiscountJPA) {
        this.pairForDiscountJPA = pairForDiscountJPA;
    }

    public PairForDiscountService(){

    }

    public void save(PairForDiscounts pairForDiscounts){
        pairForDiscountJPA.save(pairForDiscounts);
    }

    public void delete(PairForDiscounts pairForDiscounts){
        pairForDiscountJPA.delete(pairForDiscounts);
    }

    public PairForDiscounts getPairForDiscounts(int discountId, int storeId){
        return pairForDiscountJPA.findById(discountId).orElse(null);
    }
}
