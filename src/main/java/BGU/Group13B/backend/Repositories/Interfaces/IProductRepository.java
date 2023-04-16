package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IProductRepository {//deeded because it contains the product quantity

    Optional<Set<Product>> getStoreProducts(int storeId);

    void removeStoreProduct(int productId, int storeId);

    void addProduct(int StoreId /*to complete*/);//generate unique productId thanks:)

    List<Product> getProductByName(String name);

    List<Product> getProductByCategory(String category);

    List<Product> getProductByKeywords(List<String> keywords);

    List<Product> filterByPriceRange(int minPrice, int maxPrice);
}
