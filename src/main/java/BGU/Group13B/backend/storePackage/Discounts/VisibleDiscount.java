package BGU.Group13B.backend.storePackage.Discounts;

import BGU.Group13B.backend.User.BasketProduct;

import java.time.LocalDateTime;
import java.util.Collection;

public class VisibleDiscount extends Discount {
    //a very sad class :(

    public VisibleDiscount(int discountId, int parentId, double discountPercentage, LocalDateTime discountLastDate) {
        super(discountId, parentId, discountPercentage, discountLastDate);
    }

    /*@Override
    public double applyStoreDiscount(double totalAmount, Collection<BasketProduct> successfulProducts, String storeCoupon) {
        return 0;
    }*/
}
