package BGU.Group13B.backend.Repositories.Implementations.StoreDiscountsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.ConditionalDiscount;
import BGU.Group13B.backend.storePackage.Discounts.Discount;
import BGU.Group13B.backend.storePackage.Discounts.HiddenDiscount;
import BGU.Group13B.backend.storePackage.Discounts.VisibleDiscount;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreDiscountsRepositoryAsHashMap implements IStoreDiscountsRepository {
    private final ConcurrentHashMap<Integer/*storeId*/, ConcurrentSkipListSet<Discount>> storeDiscounts;

    private final AtomicInteger discountIdCounter = new AtomicInteger(0);

    public StoreDiscountsRepositoryAsHashMap() {
        storeDiscounts = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Discount> getStoreDiscounts(int storeId) {
        //if the store has no discounts, return an empty set
        return storeDiscounts.getOrDefault(storeId, new ConcurrentSkipListSet<>());
    }

    @Override
    public int addStoreVisibleDiscount(int storeId, double discountPercentage, LocalDateTime discountLastDate) {
        int discountId = discountIdCounter.getAndIncrement();
        if (!storeDiscounts.containsKey(storeId))
            storeDiscounts.put(storeId, new ConcurrentSkipListSet<>());
        storeDiscounts.get(storeId).add(new VisibleDiscount(discountId, storeId, discountPercentage, discountLastDate));
        return discountId;
    }

    @Override
    public int addStoreConditionalDiscount(int storeId, double discountPercentage, LocalDateTime discountLastDate, double totalAmountThresholdForDiscount, int quantityForDiscount) {
        int discountId = discountIdCounter.getAndIncrement();
        if (!storeDiscounts.containsKey(storeId))
            storeDiscounts.put(storeId, new ConcurrentSkipListSet<>());
        storeDiscounts.get(storeId).add(new ConditionalDiscount(discountLastDate, discountId, storeId, discountPercentage, totalAmountThresholdForDiscount, quantityForDiscount));
        return discountId;
    }

    @Override
    public int addStoreHiddenDiscount(int storeId, double discountPercentage, LocalDateTime discountLastDate, String code) {
        int discountId = discountIdCounter.getAndIncrement();
        if (!storeDiscounts.containsKey(storeId))
            storeDiscounts.put(storeId, new ConcurrentSkipListSet<>());
        storeDiscounts.get(storeId).add(new HiddenDiscount(discountId, storeId, discountPercentage, discountLastDate, code));
        return discountId;
    }

    @Override
    public void removeStoreDiscount(int storeId, int discountId) {
        if (!storeDiscounts.containsKey(storeId))
            throw new IllegalArgumentException("store " + storeId + " discount " + discountId + " does not exist");

        var discounts =  storeDiscounts.get(storeId);
        Optional<Discount> discount = discounts.stream().filter(d -> d.getDiscountId() == discountId).findFirst();
        if (discount.isEmpty())
            throw new IllegalArgumentException("store " + storeId + " discount " + discountId + " does not exist");
        discounts.remove(discount.get());
    }
}
