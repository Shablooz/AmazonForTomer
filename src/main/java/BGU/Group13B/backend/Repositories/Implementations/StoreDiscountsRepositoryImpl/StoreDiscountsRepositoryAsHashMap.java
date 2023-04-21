package BGU.Group13B.backend.Repositories.Implementations.StoreDiscountsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StoreDiscountsRepositoryAsHashMap implements IStoreDiscountsRepository {
    private final ConcurrentHashMap<Integer/*storeId*/, List<Discount>> storeDiscounts;

    public StoreDiscountsRepositoryAsHashMap() {
        storeDiscounts = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<List<Discount>> getStoreDiscounts(int storeId) {
        return Optional.ofNullable(storeDiscounts.get(storeId));

    }

    @Override
    public void addStoreDiscount(int storeId, Discount discount) {
        List<Discount> discounts = new LinkedList<>();
        discounts.add(discount);
        if(storeDiscounts.putIfAbsent(storeId, discounts) != null)
            storeDiscounts.get(storeId).add(discount);
    }
}
