package BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.User.PurchaseHistory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PurchaseHistoryRepositoryAsList implements IPurchaseHistoryRepository {

    private final List<PurchaseHistory> purchaseHistories;

    public PurchaseHistoryRepositoryAsList() {
        this.purchaseHistories = new LinkedList<>();
    }

    @Override
    public boolean isPurchase(int userId, int storeId, int productId) {
        return purchaseHistories.stream().anyMatch(purchaseHistory -> purchaseHistory.getUserId() == userId && purchaseHistory.getStoreId() == storeId &&
                purchaseHistory.getProductsId().contains(productId));
    }

    @Override
    public boolean isPurchaseFromStore(int userId, int storeId) {
        return purchaseHistories.stream().anyMatch(purchaseHistory -> purchaseHistory.getUserId() == userId && purchaseHistory.getStoreId() == storeId);
    }

    @Override
    public String getAllPurchases(int userId) {
        List<PurchaseHistory> purchases = getAllPurchasesAsList(userId);
        StringBuilder sb = new StringBuilder();
        for (PurchaseHistory purchaseHistory : purchases) {
            sb.append(purchaseHistory.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void addPurchase(int userId, int storeId, List<Integer> products, List<Integer> amounts, double price) {

        HashMap<Integer, Double> productsAmounts = new HashMap<>();
        for (int i = 0; i < products.size(); i++) {
            int productId = products.get(i);
            double amount = amounts.get(i);
            productsAmounts.put(productId, amount);
        }
        purchaseHistories.add(new PurchaseHistory(userId, storeId, productsAmounts, price));
    }

    private List<PurchaseHistory> getAllPurchasesAsList(int userId) {
        List<PurchaseHistory> purchases = new LinkedList<>();
        for (PurchaseHistory purchaseHistory : purchaseHistories) {
            if (purchaseHistory.getUserId() == userId) {
                purchases.add(purchaseHistory);
            }
        }
        return purchases;
    }

    @Override
    public void reset() {
        purchaseHistories.clear();
    }
}
