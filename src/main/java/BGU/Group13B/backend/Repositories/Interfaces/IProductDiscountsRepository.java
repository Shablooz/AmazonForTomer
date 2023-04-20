package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.List;
import java.util.Optional;

public interface IProductDiscountsRepository {
    Optional<List<Discount>> getProductDiscounts(int productId);
    void addProductDiscount(int productId, Discount discount);
    void removeProductDiscount(int productId, Discount discount);
}
