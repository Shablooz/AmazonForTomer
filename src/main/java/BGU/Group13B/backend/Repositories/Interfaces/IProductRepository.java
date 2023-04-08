package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.storePackage.Product;

import java.util.Optional;
import java.util.Set;

public interface IProductRepository {
    Optional<Set<Product>> getStoreProducts(int storeId);

    public void removeStoreProduct(int productId, int storeId);

    public void addProduct(int productId, int StoreId /*to complete*/) ;
}
