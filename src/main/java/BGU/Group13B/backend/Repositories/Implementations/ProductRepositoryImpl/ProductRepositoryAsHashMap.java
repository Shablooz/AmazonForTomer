package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepositoryAsHashMap implements IProductRepository {

    private final ConcurrentHashMap<Integer/*storeId*/, Set<Product>> storeProducts;

    public ProductRepositoryAsHashMap() {
        this.storeProducts = new ConcurrentHashMap<>();
    }


}
