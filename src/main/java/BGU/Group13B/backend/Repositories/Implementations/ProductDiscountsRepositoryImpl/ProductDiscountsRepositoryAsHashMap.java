package BGU.Group13B.backend.Repositories.Implementations.ProductDiscountsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductDiscountsRepository;
import BGU.Group13B.backend.storePackage.Discounts.Discount;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProductDiscountsRepositoryAsHashMap implements IProductDiscountsRepository {

    private final ConcurrentHashMap<Integer/*productId*/, List<Discount>> productDiscounts;

    public ProductDiscountsRepositoryAsHashMap() {
        this.productDiscounts = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<List<Discount>> getProductDiscounts(int productId) {
        if (!productDiscounts.containsKey(productId))
            return Optional.empty();
        return Optional.of(productDiscounts.get(productId));
    }

    @Override
    public void addProductDiscount(int productId, Discount discount) {
        if (!productDiscounts.containsKey(productId))
            productDiscounts.put(productId, List.of(discount));
        else {
            var discounts = productDiscounts.get(productId);
            if (discounts.contains(discount))
                throw new IllegalArgumentException("Discount already exists");
            discounts.add(discount);
        }
    }

    @Override
    public void removeProductDiscount(int productId, Discount discount) {
        if (!productDiscounts.containsKey(productId))
            throw new IllegalArgumentException("Product does not exist");
        var discounts = productDiscounts.get(productId);
        if (!discounts.contains(discount))
            throw new IllegalArgumentException("Discount does not exist");
        discounts.remove(discount);
    }
}
