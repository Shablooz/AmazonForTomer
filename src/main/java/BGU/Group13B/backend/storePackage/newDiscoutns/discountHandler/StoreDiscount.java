package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;

import java.time.LocalDate;
import java.util.List;

public class StoreDiscount{

    private final int discountId;
    private final int storeId;
    protected final String coupon;
    protected final Condition condition;
    protected final double discountPercentage;
    protected final LocalDate expirationDate;

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
        if( condition != null){
            try{
                condition.satisfied(basketInfo, userInfo);
            }
            catch (PurchaseExceedsPolicyException e){
                return false;
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
                + 100*discountPercentage + "%";

    }
}
