package BGU.Group13B.backend.storePackage.Discounts;

import java.time.DateTimeException;
import java.time.LocalDateTime;

public class HiddenDiscount extends Discount{
    private String code;//to think about

    private LocalDateTime discountLastDate;

    public HiddenDiscount(String code, Integer priority, double discountPercentage, LocalDateTime discountLastDate) {
        super(priority, discountPercentage);
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.discountLastDate = discountLastDate;
    }
    @Override
    double apply(double currentPrice) {
        if (LocalDateTime.now().isBefore(discountLastDate)) {
            return super.apply(currentPrice);
        }
        throw new DateTimeException("Discount is no longer valid");
    }
}
