package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;

import java.time.LocalDate;
import java.util.List;

public interface IDiscountRepository {
    int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String coupon);
    int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String coupon);
    int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate);
    int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate);

    int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category, String coupon);
    int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category, String coupon);
    int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category);
    int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category);

    int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId, String coupon);
    int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId, String coupon);
    int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId);
    int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId);

    List<StoreDiscount> getStoreDiscounts(int storeId);
    StoreDiscount getDiscount(int discountId, int storeId);
    void removeDiscount(int discountId, int storeId);
    void reset();

    void removeStoreProductDiscounts(int storeId, int productId);
    void removeAllStoreDiscounts(int storeId);
    void setSaveMode(boolean saveMode);
}
