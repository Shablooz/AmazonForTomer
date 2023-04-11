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
        if(!productDiscounts.containsKey(productId))
            return Optional.empty();
        return Optional.of(productDiscounts.get(productId));
    }

    @Override
    public void addProductDiscount(int productId, Discount discount) {
        if(productDiscounts.putIfAbsent(productId, List.of(discount)) != null)
            productDiscounts.get(productId).add(discount);
    }
}
