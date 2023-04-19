package BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.User.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public class PurchaseHistoryRepositoryAsJPA implements IPurchaseHistoryRepository {

    private final JpaRepository<PurchaseHistory, Integer> repo;

    public PurchaseHistoryRepositoryAsJPA(JpaRepository<PurchaseHistory, Integer> repo) {
        this.repo = repo;
    }

    @Override
    public boolean isPurchase(int userId, int storeId, int productId) {
        return false;
    }
}
