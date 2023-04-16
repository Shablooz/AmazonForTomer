package BGU.Group13B.backend.storePackage.Discounts;

import BGU.Group13B.backend.User.BasketProduct;

import java.time.LocalDateTime;
import java.util.Collection;

public abstract class Discount {
    protected Integer priority;
    protected double discountPercentage;
    protected LocalDateTime discountLastDate;


    public Discount(Integer priority, double discountPercentage, LocalDateTime discountLastDate) {
        this.priority = priority;
        this.discountPercentage = discountPercentage;
        this.discountLastDate = discountLastDate;
    }

    public double applyProductDiscount(double currentPrice, int quantity, String couponCode) {
        if (!isExpired())
            return currentPrice * (1 - discountPercentage);
        return currentPrice;
    }

    public double applyStoreDiscount(double totalAmount, Collection<BasketProduct> successfulProducts,
                                     String storeCoupon) {
        if (!isExpired())
            return totalAmount * (1 - discountPercentage);
        return totalAmount;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(discountLastDate);
    }
}

