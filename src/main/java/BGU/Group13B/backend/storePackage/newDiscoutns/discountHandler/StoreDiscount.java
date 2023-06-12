package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.PurchaseFailedException;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.purchaseBounders.PurchaseExceedsPolicyException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class StoreDiscount {
    @Id
    private int discountId;
    private int storeId;
    protected String coupon;

    @OneToOne(cascade = CascadeType.ALL)
    protected Condition condition;

    protected double discountPercentage;
    protected LocalDate expirationDate;

    public StoreDiscount(int discountId, int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String coupon) {
        this.discountId = discountId;
        this.storeId = storeId;
        this.coupon = coupon;
        if (!LocalDate.now().isBefore(expirationDate))
            throw new IllegalArgumentException("expiration date must be in the future");

        if (discountPercentage < 0 || discountPercentage > 1)
            throw new IllegalArgumentException("discount percentage must be between 0 and 1");

        this.condition = condition;
        this.expirationDate = expirationDate;
        this.discountPercentage = discountPercentage;
    }

    public StoreDiscount(int discountId, int storeId, double discountPercentage, LocalDate expirationDate, String coupon) {
        this(discountId, storeId, null, discountPercentage, expirationDate, coupon);
    }

    public StoreDiscount(int discountId, int storeId, Condition condition, double discountPercentage, LocalDate expirationDate) {
        this(discountId, storeId, condition, discountPercentage, expirationDate, null);
    }

    public StoreDiscount(int discountId, int storeId, double discountPercentage, LocalDate expirationDate) {
        this(discountId, storeId, null, discountPercentage, expirationDate, null);
    }

    public StoreDiscount() {
        this.discountId = 1;
        this.condition = null;
        this.storeId = 1;
        this.coupon = null;
        this.discountPercentage = 0;
        this.expirationDate = LocalDate.now();
    }

    public double getDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        return satisfiesCoupon(coupons) && satisfiesCondition(basketInfo, userInfo) ? discountPercentage : 0.0;
    }

    public double getProductDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, int productId, List<String> coupons) {
        return basketInfo.basketProducts().stream().anyMatch(p -> p.getProductId() == productId) ?
                getDiscountPercentage(basketInfo, userInfo, coupons) : 0;
    }

    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        ProductDiscountMap productDiscountMap = new ProductDiscountMap(basketInfo);
        double discount = getDiscountPercentage(basketInfo, userInfo, coupons);
        for (BasketProduct product : basketInfo.basketProducts()) {
            productDiscountMap.setProductDiscount(product.getProductId(), discount);
        }

        return productDiscountMap;
    }

    private boolean satisfiesCoupon(List<String> coupons) {
        return coupon == null || coupons.contains(coupon);
    }

    private boolean satisfiesCondition(BasketInfo basketInfo, UserInfo userInfo) {
        if (condition != null) {
            try {
                condition.satisfied(basketInfo, userInfo);
            } catch (PurchaseFailedException e) {
                return false;
            }
        }
        return true;
    }

    public int getDiscountId() {
        return discountId;
    }

    @Override
    public String toString() {
        return (condition != null ? (condition + " ⟹ ") : (""))
                + 100 * discountPercentage + "%";

    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
