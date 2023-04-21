package BGU.Group13B.backend.storePackage.Discounts;

import BGU.Group13B.backend.User.BasketProduct;

import java.time.LocalDateTime;
import java.util.Collection;

public abstract class Discount implements Comparable<Discount>{

    private final int discountId;
    private final int parentId;
    protected double discountPercentage;
    protected LocalDateTime discountLastDate;


    public Discount(int discountId, int parentId, double discountPercentage, LocalDateTime discountLastDate) {
        this.discountId = discountId;
        this.parentId = parentId;
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

    public int getDiscountId() {
        return discountId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public int compareTo(Discount o) {
        return Integer.compare(this.discountId, o.discountId);
    }


}

