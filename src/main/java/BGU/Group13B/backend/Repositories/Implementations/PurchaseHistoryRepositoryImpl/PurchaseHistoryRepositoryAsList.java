package BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.User.PurchaseHistory;

import java.util.LinkedList;
import java.util.List;

public class PurchaseHistoryRepositoryAsList implements IPurchaseHistoryRepository {

    private final List<PurchaseHistory> purchaseHistories;

    public PurchaseHistoryRepositoryAsList() {
        this.purchaseHistories = new LinkedList<>();
    }

    @Override
    public boolean isPurchase(int userId, int storeId, int productId) {
        return false;
    }

    @Override
    public boolean isPurchaseFromStore(int userId, int storeId) {
        return false;
    }
}
