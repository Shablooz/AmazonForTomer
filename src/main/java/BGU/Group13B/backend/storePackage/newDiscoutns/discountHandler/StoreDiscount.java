package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;

import java.time.LocalDate;

public class StoreDiscount {

    protected final Condition condition;
    protected final double discountPercentage;
    protected final LocalDate expirationDate;

    protected StoreDiscount(Condition condition, double discountPercentage, LocalDate expirationDate) {
        if (!LocalDate.now().isBefore(expirationDate))
            throw new IllegalArgumentException("expiration date must be in the future");

        if(discountPercentage < 0 || discountPercentage > 1)
            throw new IllegalArgumentException("discount percentage must be between 0 and 1");

        this.condition = condition;
        this.expirationDate = expirationDate;
        this.discountPercentage = discountPercentage;
    }

    protected StoreDiscount(double discountPercentage, LocalDate expirationDate ){
        this(null, discountPercentage, expirationDate);
    }

    public double getDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo) {
        return condition == null ? discountPercentage :
                condition.satisfied(basketInfo, userInfo) ? discountPercentage : 0.0;
    }

    public double getProductDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, int productId){
        return basketInfo.basketProducts().stream().anyMatch(p -> p.getProductId() == productId) ?
                getDiscountPercentage(basketInfo, userInfo) : 0;
    }
}
