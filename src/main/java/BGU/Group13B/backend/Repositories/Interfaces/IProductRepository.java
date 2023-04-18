package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Product;

import java.util.List;

public interface IProductRepository {
    void specialFunction();
    void add(Product item);
    void remove();
    Product get(int productId);

    List<Product> getProductByName(String name);

    List<Product> getProductByCategory(String category);

    List<Product> getProductByKeywords(List<String> keywords);

    List<Product> filterByPriceRange(int minPrice, int maxPrice);

    Product getProduct(int productId, int storeId);
}
