package BGU.Group13B.backend.Repositories.Implementations.ProductDiscountsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.ConditionalDiscount;
import BGU.Group13B.backend.storePackage.Discounts.Discount;
import BGU.Group13B.backend.storePackage.Discounts.HiddenDiscount;
import BGU.Group13B.backend.storePackage.Discounts.VisibleDiscount;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductDiscountsRepositoryAsHashMap implements IProductDiscountsRepository {

    private final ConcurrentHashMap<Integer/*productId*/, ConcurrentSkipListSet<Discount>> productDiscounts;
    private final AtomicInteger discountIdCounter = new AtomicInteger(0);

    public ProductDiscountsRepositoryAsHashMap() {
        this.productDiscounts = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Discount> getProductDiscounts(int productId) {
        //if the product has no discounts, return an empty set
        return productDiscounts.getOrDefault(productId, new ConcurrentSkipListSet<>());
    }

    @Override
    public int addProductVisibleDiscount(int productId, double discountPercentage, LocalDateTime discountLastDate) {
        int discountId = discountIdCounter.getAndIncrement();
        if(!productDiscounts.containsKey(productId))
            productDiscounts.put(productId, new ConcurrentSkipListSet<>());

        productDiscounts.get(productId).add(new VisibleDiscount(discountId, productId, discountPercentage, discountLastDate));
        return discountId;
    }

    @Override
    public int addProductConditionalDiscount(int productId, double discountPercentage, LocalDateTime discountLastDate, double minPriceForDiscount, int quantityForDiscount) {
        int discountId = discountIdCounter.getAndIncrement();
        if(!productDiscounts.containsKey(productId))
            productDiscounts.put(productId, new ConcurrentSkipListSet<>());

        productDiscounts.get(productId).add(new ConditionalDiscount(discountId, productId, discountPercentage, discountLastDate, minPriceForDiscount, quantityForDiscount));
        return discountId;
    }

    @Override
    public int addProductHiddenDiscount(int productId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        int discountId = discountIdCounter.getAndIncrement();
        if(!productDiscounts.containsKey(productId))
            productDiscounts.put(productId, new ConcurrentSkipListSet<>());

        productDiscounts.get(productId).add(new HiddenDiscount(discountId, productId, discountPercentage, discountLastDate, code));
        return discountId;
    }

    @Override
    public void removeProductDiscount(int productId, int discountId) {
        if (!productDiscounts.containsKey(productId))
            throw new IllegalArgumentException("Product " + productId + " discount " + discountId + " does not exist");

        var discounts =  productDiscounts.get(productId);
        Optional<Discount> discount = discounts.stream().filter(d -> d.getDiscountId() == discountId).findFirst();
        if (discount.isEmpty())
            throw new IllegalArgumentException("Product " + productId + " discount " + discountId + " does not exist");
        discounts.remove(discount.get());
    }

    @Override
    public void removeProductDiscounts(int productId) {
        productDiscounts.remove(productId);
    }

    @Override
    public void reset() {
        productDiscounts.clear();
        discountIdCounter.set(0);
    }
}
