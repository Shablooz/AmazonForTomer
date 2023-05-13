package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.time.LocalDateTime;
import java.util.Set;

public interface IProductDiscountsRepository {
    Set<Discount> getProductDiscounts(int productId);
    int addProductVisibleDiscount(int productId, double discountPercentage, LocalDateTime discountLastDate);
    int addProductConditionalDiscount(int productId, double discountPercentage, LocalDateTime discountLastDate,
                                      double minPriceForDiscount, int quantityForDiscount);
    int addProductHiddenDiscount(int productId, double discountPercentage, LocalDateTime discountLastDate, String code);
    void removeProductDiscount(int productId, int discountId);
    void removeProductDiscounts(int productId);

    void reset();
}
