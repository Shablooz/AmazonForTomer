package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.BasketProduct;

public interface IProductHistoryRepository {
    void addProductToHistory(BasketProduct basketProduct, int userId);
    void removeProductFromHistory(int productId, int userId);
    void removeAllProductsFromHistory(int userId);
}
