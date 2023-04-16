package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.List;
import java.util.Optional;

public interface IStoreDiscountsRepository {
    Optional<List<Discount>> getStoreDiscounts(int storeId);
    void addStoreDiscount(int storeId, Discount discount);
}
