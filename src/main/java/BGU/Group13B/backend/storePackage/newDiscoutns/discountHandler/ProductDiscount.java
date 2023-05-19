package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;


import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.DiscountAccumulationNode;
import BGU.Group13B.service.SingletonCollection;

import java.time.LocalDate;
import java.util.List;

public class ProductDiscount extends StoreDiscount {
    private final int productId;

    public ProductDiscount(int discountId, int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        super(discountId, storeId, condition, discountPercentage, expirationDate, coupon);
        this.productId = productId;
    }

    public ProductDiscount(int discountId, int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId) {
        this(discountId, storeId, condition, discountPercentage, expirationDate, productId, null);
    }

    public ProductDiscount(int discountId, int storeId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        this(discountId, storeId, null, discountPercentage, expirationDate, productId, coupon);
    }

    public ProductDiscount(int discountId, int storeId, double discountPercentage, LocalDate expirationDate, int productId) {
        this(discountId, storeId, null, discountPercentage, expirationDate, productId, null);
    }

    public double getProductDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, int productId, List<String> coupons) {
        if (productId != this.productId)
            return 0;

        return getDiscountPercentage(basketInfo, userInfo, coupons);
    }

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons) {
        ProductDiscountMap productDiscountMap = new ProductDiscountMap(basketInfo);
        for (BasketProduct product : basketInfo.basketProducts()) {
            productDiscountMap.setProductDiscount(product.getProductId(), getProductDiscountPercentage(basketInfo, userInfo, productId, coupons));
        }

        return productDiscountMap;
    }

    public int getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        //lo naim ma shekore' po
        String productName = SingletonCollection.getProductRepository().getProductById(productId).getName();
        return super.toString() + " for product " + productName;
    }
}