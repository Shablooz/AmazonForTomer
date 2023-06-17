package BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseHistoryRepositoryAsListService {

    private PurchaseHistoryRepositoryAsListJPA purchaseHistoryRepositoryAsListJPA;

    @Autowired
    public PurchaseHistoryRepositoryAsListService(PurchaseHistoryRepositoryAsListJPA purchaseHistoryRepositoryAsListJPA) {
        this.purchaseHistoryRepositoryAsListJPA = purchaseHistoryRepositoryAsListJPA;
    }

    public PurchaseHistoryRepositoryAsListService(){

    }

    public void save(PurchaseHistoryRepositoryAsList purchaseHistoryRepositoryAsList){
        this.purchaseHistoryRepositoryAsListJPA.save(purchaseHistoryRepositoryAsList);
    }

    public void delete(PurchaseHistoryRepositoryAsList purchaseHistoryRepositoryAsList){
        this.purchaseHistoryRepositoryAsListJPA.delete(purchaseHistoryRepositoryAsList);
    }

    public PurchaseHistoryRepositoryAsList getPurchaseHistoryRepository() {
        return this.purchaseHistoryRepositoryAsListJPA.findById(1).orElse(null);
    }
}
