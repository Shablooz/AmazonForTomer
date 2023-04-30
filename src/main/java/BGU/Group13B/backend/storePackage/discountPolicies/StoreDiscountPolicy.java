package BGU.Group13B.backend.storePackage.discountPolicies;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountsRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.DiscountPolicy;
import BGU.Group13B.backend.storePackage.Discounts.Discount;
import BGU.Group13B.frontEnd.service.SingletonCollection;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StoreDiscountPolicy implements DiscountPolicy {

    private final int storeId;
    private final IStoreDiscountsRepository storeDiscounts;


    public StoreDiscountPolicy(int storeId) {
        this.storeId = storeId;
        this.storeDiscounts = SingletonCollection.getStoreDiscountsRepository();
    }

    public double applyAllDiscounts(double totalPrice,
                                    ConcurrentLinkedQueue<BasketProduct> successfulProducts,
                                    String storeCoupon){
        for(Discount discount : storeDiscounts.getStoreDiscounts(storeId)){
            if(discount.isExpired())
                storeDiscounts.removeStoreDiscount(storeId, discount.getDiscountId());
            else
                totalPrice = discount.applyStoreDiscount(totalPrice, successfulProducts, storeCoupon);
        }
        return totalPrice;
    }

    @Override
    public int addVisibleDiscount(double discountPercentage, LocalDateTime discountLastDate) {
        return storeDiscounts.addStoreVisibleDiscount(storeId, discountPercentage, discountLastDate);
    }

    @Override
    public int addConditionalDiscount(double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        return storeDiscounts.addStoreConditionalDiscount(storeId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount);
    }

    @Override
    public int addHiddenDiscount(double discountPercentage, LocalDateTime discountLastDate, String code) {
        return storeDiscounts.addStoreHiddenDiscount(storeId, discountPercentage, discountLastDate, code);
    }

    @Override
    public void removeDiscount(int discountId) {
        storeDiscounts.removeStoreDiscount(storeId, discountId);
    }
}
