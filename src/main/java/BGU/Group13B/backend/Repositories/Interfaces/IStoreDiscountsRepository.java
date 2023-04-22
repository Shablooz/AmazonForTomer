package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IStoreDiscountsRepository {
    Set<Discount> getStoreDiscounts(int storeId);
    int addStoreVisibleDiscount(int storeId, double discountPercentage, LocalDateTime discountLastDate);
    int addStoreConditionalDiscount(int storeId, double discountPercentage, LocalDateTime discountLastDate,
                                    double totalAmountThresholdForDiscount, int quantityForDiscount);
    int addStoreHiddenDiscount(int storeId, double discountPercentage, LocalDateTime discountLastDate, String code);
    void removeStoreDiscount(int storeId, int discountId);
    void removeStoreDiscounts(int storeId);
}
