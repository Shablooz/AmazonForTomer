package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.PurchaseHistory;

import java.util.List;

public interface IPurchaseHistoryRepository {

    boolean isPurchase(int userId, int storeId, int productId);

    boolean isPurchaseFromStore(int userId, int storeId);

    String getAllPurchases(int userId);

    PurchaseHistory addPurchase(int userId, int storeId, List<Integer> products, List<Integer> amounts, double price);

    void reset();
}

