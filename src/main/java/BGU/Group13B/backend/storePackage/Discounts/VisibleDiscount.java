package BGU.Group13B.backend.storePackage.Discounts;

import java.time.LocalDateTime;

public class VisibleDiscount extends Discount {
    //a very sad class :(

    public VisibleDiscount(Integer priority, double discountPercentage, LocalDateTime discountLastDate) {
        super(priority, discountPercentage, discountLastDate);
    }
}
