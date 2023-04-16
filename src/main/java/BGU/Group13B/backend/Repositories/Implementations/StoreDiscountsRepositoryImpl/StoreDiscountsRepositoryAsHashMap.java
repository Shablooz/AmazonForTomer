package BGU.Group13B.backend.Repositories.Implementations.StoreDiscountsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

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
        if(!storeDiscounts.containsKey(storeId))
            return Optional.empty();
        return Optional.of(storeDiscounts.get(storeId));
    }

    @Override
    public void addStoreDiscount(int storeId, Discount discount) {
        if(storeDiscounts.putIfAbsent(storeId, List.of(discount)) != null)
            storeDiscounts.get(storeId).add(discount);
    }
}
