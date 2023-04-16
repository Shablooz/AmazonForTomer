package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Product;

import java.util.Optional;
import java.util.Set;

public interface IProductRepository {//deeded because it contains the product quantity

    Optional<Set<Product>> getStoreProducts(int storeId);

    public void removeStoreProduct(int productId, int storeId);

    public void addProduct(int StoreId /*to complete*/);//generate unique productId thanks:)

    //double calculatePrice(int storeId, int productId, int productQuantity, String couponCode);
}
