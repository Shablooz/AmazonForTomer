package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.UserInfo;

import java.time.LocalDate;
import java.util.Objects;

public class CategoryDiscount extends StoreDiscount {
    private final String category;

    public CategoryDiscount(Condition condition, double discountPercentage, LocalDate expirationDate, String category) {
        super(condition, discountPercentage, expirationDate);
        this.category = category;
    }

    public CategoryDiscount(double discountPercentage, LocalDate expirationDate, String category) {
        super(discountPercentage, expirationDate);
        this.category = category;
    }

    public double getProductDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, int productId) {
        return basketInfo.basketProducts().stream().anyMatch(p -> p.getProductId() == productId && Objects.equals(p.getCategory(), category)) ?
                getDiscountPercentage(basketInfo, userInfo) : 0;
    }

    public String getCategory() {
        return category;
    }
}