package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;


import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;

import java.time.LocalDate;

public class ProductDiscount extends StoreDiscount {
    private final int productId;

    public ProductDiscount(Condition condition, double discountPercentage, LocalDate expirationDate, int productId) {
        super(condition, discountPercentage, expirationDate);
        this.productId = productId;
    }

    public ProductDiscount(double discountPercentage, LocalDate expirationDate, int productId) {
        super(discountPercentage, expirationDate);
        this.productId = productId;
    }

    public double getProductDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, int productId){
        if(productId != this.productId)
            return 0;

        return basketInfo.basketProducts().stream().anyMatch(p -> p.getProductId() == productId) ?
                getDiscountPercentage(basketInfo, userInfo) : 0;
    }

    public int getProductId() {
        return productId;
    }
}