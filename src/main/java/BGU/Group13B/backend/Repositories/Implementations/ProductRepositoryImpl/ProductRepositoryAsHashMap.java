package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepositoryAsHashMap implements IProductRepository {

    private final ConcurrentHashMap<Integer/*storeId*/, Set<Product>> storeProducts;

    public ProductRepositoryAsHashMap() {
        this.storeProducts = new ConcurrentHashMap<>();
    }


    @Override
    public Optional<Set<Product>> getStoreProducts(int storeId) {
        return Optional.empty();
    }

    @Override
    public void removeStoreProduct(int productId, int storeId) {

    }

    @Override
    public void addProduct(int StoreId) {

    }

    @Override
    public double calculatePrice(int storeId, int productId, int productQuantity) {
        if (!storeProducts.containsKey(storeId))
            throw new IllegalArgumentException("Store not found");
        return storeProducts.get(storeId)
                .stream().filter(product -> product.getProductId() == productId).
                findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Product not found in store")
                ).calculatePrice(productQuantity);
    }
}
