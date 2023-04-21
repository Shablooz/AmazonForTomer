package BGU.Group13B.backend.storePackage.Discounts;

import BGU.Group13B.backend.User.BasketProduct;

import java.time.LocalDateTime;
import java.util.Collection;

public class ConditionalDiscount extends Discount {

    private double minPriceForDiscount;
    private double totalAmountThresholdForDiscount;
    private final int quantityForDiscount;

    /*
    * for use of products
    * */
    public ConditionalDiscount(int discountId, int productId, double discountPercentage, LocalDateTime discountLastDate,
                               double minPriceForDiscount, int quantityForDiscount) {
        super(discountId, productId, discountPercentage, discountLastDate);
        this.minPriceForDiscount = minPriceForDiscount;
        this.quantityForDiscount = quantityForDiscount;
    }
    /*
    * for use of stores
    * */
    public ConditionalDiscount(LocalDateTime discountLastDate, int discountId, int storeId, double discountPercentage,
                               double totalAmountThresholdForDiscount, int quantityForDiscount) {
        super(discountId, storeId, discountPercentage, discountLastDate);
        this.totalAmountThresholdForDiscount = totalAmountThresholdForDiscount;
        this.quantityForDiscount = quantityForDiscount;
    }

    @Override
    public double applyProductDiscount(double currentPrice, int quantity, String couponCode) {//Predicate<T> -> boolean
        double priceFromSuper = super.applyProductDiscount(currentPrice, quantity, couponCode);
        if (isExpired())//edge case
            return priceFromSuper;
        if (currentPrice >= minPriceForDiscount || quantity >= quantityForDiscount)
            return priceFromSuper * (1 - discountPercentage);
        return priceFromSuper;
    }

    @Override
    public double applyStoreDiscount(double totalAmount, Collection<BasketProduct> successfulProducts, String storeCoupon) {
        double priceFromSuper = super.applyStoreDiscount(totalAmount, successfulProducts, storeCoupon);
        if (isExpired())//edge case
            return priceFromSuper;
        if (priceFromSuper >= totalAmountThresholdForDiscount)
            return priceFromSuper * (1 - discountPercentage);
        return priceFromSuper;
    }
}
