package BGU.Group13B.backend.storePackage;

import java.time.LocalDateTime;

public interface DiscountPolicy {
    int addVisibleDiscount(double discountPercentage, LocalDateTime discountLastDate);

    int addConditionalDiscount(double discountPercentage, LocalDateTime discountLastDate,
                               double minPriceForDiscount, int quantityForDiscount);
    int addHiddenDiscount(double discountPercentage, LocalDateTime discountLastDate, String code);

    void removeDiscount(int discountId);

    void removeAllDiscounts();
}
