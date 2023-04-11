package BGU.Group13B.backend.storePackage.Discounts;

import java.time.LocalDateTime;

public class ConditionalDiscount extends Discount {

    private final double minPriceForDiscount;
    private final int quantityForDiscount;
    public ConditionalDiscount(Integer priority, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        super(priority, discountPercentage, discountLastDate);
        this.minPriceForDiscount = minPriceForDiscount;
        this.quantityForDiscount = quantityForDiscount;
    }

    @Override
    public double apply(double currentPrice, int quantity) {
        double priceFromSuper = super.apply(currentPrice, quantity);
        if (currentPrice >= minPriceForDiscount || quantity >= quantityForDiscount)
            return priceFromSuper * (1 - discountPercentage);
        return priceFromSuper;
    }
}
