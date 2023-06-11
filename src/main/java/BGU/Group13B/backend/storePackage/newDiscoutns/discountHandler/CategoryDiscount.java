package BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler;

import BGU.Group13B.backend.User.BasketInfo;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.User.UserInfo;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.DiscountAccumulationNode;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Entity
public class CategoryDiscount extends StoreDiscount {

    private String category;

    public CategoryDiscount(int discountId, int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        super(discountId, storeId, condition, discountPercentage, expirationDate, coupon);
        this.category = category;
    }

    public CategoryDiscount(int discountId, int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category) {
        this(discountId, storeId, condition, discountPercentage, expirationDate, category, null);
    }

    public CategoryDiscount(int discountId, int storeId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        this(discountId, storeId, null, discountPercentage, expirationDate, category, coupon);
    }

    public CategoryDiscount(int discountId, int storeId, double discountPercentage, LocalDate expirationDate, String category) {
        this(discountId, storeId, null, discountPercentage, expirationDate, category, null);
    }

    public CategoryDiscount() {
        super();
        category = "noder";
    }

    public double getProductDiscountPercentage(BasketInfo basketInfo, UserInfo userInfo, int productId, List<String> coupons) {
        return basketInfo.basketProducts().stream().
                anyMatch(p -> p.getProductId() == productId && Objects.equals(p.getCategory(), category)) ?
                getDiscountPercentage(basketInfo, userInfo, coupons) : 0;
    }

    @Override
    public ProductDiscountMap computeProductDiscountMap(BasketInfo basketInfo, UserInfo userInfo, List<String> coupons){
        ProductDiscountMap productDiscountMap = new ProductDiscountMap(basketInfo);
        List<BasketProduct> allCategoryProducts = getAllProductsOfCategory(basketInfo);
        double discount = getDiscountPercentage(basketInfo, userInfo, coupons);
        for(BasketProduct product : allCategoryProducts){
            productDiscountMap.setProductDiscount(product.getProductId(), discount);
        }

        return productDiscountMap;
    }

    private List<BasketProduct> getAllProductsOfCategory(BasketInfo basketInfo){
        return basketInfo.basketProducts().stream().filter(p -> p.getCategory().equals(category)).collect(Collectors.toList());
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return super.toString() + " for category " + category;
    }
}