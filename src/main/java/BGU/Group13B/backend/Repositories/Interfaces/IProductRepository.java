package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IProductRepository {//deeded because it contains the product quantity

    Optional<Set<Product>> getStoreProducts(int storeId);

    void removeStoreProduct(int productId, int storeId);

    Product addProduct(int storeId, String name, String category, double price, int stockQuantity, String description);

    List<Product> getProductByName(String name);

    List<Product> getProductByCategory(String category);

    List<Product> getProductByKeywords(List<String> keywords);

    Product getStoreProductById(int productId, int storeId);

    Product getProductById(int productId);

    void removeStoreProducts(int storeId);

    void reset();

    void hideAllStoreProducts(int storeId);

    void unhideAllStoreProducts(int storeId);

    int addHiddenProduct(int storeId, String name, String category, double price, int stockQuantity, String description);

    void setSaveMode(boolean saveMode);


    boolean getSaveMode();

    boolean isStoreProductsExists(int storeId);
}
