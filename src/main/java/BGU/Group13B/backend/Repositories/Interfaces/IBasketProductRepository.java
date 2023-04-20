package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Product;

import java.util.Optional;
import java.util.Set;

public interface IBasketProductRepository {
    Optional<Set<BasketProduct>> getBasketProducts(int storeId, int userId);
    void removeStoreProduct(int productId, int storeId, int userId);
    void addNewProductToBasket(int productId, int storeId, int userId);
    void changeProductQuantity(int productId, int storeId, int userId, int addedQuantity);
    BasketProduct getBasketProduct(int productId, int storeId, int userId) throws IllegalArgumentException;
}
