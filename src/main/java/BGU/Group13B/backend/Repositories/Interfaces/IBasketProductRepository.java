package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.BasketProduct;

import java.util.List;
import java.util.Optional;

public interface IBasketProductRepository {
    Optional<List<BasketProduct>> getBasketProducts(int storeId, int userId);
    void removeBasketProducts(int storeId, int userId);
    void addNewProductToBasket(int productId, int storeId, int userId);
    void changeProductQuantity(int productId, int storeId, int userId, int addedQuantity);
    BasketProduct getBasketProduct(int productId, int storeId, int userId) throws IllegalArgumentException;

    void removeBasketProduct(int productId, int userId, int storeId);
}
