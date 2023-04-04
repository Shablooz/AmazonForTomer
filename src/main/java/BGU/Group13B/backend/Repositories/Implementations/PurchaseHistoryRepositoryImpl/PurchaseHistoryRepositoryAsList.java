package BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl;

import BGU.Group13B.backend.User.PurchaseHistory;

import java.util.List;

public class PurchaseHistoryRepositoryAsList {

    private final List<PurchaseHistory> purchaseHistories;

    public PurchaseHistoryRepositoryAsList(List<PurchaseHistory> purchaseHistories) {
        this.purchaseHistories = purchaseHistories;
    }
}
