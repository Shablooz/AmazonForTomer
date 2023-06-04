package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.PurchaseHistory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface IPurchaseHistoryRepository {

    boolean isPurchase(int userId, int storeId, int productId);

    boolean isPurchaseFromStore(int userId, int storeId);

    List<PurchaseHistory> getAllPurchases(int userId);

    public PurchaseHistory addPurchase(int userId, int storeId, ConcurrentLinkedQueue<BasketProduct> products, double price);

    void reset();

    List<PurchaseHistory> getStorePurchaseHistory(int storeId);
     void removePurchase(PurchaseHistory purchaseHistory);
}

