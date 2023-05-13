package BGU.Group13B.backend.storePackage.discountPolicies;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.DiscountPolicy;
import BGU.Group13B.backend.storePackage.Discounts.Discount;
import BGU.Group13B.service.SingletonCollection;

import java.time.LocalDateTime;

public class ProductDiscountPolicy implements DiscountPolicy {

    private final int productId;
    private final IProductDiscountsRepository productDiscounts;


    public ProductDiscountPolicy(int productId) {
        this.productId = productId;
        this.productDiscounts = SingletonCollection.getProductDiscountsRepository();
    }

    public double applyAllDiscounts(double price, int productQuantity, String couponCodes){
        for(Discount discount : productDiscounts.getProductDiscounts(productId)){
            if(discount.isExpired())
                productDiscounts.removeProductDiscount(productId, discount.getDiscountId());
            else
                price = discount.applyProductDiscount(price, productQuantity, couponCodes);
        }
        return price;
    }


    @Override
    public int addVisibleDiscount(double discountPercentage, LocalDateTime discountLastDate) {
        return productDiscounts.addProductVisibleDiscount(productId, discountPercentage, discountLastDate);
    }

    @Override
    public int addConditionalDiscount(double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        return productDiscounts.addProductConditionalDiscount(productId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
    }

    @Override
    public int addHiddenDiscount(double discountPercentage, LocalDateTime discountLastDate, String code) {
        return productDiscounts.addProductHiddenDiscount(productId, discountPercentage, discountLastDate, code);
    }

    @Override
    public void removeDiscount(int discountId) {
        productDiscounts.removeProductDiscount(productId, discountId);
    }

    @Override
    public void removeAllDiscounts() {
        productDiscounts.removeProductDiscounts(productId);
    }
}
