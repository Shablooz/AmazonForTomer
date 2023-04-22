package BGU.Group13B.backend.storePackage.Discounts;

import BGU.Group13B.backend.User.BasketProduct;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

public class HiddenDiscount extends Discount {
    private final String realCode;


    public HiddenDiscount(int discountId, int parentId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        super(discountId, parentId, discountPercentage, discountLastDate);
        // code must be not null
        Objects.requireNonNull(code);
        this.realCode = code;
        this.discountPercentage = discountPercentage;
        this.discountLastDate = discountLastDate;
    }


    @Override
    public double applyProductDiscount(double currentPrice, int quantity, String couponCode) {
        double priceFromSuper = super.applyProductDiscount(currentPrice, quantity, couponCode);
        if(isExpired())//edge case
            return priceFromSuper;
        if(couponCode.equals(realCode))
            return priceFromSuper * (1 - discountPercentage);// additional discount
        return priceFromSuper;
    }

    @Override
    public double applyStoreDiscount(double totalAmount, Collection<BasketProduct> successfulProducts, String storeCoupon) {
        return 0;
    }

}
