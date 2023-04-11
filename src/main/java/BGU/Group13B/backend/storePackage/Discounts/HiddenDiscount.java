package BGU.Group13B.backend.storePackage.Discounts;

import java.time.DateTimeException;
import java.time.LocalDateTime;

public class HiddenDiscount extends Discount {
    private String code;//to think about


    public HiddenDiscount(String code, Integer priority, double discountPercentage, LocalDateTime discountLastDate) {
        super(priority, discountPercentage, discountLastDate);
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.discountLastDate = discountLastDate;
    }

    @Override
    public double apply(double currentPrice, int quantity) {
        double priceFromSuper = super.apply(currentPrice, quantity);
        if(code.equals("SUPER_SECRET_CODE"))
            return priceFromSuper * (1 - discountPercentage);
        return priceFromSuper;
    }

}
