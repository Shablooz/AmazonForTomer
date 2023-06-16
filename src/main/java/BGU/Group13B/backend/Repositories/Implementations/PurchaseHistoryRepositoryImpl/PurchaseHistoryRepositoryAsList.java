package BGU.Group13B.backend.Repositories.Implementations.PurchaseHistoryRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IPurchaseHistoryRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.PurchaseHistory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    public List<PurchaseHistory> getAllPurchases(int userId) {
        List<PurchaseHistory> purchases = new LinkedList<>();
        for (PurchaseHistory purchaseHistory : purchaseHistories) {
            if (purchaseHistory.getUserId() == userId) {
                purchases.add(purchaseHistory);
            }
        }
        return purchases;
    }
    @Override
    public PurchaseHistory addPurchase(int userId, int storeId, ConcurrentLinkedQueue<BasketProduct> products, double price) {

        HashMap<Integer, Integer> productsAmounts = new HashMap<>();
        for(BasketProduct basketProduct : products){
            productsAmounts.put(basketProduct.getProductId(), basketProduct.getQuantity());
        }
        PurchaseHistory purchaseHistory = new PurchaseHistory(userId, storeId, productsAmounts, price);
        purchaseHistories.add(purchaseHistory);
        return purchaseHistory;
    }


    @Override
    public void reset() {
        purchaseHistories.clear();
    }

    @Override
    public List<PurchaseHistory> getStorePurchaseHistory(int storeId) {
        List<PurchaseHistory> purchases = new LinkedList<>();
        for (PurchaseHistory purchaseHistory : purchaseHistories) {
            if (purchaseHistory.getStoreId() == storeId) {
                purchases.add(purchaseHistory);
            }
        }
        return purchases;
    }
    @Override
    public void removePurchase(PurchaseHistory purchaseHistory) {
        purchaseHistories.remove(purchaseHistory);
    }

    @Override
    public double[] getSystemHistoryIncome(LocalDate startDate, LocalDate endDate) {
        double[] historyIncome = new double[(endDate.getYear() - startDate.getYear())*365 + (endDate.getDayOfYear() - startDate.getDayOfYear()) + 1];
        for(PurchaseHistory purchase : purchaseHistories){
            LocalDate purchaseDate = purchase.getLocalDate();
            if(!(purchaseDate.isBefore(startDate) || purchaseDate.isAfter(endDate)))
                historyIncome[(endDate.getYear() - purchaseDate.getYear())*365 + purchaseDate.getDayOfYear() - startDate.getDayOfYear()] += purchase.getPrice();
        }

        return historyIncome;
    }
}
